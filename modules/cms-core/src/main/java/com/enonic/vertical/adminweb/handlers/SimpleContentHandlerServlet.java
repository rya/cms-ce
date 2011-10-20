/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.util.DateUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.adminweb.AdminStore;
import com.enonic.vertical.adminweb.VerticalAdminException;
import com.enonic.vertical.adminweb.VerticalAdminLogger;
import com.enonic.vertical.adminweb.handlers.xmlbuilders.ContentXMLBuildersSpringManagedBeansBridge;
import com.enonic.vertical.engine.AccessRight;
import com.enonic.vertical.engine.VerticalEngineException;

import com.enonic.cms.core.content.command.ImportContentCommand;
import com.enonic.cms.core.content.imports.ImportResult;
import com.enonic.cms.core.content.mail.ImportedContentAssignmentMailTemplate;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.service.AdminService;

import com.enonic.cms.core.content.AssignmentDataParser;

import com.enonic.cms.core.content.imports.ImportJob;
import com.enonic.cms.core.mail.MailRecipient;

import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.imports.ImportResultXmlCreator;
import com.enonic.cms.core.security.user.UserEntity;

final public class SimpleContentHandlerServlet
    extends ContentBaseHandlerServlet
{


    public void init( ServletConfig servletConfig )
        throws ServletException
    {
        super.init( servletConfig );
        setContentXMLBuilder( ContentXMLBuildersSpringManagedBeansBridge.getSimpleContentXMLBuilder() );
    }

    public void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, String operation )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();

        try
        {
            if ( "fileimportform".equals( operation ) )
            {
                fileImportForm( request, response, formItems );
            }
            else if ( "fileimport".equals( operation ) )
            {
                fileImport( user, request, response, formItems );
            }
            else if ( "xml".equals( operation ) )
            {
                viewXML( response, formItems );
            }
            else if ( "linkcontentineditor".equals( operation ) )
            {
                linkContentInEditor( response, request, formItems );
            }
            else
            {
                super.handlerCustom( request, response, session, adminService, formItems, operation );
            }
        }
        catch ( IOException e )
        {
            VerticalAdminLogger.errorAdmin("I/O error: %t", e );
        }
    }

    private void linkContentInEditor( HttpServletResponse response, HttpServletRequest request, ExtendedMap formItems )
    {
        User user = securityService.getLoggedInAdminConsoleUser();
        int contentKey = formItems.getInt( "key" );
        int categoryKey = formItems.getInt( "cat" );

        int unitKey = adminService.getUnitKey( categoryKey );

        Document xmlContent = adminService.getContent( user, contentKey, 0, 1, 0 ).getAsDOMDocument();

        Document doc = XMLTool.createDocument( "contents" );
        XMLTool.mergeDocuments( doc, xmlContent );

        Document xmlCategory = adminService.getSuperCategoryNames( categoryKey, false, true ).getAsDOMDocument();
        XMLTool.mergeDocuments( doc, xmlCategory, true );

        Map<String, Object> xslParams = new HashMap<String, Object>();

        xslParams.put( "content", "true" );
        xslParams.put( "unitname", unitKey );

        transformXML( request, response, doc, "editor/contentpopup_selected.xsl", xslParams );
    }

    private void viewXML( HttpServletResponse response, ExtendedMap formItems )
        throws VerticalAdminException, IOException
    {

        User user = securityService.getLoggedInAdminConsoleUser();

        // get xsl
        int key = formItems.getInt( "key" );
        String xmlData = adminService.getContent( user, key, 0, 1, 0 ).getAsString();

        response.getWriter().write( xmlData );
    }

    private void fileImport( User oldUser, HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems )
        throws VerticalAdminException
    {

        int categoryKey = formItems.getInt( "cat" );
        int unitKey = formItems.getInt( "selectedunitkey" );

        try
        {
            String importName = formItems.getString( "importname" );
            FileItem fileItem = formItems.getFileItem( "importfile" );
            Date publishFrom = null;
            Date publishTo = null;

            if ( formItems.containsKey( "date_pubdata_publishfrom" ) )
            {
                try
                {
                    StringBuffer date = new StringBuffer( formItems.getString( "date_pubdata_publishfrom" ) );
                    date.append( ' ' );
                    date.append( formItems.getString( "time_pubdata_publishfrom", "00:00" ) );
                    publishFrom = DateUtil.parseDateTime( date.toString() );
                    if ( formItems.containsKey( "date_pubdata_publishto" ) )
                    {
                        date = new StringBuffer( formItems.getString( "date_pubdata_publishto" ) );
                        date.append( ' ' );
                        date.append( formItems.getString( "time_pubdata_publishto", "00:00" ) );
                        publishTo = DateUtil.parseDateTime( date.toString() );
                    }
                }
                catch ( ParseException pe )
                {
                    String message = "Failed to parse publish from or to date: %t";
                    VerticalAdminLogger.errorAdmin(message, pe );
                }
            }

            Document doc = XMLTool.createDocument( "data" );
            XMLTool.mergeDocuments( doc, adminService.getSuperCategoryNames( categoryKey, true, true ).getAsDOMDocument(), true );

            Map<String, Object> xslParams = new HashMap<String, Object>();
            xslParams.put( "cat", formItems.getString( "cat" ) );
            xslParams.put( "page", formItems.getString( "page" ) );
            addCommonParameters( adminService, null, request, xslParams, unitKey, -1 );

            final CategoryEntity categoryToImportTo = categoryDao.findByKey( new CategoryKey( categoryKey ) );
            if ( categoryToImportTo == null )
            {
                throw new IllegalArgumentException( "Category does not exist " + categoryKey );
            }

            final ImportContentCommand importContentCommand = new ImportContentCommand();
            importContentCommand.importer = this.securityService.getUser( oldUser );
            importContentCommand.categoryToImportTo = categoryToImportTo;
            importContentCommand.importName = importName;
            importContentCommand.publishFrom = publishFrom != null ? new DateTime( publishFrom ) : null;
            importContentCommand.publishTo = publishTo != null ? new DateTime( publishTo ) : null;
            importContentCommand.inputStream = fileItem.getInputStream();

            AssignmentDataParser assignmentDataParser = new AssignmentDataParser( formItems );

            String assigneeKeyString = assignmentDataParser.getAssigneeKey();

            if ( StringUtils.isNotBlank( assigneeKeyString ) )
            {
                UserEntity assignee = userDao.findByKey( assigneeKeyString );

                if ( assignee == null )
                {
                    throw new IllegalArgumentException( "Assignee not found: " + assigneeKeyString );
                }

                String assignmentDescr = assignmentDataParser.getAssignmentDescription();
                Date assignmentDueDate = assignmentDataParser.getAssignmentDueDate();

                importContentCommand.assigneeKey = assignee.getKey();
                importContentCommand.assignmentDescription = assignmentDescr;
                importContentCommand.assignmentDueDate = assignmentDueDate;
            }

            final ImportJob importJob = importJobFactory.createImportJob( importContentCommand );
            final ImportResult report = importJob.start();

            final ImportResultXmlCreator reportCreator = new ImportResultXmlCreator();
            reportCreator.setIncludeContentInformation( false );

            final boolean sendAssignmentMail = report.getAssigned().size() > 0 && importJob.getAssignee() != null;

            if ( sendAssignmentMail )
            {
                sendAssignmentMail( oldUser, assignmentDataParser, importJob, report );
            }

            XMLTool.mergeDocuments( doc, reportCreator.getReport( report ).getAsString(), true );

            transformXML( request, response, doc, "fileimport_report.xsl", xslParams );
        }
        catch ( IOException e )
        {
            VerticalAdminLogger.errorAdmin("I/O error: %t", e );
        }
    }

    private void sendAssignmentMail( User oldUser, AssignmentDataParser assignmentDataParser, ImportJob importJob, ImportResult report )
    {
        ImportedContentAssignmentMailTemplate mailTemplate = new ImportedContentAssignmentMailTemplate( report.getAssigned().keySet(),
                                                                                                        contentDao );
        mailTemplate.setAssignmentDescription( assignmentDataParser.getAssignmentDescription() );
        mailTemplate.setAssignmentDueDate( assignmentDataParser.getAssignmentDueDate() );

        UserEntity assigner = userDao.findByKey( oldUser.getKey() );
        mailTemplate.setAssigner( assigner );

        mailTemplate.setFrom( new MailRecipient( assigner.getDisplayName(), assigner.getEmail() ) );
        mailTemplate.addRecipient( importJob.getAssignee() );
        sendMailService.sendMail( mailTemplate );
    }

    private void fileImportForm( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems )
        throws VerticalAdminException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        int categoryKey = formItems.getInt( "cat" );
        int unitKey = formItems.getInt( "selectedunitkey" );
        int contentTypeKey = adminService.getContentTypeKeyByCategory( categoryKey );

        Document doc = XMLTool.createDocument( "data" );
        XMLTool.mergeDocuments( doc, adminService.getSuperCategoryNames( categoryKey, true, true ).getAsDOMDocument(), true );
        XMLTool.mergeDocuments( doc, adminService.getAccessRights( user, AccessRight.CATEGORY, categoryKey, true ).getAsDOMDocument() );
        XMLTool.mergeDocuments( doc, adminService.getContentTypeModuleData( contentTypeKey ).getAsDOMDocument() );

        Map<String, Object> xslParams = new HashMap<String, Object>();
        xslParams.put( "cat", formItems.getString( "cat" ) );
        xslParams.put( "page", formItems.getString( "page" ) );

        xslParams.put( "_current_user_key", user.getKey().toString() );

        addCommonParameters( adminService, null, request, xslParams, unitKey, -1 );
        transformXML( request, response, doc, "fileimport_form.xsl", xslParams );
    }

    protected void addCustomData( HttpSession session, User user, AdminService admin, Document doc, int contentKey, int contentTypeKey,
                                  ExtendedMap formItems, ExtendedMap parameters )
    {
        parameters.put( "current_uid", user.getName() );
    }

    private final static String FORM_TEMPLATE = "__build_form_xsl.xsl";

    protected DOMSource buildXSL( HttpSession session, AdminService admin, int contentTypeKey )
        throws VerticalAdminException
    {

        DOMSource result = null;
        try
        {
            Document sourceDoc = admin.getContentTypeModuleData( contentTypeKey ).getAsDOMDocument();

            // Set whether fields are indexed or not
            Document indexDoc = XMLTool.domparse( admin.getIndexingParametersXML( contentTypeKey ) );
            Element[] indexingParams = XMLTool.getElements( indexDoc.getDocumentElement(), "index" );

            Element browseElem = XMLTool.getElement( sourceDoc.getDocumentElement(), "browse" );
            Element[] fields = XMLTool.getElements( browseElem, "field" );
            for ( Element field : fields )
            {
                String xpath = XMLTool.getElementText( XMLTool.getElement( field, "xpath" ) );
                boolean indexed = false;

                // Check whether this xpath is in the index doc
                if ( xpath != null )
                {
                    for ( Element indexingParam : indexingParams )
                    {
                        if ( ( xpath ).equals( indexingParam.getAttribute( "xpath" ) ) )
                        {
                            indexed = true;
                        }
                    }
                }

                field.setAttribute( "indexed", String.valueOf( indexed ) );
            }

            Element rootElement = sourceDoc.getDocumentElement();

            // check for xsl:
            boolean enablePreview = false;
            Element previewXSLElement = XMLTool.getElement( rootElement, "previewxsl" );
            if ( previewXSLElement != null )
            {
                String cdata = XMLTool.getElementText( previewXSLElement );
                if ( cdata.length() > 0 )
                {
                    enablePreview = true;
                }
            }

            // extract module xml:
            Element moduleElement = XMLTool.getElement( rootElement, "config" );
            sourceDoc = XMLTool.createDocument();
            sourceDoc.appendChild( sourceDoc.importNode( moduleElement, true ) );

            StringWriter swriter = new StringWriter();
            Map<String, Object> xslParams = new HashMap<String, Object>();

            xslParams.put( "xsl_prefix", "" );

            xslParams.put( "enablepreview", String.valueOf( enablePreview ) );
            Source xslFile = AdminStore.getStylesheet( session, FORM_TEMPLATE );
            transformXML( session, swriter, new DOMSource( sourceDoc ), xslFile, xslParams );

            result = new DOMSource( XMLTool.domparse( swriter.toString() ) );
            result.setSystemId( xslFile.getSystemId() );
        }
        catch ( TransformerConfigurationException e )
        {
            VerticalAdminLogger.errorAdmin("XSLT error: %t", e );
        }
        catch ( TransformerException e )
        {
            VerticalAdminLogger.errorAdmin("XSLT error: %t", e );
        }

        return result;
    }


}
