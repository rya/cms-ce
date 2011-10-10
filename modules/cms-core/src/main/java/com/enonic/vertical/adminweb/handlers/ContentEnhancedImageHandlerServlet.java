/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.adminweb.VerticalAdminException;
import com.enonic.vertical.adminweb.handlers.xmlbuilders.ContentXMLBuildersSpringManagedBeansBridge;
import com.enonic.vertical.adminweb.wizard.Wizard;
import com.enonic.vertical.engine.VerticalEngineException;

import com.enonic.cms.core.content.binary.BinaryData;
import com.enonic.cms.core.service.AdminService;

import com.enonic.cms.core.security.user.User;

public class ContentEnhancedImageHandlerServlet
    extends ContentBaseHandlerServlet
{
    private static final String WIZARD_IMPORT_IMAGES = "wizardconfig_import_images.xml";

    public static class ImportImagesWizard
        extends ImportZipWizard
    {
        /**
         * @see com.enonic.vertical.adminweb.handlers.ContentBaseHandlerServlet.ImportZipWizard#cropName(java.lang.String)
         */
        protected String cropName( String name )
        {
            int dotIdx = name.lastIndexOf( '.' );
            if ( dotIdx > 0 )
            {
                return name.substring( 0, dotIdx );
            }
            else
            {
                return name;
            }
        }

        /**
         * @see com.enonic.vertical.adminweb.handlers.ContentBaseHandlerServlet.ImportZipWizard#isFiltered(java.lang.String)
         */
        protected boolean isFiltered( String name )
        {
            int dotIdx = name.lastIndexOf( '.' );
            if ( dotIdx > 0 )
            {
                String extension = name.substring( dotIdx + 1 );
                return "jpg".equalsIgnoreCase( extension ) == false && "jpeg".equalsIgnoreCase( extension ) == false &&
                    "png".equalsIgnoreCase( extension ) == false && "gif".equalsIgnoreCase( extension ) == false;
            }
            return true;
        }

        protected BinaryData[] getBinaries( ContentBaseHandlerServlet cbhServlet, AdminService admin, ExtendedMap formItems, File file )
            throws VerticalAdminException
        {
            formItems.put( "origimagefilename", new DummyFileItem( file ) );
            return cbhServlet.contentXMLBuilder.getBinaries( formItems );
        }
    }

    public ContentEnhancedImageHandlerServlet()
    {
        super();

        FORM_XSL = "enhancedimage_form.xsl";
    }

    public void init( ServletConfig servletConfig )
        throws ServletException
    {
        super.init( servletConfig );
        setContentXMLBuilder( ContentXMLBuildersSpringManagedBeansBridge.getContentEnhancedImageXMLBuilder() );
    }

    public void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, String operation, ExtendedMap parameters, User user, Document verticalDoc )
        throws VerticalAdminException, VerticalEngineException
    {

        if ( operation.equals( "insert" ) )
        {
            int categoryKey = formItems.getInt( "cat" );
            int unitKey = admin.getUnitKey( categoryKey );
            int page = formItems.getInt( "page" );

            ExtendedMap xslParams = new ExtendedMap();
            xslParams.put( "page", String.valueOf( page ) );

            String xmlData = admin.getContent( user, Integer.parseInt( formItems.getString( "key" ) ), 0, 1, 0 );

            Document newDoc = XMLTool.domparse( xmlData );
            if ( newDoc != null )
            {
                String filename = XMLTool.getElementText( newDoc, "/contents/content/contentdata/name" );
                int index = filename.lastIndexOf( "." );
                if ( index != -1 )
                {
                    String filetype = filename.substring( index + 1 ).toLowerCase();
                    if ( "swf".equals( filetype ) )
                    {
                        xslParams.put( "flash", "true" );
                    }
                    else if ( "jpg".equals( filetype ) || "jpeg".equals( filetype ) || "png".equals( filetype ) ||
                        "gif".equals( filetype ) )
                    {
                        xslParams.put( "image", "true" );
                    }
                }
            }

            String xmlCategory = admin.getSuperCategoryNames( categoryKey, false, true );
            XMLTool.mergeDocuments( newDoc, XMLTool.domparse( xmlCategory ), true );

            addCommonParameters( admin, user, request, xslParams, unitKey, -1 );

            if ( formItems.containsKey( "subop" ) )
            {
                xslParams.put( "subop", formItems.getString( "subop" ) );
            }

            transformXML( request, response, newDoc, "editor/" + "imagepopup_selected.xsl", xslParams );
        }
        else
        {
            super.handlerCustom( request, response, session, admin, formItems, operation, parameters, user, verticalDoc );
        }
    }

    public void handlerWizard( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, ExtendedMap parameters, User user, String wizardName )
        throws VerticalAdminException, VerticalEngineException, TransformerException, IOException
    {
        if ( "import".equals( wizardName ) )
        {
            Wizard importImagesWizard = Wizard.getInstance( admin, applicationContext, this, session, formItems, WIZARD_IMPORT_IMAGES );
            importImagesWizard.processRequest( request, response, session, admin, formItems, parameters, user );
        }
        else
        {
            super.handlerWizard( request, response, session, admin, formItems, parameters, user, wizardName );
        }
    }
}
