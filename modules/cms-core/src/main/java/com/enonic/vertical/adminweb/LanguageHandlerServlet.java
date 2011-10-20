/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.transform.JDOMSource;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.containers.MultiValueMap;
import com.enonic.vertical.engine.VerticalEngineException;

import com.enonic.cms.core.LanguageKey;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.service.AdminService;

public class LanguageHandlerServlet
    extends AdminHandlerBaseServlet
{

    public void handlerBrowse( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        Document languagesDoc = admin.getLanguages().getAsJDOMDocument();

        JDOMSource xmlSource = new JDOMSource( languagesDoc );

        // StyleSheet
        Source xslSource = AdminStore.getStylesheet( session, "language_browse.xsl" );

        // Parameters
        ExtendedMap parameters = new ExtendedMap();
        parameters.put( "page", String.valueOf( request.getParameter( "page" ).toString() ) );
        parameters.put( "administrator", String.valueOf( user.isEnterpriseAdmin() ) );

        addSortParamteres( "@languagecode", "ascending", formItems, session, parameters );

        try
        {
            transformXML( session, response.getWriter(), xmlSource, xslSource, parameters );
        }
        catch ( Exception e )
        {
            String message = "Failed to transform & print output: %t";
            VerticalAdminLogger.errorAdmin(message, e );
        }
    }

    public void handlerCreate( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        String languageCode = formItems.getString( "languagecode" );
        String description = formItems.getString( "description" );
        admin.createLanguage( user, languageCode, description );

        MultiValueMap queryParams = new MultiValueMap();
        queryParams.put( "page", formItems.get( "page" ) );
        queryParams.put( "op", "browse" );
        redirectClientToAdminPath( "adminpage", queryParams, request, response );
    }

    public void handlerForm( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems )
        throws VerticalAdminException
    {

        // XSL paramters
        HashMap parameters = new HashMap();
        parameters.put( "page", formItems.get( "page" ) );

        int languageKeyInt = formItems.getInt( "key", -1 );
        Document languagesDoc = null;
        if ( languageKeyInt >= 0 )
        {
            // update
            parameters.put( "create", "0" );
            languagesDoc = admin.getLanguage(new LanguageKey(languageKeyInt)).getAsJDOMDocument();
        }
        else
        {
            // create
            parameters.put( "create", "1" );
            languagesDoc = new Document(new Element("languages"));
        }
        JDOMSource xmlSource = new JDOMSource( languagesDoc );

        // StyleSheet
        Source xslSource = AdminStore.getStylesheet( session, "language_form.xsl" );
        try
        {
            transformXML( session, response.getWriter(), xmlSource, xslSource, parameters );
        }
        catch ( IOException ioe )
        {
            String message = "Failed to get writer: %t";
            VerticalAdminLogger.errorAdmin(message, ioe );
        }
        catch ( TransformerException te )
        {
            String message = "Failed to transform: %t";
            VerticalAdminLogger.errorAdmin(message, te );
        }
    }

    public void handlerRemove( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, int key )
        throws VerticalAdminException, VerticalEngineException
    {

        admin.removeLanguage( new LanguageKey( key ) );
        redirectClientToReferer( request, response );
    }

    public void handlerUpdate( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        int languageKeyInt = formItems.getInt( "key" );
        String languageCode = formItems.getString( "languagecode" );
        String description = formItems.getString( "description" );

        admin.updateLanguage( new LanguageKey( languageKeyInt ), languageCode, description );

        MultiValueMap queryParams = new MultiValueMap();
        queryParams.put( "page", formItems.get( "page" ) );
        queryParams.put( "op", "browse" );
        redirectClientToAdminPath( "adminpage", queryParams, request, response );
    }
}
