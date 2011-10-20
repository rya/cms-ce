/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.dom.DOMSource;

import com.google.common.io.Files;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateMidnight;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.collect.Sets;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.io.FileUtil;
import com.enonic.esl.net.URL;
import com.enonic.esl.servlet.http.CookieUtil;
import com.enonic.esl.util.ArrayUtil;
import com.enonic.esl.util.DateUtil;
import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.VerticalProperties;
import com.enonic.vertical.adminweb.AdminHandlerBaseServlet;
import com.enonic.vertical.adminweb.AdminHelper;
import com.enonic.vertical.adminweb.AdminStore;
import com.enonic.vertical.adminweb.AssigneeFormModel;
import com.enonic.vertical.adminweb.AssigneeFormModelFactory;
import com.enonic.vertical.adminweb.SearchUtility;
import com.enonic.vertical.adminweb.VerticalAdminException;
import com.enonic.vertical.adminweb.VerticalAdminLogger;
import com.enonic.vertical.adminweb.wizard.Wizard;
import com.enonic.vertical.adminweb.wizard.WizardException;
import com.enonic.vertical.adminweb.wizard.WizardLogger;
import com.enonic.vertical.engine.AccessRight;
import com.enonic.vertical.engine.CategoryAccessRight;
import com.enonic.vertical.engine.ContentAccessRight;
import com.enonic.vertical.engine.Types;
import com.enonic.vertical.engine.VerticalEngineException;
import com.enonic.vertical.engine.VerticalSecurityException;
import com.enonic.vertical.engine.VerticalUpdateException;
import com.enonic.vertical.engine.criteria.CategoryCriteria;
import com.enonic.vertical.presentation.renderer.VerticalRenderException;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.content.AssignContentResult;
import com.enonic.cms.core.content.AssignmentAction;
import com.enonic.cms.core.content.AssignmentActionResolver;
import com.enonic.cms.core.content.AssignmentDataParser;
import com.enonic.cms.core.content.ContentAccessEntity;
import com.enonic.cms.core.content.ContentAccessException;
import com.enonic.cms.core.content.ContentAndVersion;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentLocationSpecification;
import com.enonic.cms.core.content.ContentLocations;
import com.enonic.cms.core.content.ContentMoveAccessException;
import com.enonic.cms.core.content.ContentParserService;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.ContentSourceXmlCreator;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.ContentVersionKey;
import com.enonic.cms.core.content.ContentXMLCreator;
import com.enonic.cms.core.content.PageCacheInvalidatorForContent;
import com.enonic.cms.core.content.UnassignContentResult;
import com.enonic.cms.core.content.UpdateContentResult;
import com.enonic.cms.core.content.access.ContentAccessResolver;
import com.enonic.cms.core.content.binary.BinaryData;
import com.enonic.cms.core.content.binary.BinaryDataAndBinary;
import com.enonic.cms.core.content.binary.BinaryDataKey;
import com.enonic.cms.core.content.category.CategoryAccessType;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.category.access.CategoryAccessResolver;
import com.enonic.cms.core.content.command.AssignContentCommand;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.command.SnapshotContentCommand;
import com.enonic.cms.core.content.command.UnassignContentCommand;
import com.enonic.cms.core.content.command.UpdateAssignmentCommand;
import com.enonic.cms.core.content.command.UpdateContentCommand;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.mail.AssignmentMailSender;
import com.enonic.cms.core.content.mail.ImportedContentAssignmentMailTemplate;
import com.enonic.cms.core.content.query.RelatedChildrenContentQuery;
import com.enonic.cms.core.content.resultset.RelatedContentResultSet;
import com.enonic.cms.core.log.LogType;
import com.enonic.cms.core.log.StoreNewLogEntryCommand;
import com.enonic.cms.core.log.Table;
import com.enonic.cms.core.mail.ApproveAndRejectMailTemplate;
import com.enonic.cms.core.mail.MailRecipient;
import com.enonic.cms.core.mail.SendMailService;
import com.enonic.cms.core.resource.ResourceFile;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.service.AdminService;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemAccessRightAccumulator;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateKey;
import com.enonic.cms.core.structure.page.template.PageTemplateType;
import com.enonic.cms.core.xslt.XsltProcessor;
import com.enonic.cms.core.xslt.XsltProcessorException;
import com.enonic.cms.core.xslt.XsltProcessorManager;
import com.enonic.cms.core.xslt.XsltProcessorManagerAccessor;
import com.enonic.cms.core.xslt.XsltResource;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.business.DeploymentPathResolver;
import com.enonic.cms.business.portal.cache.SiteCachesService;
import com.enonic.cms.business.preview.NoLazyInitializationEnforcerForPreview;

import com.enonic.cms.domain.portal.rendering.RenderedPageResult;
import com.enonic.cms.domain.stylesheet.StylesheetNotFoundException;

/**
 * Base servlet for servlets handling content. Provides common methods.
 */
