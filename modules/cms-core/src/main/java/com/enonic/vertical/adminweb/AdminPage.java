/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.IOException;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.net.URLUtil;
import com.enonic.vertical.engine.VerticalSecurityException;

import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.service.AdminService;

import com.enonic.cms.core.security.SecurityHolderAdmin;

import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserSpecification;

/**
 * Routes a request for a page in the administration web to the correct servlet.
 */
public class AdminPage
    extends AbstractAdminwebServlet
{

    private static final Logger LOG = LoggerFactory.getLogger( AdminPage.class );

    public void doGet( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {
        performTask( request, response );
    }

    public void doPost( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {
        performTask( request, response );
    }

    public void performTask( HttpServletRequest request, HttpServletResponse response )
    {
        // Dirty hack so that it can respond to /admin not only /admin/.
        if ( ( request.getPathInfo() == null ) || request.getPathInfo().endsWith( "/admin" ) )
        {
            try
            {
                redirectClientToAdminPath( "adminpage", request, response );
                return;
            }
            catch ( VerticalAdminException vae )
            {
                String message = "Failed to redirect to admin page: %t";
                VerticalAdminLogger.errorAdmin(message, vae);
            }
        }

        HttpSession session = request.getSession( false );

        // lookup admin bean
        AdminService admin = lookupAdminBean();

        User user = null;
        if ( session != null )
        {
            user = securityService.getLoggedInAdminConsoleUser();
        }

        if ( user == null )
        {
            String remoteUserUID = request.getRemoteUser();
            if ( remoteUserUID != null )
            {
                try
                {
                    UserStoreKey userStoreKey = userStoreService.getDefaultUserStore().getKey();
                    UserSpecification remoteUserSpec = new UserSpecification();
                    remoteUserSpec.setDeletedStateNotDeleted();
                    remoteUserSpec.setUserStoreKey( userStoreKey );
                    remoteUserSpec.setName( remoteUserUID );
                    User remoteUser = userDao.findSingleBySpecification( remoteUserSpec );
                    if ( remoteUser != null )
                    {
                        if ( adminConsoleLoginAccessResolver.hasAccess( securityService.getUser( remoteUser ) ) )
                        {
                            if ( session == null )
                            {
                                session = request.getSession( true );
                            }

                            user = remoteUser;
                            SecurityHolderAdmin.setUser( user.getKey() );
                            String message = "Logged in remote user {0} automatically";
                            VerticalAdminLogger.info(message, remoteUserUID, null );
                        }
                        else
                        {
                            String message = "User {0} is not authorized to use administration console.";
                            VerticalAdminLogger.error(message, remoteUserUID, null );
                        }
                    }
                    else
                    {
                        String message = "Failed to log in remote user with uid {0}";
                        VerticalAdminLogger.error(message, remoteUserUID, null );
                    }
                }
                catch ( VerticalSecurityException vse )
                {
                    String message = "Failed to log in remote user with uid {0}: %t";
                    VerticalAdminLogger.error(message, remoteUserUID, vse );
                }
            }
        }

        if ( user == null )
        {
            // not logged in, redirect to login
            try
            {
                // ren: VS-1970
                Map queryValues = URLUtil.decodeParameterMap( request.getParameterMap() );
                ExtendedMap params = new ExtendedMap( queryValues );
                int editContent = params.getInt( "editContent", -1 );
                ExtendedMap editContentParam = new ExtendedMap();
                if ( editContent > -1 )
                {
                    editContentParam.putInt( "editContent", editContent );
                }
                redirectClientToAdminPath( "login", editContentParam, request, response );
                // end: VS-1970

                //redirectClientToAdminPath( "login", request, response );
            }
            catch ( VerticalAdminException vae )
            {
                String message = "Failed to redirect to login page: %t";
                VerticalAdminLogger.errorAdmin(message, vae);
            }
        }
        else
        {

            String pageStr = null;
            String enctype = request.getContentType();
            if ( enctype != null && enctype.startsWith( "multipart/form-data" ) )
            {
                // Handle multipart forms
                try
                {
                    Map queryValues = URLUtil.decodeParameterMap( request.getParameterMap() );
                    if ( queryValues.containsKey( "page" ) )
                    {
                        pageStr = ( (String[]) queryValues.get( "page" ) )[0];
                    }
                    else
                    {
                        pageStr = null;
                    }
                }
                catch ( IllegalArgumentException iae )
                {
                    String message = "Failed to parse multi-part request";
                    VerticalAdminLogger.errorAdmin(message, iae);
                }
            }
            else
            {
                pageStr = request.getParameter( "page" );
            }

            int page = -1;
            try
            {
                if ( pageStr != null )
                {
                    page = Integer.parseInt( pageStr );
                }
                else
                {
                    page = 0;
                }
            }
            catch ( NumberFormatException nfe )
            {
                String message = "Failed to parse page number: {0}";
                VerticalAdminLogger.error(message, pageStr, nfe );
                ErrorPageServlet.Error error = new ErrorPageServlet.ThrowableError( nfe );
                session.setAttribute( "com.enonic.vertical.error", error );
                try
                {
                    redirectClientToAdminPath( "errorpage", request, response );
                }
                catch ( VerticalAdminException vae )
                {
                    message = "Failed to redirect to error page: %t";
                    VerticalAdminLogger.errorAdmin(message, vae);
                }
            }

            if ( "true".equals( request.getParameter( "waitscreen" ) ) )
            {
                page = 5;
            }

            if ( page == 993 )
            {
                int contentKey = -1;

                String contentKeyStr = request.getParameter( "key" );
                if ( contentKeyStr != null )
                {
                    contentKey = Integer.parseInt( contentKeyStr );
                }

                if ( contentKey == -1 )
                {
                    String versionKeyStr = request.getParameter( "versionkey" );
                    if ( versionKeyStr != null )
                    {
                        int versionKey = Integer.parseInt( versionKeyStr );
                        contentKey = admin.getContentKeyByVersionKey( versionKey );
                    }
                }

                if ( contentKey != -1 )
                {
                    int contentTypeKey = admin.getContentTypeKey( contentKey );
                    page = contentTypeKey + 999;
                }
            }

            String servlet = null;
            switch ( page )
            {

                // Framework
                case 0:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.FramesetServlet";
                    break;
                case 1:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.NavigatorServlet";
                    break;
                case 2:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.MainMenuServlet";
                    break;
                case 3:
                    servlet = "/admin/logout";
                    break;
                case 4:
                    servlet = "/admin/login";
                    break;
                case 5:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.SplashServlet";
                    break;
                case 10:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.SystemHandlerServlet";
                    break;
                case 50:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.AdminFrontPageServlet";
                    break;
                case 200:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.CategoryHandlerServlet";
                    break;
                case 275:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.ObjectClassHandlerServlet";
                    break;
                case 280:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.LDAPServerHandlerServlet";
                    break;
                case 290:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.UserStoreHandlerServlet";
                    break;
                case 350:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.LogHandlerServlet";
                    break;
                case 360:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.LanguageHandlerServlet";
                    break;
                case 400:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.ContentTypeHandlerServlet";
                    break;
                case 500:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.PageHandlerServlet";
                    break;
                case 510:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.handlers.PagelinkHandlerServlet";
                    break;
                case 550:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.PageTemplateHandlerServlet";
                    break;
                case 600:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.ArchiveHandlerServlet";
                    break;
                case 700:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.UserHandlerServlet";
                    break;
                case 701:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.GroupHandlerServlet";
                    break;
                case 800:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.ResourceHandlerServlet";
                    break;
                case 850:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.MenuHandlerServlet";
                    break;
                case 851:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.PresentationLayerServlet";
                    break;
                case 855:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.ContentTemplateHandlerServlet";
                    break;
                case 900:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.ContentObjectHandlerServlet";
                    break;
                case 910:
                    servlet = "/admin/servlet/tools/com.enonic.cms.core.tools.PluginInfoController";
                    break;
                case 912:
                    servlet = "/admin/servlet/tools/com.enonic.cms.core.tools.LivePortalTraceController";
                    break;
                  case 916:
                    servlet = "/admin/servlet/tools/com.enonic.cms.core.tools.ReindexContentToolController";
                    break;
                case 950:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.SectionHandlerServlet";
                    break;
                case 960:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.MyPageServlet";
                    break;
                case 990:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.DatabaseServlet";
                    break;
                case 991:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.handlers.ContentBaseHandlerServlet";
                    break;

                // Try-out: A fixed way of reaching the image handler servlet. Needed by html-editor edit-image functionality.
                case 992:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.handlers.ContentEnhancedImageHandlerServlet";
                    break;

                // Try-out: A fixed way of (dirty hack) for reaching simple content handler servlet?
                case 994:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.handlers.SimpleContentHandlerServlet";
                    break;

                case 1048:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.EditorHandlerServlet";
                    break;
                case 1050:
                    servlet = "/admin/servlet/com.enonic.vertical.adminweb.ContentHandlerHandlerServlet";
                    break;
                case 1060:
                    servlet = "/admin/servlet/tools/com.enonic.vertical.adminweb.ContentHandlerHandlerServlet";
                    break;
            }

            try
            {
                if ( servlet == null )
                {
                    // Find the right handler
                    String handlerClass = admin.getContentHandlerClassForContentType( page - 999 );
                    ContentHandlerName handlerName = ContentHandlerName.parse( handlerClass );
                    if ( handlerName == null )
                    {
                        String message = "No handler set for content type.";
                        VerticalAdminLogger.errorAdmin(message, null );
                    }
                    servlet = "/admin/servlet/" + handlerClass;
                }

                forwardRequest( servlet, request, response );
            }
            catch ( Exception e )
            {
                String message = "Forward failed: {0} %t";
                VerticalAdminLogger.error(message, servlet, e );
                ErrorPageServlet.Error error = new ErrorPageServlet.ThrowableError( e );
                session.setAttribute( "com.enonic.vertical.error", error );
                try
                {
                    redirectClientToAdminPath( "errorpage", request, response );
                }
                catch ( VerticalAdminException vae )
                {
                    message = "Failed to redirect to error page: %t";
                    VerticalAdminLogger.errorAdmin(message, vae);
                }
            }
        }
    }

    protected void forwardRequest( String servletPath, HttpServletRequest request, HttpServletResponse response )
    {
        try
        {
            RequestDispatcher dispatcher = request.getRequestDispatcher( servletPath );
            dispatcher.forward( request, response );
        }
        catch ( IOException ioe )
        {
        }
        catch ( ServletException se )
        {
            // Do nothing
            LOG.warn( "Unable to forward ", se );
        }
    }
}