public class ContentBaseHandlerServlet
    extends AbstractContentHandlerServlet
{

    public static final int COOKIE_TIMEOUT = 60 * 60 * 24 * 365 * 50;

    private static final Logger LOG = LoggerFactory.getLogger( ContentBaseHandlerServlet.class );

    protected static class DummyFileItem
        implements FileItem
    {

        String fieldName;

        File file;

        DummyFileItem( File file )
        {
            this.file = file;
        }

        public void delete()
        {
        }

        public byte[] get()
        {
            return null;
        }

        public String getContentType()
        {
            return null;
        }

        public String getFieldName()
        {
            return fieldName;
        }

        public InputStream getInputStream()
            throws IOException
        {
            return new FileInputStream( file );
        }

        public String getName()
        {
            return file.getName();
        }

        public OutputStream getOutputStream()
            throws IOException
        {
            return null;
        }

        public long getSize()
        {
            return 0;
        }

        public String getString()
        {
            return null;
        }

        public String getString( String arg0 )
            throws UnsupportedEncodingException
        {
            return null;
        }

        public boolean isFormField()
        {
            return false;
        }

        public boolean isInMemory()
        {
            return false;
        }

        public void setFieldName( String arg0 )
        {
        }

        public void setFormField( boolean arg0 )
        {
        }

        public void write( File arg0 )
            throws Exception
        {
        }
    }

    public static abstract class ImportZipWizard
        extends Wizard
    {
        @Autowired
        private ContentDao contentDao;

        @Autowired
        private UserDao userDao;

        @Autowired
        private SendMailService sendMailService;

        @Autowired
        private ContentService contentService;

        @Autowired
        private SecurityService securityService;

        @Autowired
        private ContentParserService contentParserService;

        @Autowired
        private SiteCachesService siteCachesService;

        private int[] imageContentTypes;

        private int[] fileContentTypes;

        protected void initialize( AdminService admin, Document wizardconfigDoc )
            throws WizardException
        {
            // fetch image and file content types
            imageContentTypes = admin.getContentTypeKeysByHandler( ContentEnhancedImageHandlerServlet.class.getName() );
            Arrays.sort( imageContentTypes );
            fileContentTypes = admin.getContentTypeKeysByHandler( ContentFileHandlerServlet.class.getName() );
            Arrays.sort( fileContentTypes );
        }

        protected boolean evaluate( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems,
                                    String testCondition )
            throws WizardException
        {
            // no conditions defined
            return false;
        }

        protected void appendCustomData( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems,
                                         ExtendedMap parameters, User user, Document dataconfigDoc, Document wizarddataDoc )
            throws WizardException
        {
            int categoryKey = formItems.getInt( "cat" );
            String categoryName = admin.getCategoryName( categoryKey );
            formItems.put( "categoryname", categoryName );
            int unitKey = admin.getUnitKey( categoryKey );
            formItems.put( "selectedunitkey", unitKey );
            String xmlCat = admin.getSuperCategoryNames( categoryKey, false, true );
            XMLTool.mergeDocuments( wizarddataDoc, XMLTool.domparse( xmlCat ), true );
        }

        protected boolean validateState( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems )
        {
            boolean validState;
            String currentStepName = wizardState.getCurrentStep().getName();
            Document stepstateDoc = wizardState.getCurrentStepState().getStateDoc();
            if ( "step0".equals( currentStepName ) )
            {
                if ( !wizardState.hasErrors() )
                {
                    Element zipElem = XMLTool.getElement( stepstateDoc.getDocumentElement(), "zip" );
                    if ( zipElem == null || !zipElem.hasAttribute( "dir" ) )
                    {
                        validState = false;
                        wizardState.addError( "6", "zipfile" );
                    }
                    else
                    {
                        validState = true;
                    }
                }
                else
                {
                    validState = false;
                }
            }
            else if ( "step1".equals( currentStepName ) )
            {
                validState = false;
                for ( Object o : formItems.keySet() )
                {
                    String key = (String) o;
                    String entryPrefix = "entry_0_";
                    if ( key.startsWith( entryPrefix ) )
                    {
                        validState = true;
                        break;
                    }
                }
                if ( !validState )
                {
                    wizardState.addError( "10", "files" );
                }
            }
            else
            {
                validState = true;
            }

            return validState;
        }

        protected void saveState( WizardState wizardState, HttpServletRequest request, HttpServletResponse response, AdminService admin,
                                  User user, ExtendedMap formItems )
            throws WizardException
        {
            super.saveState( wizardState, request, response, admin, user, formItems );

            String stepName = wizardState.getCurrentStep().getName();
            // step 0: unzip zip file and create temporary directory
            if ( "step0".equals( stepName ) )
            {
                StepState stepState = wizardState.getCurrentStepState();
                Document stateDoc = stepState.getStateDoc();
                Element stepstateElem = stateDoc.getDocumentElement();
                try
                {
                    FileItem zipFileItem = formItems.getFileItem( "zipfile", null );
                    String zipFileName = zipFileItem.getName();
                    if ( !zipFileName.endsWith( ".zip" ) )
                    {
                        wizardState.addError( "11", "zipfile" );
                    }

                    File dir = Files.createTempDir();
                    FileUtil.inflateZipFile( zipFileItem.getInputStream(), dir, VerticalProperties.getVerticalProperties().getProperty(
                        "cms.admin.zipimport.excludePattern" ) );
                    File[] files = dir.listFiles();
                    Element zipElem = XMLTool.createElement( stateDoc, stepstateElem, "zip" );
                    zipElem.setAttribute( "dir", dir.getAbsolutePath() );
                    int categoryKey = formItems.getInt( "cat" );
                    CategoryAccessRight categoryAccessRight = admin.getCategoryAccessRight( user, categoryKey );
                    boolean adminRight = categoryAccessRight.getAdministrate();
                    if ( adminRight )
                    {
                        zipElem.setAttribute( "admin", "true" );
                    }
                    int contentTypeKey = admin.getContentTypeKeyByCategory( categoryKey );
                    boolean someExists = filesToXML( zipElem, files, admin, user, categoryKey, adminRight, contentTypeKey );
                    if ( !someExists )
                    {
                        zipElem.setAttribute( "allchecked", "true" );
                    }
                }
                catch ( IOException ioe )
                {
                    String message = "Failed to inflate zip file: %t";
                    WizardLogger.error(message, ioe );
                    wizardState.addError( "12", "zipfile", StringUtil.expandString( message, ioe ) );
                }
            }
            // update zip structure from first state
            else if ( "step1".equals( stepName ) )
            {
                Document firstStateDoc = wizardState.getFirstStepState().getStateDoc();
                Element firstZipElem = XMLTool.getFirstElement( firstStateDoc.getDocumentElement() );
                Document stateDoc = wizardState.getCurrentStepState().getStateDoc();
                Element zipElem = XMLTool.getFirstElement( stateDoc.getDocumentElement() );
                zipElem.setAttribute( "dir", firstZipElem.getAttribute( "dir" ) );

                boolean publish = Boolean.valueOf( zipElem.getAttribute( "publish" ) );
                if ( publish )
                {
                    if ( formItems.containsKey( "date_publishfrom" ) )
                    {
                        Element publishfromElem = XMLTool.createElement( stateDoc, zipElem, "publishfrom" );
                        publishfromElem.setAttribute( "date", formItems.getString( "date_publishfrom" ) );
                        publishfromElem.setAttribute( "time", formItems.getString( "time_publishfrom" ) );

                    }
                    if ( formItems.containsKey( "date_publishto" ) )
                    {
                        Element publishfromElem = XMLTool.createElement( stateDoc, zipElem, "publishto" );
                        publishfromElem.setAttribute( "date", formItems.getString( "date_publishto" ) );
                        publishfromElem.setAttribute( "time", formItems.getString( "time_publishto" ) );
                    }
                }

                Map<String, Element> entryMap = new HashMap<String, Element>();
                for ( Object o : formItems.keySet() )
                {
                    String key = (String) o;
                    String entryPrefix = "entry_0_";
                    if ( key.startsWith( entryPrefix ) )
                    {
                        StringBuffer entryId = new StringBuffer( entryPrefix );
                        StringTokenizer positions = new StringTokenizer( key.substring( 8 ), "_" );

                        Element parentElem = zipElem;
                        Element[] entryElems = XMLTool.getElements( firstZipElem );
                        while ( positions.hasMoreTokens() )
                        {
                            int position = Integer.parseInt( positions.nextToken() ) - 1;
                            entryId.append( "_" );
                            entryId.append( position );
                            Element entryElem = entryMap.get( entryId.toString() );
                            if ( entryElem == null )
                            {
                                entryElem = XMLTool.createElement( stateDoc, parentElem, "entry" );
                                entryElem.setAttribute( "type", entryElems[position].getAttribute( "type" ) );
                                entryElem.setAttribute( "name", entryElems[position].getAttribute( "name" ) );
                                if ( entryElems[position].hasAttribute( "exists" ) )
                                {
                                    entryElem.setAttribute( "exists", entryElems[position].getAttribute( "exists" ) );
                                }
                                entryMap.put( entryId.toString(), entryElem );
                            }

                            parentElem = entryElem;
                            entryElems = XMLTool.getElements( entryElems[position] );
                        }
                    }
                }
            }
        }

        /**
         * @see com.enonic.vertical.adminweb.wizard.Wizard#cancelClicked(com.enonic.vertical.adminweb.wizard.Wizard.WizardState)
         */
        protected void cancelClicked( WizardState wizardState )
        {
            cleanup( wizardState );
        }

        private void cleanup( WizardState wizardState )
        {
            StepState firstStepState = wizardState.getFirstStepState();
            if ( firstStepState != null )
            {
                Document stateDoc = firstStepState.getStateDoc();
                Element zipElem = XMLTool.getFirstElement( stateDoc.getDocumentElement() );
                if ( zipElem != null )
                {
                    File dir = new File( zipElem.getAttribute( "dir" ) );

                    try {
                        FileUtils.deleteDirectory(dir);
                    } catch (final Exception e) {
                        // Do nothing
                    }
                }
            }
        }

        private boolean filesToXML( Element root, File[] files, AdminService admin, User user, int categoryKey, boolean adminRight,
                                    int contentTypeKey )
        {
            boolean someExists = false;
            for ( File file : files )
            {
                someExists |= fileToXML( root, file, admin, user, categoryKey, adminRight, contentTypeKey );
            }
            return someExists;
        }

        private boolean fileToXML( Element root, File file, AdminService admin, User user, int superCategoryKey, boolean superAdminRight,
                                   int superContentTypeKey )
        {
            Document doc = root.getOwnerDocument();
            Element entryElem = XMLTool.createElement( doc, root, "entry" );
            String name = file.getName();
            entryElem.setAttribute( "name", name );
            boolean someExists;
            if ( file.isDirectory() )
            {
                entryElem.setAttribute( "type", "dir" );

                boolean adminRight;
                int contentTypeKey;
                int categoryKey = ( superCategoryKey >= 0 ? admin.getCategoryKey( superCategoryKey, name ) : -1 );
                if ( categoryKey >= 0 )
                {
                    entryElem.setAttribute( "exists", "true" );
                    contentTypeKey = admin.getContentTypeKeyByCategory( categoryKey );

                    // check rights
                    CategoryAccessRight categoryAccessRight = admin.getCategoryAccessRight( user, categoryKey );
                    adminRight = categoryAccessRight.getAdministrate();
                }
                else
                {
                    adminRight = superAdminRight;
                    contentTypeKey = superContentTypeKey;
                }

                if ( adminRight )
                {
                    entryElem.setAttribute( "admin", "true" );
                }
                String contentType = getContentTypeInternal( contentTypeKey );
                if ( contentType != null )
                {
                    entryElem.setAttribute( "contenttype", contentType );
                }

                someExists = filesToXML( entryElem, file.listFiles(), admin, user, categoryKey, adminRight, contentTypeKey );
                if ( !someExists )
                {
                    entryElem.setAttribute( "allchecked", "true" );
                }
            }
            else
            {
                entryElem.setAttribute( "type", "file" );
                if ( isFiltered( name ) )
                {
                    entryElem.setAttribute( "filtered", "true" );
                }
                if ( superCategoryKey >= 0 && admin.contentExists( superCategoryKey, cropName( name ) ) )
                {
                    entryElem.setAttribute( "exists", "true" );
                    someExists = true;

                    int contentKey = admin.getContentKey( superCategoryKey, cropName( name ) );
                    ContentAccessRight contentAccessRight = admin.getContentAccessRight( user, contentKey );
                    if ( contentAccessRight.getUpdate() )
                    {
                        entryElem.setAttribute( "update", "true" );
                    }
                }
                else
                {
                    someExists = false;
                    if ( superAdminRight )
                    {
                        entryElem.setAttribute( "update", "true" );
                    }
                }
            }
            return someExists;
        }

        protected abstract boolean isFiltered( String name );

        private String getContentTypeInternal( int contentTypeKey )
        {
            String contentType;
            if ( Arrays.binarySearch( imageContentTypes, contentTypeKey ) >= 0 )
            {
                contentType = "image";
            }
            else if ( Arrays.binarySearch( fileContentTypes, contentTypeKey ) >= 0 )
            {
                contentType = "file";
            }
            else
            {
                contentType = null;
            }
            return contentType;
        }

        protected void processWizardData( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems,
                                          User user, Document dataDoc )
            throws VerticalAdminException, VerticalEngineException
        {
            Document stateDoc = wizardState.getStepState( "step1" ).getStateDoc();
            Element zipElem = XMLTool.getFirstElement( stateDoc.getDocumentElement() );
            File dir = new File( zipElem.getAttribute( "dir" ) );

            // if content is to be published, set publish dates
            final String importContentStatus = zipElem.getAttribute( "publish" );
            boolean doPublish = importContentStatus.equals( Integer.toString( ContentStatus.APPROVED.getKey() ) );

            if ( doPublish )
            {
                formItems.put( "importedContentStatus", 2 );
                formItems.put( "published", true );
                Element publishfromElem = XMLTool.getElement( zipElem, "publishfrom" );
                if ( publishfromElem != null )
                {
                    formItems.put( "date_pubdata_publishfrom", publishfromElem.getAttribute( "date" ) );
                    formItems.put( "time_pubdata_publishfrom", publishfromElem.getAttribute( "time" ) );
                }
                Element publishtoElem = XMLTool.getElement( zipElem, "publishto" );
                if ( publishtoElem != null )
                {
                    formItems.put( "date_pubdata_publishto", publishtoElem.getAttribute( "date" ) );
                    formItems.put( "time_pubdata_publishto", publishtoElem.getAttribute( "time" ) );
                }
            }
            else
            {
                formItems.put( "importedContentStatus", 0 );
            }

            // save files
            Element[] entryElems = XMLTool.getElements( zipElem, "entry" );
            int categoryKey = formItems.getInt( "cat" );
            int unitKey = admin.getUnitKey( categoryKey );
            formItems.put( "selectedunitkey", unitKey );
            ContentBaseHandlerServlet cbhServlet = (ContentBaseHandlerServlet) servlet;

            Set<ContentKey> assignedContent = Sets.newHashSet();

            AssignmentDataParser assignmentDataParser = new AssignmentDataParser( formItems );
            String assigneeKey = assignmentDataParser.getAssigneeKey();

            saveEntries( user, admin, formItems, cbhServlet, categoryKey, entryElems, dir, assignmentDataParser, assignedContent );

            if ( assignedContent.size() > 0 && StringUtils.isNotBlank( assigneeKey ) )
            {
                sendImportedContentAssignedMail( user, assignedContent, assignmentDataParser, assigneeKey );
            }

            // clean up
            cleanup( wizardState );
        }

        private void sendImportedContentAssignedMail( User user, Set<ContentKey> assignedContent, AssignmentDataParser assignmentDataParser,
                                                      String assigneeKey )
        {
            ImportedContentAssignmentMailTemplate mailTemplate = new ImportedContentAssignmentMailTemplate( assignedContent, contentDao );
            mailTemplate.setAssignmentDescription( assignmentDataParser.getAssignmentDescription() );
            mailTemplate.setAssignmentDueDate( assignmentDataParser.getAssignmentDueDate() );

            UserEntity assigner = userDao.findByKey( user.getKey() );
            mailTemplate.setAssigner( assigner );

            mailTemplate.setFrom( new MailRecipient( user.getDisplayName(), user.getEmail() ) );

            UserEntity assignee = userDao.findByKey( assigneeKey );

            mailTemplate.addRecipient( new MailRecipient( assignee.getName(), assignee.getEmail() ) );
            sendMailService.sendMail( mailTemplate );
        }

        private void saveEntries( User user, AdminService admin, ExtendedMap oldFormItems, ContentBaseHandlerServlet cbhServlet,
                                  int superCategoryKey, Element[] entryElems, File parentDir, AssignmentDataParser assignmentDataParser,
                                  Set<ContentKey> assignedContent )
            throws VerticalUpdateException, VerticalSecurityException, VerticalAdminException
        {
            oldFormItems.put( "newimage", true );

            for ( Element entryElem : entryElems )
            {
                ExtendedMap formItems = new ExtendedMap( oldFormItems );

                ContentKey storedContentKey = null;

                String fileName = entryElem.getAttribute( "name" );
                String fileType = entryElem.getAttribute( "type" );
                ContentStatus newVersionStatus = ContentStatus.get( formItems.getInt( "importedContentStatus" ) );

                boolean contentExists = Boolean.valueOf( entryElem.getAttribute( "exists" ) );
                boolean hasAssignee = assignmentDataParser.getAssigneeKey() != null;

                if ( "dir".equals( fileType ) )
                {
                    File dir = new File( parentDir, fileName );
                    int categoryKey;
                    if ( contentExists )
                    {
                        categoryKey = admin.getCategoryKey( superCategoryKey, fileName );
                    }
                    else
                    {
                        categoryKey = admin.createCategory( user, superCategoryKey, fileName );
                    }
                    saveEntries( user, admin, formItems, cbhServlet, categoryKey, XMLTool.getElements( entryElem ), dir,
                                 assignmentDataParser, assignedContent );
                }
                else
                {
                    File file = new File( parentDir, fileName );
                    BinaryData[] binaries = getBinaries( cbhServlet, admin, formItems, file );

                    formItems.put( cbhServlet.getContentXMLBuilder().getTitleFormKey(), cropName( fileName ) );

                    formItems.put( "cat", superCategoryKey );
                    if ( contentExists )
                    {
                        int contentKey = admin.getContentKey( superCategoryKey, cropName( fileName ) );
                        int versionKey = admin.getCurrentVersionKey( contentKey );
                        formItems.put( "createnewversion", true );
                        formItems.put( "key", contentKey );
                        formItems.put( "versionkey", versionKey );
                        formItems.put( "_pubdata_created", admin.getContentCreatedTimestamp( contentKey ) );

                        boolean published = formItems.getBoolean( "published", false );
                        if ( !published )
                        {
                            Date publishFrom = admin.getContentPublishFromTimestamp( contentKey );
                            if ( publishFrom != null )
                            {
                                String dateString = DateUtil.formatDateTime( publishFrom );
                                formItems.put( "date_pubdata_publishfrom", dateString.substring( 0, 10 ) );
                                formItems.put( "time_pubdata_publishfrom", dateString.substring( 11 ) );

                                Date publishTo = admin.getContentPublishToTimestamp( contentKey );
                                if ( publishTo != null )
                                {
                                    dateString = DateUtil.formatDateTime( publishTo );
                                    formItems.put( "date_pubdata_publishto", dateString.substring( 0, 10 ) );
                                    formItems.put( "time_pubdata_publishto", dateString.substring( 11 ) );
                                }
                            }
                        }

                        int versionState = admin.getContentVersionState( versionKey );
                        boolean setCurrentVersion = ( versionState < 2 ) || published;
                        String xmlData = cbhServlet.getContentXMLBuilder().buildXML( formItems, user, false, false, false );

                        UpdateContentResult result =
                            updateContent( user, xmlData, BinaryDataAndBinary.createNewFrom( binaries ), setCurrentVersion );

                        storedContentKey = result.getTargetedVersion().getContent().getKey();
                    }
                    else
                    {
                        String xmlData = cbhServlet.getContentXMLBuilder().buildXML( formItems, user, true, false, false );
                        storedContentKey = storeNewContent( user, binaries, xmlData );
                    }
                }

                if ( storedContentKey != null )
                {

                    final boolean doAssignContent = newVersionStatus.equals( ContentStatus.DRAFT ) && hasAssignee;

                    if ( doAssignContent )
                    {
                        AssignContentCommand assignContentCommand = new AssignContentCommand();
                        assignContentCommand.setAssigneeKey( new UserKey( assignmentDataParser.getAssigneeKey() ) );
                        assignContentCommand.setAssignerKey( user.getKey() );
                        assignContentCommand.setContentKey( storedContentKey );
                        assignContentCommand.setAssignmentDescription( assignmentDataParser.getAssignmentDescription() );
                        assignContentCommand.setAssignmentDueDate( assignmentDataParser.getAssignmentDueDate() );

                        contentService.assignContent( assignContentCommand );

                        assignedContent.add( storedContentKey );
                    }
                    else
                    {
                        ContentEntity storedContent = contentDao.findByKey( storedContentKey );

                        if ( storedContent.isAssigned() )
                        {
                            UnassignContentCommand unassignContentCommand = new UnassignContentCommand();
                            unassignContentCommand.setContentKey( storedContentKey );
                            unassignContentCommand.setUnassigner( user.getKey() );

                            contentService.unassignContent( unassignContentCommand );
                        }

                    }
                }
            }
        }

        protected ContentKey storeNewContent( User oldUser, BinaryData[] binaries, String xmlData )
        {
            UserEntity runningUser = securityService.getUser( oldUser );
            List<BinaryDataAndBinary> binaryDataAndBinaries = BinaryDataAndBinary.createNewFrom( binaries );

            boolean parseContentData = true; // always parse content data when creating content
            ContentAndVersion parsedContentAndVersion = contentParserService.parseContentAndVersion( xmlData, null, parseContentData );
            ContentEntity parsedContent = parsedContentAndVersion.getContent();
            ContentVersionEntity parsedVersion = parsedContentAndVersion.getVersion();

            CreateContentCommand createCommand = new CreateContentCommand();
            createCommand.setCreator( runningUser );
            createCommand.setAccessRightsStrategy( CreateContentCommand.AccessRightsStrategy.INHERIT_FROM_CATEGORY );

            /** Populate command with ContentEntity-data **/
            createCommand.populateCommandWithContentValues( parsedContent );
            createCommand.populateCommandWithContentVersionValues( parsedVersion );

            createCommand.setBinaryDatas( binaryDataAndBinaries );
            createCommand.setUseCommandsBinaryDataToAdd( true );

            return contentService.createContent( createCommand );
        }

        protected UpdateContentResult updateContent( User oldTypeUser, String xmlData, List<BinaryDataAndBinary> binariesToAdd,
                                                     boolean asCurrentVersion )
        {
            UserEntity runningUser = securityService.getUser( oldTypeUser );

            boolean parseContentData = true;
            ContentAndVersion parsedContentAndVersion = contentParserService.parseContentAndVersion( xmlData, null, parseContentData );
            ContentEntity parsedContent = parsedContentAndVersion.getContent();

            // be sure to add existing content's access rights, else the content will loose them all
            ContentEntity persistedContent = contentDao.findByKey( parsedContent.getKey() );
            for ( ContentAccessEntity contentAccess : persistedContent.getContentAccessRights() )
            {
                parsedContent.addContentAccessRight( contentAccess.copy() );
            }

            UpdateContentCommand updateContentCommand =
                UpdateContentCommand.storeNewVersionEvenIfUnchanged( persistedContent.getMainVersion().getKey() );
            updateContentCommand.setModifier( runningUser );

            updateContentCommand.populateContentValuesFromContent( parsedContent );
            updateContentCommand.populateContentVersionValuesFromContentVersion( parsedContentAndVersion.getVersion() );

            updateContentCommand.setUpdateAsMainVersion( asCurrentVersion );

            // always as new version
            updateContentCommand.setSyncAccessRights( false );

            updateContentCommand.setBinaryDataToAdd( binariesToAdd );
            updateContentCommand.setUseCommandsBinaryDataToAdd( true );

            // always removing all the previous binaries
            updateContentCommand.setBinaryDataToRemove( persistedContent.getMainVersion().getContentBinaryDataKeys() );
            updateContentCommand.setUseCommandsBinaryDataToRemove( true );

            UpdateContentResult updateContentResult = contentService.updateContent( updateContentCommand );

            if ( updateContentResult.isAnyChangesMade() )
            {
                new PageCacheInvalidatorForContent( siteCachesService ).invalidateForContent( updateContentResult.getTargetedVersion() );
            }

            return updateContentResult;
        }

        protected String cropName( String name )
        {
            return name;
        }

        protected abstract BinaryData[] getBinaries( ContentBaseHandlerServlet cbhServlet, AdminService admin, ExtendedMap formItems,
                                                     File file )
            throws VerticalAdminException;

    }

    protected String FORM_XSL = null;

    // handlerForm variables:

    protected ArrayList<String> extraFormXMLFiles = new ArrayList<String>();

    protected boolean alwaysDisabled = false;

    protected final void addEditorDataToDocument( AdminService admin, Document doc )
    {
        Document xmlLanguages = admin.getLanguages().getAsDOMDocument();
        XMLTool.mergeDocuments( doc, xmlLanguages, true );
    }

    protected void addCustomData( HttpSession session, User user, AdminService admin, Document doc, int contentKey, int contentTypeKey,
                                  ExtendedMap formItems, ExtendedMap parameters )
        throws VerticalAdminException
    {

        // Intentionally left blank for sub-classes to override
    }

    protected void preProcessContentDocument( User user, AdminService admin, Document doc, ExtendedMap formItems,
                                              HttpServletRequest request )
        throws VerticalAdminException
    {

        // Intentionally left blank for sub-classes to override
    }

    protected final void addUserRightToDocument( AdminService admin, User user, Document doc, int categoryKey )
    {
        String xmlAccessRights = admin.getAccessRights( user, AccessRight.CATEGORY, categoryKey, true );
        Document docAccessRights = XMLTool.domparse( xmlAccessRights );
        Element userrightElem = XMLTool.getElement( docAccessRights.getDocumentElement(), "userright" );
        if ( userrightElem != null )
        {
            docAccessRights.replaceChild( userrightElem, docAccessRights.getDocumentElement() );
            XMLTool.mergeDocuments( doc, docAccessRights, true );
        }
    }

    protected final void buildRelatedContentsXML( AdminService admin, User user, ExtendedMap formItems, Element contentsElem )
    {
        Document doc = contentsElem.getOwnerDocument();
        Element contentElem = XMLTool.getFirstElement( contentsElem );
        Element relatedcontentkeysElem = XMLTool.createElement( doc, contentElem, "relatedcontentkeys" );
        Element relatedcontentsElem = XMLTool.createElement( doc, contentsElem, "relatedcontents" );

        int[] relatedContentKeys = contentXMLBuilder.getRelatedContentKeys( formItems );
        if ( relatedContentKeys != null && relatedContentKeys.length > 0 )
        {
            for ( int relatedContentKey : relatedContentKeys )
            {
                Element elem = XMLTool.createElement( doc, relatedcontentkeysElem, "relatedcontentkey" );
                elem.setAttribute( "key", Integer.toString( relatedContentKey ) );
                elem.setAttribute( "level", "1" );

                // add related content
                Document tempDoc = XMLTool.domparse( admin.getContent( user, relatedContentKey, 0, 0, 0 ) );
                relatedcontentsElem.appendChild( doc.importNode( tempDoc.getDocumentElement().getFirstChild(), true ) );
            }
        }
    }

    public void handlerCopy( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems, User user, int key )
        throws VerticalAdminException, VerticalEngineException
    {

        UserEntity copier = securityService.getUser( user );
        ContentEntity content = contentDao.findByKey( new ContentKey( key ) );
        if ( content != null )
        {
            CategoryEntity toCategory = content.getCategory();
            contentService.copyContent( copier, content, toCategory );
        }

        redirectToReferer( request, response, formItems );
    }

    public final void handlerCreate( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                     ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();

        handlerCreate( request, response, session, admin, formItems, user );
    }

    public void handlerCreate( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, User user )
        throws VerticalAdminException, VerticalEngineException
    {
        // binarys
        BinaryData[] binariesOldTypes = contentXMLBuilder.getBinaries( formItems );
        List<BinaryDataAndBinary> binaryDataAndBinaries = BinaryDataAndBinary.createNewFrom( binariesOldTypes );

        boolean published = formItems.getBoolean( "published", false );
        boolean addToSection = formItems.getBoolean( "addtosection", false );
        boolean sentToApproval = formItems.getBoolean( "senttoapproval", false );
        boolean closeAfterSuccess = formItems.getBoolean( "closeaftersuccess", false );
        boolean saveAndAssign = formItems.getBoolean( "assignto", false );
        boolean createSnapshot = formItems.getBoolean( "_create_snapshot", false );

        String xmlData = contentXMLBuilder.buildXML( formItems, user, true, false, false );

        boolean parseContentData = true; // always parse content data when creating content
        ContentAndVersion parsedContentAndVersion = contentParserService.parseContentAndVersion( xmlData, null, parseContentData );

        UserEntity creator = securityService.getUser( user );

        ContentEntity parsedContent = parsedContentAndVersion.getContent();

        CategoryAccessResolver categoryAccessResolver = new CategoryAccessResolver( groupDao );
        CreateContentCommand.AccessRightsStrategy strategy = CreateContentCommand.AccessRightsStrategy.INHERIT_FROM_CATEGORY;
        if ( categoryAccessResolver.hasAccess( creator, parsedContent.getCategory(), CategoryAccessType.ADMINISTRATE ) )
        {
            strategy = CreateContentCommand.AccessRightsStrategy.USE_GIVEN;
        }

        CreateContentCommand createCommand = new CreateContentCommand();
        createCommand.setAccessRightsStrategy( strategy );
        createCommand.populateCommandWithContentValues( parsedContent );
        createCommand.populateCommandWithContentVersionValues( parsedContentAndVersion.getVersion() );
        createCommand.setCreator( creator );

        createCommand.setBinaryDatas( binaryDataAndBinaries );
        createCommand.setUseCommandsBinaryDataToAdd( true );

        ContentKey contentKey = contentService.createContent( createCommand );

        ContentEntity persistedContent = contentDao.findByKey( contentKey );
        ContentVersionEntity persistedVersion = persistedContent.getMainVersion();

        boolean doSetAssignmentValues = persistedVersion.isDraft();
        if ( doSetAssignmentValues )
        {
            if ( parsedContent.getAssignee() != null && !parsedContent.getAssignee().isAnonymous() )
            {
                AssignContentCommand assignCommand = new AssignContentCommand();
                assignCommand.setAssigneeKey( parsedContent.getAssignee().getKey() );
                UserKey assignerKey =
                    parsedContent.getAssigner() != null ? parsedContent.getAssigner().getKey() : parsedContent.getAssignee().getKey();
                assignCommand.setAssignerKey( assignerKey );
                assignCommand.setAssignmentDueDate( parsedContent.getAssignmentDueDate() );
                assignCommand.setAssignmentDescription( parsedContent.getAssignmentDescription() );
                assignCommand.setContentKey( persistedContent.getKey() );

                contentService.assignContent( assignCommand );
            }
        }

        if ( createSnapshot )
        {
            if ( !persistedContent.hasDraft() )
            {
                throw new IllegalArgumentException( "Not allowed to snapshot content with no draft" );
            }

            SnapshotContentCommand snapshotCommand = new SnapshotContentCommand();
            snapshotCommand.setContentKey( persistedContent.getKey() );
            snapshotCommand.setSnapshotterKey( creator.getKey() );
            snapshotCommand.setClearCommentInDraft( true );
            snapshotCommand.setSnapshotComment( formItems.getString( "_comment", null ) );

            contentService.snapshotContent( snapshotCommand );
        }

        formItems.putInt( "key", contentKey.toInt() );
        formItems.putInt( "versionkey", persistedVersion.getKey().toInt() );

        if ( published )
        {
            formItems.put( "feedback", "2" );
        }
        else if ( sentToApproval )
        {
            formItems.put( "feedback", "3" );
        }
        else
        {
            formItems.put( "feedback", "1" );
        }

        if ( saveAndAssign )
        {
            redirectToSendToAssigneeForm( request, response, formItems );
        }
        else if ( addToSection )
        {
            redirectToPublishWizard( request, response, formItems );
        }
        else if ( sentToApproval )
        {
            redirectToNotifyForm( request, response, formItems );
        }
        else if ( closeAfterSuccess )
        {
            redirectToReferer( request, response, formItems );
        }
        else
        {
            redirectToForm( request, response, formItems );
        }
    }

    public void handlerReport( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, String subOp )
        throws VerticalAdminException
    {

        try
        {
            User user = securityService.getLoggedInAdminConsoleUser();
            ExtendedMap parameters = formItems;

            if ( "form".equals( subOp ) )
            {
                int unitKey = formItems.getInt( "selectedunitkey", -1 );
                int categoryKey = formItems.getInt( "cat" );
                int contentTypeKey = getContentTypeKey( formItems );

                String assignmentDueDate = formItems.getString( "date_assignmentDueDate", "" );
                String assignmentDueDateOp = formItems.getString( "_assignmentDueDate.op", "" );
                String assigneeUserKey = formItems.getString( "_assignee", "" );
                String assignerUserKey = formItems.getString( "_assigner", "" );

                String superCategoryXML = admin.getSuperCategoryNames( categoryKey, false, true );
                Document doc = XMLTool.domparse( superCategoryXML );

                addCommonParameters( admin, user, request, parameters, unitKey, -1 );
                parameters.put( "contenttypekey", String.valueOf( contentTypeKey ) );

                parameters.put( "assignment.dueDate", assignmentDueDate );
                parameters.put( "assignment.dueDate.op", assignmentDueDateOp );
                parameters.put( "assignment.assigneeUserKey", assigneeUserKey );
                parameters.put( "assignment.assignerUserKey", assignerUserKey );

                transformXML( request, response, doc, "report_form.xsl", parameters );
            }
            else if ( "create".equals( subOp ) )
            {
                ResourceKey stylesheetKey = new ResourceKey( formItems.getString( "stylesheetkey" ) );
                ResourceFile res = resourceService.getResourceFile( stylesheetKey );
                if ( res == null )
                {
                    throw new StylesheetNotFoundException( stylesheetKey );
                }

                int cat = formItems.getInt( "cat" );
                String reportXML;
                String searchType = formItems.getString( "searchtype" );
                if ( "simple".equals( searchType ) )
                {
                    reportXML =
                        new SearchUtility( userDao, groupDao, securityService, contentService ).simpleReport( user, formItems, cat );
                }
                else
                {
                    // reportXML = SearchUtility.advancedReport( user, admin, formItems );
                    String[] contentTypeStringArray = formItems.getStringArray( "contenttypestring" );
                    int[] contentTypes = resolveContentTypes( contentTypeStringArray );

                    String ownerGroupKey = formItems.getString( "owner", "" );
                    if ( !"".equals( ownerGroupKey ) )
                    {
                        User ownerUser = getUserFromUserGroupKey( ownerGroupKey );

                        addUserKeyToFormItems( formItems, "owner.key", ownerUser );
                    }

                    String modifierGroupKey = formItems.getString( "modifier", "" );
                    if ( !"".equals( modifierGroupKey ) )
                    {
                        User modifierUser = getUserFromUserGroupKey( modifierGroupKey );

                        addUserKeyToFormItems( formItems, "modifier.key", modifierUser );
                    }
                    reportXML = new SearchUtility( userDao, groupDao, securityService, contentService ).advancedReport( user, formItems,
                                                                                                                        contentTypes );
                }
                Document reportDoc = XMLTool.domparse( reportXML );
                Element contentsElem = reportDoc.getDocumentElement();
                String datasourcesDefaultResultElementName = verticalProperties.getDatasourceDefaultResultRootElement();
                Element verticaldataElem = XMLTool.createElement( reportDoc, datasourcesDefaultResultElementName );
                reportDoc.replaceChild( verticaldataElem, contentsElem );
                verticaldataElem.appendChild( contentsElem );
                DOMSource reportSource = new DOMSource( reportDoc );

                XsltResource xslResource = new XsltResource( res.getDataAsXml().getAsString() );
                XsltProcessorManager procManager = XsltProcessorManagerAccessor.getProcessorManager();
                XsltProcessor proc = procManager.createProcessor( xslResource, getStylesheetURIResolver( admin ) );
                proc.setParameter( "datetoday", DateUtil.formatISODateTime( new Date() ) );

                response.setContentType( proc.getOutputMediaType() + "; charset=UTF-8" );
                response.getWriter().write( proc.process( reportSource ) );
            }
            else
            {
                String message = "Unknown sub-operation for operation report: %t";
                VerticalAdminLogger.errorAdmin(message, null );
            }
        }
        catch ( XsltProcessorException e )
        {
            String message = "Failed to transmform XML document: %t";
            VerticalAdminLogger.errorAdmin(message, e );
        }
        catch ( IOException e )
        {
            String message = "Failed to transmform XML document: %t";
            VerticalAdminLogger.errorAdmin(message, e );
        }

    }

    private void addUserKeyToFormItems( ExtendedMap formItems, String userKey, User user )
    {
        formItems.put( userKey, user.getKey().toString() );
    }

    private UserEntity getUserFromUserGroupKey( String userGroupKey )
    {
        UserSpecification userSpec = new UserSpecification();
        userSpec.setUserGroupKey( new GroupKey( userGroupKey ) );
        userSpec.setDeletedState( UserSpecification.DeletedState.ANY );
        return userDao.findSingleBySpecification( userSpec );
    }

    private UserEntity getUserFromUserKey( String userKey )
    {
        UserSpecification userSpec = new UserSpecification();
        userSpec.setKey( new UserKey( userKey ) );
        userSpec.setDeletedState( UserSpecification.DeletedState.ANY );
        return userDao.findSingleBySpecification( userSpec );
    }

    public final void handlerForm( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                   ExtendedMap formItems )
        throws VerticalAdminException
    {

        User user = securityService.getLoggedInAdminConsoleUser();

        int contentKey = -1;
        int versionKey = -1;

        if ( formItems.containsKey( "key" ) )
        {
            contentKey = formItems.getInt( "key" );
        }

        if ( formItems.containsKey( "versionkey" ) )
        {
            versionKey = formItems.getInt( "versionkey" );
        }

        if ( contentKey != -1 && versionKey == -1 )
        {
            ContentEntity contentEntity = contentDao.findByKey( new ContentKey( contentKey ) );

            if ( contentEntity != null )
            {
                if ( contentEntity.getDraftVersion() != null )
                {
                    versionKey = contentEntity.getDraftVersion().getKey().toInt();
                }
                else
                {
                    versionKey = contentEntity.getMainVersion().getKey().toInt();
                }
            }
        }

        if ( contentKey == -1 && versionKey != -1 )
        {
            contentKey = admin.getContentKeyByVersionKey( versionKey );
        }

        int categoryKey;
        if ( formItems.containsKey( "cat" ) )
        {
            categoryKey = formItems.getInt( "cat" );
        }
        else
        {
            categoryKey = admin.getCategoryKey( contentKey );
        }

        int unitKey = admin.getUnitKey( categoryKey );
        int contentTypeKey = getContentTypeKey( formItems );

        formItems.put( "cat", categoryKey );
        formItems.put( "selectedunitkey", unitKey );

        if ( "popup".equals( formItems.getString( "subop", null ) ) )
        {
            if ( formItems.containsKey( "referer" ) )
            {
                URL redirectURL = new URL( formItems.getString( "referer" ) );
                formItems.put( "referer", redirectURL );
            }
            else
            {
                URL redirectURL = new URL( "adminpage" );
                redirectURL.setParameter( "op", "callback" );
                if ( formItems.containsKey( "callback" ) )
                {
                    redirectURL.setParameter( "callback", formItems.getString( "callback" ) );
                }
                redirectURL.setParameter( "page", formItems.getInt( "page" ) );
                redirectURL.setParameter( "key", contentKey );
                redirectURL.setParameter( "fieldname", formItems.getString( "fieldname" ) );
                redirectURL.setParameter( "fieldrow", formItems.getString( "fieldrow" ) );

                formItems.put( "referer", redirectURL );
            }
        }

        boolean createContent = ( contentKey == -1 );

        if ( ( !createContent && versionKey == -1 ) || ( createContent && versionKey != -1 ) )
        {
            VerticalAdminLogger.error( "Parameter error!", null );
        }

        handlerForm( request, response, session, admin, formItems, user, createContent, unitKey, categoryKey, contentTypeKey, contentKey,
                     versionKey );
    }

    public void handlerForm( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems, User oldUser, boolean createContent, int unitKey, int categoryKey, int contentTypeKey,
                             int contentKey, int versionKey )
        throws VerticalAdminException
    {
        ContentAccessResolver contentAccessResolver = new ContentAccessResolver( groupDao );
        UserEntity executor = securityService.getUser( oldUser );

        ContentVersionEntity selectedVersion = contentVersionDao.findByKey( new ContentVersionKey( versionKey ) );

        if ( !createContent && selectedVersion == null )
        {
            throw new IllegalArgumentException( "No version found for content key " + contentKey + " with version key " + versionKey );
        }

        if ( !createContent &&
            !contentAccessResolver.hasReadContentAccess( securityService.getUser( oldUser ), selectedVersion.getContent() ) )
        {
            throw new IllegalArgumentException( "No read access to content with key: " + contentKey );
        }

        //should not be able to view deleted content
        if ( !createContent && selectedVersion.getContent().isDeleted() )
        {
            formItems.put( "feedback", "9" );
            redirectToReferer( request, response, formItems );
        }

        Integer populateContentDataFromVersion = null;
        if ( formItems.containsKey( "populateFromVersion" ) )
        {
            populateContentDataFromVersion = formItems.getInt( "populateFromVersion" );
        }

        Document doc = getContentDocument( admin, oldUser, contentKey, categoryKey, versionKey, populateContentDataFromVersion );
        Element root = doc.getDocumentElement();

        if ( !root.hasChildNodes() )
        {
            String message = "Access denied.";
            VerticalAdminLogger.errorAdmin(message, null );
        }

        if ( !createContent )
        {
            // add repository path to related contents
            Element relatedcontentsElem = XMLTool.getElement( doc.getDocumentElement(), "relatedcontents" );
            addRepositoryPath( admin, relatedcontentsElem );

            ContentEditFormModelFactory contentEditFormModelFactory =
                new ContentEditFormModelFactory( contentDao, securityService, new MenuItemAccessRightAccumulator( securityService ) );
            ContentEditFormModel model = contentEditFormModelFactory.createContentEditFormModel( new ContentKey( contentKey ), executor );

            XMLTool.mergeDocuments( doc, model.locationsToXML().getAsDOMDocument(), true );
            XMLTool.mergeDocuments( doc, model.locationMenuitemsToXML().getAsDOMDocument(), true );
            XMLTool.mergeDocuments( doc, model.locationSitesToXML().getAsDOMDocument(), true );
            XMLTool.mergeDocuments( doc, model.pageTemplateBySiteToXML().getAsDOMDocument(), true );

        }

        String xmlCat = admin.getSuperCategoryNames( categoryKey, false, true );
        XMLTool.mergeDocuments( doc, xmlCat, true );

        addUserRightToDocument( admin, oldUser, doc, categoryKey );
        addEditorDataToDocument( admin, doc );

        if ( !createContent && memberOfResolver.hasDeveloperPowers( oldUser.getKey() ) )
        {
            ContentVersionEntity includeSourceContentVerision;
            if ( populateContentDataFromVersion != null )
            {
                includeSourceContentVerision = contentVersionDao.findByKey( new ContentVersionKey( populateContentDataFromVersion ) );
            }
            else
            {
                includeSourceContentVerision = selectedVersion;
            }
            appendContentSource( doc, includeSourceContentVerision );
        }

        // Feedback
        addFeedback( doc, formItems );

        // pre-process content document
        preProcessContentDocument( oldUser, admin, doc, formItems, request );

        for ( String extraFormXMLFile : extraFormXMLFiles )
        {
            Document tmpDoc = XMLTool.domparse( AdminStore.getXML( session, extraFormXMLFile ) );
            XMLTool.mergeDocuments( doc, tmpDoc, true );
        }

        // Stylesheet parameters
        ExtendedMap parameters = new ExtendedMap();
        addCustomData( session, oldUser, admin, doc, contentKey, contentTypeKey, formItems, parameters );

        int siteKey = formItems.getInt( "menukey", -1 );
        if ( siteKey == -1 )
        {
            addPageTemplatesOfUserSitesToDocument( admin, executor, PageTemplateType.CONTENT, doc );
        }
        else
        {
            addPageTemplatesOfSiteToDocument( siteKey, PageTemplateType.CONTENT, doc );
        }

        addCommonParameters( admin, oldUser, request, parameters, unitKey, -1 );

        addDefaultParameters( admin, request, parameters, contentKey, unitKey, categoryKey, contentTypeKey, formItems );

        addAccessLevelParameters( oldUser, parameters );

        DOMSource xslSource = buildXSL( session, admin, contentTypeKey );

        if ( xslSource != null )
        {
            transformXML( request, response, doc, xslSource, parameters );
        }
        else
        {
            transformXML( request, response, doc, FORM_XSL, parameters );
        }

        // log read access
        if ( formItems.getString( "logread", "false" ).equalsIgnoreCase( "true" ) )
        {
            logVisited( oldUser, contentKey, selectedVersion, selectedVersion.getContent().getPathAsString() );
        }

    }

    private void logVisited( User user, int contentKey, ContentVersionEntity version, String path )
    {
        UserEntity reader = securityService.getUser( user );
        final String title = version.getTitle() + " (" + version.getContent().getKey() + ")";

        StoreNewLogEntryCommand command = new StoreNewLogEntryCommand();
        command.setTableKey( Table.CONTENT );
        command.setType( LogType.ENTITY_OPENED );
        command.setUser( reader.getKey() );
        command.setPath( path );
        command.setTableKeyValue( contentKey );
        command.setTitle( title );
        command.setXmlData( version.getContentDataAsJDomDocument() );
        logService.storeNew( command );

    }

    public final Document getContentDocument( AdminService admin, User user, int contentKey, int categoryKey, int versionKey,
                                              Integer populateContentDataFromVersion )
    {
        Document asW3cDoc;

        if ( contentKey != -1 )
        {
            CategoryAccessResolver categoryAccessResolver = new CategoryAccessResolver( groupDao );
            ContentAccessResolver contentAccessResolver = new ContentAccessResolver( groupDao );

            ContentXMLCreator contentXMLCreator = new ContentXMLCreator();
            contentXMLCreator.setIncludeAccessRightsInfo( true );
            contentXMLCreator.setIncludeUserRightsInfo( true, categoryAccessResolver, contentAccessResolver );
            contentXMLCreator.setIncludeUserRightsInfoForRelated( true, categoryAccessResolver, contentAccessResolver );
            contentXMLCreator.setIncludeVersionsInfoForAdmin( true );
            contentXMLCreator.setIncludeRelatedContentsInfo( true );
            contentXMLCreator.setIncludeRepositoryPathInfo( true );
            contentXMLCreator.setIncludeAssignment( true );
            contentXMLCreator.setIncludeDraftInfo( true );
            contentXMLCreator.setOrderByCreatedAtDescending( true );

            ContentVersionEntity version = contentVersionDao.findByKey( new ContentVersionKey( versionKey ) );

            ContentVersionEntity versionToPopulateContentDataFrom;
            if ( populateContentDataFromVersion != null && !populateContentDataFromVersion.equals( versionKey ) )
            {
                versionToPopulateContentDataFrom = contentVersionDao.findByKey( new ContentVersionKey( populateContentDataFromVersion ) );
            }
            else
            {
                versionToPopulateContentDataFrom = version;
            }

            UserEntity runningUser = securityService.getUser( user );

            RelatedChildrenContentQuery relatedChildrenContentQuery =
                new RelatedChildrenContentQuery( timeService.getNowAsDateTime().toDate() );
            relatedChildrenContentQuery.setContentVersion( versionToPopulateContentDataFrom );
            relatedChildrenContentQuery.setChildrenLevel( 1 );
            relatedChildrenContentQuery.setIncludeOffline();
            /*
             * Include related content even if running user doesn't have access - if not the user will not see related content.
             */
            relatedChildrenContentQuery.setUser( null );

            RelatedContentResultSet relatedContents = contentService.queryRelatedContent( relatedChildrenContentQuery );
            XMLDocument contentsDocAsXMLDocument = contentXMLCreator.createContentsDocument( runningUser, version, relatedContents );
            if ( populateContentDataFromVersion != null && !populateContentDataFromVersion.equals( versionKey ) )
            {
                // Fetching the content-XML without related content.  This XML is only used to get the elements from
                // the version to populate from, and replacing them in the draft version which is being edited.
                org.jdom.Document contentsDocAsJdomDocument = contentsDocAsXMLDocument.getAsJDOMDocument();
                org.jdom.Document contentXmlFromVersionToPopulateFrom =
                    contentXMLCreator.createContentsDocument( runningUser, versionToPopulateContentDataFrom, null ).getAsJDOMDocument();

                org.jdom.Element contentElInOriginal = contentsDocAsJdomDocument.getRootElement().getChild( "content" );

                // Replace <contentdata>
                org.jdom.Element contentdataElInVersionToPopulateFrom =
                    contentXmlFromVersionToPopulateFrom.getRootElement().getChild( "content" ).getChild( "contentdata" );
                contentElInOriginal.removeChild( "contentdata" );
                contentElInOriginal.addContent( contentdataElInVersionToPopulateFrom.detach() );

                // Replace <binaries>
                org.jdom.Element binariesElInVersionToPopulateFrom =
                    contentXmlFromVersionToPopulateFrom.getRootElement().getChild( "content" ).getChild( "binaries" );
                contentElInOriginal.removeChild( "binaries" );
                contentElInOriginal.addContent( binariesElInVersionToPopulateFrom.detach() );

                asW3cDoc = XMLDocumentFactory.create( contentsDocAsJdomDocument ).getAsDOMDocument();

            }
            else
            {
                asW3cDoc = contentsDocAsXMLDocument.getAsDOMDocument();
            }
        }
        else
        {
            // Blank form, make dummy document
            asW3cDoc = XMLTool.createDocument( "contents" );
            String xmlAccessRights = admin.getDefaultAccessRights( user, AccessRight.CONTENT, categoryKey );
            XMLTool.mergeDocuments( asW3cDoc, xmlAccessRights, true );
        }

        return asW3cDoc;
    }

    protected final void addDefaultParameters( AdminService admin, HttpServletRequest request, Map<String, Object> parameters,
                                               int contentKey, int unitKey, int categoryKey, int contentTypeKey, ExtendedMap formItems )
    {

        if ( contentKey != -1 )
        {
            parameters.put( "create", "0" );
            parameters.put( "currentkey", contentKey );
        }
        else
        {
            parameters.put( "create", "1" );
        }

        parameters.put( "contenttypekey", String.valueOf( contentTypeKey ) );

        if ( unitKey != -1 )
        {
            parameters.put( "unitkey", String.valueOf( unitKey ) );
            parameters.put( "selectedunitkey", String.valueOf( unitKey ) );
        }

        parameters.put( "page", formItems.getInt( "page" ) );
        parameters.put( "subop", formItems.getString( "subop", "" ) );
        parameters.put( "cat", categoryKey );
        parameters.put( "fieldname", formItems.getString( "fieldname", "" ) );
        parameters.put( "fieldrow", formItems.getString( "fieldrow", "" ) );
        parameters.put( "minoccurrence", formItems.getString( "minoccurrence", "" ) );
        parameters.put( "maxoccurrence", formItems.getString( "maxoccurrence", "" ) );
        parameters.put( "contenttypestring", formItems.getString( "contenttypestring", "" ) );
        parameters.put( "selectedtabpage", formItems.getString( "selectedtabpage", "" ) );
        parameters.put( "modulename", admin.getContentTypeName( contentTypeKey ) );
        parameters.put( "alwaysdisabled", alwaysDisabled );
        if ( formItems.containsKey( "creatednewversion" ) )
        {
            parameters.put( "creatednewversion", formItems.getString( "creatednewversion" ) );
        }

        if ( formItems.containsKey( "referer" ) )
        {
            parameters.put( "referer", formItems.getString( "referer" ) );
        }
        else
        {
            parameters.put( "referer", getReferer( request ) );
        }

        if ( formItems.containsKey( "previouspage" ) )
        {
            parameters.put( "previouspage", formItems.get( "previouspage" ) );
        }

        if ( formItems.containsKey( "editlockedversionmode" ) )
        {
            parameters.put( "editlockedversionmode", formItems.get( "editlockedversionmode" ) );
        }

        ResourceKey cssKey = admin.getContentTypeCSSKey( contentTypeKey );
        if ( cssKey != null )
        {
            parameters.put( "csskey", String.valueOf( cssKey ) );
        }
    }

    protected final void addRepositoryPath( AdminService admin, Element contentsElem )
    {

        // Find supercategory names
        Element[] contentElems = XMLTool.getElements( contentsElem, "content" );
        for ( Element contentElem : contentElems )
        {
            Element categorynameElem = XMLTool.getElement( contentElem, "categoryname" );
            int categoryKey = Integer.parseInt( categorynameElem.getAttribute( "key" ) );
            Document categoryNamesDoc = XMLTool.domparse( admin.getSuperCategoryNames( categoryKey, false, true ) );
            Element[] categoryElems = XMLTool.getElements( categoryNamesDoc.getDocumentElement() );

            // set repository path
            if ( categoryElems.length >= 1 )
            {
                String archiveName = XMLTool.getElementText( categoryElems[0] );

                if ( categoryElems.length == 1 )
                {
                    contentElem.setAttribute( "repositorypath", "/" + archiveName );
                }
                else if ( categoryElems.length >= 2 )
                {
                    StringBuffer repositoryPath;

                    repositoryPath = new StringBuffer();
                    for ( Element categoryElem : categoryElems )
                    {
                        repositoryPath.append( "/" ).append( XMLTool.getElementText( categoryElem ) );
                    }
                    contentElem.setAttribute( "repositorypath", repositoryPath.toString() );
                }
            }
        }
    }

    public final void handlerRemove( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                     ExtendedMap formItems, int key )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();

        final ContentKey contentKey = new ContentKey( key );

        ContentEntity content = contentDao.findByKey( contentKey );

        if ( content != null && !content.isDeleted() )
        {
            // just to avoid any effects of the delete function, we find the content's locations before we delete it
            ContentLocationSpecification contentLocationSpecification = new ContentLocationSpecification();
            contentLocationSpecification.setIncludeInactiveLocationsInSection( false );
            ContentLocations contentLocations = content.getLocations( contentLocationSpecification );

            UserEntity runningerUser = securityService.getUser( user );

            contentService.deleteContent( runningerUser, content );

            new PageCacheInvalidatorForContent( siteCachesService ).invalidateForContentLocations( contentLocations );
        }

        CategoryKey categoryKey = new CategoryKey( formItems.getInt( "cat" ) );
        int contentCount = admin.getContentCount( categoryKey.toInt(), false );

        // Fix referer from/to
        URL url = getReferer( request );
        String fromStr = url.getParameter( "from" );
        if ( fromStr != null )
        {
            int from = Integer.parseInt( fromStr );

            String toStr = url.getParameter( "to" );
            int to;
            if ( toStr != null )
            {
                to = Integer.parseInt( toStr );
            }
            else
            {
                to = -1;
            }

            if ( from >= contentCount )
            {
                int range = to - from + 1;
                url.setParameter( "from", String.valueOf( from - range ) );
                url.setParameter( "to", String.valueOf( from - 1 ) );
            }
            else
            {
                url.setParameter( "from", String.valueOf( from ) );
                url.setParameter( "to", String.valueOf( to ) );
            }
            formItems.put( "referer", url.toString() );
        }
        redirectToReferer( request, response, formItems );
    }

    public final void handlerUpdate( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                     ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();

        handlerUpdate( request, response, formItems, user );
    }

    public void handlerUpdate( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems, User user )
        throws VerticalAdminException, VerticalEngineException
    {

        boolean makeAvailable = formItems.getBoolean( "published", false );
        boolean closeAfterSuccess = formItems.getBoolean( "closeaftersuccess", false );
        boolean addToSection = formItems.getBoolean( "addtosection", false );
        boolean sentToApproval = formItems.getBoolean( "senttoapproval", false );
        boolean rejected = formItems.getBoolean( "rejected", false );
        boolean activated = formItems.getBoolean( "_pubdata_activate", false );
        boolean editingDisabled = formItems.getBoolean( "formdisabled", false ) || alwaysDisabled;
        boolean asMainVersion = activated || makeAvailable;
        boolean editLockedVersion = formItems.getBoolean( "editlockedversionmode", false );
        boolean assignTo = formItems.getBoolean( "assignto", false );
        boolean doSendAssignmentEmails = true;
        boolean createSnapshot = formItems.getBoolean( "_create_snapshot", false );

        String owner = formItems.getString( "_pubdata_owner", "" );
        boolean isEnterpriseAdmin = userDao.findBuiltInEnterpriseAdminUser().getKey().toString().equals( owner );

        if ( editLockedVersion )
        {
            formItems.put( "_pubdata_status", "0" );
        }

        // binaries
        BinaryData[] binariesToAddAsOldStyle = null;
        List<BinaryDataKey> binariesToRemoveAsBinaryDataKey = null;
        if ( !editingDisabled )
        {
            binariesToAddAsOldStyle = contentXMLBuilder.getBinaries( formItems );
        }

        List<BinaryDataAndBinary> binariesToAdd = BinaryDataAndBinary.createNewFrom( binariesToAddAsOldStyle );

        boolean usePersistedContentdataXML = editLockedVersion && editingDisabled;
        boolean excludeContentdataXML = !editLockedVersion && editingDisabled; // use persisted contentdata -> update only
        // meta data
        String xmlData = contentXMLBuilder.buildXML( formItems, user, false, excludeContentdataXML, usePersistedContentdataXML );

        if ( !editingDisabled )
        {
            binariesToRemoveAsBinaryDataKey = BinaryDataKey.convertToList( contentXMLBuilder.getDeleteBinaries( formItems ) );
        }

        UserEntity modifier = securityService.getUser( user );

        boolean parseContentData = !excludeContentdataXML; // if false we load contentdata from storage
        ContentAndVersion parsedContentAndVersion = contentParserService.parseContentAndVersion( xmlData, null, parseContentData );
        ContentEntity parsedContent = parsedContentAndVersion.getContent();
        ContentVersionEntity parsedVersion = parsedContentAndVersion.getVersion();
        ContentEntity persistedContent = contentDao.findByKey( parsedContent.getKey() );

        boolean submittedVersionIsDraft = parsedVersion.hasStatus( ContentStatus.DRAFT );
        boolean submittedVersionIsApproved = parsedVersion.hasStatus( ContentStatus.APPROVED );
        boolean submittedVersionIsArchived = parsedVersion.hasStatus( ContentStatus.ARCHIVED );

        boolean persistedContentHasDraft = persistedContent.hasDraft();

        boolean createNewDraftVersion = !persistedContentHasDraft && submittedVersionIsDraft;
        boolean updateExistingDraft = persistedContentHasDraft && submittedVersionIsDraft;

        boolean createNewApprovedVersion = submittedVersionIsApproved && !persistedContentHasDraft && editLockedVersion;
        boolean updateExistingDraftAsApproved = submittedVersionIsApproved && persistedContentHasDraft && editLockedVersion;

        boolean updateExistingDraftAsArchived = submittedVersionIsArchived && persistedContentHasDraft && editLockedVersion;

        UpdateContentCommand updateContentCommand;

        if ( createNewDraftVersion )
        {
            ContentVersionKey versionKeyToBaseNewVersionOn = parsedVersion.getKey();
            updateContentCommand = UpdateContentCommand.storeNewVersionEvenIfUnchanged( versionKeyToBaseNewVersionOn );
        }
        else if ( updateExistingDraft )
        {
            ContentVersionKey versionKeyToUpdate = persistedContent.getDraftVersion().getKey();
            updateContentCommand = UpdateContentCommand.updateExistingVersion2( versionKeyToUpdate );
        }
        else if ( createNewApprovedVersion )
        {
            ContentVersionKey versionKeyToBaseNewVersionOn = parsedVersion.getKey();
            updateContentCommand = UpdateContentCommand.storeNewVersionEvenIfUnchanged( versionKeyToBaseNewVersionOn );
        }
        else if ( updateExistingDraftAsApproved )
        {
            ContentVersionKey versionKeyToUpdate = persistedContent.getDraftVersion().getKey();
            updateContentCommand = UpdateContentCommand.updateExistingVersion2( versionKeyToUpdate );
        }
        else if ( updateExistingDraftAsArchived )
        {
            ContentVersionKey versionKeyToUpdate = persistedContent.getDraftVersion().getKey();
            updateContentCommand = UpdateContentCommand.updateExistingVersion2( versionKeyToUpdate );
        }
        else
        {
            ContentVersionKey versionKeyToUpdate = parsedVersion.getKey();
            updateContentCommand = UpdateContentCommand.updateExistingVersion2( versionKeyToUpdate );
        }

        updateContentCommand.setModifier( modifier );

        updateContentCommand.populateContentValuesFromContent( parsedContent );
        updateContentCommand.populateContentVersionValuesFromContentVersion( parsedVersion );

        CategoryAccessResolver categoryAccessResolver = new CategoryAccessResolver( groupDao );
        final boolean syncAccessRights =
            categoryAccessResolver.hasAccess( modifier, persistedContent.getCategory(), CategoryAccessType.ADMINISTRATE );
        updateContentCommand.setSyncAccessRights( syncAccessRights );
        updateContentCommand.setUpdateAsMainVersion( asMainVersion );
        updateContentCommand.setBinaryDataToAdd( binariesToAdd );
        updateContentCommand.setUseCommandsBinaryDataToAdd( true );
        updateContentCommand.setBinaryDataToRemove( binariesToRemoveAsBinaryDataKey );
        updateContentCommand.setUseCommandsBinaryDataToRemove( true );

        UpdateContentResult updateContentResult = contentService.updateContent( updateContentCommand );

        if ( updateContentResult.isAnyChangesMade() )
        {
            new PageCacheInvalidatorForContent( siteCachesService ).invalidateForContent( updateContentResult.getTargetedVersion() );
        }

        AssignmentActionResolver assignmentActionResolver = new AssignmentActionResolver();
        AssignmentAction assignmentAction =
            assignmentActionResolver.resolveAssignmentAction( parsedContent, parsedVersion, persistedContent );

        switch ( assignmentAction )
        {
            case UNASSIGN:
                unassignContent( persistedContent, modifier );
                break;
            case ASSIGN:
                assignContent( parsedContent, modifier, doSendAssignmentEmails );
                break;
            case REASSIGN:
                assignContent( parsedContent, modifier, doSendAssignmentEmails );
                break;
            case UNASSIGN_SINCE_APPROVED:
                unassignContentSinceApproved( persistedContent, modifier, doSendAssignmentEmails );
                break;
            case UPDATE_ASSIGNMENT:
                updateAssignment( parsedContent, modifier );
                break;
        }

        if ( createSnapshot )

        {
            SnapshotContentCommand snapshotCommand = new SnapshotContentCommand();
            snapshotCommand.setContentKey( persistedContent.getKey() );
            snapshotCommand.setSnapshotterKey( modifier.getKey() );
            snapshotCommand.setClearCommentInDraft( true );
            snapshotCommand.setSnapshotComment( formItems.getString( "_comment", null ) );

            contentService.snapshotContent( snapshotCommand );
        }

        if ( !updateContentResult.isAnyChangesMade() && assignmentAction.equals( AssignmentAction.DONT_TOUCH ) )
        {
            formItems.put( "feedback", "0" );
        }

        else if ( makeAvailable )

        {
            formItems.put( "feedback", "2" );
        }

        else if ( sentToApproval )

        {
            formItems.put( "feedback", "3" );
        }

        else if ( rejected )

        {
            formItems.put( "feedback", "4" );
        }

        else if ( createNewDraftVersion )

        {
            formItems.put( "feedback", "5" );
        }

        else

        {
            formItems.put( "feedback", "1" );
        }

        formItems.put( "versionkey", updateContentResult.getTargetedVersionKey().

            toInt()

        );

        if ( assignTo )
        {
            redirectToSendToAssigneeForm( request, response, formItems );
        }

        else if ( addToSection )
        {
            redirectToPublishWizard( request, response, formItems );
        }

        else if ( ( sentToApproval || rejected ) && !isEnterpriseAdmin )
        {
            redirectToNotifyForm( request, response, formItems );
        }

        else if ( ( sentToApproval || rejected ) && isEnterpriseAdmin )
        {
            if ( closeAfterSuccess )
            {
                redirectToReferer( request, response, formItems );
            }
            redirectToForm( request, response, formItems );
        }

        else if ( closeAfterSuccess )
        {
            redirectToReferer( request, response, formItems );
        }

        else
        {
            redirectToForm( request, response, formItems );
        }

    }

    private void updateAssignment( ContentEntity parsedContent, UserEntity updater )
    {
        UpdateAssignmentCommand command = new UpdateAssignmentCommand();
        command.setContentKey( parsedContent.getKey() );
        command.setUpdater( updater.getKey() );
        command.setAssignmentDescription( parsedContent.getAssignmentDescription() );
        command.setAssignmentDueDate( parsedContent.getAssignmentDueDate() );

        contentService.updateAssignment( command );
    }

    private void assignContent( ContentEntity submittedContent, UserEntity assigner, boolean sendMail )
    {
        if ( submittedContent.getAssignee() == null )
        {
            throw new IllegalArgumentException( "Cannot assign content when no assignee given" );
        }

        if ( submittedContent.getAssigner() == null )
        {
            throw new IllegalArgumentException( "Cannot assign content when no assigner given" );
        }

        AssignContentCommand command = new AssignContentCommand();
        command.setAssigneeKey( submittedContent.getAssignee().getKey() );
        command.setAssignerKey( assigner.getKey() );
        command.setAssignmentDescription( submittedContent.getAssignmentDescription() );
        command.setAssignmentDueDate( submittedContent.getAssignmentDueDate() );
        command.setContentKey( submittedContent.getKey() );

        AssignContentResult result = contentService.assignContent( command );

        if ( sendMail )
        {
            ContentEntity persistedContent = contentDao.findByKey( result.getAssignedContentKey() );

            AssignmentMailSender assignmentMailSender = new AssignmentMailSender( sendMailService );
            assignmentMailSender.setAssignedContent( persistedContent );
            assignmentMailSender.setAssignmentDescription( submittedContent.getAssignmentDescription() );
            assignmentMailSender.setAssignmentDueDate( submittedContent.getAssignmentDueDate() );
            assignmentMailSender.setNewAssignee( assigner );
            assignmentMailSender.setUpdater( assigner );
            assignmentMailSender.setOriginalAssignee( result.getOriginalAssignee() );
            assignmentMailSender.setOriginalAssigner( result.getOriginalAssigner() );

            assignmentMailSender.sendAssignmentMails();
        }
    }

    private UnassignContentResult unassignContent( ContentEntity content, UserEntity unassigner )
    {
        UnassignContentCommand command = new UnassignContentCommand();
        command.setContentKey( content.getKey() );
        command.setUnassigner( unassigner.getKey() );

        return contentService.unassignContent( command );
    }

    private void unassignContentSinceApproved( ContentEntity content, UserEntity unassigner, boolean sendMail )
    {
        UnassignContentResult result = unassignContent( content, unassigner );
    }

    public void handlerSaveAndAssignForm( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems, User user )
        throws VerticalAdminException, VerticalEngineException
    {

        AssigneeFormModelFactory modelFactory = new AssigneeFormModelFactory( this.siteDao, this.contentDao );

        String contentKey = formItems.getString( "key", null );

        UserEntity updater = securityService.getUser( user );

        AssigneeFormModel model = modelFactory.createAssigneeFormModel( updater, contentKey );

        XMLDocument doc = model.toXML();

        // Stylesheet parameters
        ExtendedMap parameters = new ExtendedMap();
        parameters.put( "op", "save_and_assign" );
        parameters.put( "page", formItems.getInt( "page" ) );
        parameters.put( "subop", formItems.getString( "subop", "" ) );
        parameters.put( "key", contentKey );
        parameters.put( "versionkey", formItems.getInt( "versionkey" ) );
        parameters.put( "cat", formItems.getInt( "cat" ) );
        parameters.put( "selectedtabpage", formItems.getString( "selectedtabpage", "" ) );
        parameters.put( "referer", formItems.getString( "referer", "" ) );
        parameters.put( "creatednewversion", formItems.getString( "creatednewversion", "" ) );
        parameters.put( "saved", formItems.getString( "saved", "" ) );
        parameters.put( "feedback", formItems.getString( "feedback", "" ) );
        parameters.put( "fieldname", formItems.getString( "fieldname", "" ) );
        parameters.put( "fieldrow", formItems.getString( "fieldrow", "" ) );
        parameters.put( "contenttypestring", formItems.getString( "contenttypestring", "" ) );
        parameters.put( "minoccurrence", formItems.getString( "minoccurrence", "" ) );
        parameters.put( "maxoccurrence", formItems.getString( "maxoccurrence", "" ) );
        parameters.put( "contenttypekey", formItems.getString( "contenttypekey", "" ) );

        transformXML( request, response, doc.getAsDOMDocument(), "assign-to-form.xsl", parameters );
    }


    public void redirectToReferer( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems )
        throws VerticalAdminException
    {

        String redirect;
        if ( formItems.containsKey( "referer" ) )
        {
            redirect = formItems.getString( "referer" );
            if ( !redirect.startsWith( "http" ) )
            {
                redirect = AdminHelper.getAdminPath( request, true ) + "/" + redirect;
            }
        }
        else
        {
            redirect = getReferer( request ).toString();
        }

        URL redirectURL = new URL( redirect );

        if ( formItems.containsKey( "key" ) )
        {
            redirectURL.setParameter( "key", formItems.getString( "key" ) );
        }

        if ( formItems.containsKey( "versionkey" ) )
        {
            redirectURL.setParameter( "versionkey", formItems.getString( "versionkey" ) );
        }

        if ( formItems.containsKey( "feedback" ) )
        {
            redirectURL.setParameter( "feedback", formItems.getString( "feedback" ) );
        }
        else if ( redirectURL.getParameter( "feedback" ) != null )
        {
            redirectURL.removeParameter( "feedback" );
        }

        redirectClientToURL( redirectURL, response );
    }

    public void redirectToNotifyForm( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems )
        throws VerticalAdminException
    {

        MultiValueMap params = new MultiValueMap();

        params.put( "op", "notify" );
        params.put( "page", formItems.getInt( "page" ) );
        params.put( "key", formItems.getInt( "key" ) );
        params.put( "versionkey", formItems.getInt( "versionkey" ) );
        params.put( "cat", formItems.getInt( "cat" ) );
        params.put( "selectedtabpage", formItems.getString( "selectedtabpage", "" ) );
        params.put( "referer", formItems.getString( "referer", "" ) );
        params.put( "creatednewversion", formItems.getString( "creatednewversion", "" ) );
        params.put( "saved", formItems.getString( "saved", "" ) );
        params.put( "feedback", formItems.getString( "feedback", "" ) );
        params.put( "fieldname", formItems.getString( "fieldname", "" ) );
        params.put( "fieldrow", formItems.getString( "fieldrow", "" ) );
        params.put( "contenttypestring", formItems.getString( "contenttypestring", "" ) );

        if ( formItems.containsKey( "senttoapproval" ) )
        {
            params.put( "senttoapproval", formItems.getString( "senttoapproval" ) );
        }
        if ( formItems.containsKey( "senttoactivation" ) )
        {
            params.put( "senttoactivation", formItems.getString( "senttoactivation" ) );
        }
        if ( formItems.containsKey( "rejected" ) )
        {
            params.put( "rejected", formItems.getString( "rejected" ) );
        }

        redirectClientToAdminPath( "adminpage", params, request, response );
    }


    private void redirectToPublishWizard( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems )
        throws VerticalAdminException
    {
        MultiValueMap params = new MultiValueMap();
        params.put( "page", "950" );
        params.put( "op", "wizard" );
        params.put( "name", "publish" );
        params.put( "cat", formItems.getInt( "cat" ) );
        params.put( "contentkey", formItems.getInt( "key" ) );
        params.put( "versionkey", formItems.getInt( "versionkey" ) );
        if ( formItems.containsKey( "selectedunitkey" ) )
        {
            params.put( "selectedunitkey", formItems.getInt( "selectedunitkey" ) );
        }
        else if ( formItems.containsKey( "unitkey" ) )
        {
            params.put( "selectedunitkey", formItems.getInt( "unitkey" ) );
        }
        params.put( "redirect", formItems.getString( "referer", "" ) );

        redirectClientToAdminPath( "adminpage", params, request, response );
    }


    private void redirectToSendToAssigneeForm( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems )
        throws VerticalAdminException
    {
        MultiValueMap params = new MultiValueMap();

        params.put( "op", "save_and_assign_form" );
        params.put( "page", formItems.getInt( "page" ) );
        params.put( "key", formItems.getInt( "key" ) );
        params.put( "versionkey", formItems.getInt( "versionkey" ) );
        params.put( "cat", formItems.getInt( "cat" ) );
        params.put( "selectedtabpage", formItems.getString( "selectedtabpage", "" ) );
        params.put( "referer", formItems.getString( "referer", "" ) );
        params.put( "creatednewversion", formItems.getString( "creatednewversion", "" ) );
        params.put( "saved", formItems.getString( "saved", "" ) );
        params.put( "feedback", formItems.getString( "feedback", "" ) );
        params.put( "fieldname", formItems.getString( "fieldname", "" ) );
        params.put( "fieldrow", formItems.getString( "fieldrow", "" ) );
        params.put( "contenttypestring", formItems.getString( "contenttypestring", "" ) );

        if ( formItems.containsKey( "selectedunitkey" ) )
        {
            params.put( "selectedunitkey", formItems.getInt( "selectedunitkey" ) );
        }
        else if ( formItems.containsKey( "unitkey" ) )
        {
            params.put( "selectedunitkey", formItems.getInt( "unitkey" ) );
        }
        params.put( "redirect", formItems.getString( "referer", "" ) );

        redirectClientToAdminPath( "adminpage", params, request, response );
    }

    public void redirectToForm( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems )
        throws VerticalAdminException
    {

        MultiValueMap params = new MultiValueMap();
        params.put( "op", "form" );
        params.put( "page", formItems.getInt( "page" ) );
        params.put( "subop", formItems.getString( "subop", "" ) );
        params.put( "key", formItems.getInt( "key" ) );
        params.put( "versionkey", formItems.getInt( "versionkey" ) );
        params.put( "cat", formItems.getInt( "cat" ) );
        params.put( "selectedtabpage", formItems.getString( "selectedtabpage", "" ) );
        params.put( "referer", formItems.getString( "referer", "" ) );
        params.put( "creatednewversion", formItems.getString( "creatednewversion", "" ) );
        params.put( "saved", formItems.getString( "saved", "" ) );
        params.put( "feedback", formItems.getString( "feedback", "" ) );
        params.put( "fieldname", formItems.getString( "fieldname", "" ) );
        params.put( "fieldrow", formItems.getString( "fieldrow", "" ) );
        params.put( "contenttypestring", formItems.getString( "contenttypestring", "" ) );
        params.put( "minoccurrence", formItems.getString( "minoccurrence", "" ) );
        params.put( "maxoccurrence", formItems.getString( "maxoccurrence", "" ) );

        redirectClientToAdminPath( "adminpage", params, request, response );
    }

    public void handlerSearch( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException
    {

        Document doc;

        // Get site- and unitKey:
        int unitKey = formItems.getInt( "selectedunitkey", -1 );
        CategoryKey categoryKey = CategoryKey.parse( formItems.getInt( "cat", -1 ) );
        int contentTypeKey = getContentTypeKey( formItems );

        String contentTypeString = formItems.getString( "contenttypestring", null );
        int[] contentTypes = null;
        if ( contentTypeString != null )
        {
            String[] contentTypeStrings = StringUtil.splitString( contentTypeString, "," );
            contentTypes = ArrayUtil.toIntArray( contentTypeStrings );
        }

        // Blank form, make dummy document
        doc = XMLTool.createDocument();

        // Create content element
        XMLTool.createRootElement( doc, "data" );

        Document headerDoc = XMLTool.domparse( admin.getCategoryPathXML( categoryKey, contentTypes ) );
        XMLTool.mergeDocuments( doc, headerDoc, true );

        // Get content types for this site
        XMLDocument siteContentTypesDoc = admin.getContentTypes( false );
        final Document siteContentTypesDocument = siteContentTypesDoc.getAsDOMDocument();
        XMLTool.renameElement( siteContentTypesDocument.getDocumentElement(), "sitecontenttypes" );
        XMLTool.mergeDocuments( doc, siteContentTypesDocument, true );

        addEditorDataToDocument( admin, doc );

        // Stylesheet parameters
        ExtendedMap parameters = new ExtendedMap();
        if ( unitKey != -1 )
        {
            parameters.put( "unitkey", String.valueOf( unitKey ) );
            parameters.put( "selectedunitkey", String.valueOf( unitKey ) );
        }
        parameters.put( "contenttypekey", String.valueOf( contentTypeKey ) );
        parameters.put( "page", formItems.getString( "page" ) );
        if ( categoryKey != null )
        {
            parameters.putInt( "cat", categoryKey.toInt() );
        }
        parameters.put( "subop", formItems.getString( "subop", "" ) );
        parameters.put( "fieldname", formItems.getString( "fieldname", "" ) );
        parameters.put( "fieldrow", formItems.getString( "fieldrow", "" ) );
        parameters.put( "contenttypestring", formItems.getString( "contenttypestring", "" ) );

        addCommonParameters( admin, null, request, parameters, unitKey, -1 );
        parameters.putString( "contentselector", formItems.getString( "contentselector", null ) );
        parameters.putString( "contentselector_name", formItems.getString( "contentselector_name", null ) );
        parameters.putString( "contentselector_contenttypekey", formItems.getString( "contentselector_contenttypekey", null ) );
        parameters.putString( "minoccurrence", formItems.getString( "minoccurrence", null ) );
        parameters.putString( "maxoccurrence", formItems.getString( "maxoccurrence", null ) );
        parameters.putString( "contenthandler", formItems.getString( "contenthandler", null ) );

        transformXML( request, response, doc, "generic_search.xsl", parameters );
    }

    public void handlerPreview( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {
        User user = securityService.getLoggedInAdminConsoleUser();

        String subop = formItems.getString( "subop" );
        if ( "frameset".equals( subop ) )
        {
            handlerPreviewFrameset( request, response, session, admin, formItems, user );
        }
        else if ( "list".equals( subop ) )
        {
            handlerPreviewSiteList( request, response, admin, formItems, user );
        }
        else if ( "pagetemplate".equals( subop ) )
        {
            handlerPreviewPageTemplate( request, response, session, admin, formItems, user );
        }
        else
        {
            String message = "Unknown sub-operation: {0}";
            VerticalAdminLogger.errorAdmin(message, subop, null );
        }
    }

    private void handlerPreviewSiteList( HttpServletRequest request, HttpServletResponse response, AdminService admin,
                                         ExtendedMap formItems, User user )
        throws VerticalAdminException, VerticalEngineException
    {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put( "page", formItems.get( "page" ) );
        int unitKey = formItems.getInt( "selectedunitkey", -1 );
        int siteKey = formItems.getInt( "menukey", -1 );

        int contentKey = formItems.getInt( "contentkey", -1 );
        int contentTypeKey;
        if ( contentKey >= 0 )
        {
            parameters.put( "contentkey", contentKey );
            contentTypeKey = admin.getContentTypeKey( contentKey );
            parameters.put( "sessiondata", formItems.getBoolean( "sessiondata", false ) );
        }
        else
        {
            contentTypeKey = formItems.getInt( "contenttypekey", -1 );
        }
        parameters.put( "contenttypekey", contentTypeKey );

        int versionKey = formItems.getInt( "versionkey", -1 );
        if ( versionKey != -1 )
        {
            parameters.put( "versionkey", versionKey );
        }

        Document doc = XMLTool.domparse( admin.getAdminMenu( user, -1 ) );
        Element rootSitesElement = doc.getDocumentElement();
        Element[] allSiteElements = XMLTool.getElements( rootSitesElement );
        int defaultPageTemplateKey = -1;
        if ( allSiteElements.length > 0 )
        {
            TreeMap<String, Element> allSitesMap = new TreeMap<String, Element>();
            for ( Element siteElement : allSiteElements )
            {
                int mKey = Integer.valueOf( siteElement.getAttribute( "key" ) );
                if ( admin.hasContentPageTemplates( mKey, contentTypeKey ) )
                {
                    String name = siteElement.getAttribute( "name" );
                    allSitesMap.put( name, siteElement );
                }
                rootSitesElement.removeChild( siteElement );
            }

            if ( allSitesMap.size() > 0 )
            {
                Element firstMenuElem = allSitesMap.get( allSitesMap.firstKey() );
                if ( siteKey < 0 )
                {
                    siteKey = Integer.valueOf( firstMenuElem.getAttribute( "key" ) );
                }

                for ( Element siteElement : allSitesMap.values() )
                {
                    rootSitesElement.appendChild( siteElement );
                    int key = Integer.parseInt( siteElement.getAttribute( "key" ) );
                    if ( key == siteKey )
                    {
                        String defaultPageTemplateAttr = siteElement.getAttribute( "defaultpagetemplate" );
                        if ( defaultPageTemplateAttr != null && !defaultPageTemplateAttr.equals( "" ) )
                        {
                            defaultPageTemplateKey = Integer.parseInt( defaultPageTemplateAttr );
                        }

                    }
                }
            }
        }

        addCommonParameters( admin, user, request, parameters, unitKey, siteKey );

        if ( siteKey >= 0 )
        {
            String pageTemplateXML = admin.getPageTemplatesByMenu( siteKey, EXCLUDED_TYPE_KEYS_IN_PREVIEW );
            Document ptDoc = XMLTool.domparse( pageTemplateXML );
            XMLTool.mergeDocuments( doc, ptDoc, true );

            if ( contentKey >= 0 )
            {
                Document chDoc = XMLTool.domparse( admin.getContentHomes( contentKey ) );
                XMLTool.mergeDocuments( doc, chDoc, true );
            }

            if ( formItems.containsKey( "pagetemplatekey" ) )
            {
                int pageTemplateKey = formItems.getInt( "pagetemplatekey" );
                parameters.put( "pagetemplatekey", String.valueOf( pageTemplateKey ) );
            }
            else
            {
                if ( contentTypeKey >= 0 )
                {
                    org.jdom.Document pageTemplateDocument = XMLTool.jdomparse( pageTemplateXML );
                    org.jdom.Element root = pageTemplateDocument.getRootElement();
                    List<org.jdom.Element> pageTemplates = root.getChildren( "pagetemplate" );
                    Set<KeyValue> pageTemplateKeys = new HashSet<KeyValue>();
                    for ( org.jdom.Element pageTemplate : pageTemplates )
                    {

                        int pageTemplateKey = Integer.parseInt( pageTemplate.getAttribute( "key" ).getValue() );
                        org.jdom.Element contentTypesNode = pageTemplate.getChild( "contenttypes" );
                        List<org.jdom.Element> contentTypeElements = contentTypesNode.getChildren( "contenttype" );

                        if ( checkMatchingContentType( contentTypeKey, contentTypeElements ) )
                        {
                            KeyValue keyValue = new KeyValue( pageTemplateKey, pageTemplate.getChildText( "name" ) );
                            pageTemplateKeys.add( keyValue );
                        }
                    }
                    if ( pageTemplateKeys.size() > 0 )
                    {
                        KeyValue[] keys = new KeyValue[pageTemplateKeys.size()];
                        keys = pageTemplateKeys.toArray( keys );
                        Arrays.sort( keys );
                        parameters.put( "pagetemplatekey", keys[0].key );
                    }
                    else
                    {
                        if ( defaultPageTemplateKey < 0 )
                        {
                            throw new VerticalAdminException( "Unable to resolve page template. " +
                                                                  "No matching page template found and default page template is not set." );
                        }
                        parameters.put( "pagetemplatekey", String.valueOf( defaultPageTemplateKey ) );
                    }

                }
            }

            if ( formItems.containsKey( "menuitemkey" ) )
            {
                parameters.put( "menuitemkey", formItems.get( "menuitemkey" ) );
            }
        }

        transformXML( request, response, doc, "contenttype_preview_list.xsl", parameters );
    }

    private Boolean checkMatchingContentType( int contentTypeKey, List<org.jdom.Element> contentTypeElements )
    {
        for ( org.jdom.Element contentTypeElement : contentTypeElements )
        {
            int contentTypeElementKey = Integer.parseInt( contentTypeElement.getAttributeValue( "key" ) );
            if ( contentTypeElementKey == contentTypeKey )
            {
                return true;
            }
        }
        return false;
    }

    private void handlerPreviewFrameset( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                         ExtendedMap formItems, User user )
        throws VerticalAdminException, VerticalEngineException
    {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put( "page", formItems.get( "page" ) );

        int unitKey;
        String contentTitle;

        if ( formItems.getBoolean( "sessiondata", false ) == false )
        {
            // Happens when previewing an uneditable version, i.e. there is not unsaved changes and we do not need to parse submitted content data
            final ContentKey contentKey = new ContentKey( formItems.getInt( "contentkey" ) );
            final ContentEntity content = contentDao.findByKey( contentKey );
            parameters.put( "contentkey", contentKey.toInt() );
            final ContentVersionKey versionKey;
            final ContentVersionEntity contentVersion;
            if ( formItems.containsKey( "versionkey" ) )
            {
                versionKey = new ContentVersionKey( formItems.getInt( "versionkey" ) );
                contentVersion = contentVersionDao.findByKey( versionKey );
            }
            else
            {
                versionKey = content.getMainVersion().getKey();
                contentVersion = contentVersionDao.findByKey( versionKey );
            }
            parameters.put( "versionkey", versionKey );
            contentTitle = contentVersion.getTitle();
            unitKey = content.getCategory().getUnit().getKey();
        }
        else
        {
            // Happens when previewing an editable or unsaved version, i.e. there is unsaved changes and we need to parse submitted content data
            int contentKey = getContentKey( formItems, -1 );
            boolean formDisabled = formItems.getBoolean( "formdisabled", false ) || alwaysDisabled;
            String xmlData = contentXMLBuilder.buildXML( formItems, user, contentKey < 0, false, formDisabled );
            contentTitle = formItems.getString( contentXMLBuilder.getTitleFormKey(), null );
            Document doc = XMLTool.domparse( xmlData );
            Element contentElem = doc.getDocumentElement();
            doc.removeChild( contentElem );
            Element contentsElem = XMLTool.createElement( doc, "contents" );
            contentsElem.appendChild( contentElem );
            doc.appendChild( contentsElem );

            // add related contents
            buildRelatedContentsXML( admin, user, formItems, contentsElem );
            xmlData = XMLTool.documentToString( doc );

            XMLDocument xmlDocument = XMLDocumentFactory.create( xmlData );
            org.jdom.Document documentAsJDom = xmlDocument.getAsJDOMDocument();
            org.jdom.Element contentEl = documentAsJDom.getRootElement().getChild( "content" );
            ContentAndVersion parsedContentAndVersion = contentParserService.parseContentAndVersionForPreview( contentEl, null, true );
            NoLazyInitializationEnforcerForPreview.enforceNoLazyInitialization( parsedContentAndVersion.getContent() );

            // Putting the parsed content and version on the session so it can be picked up at the actual preview request
            session.setAttribute( "_preview-content-and-version", parsedContentAndVersion );

            unitKey = formItems.getInt( "selectedunitkey", -1 );

            int contentTypeKey = getContentTypeKey( formItems );
            parameters.put( "contenttypekey", contentTypeKey );

            if ( contentKey >= 0 )
            {
                parameters.put( "contentkey", contentKey );
            }
            parameters.put( "sessiondata", "true" );
        }

        if ( formItems.containsKey( "pagetemplatekey" ) )
        {
            parameters.put( "pagetemplatekey", formItems.get( "pagetemplatekey" ) );
        }

        addCommonParameters( admin, user, request, parameters, unitKey, -1 );
        if ( contentTitle != null )
        {
            parameters.put( "contenttitle", contentTitle );
        }

        if ( formItems.containsKey( "menuitemkey" ) )
        {
            parameters.put( "menuitemkey", formItems.get( "menuitemkey" ) );
        }

        if ( formItems.containsKey( "menukey" ) )
        {
            parameters.put( "menukey", formItems.get( "menukey" ) );
        }

        Document doc = XMLTool.createDocument( "data" );

        parameters.put( "user-agent", request.getHeader( "user-agent" ) );
        transformXML( request, response, doc, "contenttype_preview_frameset.xsl", parameters );
    }


    private void handlerPreviewPageTemplate( HttpServletRequest request, HttpServletResponse response, HttpSession session,
                                             AdminService admin, ExtendedMap formItems, User oldUser )
        throws VerticalAdminException, VerticalEngineException
    {
        try
        {
            final int contentKeyInt = formItems.getInt( "contentkey", -1 );
            final ContentKey contentKey;
            if ( contentKeyInt > -1 )
            {
                contentKey = new ContentKey( contentKeyInt );
            }
            else
            {
                contentKey = null;
            }

            SiteKey siteKey = new SiteKey( formItems.getInt( "menukey" ) );
            SiteEntity site = siteDao.findByKey( siteKey );
            PageTemplateKey pageTemplateKey = new PageTemplateKey( formItems.getInt( "pagetemplatekey" ) );
            PageTemplateEntity pageTemplate = pageTemplateDao.findByKey( pageTemplateKey.toInt() );

            UserEntity previewer = securityService.getUser( oldUser );

            PreviewContentHandler previewContentHandler = new PreviewContentHandler( contentKey );
            previewContentHandler.setSite( site );
            previewContentHandler.setTimeService( timeService );
            previewContentHandler.setPreviewService( previewService );
            previewContentHandler.setPageTemplate( pageTemplate );
            previewContentHandler.setFormItems( formItems );
            previewContentHandler.setPreviewer( previewer );
            previewContentHandler.setSessionId( session.getId() );
            previewContentHandler.setRequest( request );
            previewContentHandler.setSession( session );
            previewContentHandler.setContentDao( contentDao );
            previewContentHandler.setContentVersionDao( contentVersionDao );
            previewContentHandler.setPageRendererFactory( pageRendererFactory );
            previewContentHandler.setDeviceClassResolverService( deviceClassResolverService );
            previewContentHandler.setLocaleResolverService( localeResolverService );

            RenderedPageResult result = previewContentHandler.renderPreview();

            PrintWriter writer = response.getWriter();
            writer.write( result.getContent() );
        }
        catch ( VerticalRenderException vre )
        {
            String message = "Failed to render page: %t";
            VerticalAdminLogger.errorAdmin(message, vre );
        }
        catch ( IOException ioe )
        {
            String message = "Failed to redirect user client: %t";
            VerticalAdminLogger.errorAdmin(message, ioe );
        }
    }

    private int[] resolveContentTypes( String[] contentTypeStringArray )
    {

        if ( contentTypeStringArray != null && contentTypeStringArray.length > 0 && contentTypeStringArray[0] != null )
        {
            if ( contentTypeStringArray[0].indexOf( "," ) > -1 )
            {
                // contenttypene ligger kommaseparert i første element (skjer ved linker)
                contentTypeStringArray = contentTypeStringArray[0].split( "," );
            }
            int[] types = ArrayUtil.toIntArray( contentTypeStringArray );
            if ( ArrayUtil.contains( types, -1 ) )
            {
                return null;
            }
            return types;
        }
        return null;
    }

    private StringBuffer createContentTypesString( final int[] contentTypes )
    {
        StringBuffer contentTypesString = new StringBuffer( "" );

        if ( contentTypes != null )
        {
            for ( int i = 0; i < contentTypes.length; i++ )
            {
                contentTypesString.append( contentTypes[i] );
                if ( i < contentTypes.length - 1 )
                {
                    contentTypesString.append( "," );
                }
            }
        }
        return contentTypesString;
    }

    public void handlerBrowse( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, ExtendedMap parameters, User oldUser, Document verticalDoc )
        throws VerticalAdminException
    {

        UserEntity user = securityService.getUser( oldUser );
        String op = formItems.getString( "op" );
        String subop = formItems.getString( "subop", "browse" );

        String contenthandler = formItems.getString( "contenthandler", "" );

        int submittetCategoryKey = formItems.getInt( "categorykey", -1 );

        if ( submittetCategoryKey == -1 )
        {
            submittetCategoryKey = formItems.getInt( "cat", -1 );
        }

        CategoryKey categoryKey = CategoryKey.parse( submittetCategoryKey );

        boolean categoryDisabled_which_means_user_do_not_have_read_access = formItems.getBoolean( "disabled", false );

        String[] contentTypeStringArray = formItems.getStringArray( "contenttypestring" );
        int[] contentTypes = resolveContentTypes( contentTypeStringArray );
        StringBuffer contentTypesString = createContentTypesString( contentTypes );

        if ( !"browse".equals( subop ) )
        {
            String deploymentPath = DeploymentPathResolver.getAdminDeploymentPath( request );
            CookieUtil.setCookie( response, getPopupCookieName( contentTypesString.toString() ),
                                  categoryKey != null ? categoryKey.toString() : "-1", COOKIE_TIMEOUT, deploymentPath );
        }

        int contentTypeKey = -1;
        boolean hasAdminReadOnCategory = true;
        boolean hasCategoryRead = false;
        boolean hasCategoryCreate = false;
        boolean hasCategoryPublish = false;
        boolean hasCategoryAdministrate = false;
        CategoryAccessResolver categoryAccessResolver = new CategoryAccessResolver( groupDao );

        if ( categoryKey != null )
        {
            CategoryEntity category = categoryDao.findByKey( categoryKey );
            hasAdminReadOnCategory = categoryAccessResolver.hasAdminBrowseCategoryAccess( user, category );
            hasCategoryRead = categoryAccessResolver.hasReadCategoryAccess( user, category );
            hasCategoryCreate = categoryAccessResolver.hasCreateContentAccess( user, category );
            hasCategoryPublish = categoryAccessResolver.hasApproveContentAccess( user, category );
            hasCategoryAdministrate = categoryAccessResolver.hasAdministrateCategoryAccess( user, category );

            ContentTypeEntity contentType = category.getContentType();
            if ( contentType != null )
            {
                contentTypeKey = contentType.getKey();
            }
        }

        String sortBy = formItems.getString( "sortby", "@timestamp" );
        String sortByDirection = formItems.getString( "sortby-direction", "DESC" );

        StringBuffer orderBy = new StringBuffer();
        orderBy.append( sortBy );
        orderBy.append( " " );
        orderBy.append( sortByDirection );

        final String cookieName = "archiveBrowseItemsPerPage";
        int index = formItems.getInt( "index", 0 );
        int count = ListCountResolver.resolveCount( request, formItems, cookieName );
        CookieUtil.setCookie( response, cookieName, Integer.toString( count ), COOKIE_TIMEOUT,
                              DeploymentPathResolver.getAdminDeploymentPath( request ) );

        XMLDocument xmlContent = null;
        String searchType = formItems.getString( "searchtype", null );

        // Get contents
        if ( searchType != null )
        {

            if ( searchType.equals( "simple" ) )
            {
                xmlContent =
                    new SearchUtility( userDao, groupDao, securityService, contentService ).simpleSearch( oldUser, formItems, categoryKey,
                                                                                                          contentTypes, orderBy.toString(),
                                                                                                          index, count );
                parameters.put( "searchtext", formItems.getString( "searchtext", "" ) );
                parameters.put( "scope", formItems.getString( "scope" ) );
            }
            else
            {
                String ownerGroupKey = formItems.getString( "owner", "" );
                if ( !"".equals( ownerGroupKey ) )
                {
                    User ownerUser = getUserFromUserGroupKey( ownerGroupKey );

                    parameters.put( "owner.uid", ownerUser.getName() );
                    parameters.put( "owner.fullName", ownerUser.getDisplayName() );
                    parameters.put( "owner.qualifiedName", ownerUser.getQualifiedName() );

                    addUserKeyToFormItems( formItems, "owner.key", ownerUser );
                }

                String modifierGroupKey = formItems.getString( "modifier", "" );
                if ( !"".equals( modifierGroupKey ) )
                {
                    User modifierUser = getUserFromUserGroupKey( modifierGroupKey );

                    parameters.put( "modifier.uid", modifierUser.getName() );
                    parameters.put( "modifier.fullName", modifierUser.getDisplayName() );
                    parameters.put( "modifier.qualifiedName", modifierUser.getQualifiedName() );

                    addUserKeyToFormItems( formItems, "modifier.key", modifierUser );
                }

                String assignee = formItems.getString( "_assignee", "" );
                if ( !"".equals( assignee ) )
                {
                    User assigneeUser = getUserFromUserKey( assignee );
                    if ( assigneeUser == null )
                    {
                        assigneeUser = getUserFromUserGroupKey( assignee );
                    }

                    parameters.put( "assignment.assigneeUserKey", assignee );
                    parameters.put( "assignment.assigneeDisplayName", assigneeUser.getDisplayName() );
                    parameters.put( "assignment.assigneeQualifiedName", assigneeUser.getQualifiedName().toString() );
                }

                String assigner = formItems.getString( "_assigner", "" );
                if ( !"".equals( assigner ) )
                {
                    User assignerUser = getUserFromUserKey( assigner );
                    if ( assignerUser == null )
                    {
                        assignerUser = getUserFromUserGroupKey( assigner );
                    }

                    parameters.put( "assignment.assignerUserKey", assigner );
                    parameters.put( "assignment.assignerDisplayName", assignerUser.getDisplayName() );
                    parameters.put( "assignment.assignerQualifiedName", assignerUser.getQualifiedName().toString() );
                }

                String assignmentDueDate = formItems.getString( "date_assignmentDueDate", "" );
                if ( !"".equals( assignmentDueDate ) )
                {
                    DateTimeFormatter norwegianDateFormatter = DateTimeFormat.forPattern( "dd.MM.yyyy" );
                    DateMidnight assignmentDueDateAsDateTime = norwegianDateFormatter.parseDateTime( assignmentDueDate ).toDateMidnight();

                    DateTimeFormatter isoDateFormatter = DateTimeFormat.forPattern( "yyyy-MM-dd" );
                    String assignmentDueDateAsStringIsoFormatted = isoDateFormatter.print( assignmentDueDateAsDateTime );
                    parameters.put( "assignment.dueDate", assignmentDueDateAsStringIsoFormatted );
                    parameters.put( "assignment.dueDate.op", formItems.getString( "_assignmentDueDate.op", "" ) );
                }

                xmlContent = new SearchUtility( userDao, groupDao, securityService, contentService ).advancedSearch( oldUser, formItems,
                                                                                                                     contentTypes,
                                                                                                                     orderBy.toString(),
                                                                                                                     index, count );
                parameters.put( "asearchtext", formItems.getString( "asearchtext", "" ) );
                parameters.put( "ascope", formItems.getString( "ascope" ) );
                parameters.put( "subcategories", formItems.getString( "subcategories" ) );
                parameters.put( "state", formItems.getString( "state", "" ) );
                parameters.put( "owner", ownerGroupKey );

                parameters.put( "modifier", modifierGroupKey );

                parameters.put( "created.op", formItems.getString( "created.op", "" ) );
                parameters.put( "created", formItems.getString( "datecreated", "" ) );
                parameters.put( "modified.op", formItems.getString( "modified.op", "" ) );
                parameters.put( "modified", formItems.getString( "datemodified", "" ) );
                parameters.put( "acontentkey", formItems.getString( "acontentkey", "" ) );
                parameters.put( "filter", formItems.getString( "filter", "" ) );
                parameters.put( "selectedtabpage", formItems.getString( "selectedtabpage", "" ) );
                parameters.put( "duedate", assignmentDueDate );
            }
            parameters.put( "searchtype", searchType );
        }
        else if ( hasAdminReadOnCategory )
        {
            xmlContent = admin.getContent( oldUser, categoryKey, false, orderBy.toString(), index, count, 0, 0, 0 );
        }

        if ( xmlContent != null )
        {
            Document contentDoc = xmlContent.getAsDOMDocument();
            XMLTool.mergeDocuments( verticalDoc, contentDoc, true );

            // Find all content types and categories in this list
            Element[] contentElems = XMLTool.getElements( contentDoc.getDocumentElement(), "content" );
            Set<Integer> contentTypeKeys = new HashSet<Integer>();
            Set<Integer> categoryKeys = new HashSet<Integer>();
            for ( Element contentElem : contentElems )
            {
                contentTypeKeys.add( Integer.parseInt( contentElem.getAttribute( "contenttypekey" ) ) );
                Element categoryElem = XMLTool.getElement( contentElem, "categoryname" );
                categoryKeys.add( Integer.parseInt( categoryElem.getAttribute( "key" ) ) );
            }

            if ( contentTypeKeys.size() == 0 && searchType == null )
            {
                // This is a normal listing of an empty category
                contentTypeKeys.add( contentTypeKey );
            }

            if ( contentTypeKeys.size() > 0 )
            {
                Integer[] keyArray = new Integer[contentTypeKeys.size()];
                int[] primitiveArray = ArrayUtils.toPrimitive( contentTypeKeys.toArray( keyArray ) );
                XMLDocument ctyDoc = admin.getContentTypes( primitiveArray, true );
                XMLTool.mergeDocuments( verticalDoc, ctyDoc.getAsDOMDocument(), true );
            }

            // Get content types for this site
            XMLDocument siteContentTypesDoc = admin.getContentTypes( false );
            final Document siteContentTypesDocument = siteContentTypesDoc.getAsDOMDocument();
            XMLTool.renameElement( siteContentTypesDocument.getDocumentElement(), "sitecontenttypes" );
            XMLTool.mergeDocuments( verticalDoc, siteContentTypesDocument, true );

            // Get all categories
            if ( categoryKeys.size() > 0 )
            {

                Integer[] keyArray = new Integer[categoryKeys.size()];
                keyArray = categoryKeys.toArray( keyArray );

                CategoryCriteria categoryCriteria = new CategoryCriteria();
                categoryCriteria.addCategoryKeys( Arrays.asList( keyArray ) );
                Document categoriesDoc = admin.getMenu( oldUser, categoryCriteria ).getAsDOMDocument();
                XMLTool.mergeDocuments( verticalDoc, categoriesDoc, false );
            }
        }

        Document headerDoc = XMLTool.domparse( admin.getCategoryPathXML( categoryKey, contentTypes ) );
        XMLTool.mergeDocuments( verticalDoc, headerDoc, true );

        // Default browse config
        Document defaultBrowseConfig = XMLTool.domparse( AdminStore.getXML( session, "defaultbrowseconfig.xml" ) );
        XMLTool.mergeDocuments( verticalDoc, defaultBrowseConfig, true );

        // Feedback
        if ( formItems.containsKey( "feedback" ) )
        {
            addFeedback( verticalDoc, formItems.getInt( "feedback" ) );
        }

        // Category header
        if ( categoryKey != null )
        {
            // Category

            // Small hack: we put the current category on /data/category, all categories
            // used are also present in /data/categories/category, but without contentcount and accessrights
            Document categoryDoc = XMLTool.domparse( admin.getCategory( oldUser, categoryKey.toInt() ) );
            XMLTool.mergeDocuments( verticalDoc, categoryDoc, false );

            int superCategoryKey = admin.getSuperCategoryKey( categoryKey.toInt() );
            if ( superCategoryKey != -1 )
            {
                CategoryAccessRight supercar = admin.getCategoryAccessRight( oldUser, superCategoryKey );
                parameters.put( "parentcategoryadministrate", supercar.getAdministrate() );
            }

            // Trenger indexparametre for å vite hvilke felt det kan sorteres på.. list.xsl
            Document indexingDoc = XMLTool.domparse( admin.getIndexingParametersXML( contentTypeKey ) );
            XMLTool.mergeDocuments( verticalDoc, indexingDoc, true );

            parameters.put( "cat", categoryKey.toString() );
            parameters.put( "contenttypekey", Integer.toString( contentTypeKey ) );
            parameters.put( "selectedunitkey", Integer.toString( admin.getUnitKey( categoryKey.toInt() ) ) );
        }
        else
        {
            parameters.putInt( "cat", -1 );
            parameters.putInt( "selectedunitkey", -1 );
        }

        if ( categoryDisabled_which_means_user_do_not_have_read_access )
        {
            parameters.put( "searchonly", "true" );
        }
        parameters.put( "index", index );
        parameters.put( "count", count );
        parameters.put( "op", op );
        parameters.put( "subop", subop );
        parameters.put( "hasAdminBrowse", hasAdminReadOnCategory );
        parameters.put( "hasCategoryRead", hasCategoryRead );
        parameters.put( "hasCategoryCreate", hasCategoryCreate );
        parameters.put( "hasCategoryPublish", hasCategoryPublish );
        parameters.put( "hasCategoryAdministrate", hasCategoryAdministrate );

        parameters.put( "fieldname", formItems.getString( "fieldname", "" ) );
        parameters.put( "fieldrow", formItems.getString( "fieldrow", "" ) );
        parameters.put( "contenttypestring", contentTypesString.toString() );
        parameters.put( "sortby", sortBy );
        parameters.put( "sortby-direction", sortByDirection );

        parameters.put( "contenthandler", contenthandler );
        parameters.put( "minoccurrence", formItems.getString( "minoccurrence", "" ) );
        parameters.put( "maxoccurrence", formItems.getString( "maxoccurrence", "" ) );

        if ( formItems.containsKey( "reload" ) )
        {
            parameters.put( "reload", formItems.getString( "reload" ) );
        }

        addPageTemplatesOfUserSitesToDocument( admin, user, PageTemplateType.CONTENT, verticalDoc );

        transformXML( request, response, verticalDoc, "content_list.xsl", parameters );
    }

    public void handlerNotify( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, User user )
        throws VerticalAdminException
    {

        boolean sentToApproval = formItems.containsKey( "senttoapproval" );
        boolean sentToActivation = formItems.containsKey( "senttoactivation" );
        boolean rejected = formItems.containsKey( "rejected" );

        if ( sentToApproval || sentToActivation || rejected )
        {
            // First part of the send notification process: Show notify page

            int contentKey = formItems.getInt( "key" );
            int versionKey = formItems.getInt( "versionkey" );
            Document doc = null, headerDoc = null;
            if ( sentToApproval )
            {
                int categoryKey = admin.getCategoryKey( contentKey );
                doc = XMLTool.domparse( admin.getUsersWithPublishRight( categoryKey ) );

                // Category header
                headerDoc = XMLTool.domparse( admin.getPath( user, Types.CATEGORY, categoryKey ) );
            }
            else if ( rejected )
            {
                int categoryKey = admin.getCategoryKey( contentKey );
                doc = XMLTool.domparse( admin.getContentOwner( contentKey ) );

                // Category header
                headerDoc = XMLTool.domparse( admin.getPath( user, Types.CATEGORY, categoryKey ) );
            }
            else
            {
                // Not yet implemented
            }
            if ( doc.getDocumentElement().getChildNodes().getLength() > 0 )
            {
                XMLTool.mergeDocuments( doc, headerDoc, false );
                ExtendedMap parameters = new ExtendedMap();

                parameters.put( "key", formItems.getInt( "key" ) );
                parameters.put( "versionkey", formItems.getInt( "versionkey" ) );
                parameters.put( "page", formItems.getInt( "page" ) );
                parameters.put( "subop", formItems.getString( "subop", "" ) );
                parameters.put( "cat", formItems.getString( "cat" ) );
                parameters.put( "fieldname", formItems.getString( "fieldname", "" ) );
                parameters.put( "fieldrow", formItems.getString( "fieldrow", "" ) );
                parameters.put( "contenttypestring", formItems.getString( "contenttypestring", "" ) );
                parameters.put( "selectedtabpage", formItems.getString( "selectedtabpage", "" ) );
                parameters.put( "feedback", formItems.getString( "feedback", "" ) );

                parameters.putBoolean( "alwaysdisabled", alwaysDisabled );
                if ( formItems.containsKey( "creatednewversion" ) )
                {
                    parameters.put( "creatednewversion", formItems.getString( "creatednewversion" ) );
                }
                if ( formItems.containsKey( "referer" ) )
                {
                    parameters.put( "referer", formItems.getString( "referer" ) );
                }

                parameters.put( "page", formItems.getString( "page" ) );
                if ( sentToApproval )
                {
                    parameters.put( "notify", "senttoapproval" );
                }
                else if ( sentToActivation )
                {
                    parameters.put( "notify", "senttoactivation" );
                }
                else
                {
                    parameters.put( "notify", "rejected" );
                }
                parameters.put( "contenttitle", admin.getContentTitle( versionKey ) );

                transformXML( request, response, doc, "notify_form.xsl", parameters );
            }
            else
            {
                // No users to send mail to...
                // Very important to remove these parameters...
                formItems.remove( "senttoapproval" );
                formItems.remove( "senttoactivation" );
                formItems.remove( "rejected" );

                redirectToReferer( request, response, formItems );
            }
        }
        else
        {
            if ( formItems.getBoolean( "sendmail", false ) )
            {
                // Second part of the send notification process: Send emails and redirect

                String[] recipientKeys = AdminHandlerBaseServlet.getArrayFormItem( formItems, "recipientkeys" );

                if ( recipientKeys.length > 0 )
                {

                    UserEntity userEntity = userDao.findByKey( user.getKey() );

                    String body = formItems.getString( "body", "" );
                    String contentKeyString = formItems.getString( "key", null );

                    if ( StringUtils.isNotBlank( contentKeyString ) )
                    {
                        ApproveAndRejectMailTemplate mailCreator =
                            new ApproveAndRejectMailTemplate( body, new ContentKey( contentKeyString ), userEntity );

                        addRecipientsForRejectMail( formItems, recipientKeys, mailCreator );

                        mailCreator.setReject( true );
                        mailCreator.setFrom( new MailRecipient( user.getDisplayName(), user.getEmail() ) );

                        sendMailService.sendMail( mailCreator );
                    }
                }
            }
            redirectToReferer( request, response, formItems );
        }
    }

    private void addRecipientsForRejectMail( ExtendedMap formItems, String[] recipientKeys, ApproveAndRejectMailTemplate mailCreator )
    {
        for ( int i = 0; i < recipientKeys.length; i++ )
        {
            String recipientName = formItems.getString( "name_" + recipientKeys[i] );
            String recipientEmail = formItems.getString( "email_" + recipientKeys[i] );

            mailCreator.addRecipient( new MailRecipient( recipientName, recipientEmail ) );
        }
    }

    public void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, String operation, ExtendedMap parameters, User user, Document verticalDoc )
        throws VerticalAdminException, VerticalEngineException
    {

        if ( "delete_version".equals( operation ) )
        {
            handlerDeleteVersion( request, response, session, admin, formItems, operation, parameters, user, verticalDoc );
        }
        else if ( "batch_remove".equals( operation ) )
        {
            handlerBatchRemove( request, response, session, admin, formItems, operation, parameters, user, verticalDoc );
        }
        else if ( "batch_move".equals( operation ) )
        {
            handlerBatchMove( request, response, session, admin, formItems, operation, parameters, user, verticalDoc );
        }
        else if ( "batch_archive".equals( operation ) )
        {
            handlerBatchArchive( request, response, session, admin, formItems, operation, parameters, user, verticalDoc );
        }
        else if ( "batch_approve".equals( operation ) )
        {
            handlerBatchApprove( request, response, session, admin, formItems, operation, parameters, user, verticalDoc );
        }
        else if ( "batch_copy".equals( operation ) )
        {
            handlerBatchCopy( request, response, session, admin, formItems, operation, parameters, user, verticalDoc );
        }
        else if ( "callback".equals( operation ) )
        {
            handlerCallbackClose( request, response, admin, formItems, parameters, verticalDoc );
        }
        else if ( "save_and_assign_form".equals( operation ) )
        {
            handlerSaveAndAssignForm( request, response, formItems, user );
        }
        else if ( "save_and_assign".equals( operation ) )
        {
            handlerSaveAndAssign( request, response, session, admin, formItems, operation, parameters, user, verticalDoc );
        }
        else
        {
            super.handlerCustom( request, response, session, admin, formItems, operation, parameters, user, verticalDoc );
        }
    }

    public void handlerCallbackClose( HttpServletRequest request, HttpServletResponse response, AdminService admin, ExtendedMap formItems,
                                      ExtendedMap parameters, Document verticalDoc )
        throws VerticalAdminException, VerticalEngineException
    {

        int key = formItems.getInt( "key" );
        int versionKey;
        int currentVersionKey = admin.getCurrentVersionKey( key );
        if ( formItems.containsKey( "versionkey" ) )
        {
            versionKey = formItems.getInt( "versionkey" );
        }
        else
        {
            versionKey = currentVersionKey;
        }

        parameters.put( "key", key );
        parameters.put( "versionkey", versionKey );
        parameters.put( "current", ( versionKey == currentVersionKey ) );
        if ( formItems.containsKey( "title" ) )
        {
            parameters.put( "title", formItems.getString( "title" ) );
        }
        else
        {
            parameters.put( "title", admin.getContentTitle( versionKey ) );
        }
        parameters.put( "fieldname", formItems.getString( "fieldname" ) );
        parameters.put( "fieldrow", formItems.getString( "fieldrow" ) );
        parameters.put( "callback", formItems.getString( "callback" ) );

        transformXML( request, response, verticalDoc, "callback_close.xsl", parameters );
    }

    public void handlerBatchCopy( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                  ExtendedMap formItems, String operation, ExtendedMap parameters, User user, Document verticalDoc )
        throws VerticalAdminException, VerticalEngineException
    {

        String[] contentKeyStrings = formItems.getStringArray( "batch_operation" );
        ContentKey[] contentKeys = new ContentKey[contentKeyStrings.length];

        for ( int i = 0; i < contentKeyStrings.length; i++ )
        {
            contentKeys[i] = new ContentKey( contentKeyStrings[i] );
        }

        CategoryKey newCategoryKey = new CategoryKey( formItems.getInt( "newcategory" ) );

        CategoryEntity toCategory = categoryDao.findByKey( newCategoryKey );
        if ( toCategory == null )
        {
            throw new IllegalArgumentException( "Category to move to not found, key: " + newCategoryKey );
        }

        UserEntity copier = securityService.getUser( user );
        for ( ContentKey contentKey : contentKeys )
        {
            ContentEntity content = contentDao.findByKey( contentKey );
            if ( content != null )
            {
                contentService.copyContent( copier, content, toCategory );
            }
        }

        // JSI 29.11.09: Commented out, because the params object is not used after it is filled with info.
//        ExtendedMap params = new ExtendedMap();
//        params.put( "page", 991 );
//        params.put( "op", "browse" );
//        params.put( "cat", newCategoryKey );

        redirectToReferer( request, response, formItems );
    }

    public void handlerDeleteVersion( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                      ExtendedMap formItems, String operation, ExtendedMap parameters, User user, Document verticalDoc )
        throws VerticalAdminException, VerticalEngineException
    {
        boolean closeonsuccess = formItems.getBoolean( "closeonsuccess", false );

        ContentVersionKey versionKey = new ContentVersionKey( formItems.getInt( "versionkey" ) );
        // Test no versionkey
        ContentVersionEntity contentVersion = contentVersionDao.findByKey( versionKey );
        ContentEntity content = contentVersion.getContent();
        if ( content.getVersionCount() <= 1 )
        {
            throw new IllegalArgumentException( "Not allowed to delete the one and only version" );
        }

        if ( contentVersion.getStatus() == ContentStatus.APPROVED || contentVersion.getStatus() == ContentStatus.ARCHIVED )
        {
            throw new IllegalArgumentException( "Not allowed to delete a version that is approved or archived" );
        }

        UserEntity deleter = securityService.getUser( user );

        contentService.deleteVersion( deleter, versionKey );

        formItems.remove( "versionkey" );

        // resolve new version to display...
        ContentVersionEntity versionToDisplay = content.getMainVersion();
        formItems.put( "versionkey", versionToDisplay.getKey() );

        if ( closeonsuccess )
        {
            redirectToReferer( request, response, formItems );
        }
        else
        {
            redirectToForm( request, response, formItems );
        }
    }

    public void handlerBatchRemove( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                    ExtendedMap formItems, String operation, ExtendedMap parameters, User user, Document verticalDoc )
        throws VerticalAdminException, VerticalEngineException
    {

        String[] contentKeyStrings = formItems.getStringArray( "batch_operation" );
        int[] contentKeys = new int[contentKeyStrings.length];

        for ( int i = 0; i < contentKeyStrings.length; i++ )
        {
            contentKeys[i] = Integer.parseInt( contentKeyStrings[i] );

            UserEntity runningUser = securityService.getUser( user );
            try
            {
                final ContentKey contentKey = new ContentKey( contentKeys[i] );
                ContentEntity content = contentDao.findByKey( contentKey );
                if ( content != null && !content.isDeleted() )
                {
                    ContentLocationSpecification contentLocationSpecification = new ContentLocationSpecification();
                    contentLocationSpecification.setIncludeInactiveLocationsInSection( false );
                    ContentLocations contentLocations = content.getLocations( contentLocationSpecification );

                    contentService.deleteContent( runningUser, content );

                    new PageCacheInvalidatorForContent( siteCachesService ).invalidateForContentLocations( contentLocations );
                }
            }
            catch ( ContentAccessException e )
            {
                LOG.info( e.getMessage() );
            }
        }

        /* int[] noRemoveRight = */
        // admin.removeContents( user, contentKeys );
        redirectToReferer( request, response, formItems );
    }

    public void handlerBatchMove( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                  ExtendedMap formItems, String operation, ExtendedMap parameters, User user, Document verticalDoc )
        throws VerticalAdminException, VerticalEngineException
    {

        String[] contentKeyStrings = formItems.getStringArray( "batch_operation" );
        ContentKey[] contentKeys = new ContentKey[contentKeyStrings.length];

        for ( int i = 0; i < contentKeyStrings.length; i++ )
        {
            contentKeys[i] = new ContentKey( contentKeyStrings[i] );
        }

        CategoryKey newCategoryKey = new CategoryKey( formItems.getInt( "newcategory" ) );
        CategoryEntity category = categoryDao.findByKey( newCategoryKey );

        UserEntity runningUser = securityService.getUser( user );

        for ( ContentKey contentKey : contentKeys )
        {
            ContentEntity content = contentDao.findByKey( contentKey );
            if ( content != null )
            {
                try
                {
                    contentService.moveContent( runningUser, content, category );
                }
                catch ( ContentMoveAccessException e )
                {
                    // do nothing
                }
            }
        }

        redirectToReferer( request, response, formItems );
    }

    public void handlerBatchArchive( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                     ExtendedMap formItems, String operation, ExtendedMap parameters, User user, Document verticalDoc )
        throws VerticalAdminException, VerticalEngineException
    {

        String[] contentKeyStrings = formItems.getStringArray( "batch_operation" );
        ContentKey[] contentKeys = new ContentKey[contentKeyStrings.length];

        for ( int i = 0; i < contentKeyStrings.length; i++ )
        {
            contentKeys[i] = new ContentKey( contentKeyStrings[i] );
        }

        UserEntity runningUser = securityService.getUser( user );

        for ( ContentKey contentKey : contentKeys )
        {
            ContentEntity content = contentDao.findByKey( contentKey );
            if ( content != null )
            {
                contentService.archiveContent( runningUser, content );
            }
        }

        redirectToReferer( request, response, formItems );
    }

    public void handlerSaveAndAssign( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                      ExtendedMap formItems, String operation, ExtendedMap parameters, User user, Document verticalDoc )
        throws VerticalAdminException, VerticalEngineException
    {
        boolean sendEmail = formItems.getBoolean( "_send_email", true );
        String assignmentDescription = formItems.getString( "_assignment_description", null );
        String changeComment = formItems.getString( "_comment", null );

        UserEntity updater = securityService.getUser( user );

        int contentKey = formItems.getInt( "key" );

        ContentEntity content = contentDao.findByKey( new ContentKey( contentKey ) );

        formItems.put( "contenttypekey", content.getContentType().getKey() );

        if ( content == null )
        {
            throw new IllegalArgumentException( "Didnt find content: " + contentKey );
        }

        SnapshotContentCommand snapshotCommand = new SnapshotContentCommand();
        snapshotCommand.setContentKey( content.getKey() );
        snapshotCommand.setSnapshotterKey( updater.getKey() );
        snapshotCommand.setClearCommentInDraft( true );
        snapshotCommand.setSnapshotComment( changeComment );
        contentService.snapshotContent( snapshotCommand );

        AssignmentDataParser assignmentDataParser = new AssignmentDataParser( formItems );

        String assigneeKey = assignmentDataParser.getAssigneeKey();

        if ( StringUtils.isNotBlank( assigneeKey ) )
        {
            UserEntity assignee = userDao.findByKey( assigneeKey );

            Date dueDate = assignmentDataParser.getAssignmentDueDate();

            AssignContentCommand assignCommand = new AssignContentCommand();
            assignCommand.setAssigneeKey( assignee.getKey() );
            assignCommand.setContentKey( content.getKey() );
            assignCommand.setAssignmentDueDate( dueDate );
            assignCommand.setAssignerKey( updater.getKey() );
            assignCommand.setAssignmentDescription( assignmentDescription );
            AssignContentResult result = contentService.assignContent( assignCommand );

            if ( sendEmail )
            {
                AssignmentMailSender assignmentMailSender = new AssignmentMailSender( sendMailService );

                ContentEntity persistedContent = contentDao.findByKey( result.getAssignedContentKey() );

                assignmentMailSender.setAssignedContent( persistedContent );
                assignmentMailSender.setOriginalAssignee( result.getOriginalAssignee() );
                assignmentMailSender.setOriginalAssigner( result.getOriginalAssigner() );
                assignmentMailSender.setNewAssignee( result.getNewAssignee() );
                assignmentMailSender.setUpdater( updater );
                assignmentMailSender.setAssignmentDescription( assignmentDescription );
                assignmentMailSender.setAssignmentDueDate( dueDate );

                assignmentMailSender.sendAssignmentMails();
            }
        }

        redirectToReferer( request, response, formItems );
    }

    public void handlerBatchApprove( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                     ExtendedMap formItems, String operation, ExtendedMap parameters, User user, Document verticalDoc )
        throws VerticalAdminException, VerticalEngineException
    {

        String[] contentKeyStrings = formItems.getStringArray( "batch_operation" );
        ContentKey[] contentKeys = new ContentKey[contentKeyStrings.length];

        for ( int i = 0; i < contentKeyStrings.length; i++ )
        {
            contentKeys[i] = new ContentKey( contentKeyStrings[i] );
        }

        UserEntity runningUser = securityService.getUser( user );

        for ( ContentKey contentKey : contentKeys )
        {
            ContentEntity content = contentDao.findByKey( contentKey );
            if ( content != null )
            {
                Date availableFrom = content.getAvailableFrom();
                if ( availableFrom == null )
                {
                    content.setAvailableFrom( new Date() );
                }
                contentService.approveContent( runningUser, content );
            }
        }

        redirectToReferer( request, response, formItems );
    }

    public static int getContentTypeKey( ExtendedMap formItems )
    {
        int page = formItems.getInt( "page", -1 );
        if ( page == -1 )
        {
            // Throw exception
            return -1;
        }
        else
        {
            return page - 999;
        }
    }

    public static int getContentKey( ExtendedMap formItems, int defaultKey )
    {
        return formItems.getInt( "key", defaultKey );
    }

    protected DOMSource buildXSL( HttpSession session, AdminService admin, int contentTypeKey )
        throws VerticalAdminException
    {
        return null;
    }

    public static String getPopupCookieName( String contentTypesString )
    {
        return "contentPopup";
    }

    private void appendContentSource( Document doc, ContentVersionEntity version )
    {
        ContentSourceXmlCreator contentSourceXmlCreator = new ContentSourceXmlCreator( contentIndexDao );
        XMLDocument sourceDocument = contentSourceXmlCreator.createSourceDocument( version );

        Element sourceEl = sourceDocument.getAsDOMDocument().getDocumentElement();
        doc.getDocumentElement().appendChild( doc.importNode( sourceEl, true ) );
    }
}

class KeyValue
    implements Comparable<KeyValue>
{
    protected int key;

    protected String value;

    public KeyValue( int key, String value )
    {
        this.key = key;
        this.value = value;
    }

    public int compareTo( KeyValue o )
    {

        return value.compareTo( o.value );
    }

    @Override
    public String toString()
    {
        return "KeyValue [key=" + key + ", value=" + value + "]";
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + key;
        result = prime * result + ( ( value == null ) ? 0 : value.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        KeyValue other = (KeyValue) obj;
        if ( key != other.key )
        {
            return false;
        }
        if ( value == null )
        {
            if ( other.value != null )
            {
                return false;
            }
        }
        else if ( !value.equals( other.value ) )
        {
            return false;
        }
        return true;
    }


}
