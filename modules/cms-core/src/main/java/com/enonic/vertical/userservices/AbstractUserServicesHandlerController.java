/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.userservices;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.enonic.cms.core.SiteContext;
import com.enonic.cms.core.SitePathResolver;
import com.enonic.cms.core.content.ContentParserService;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.mail.SendMailService;
import com.enonic.cms.core.portal.SiteRedirectHelper;
import com.enonic.cms.core.portal.cache.SiteCachesService;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.structure.SiteService;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.SiteDao;
import com.enonic.vertical.VerticalProperties;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.containers.MultiValueMap;
import com.enonic.vertical.adminweb.VerticalAdminLogger;
import com.enonic.vertical.engine.VerticalCreateException;
import com.enonic.vertical.engine.VerticalEngineException;
import com.enonic.vertical.engine.VerticalRemoveException;
import com.enonic.vertical.engine.VerticalSecurityException;

import com.enonic.cms.framework.util.UrlPathDecoder;

import com.enonic.cms.core.captcha.CaptchaService;
import com.enonic.cms.core.service.UserServicesService;

import com.enonic.cms.core.security.UserStoreParser;

import com.enonic.cms.core.Attribute;
import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.content.ContentAccessException;
import com.enonic.cms.core.content.category.CategoryAccessException;
import com.enonic.cms.core.portal.VerticalSession;
import com.enonic.cms.core.portal.httpservices.UserServicesException;
import org.springframework.web.servlet.mvc.Controller;

public abstract class AbstractUserServicesHandlerController
    implements Controller
{
    // fatal errors

    public final static int ERR_OPERATION_BACKEND = 504;

    public final static int ERR_OPERATION_HANDLER = 505;

    public final static int ERR_SECURITY_EXCEPTION = 506;

    // general errors

    public final static int ERR_PARAMETERS_MISSING = 400;

    public final static int ERR_PARAMETERS_INVALID = 401;

    public final static int ERR_EMAIL_SEND_FAILED = 402;

    public final static int ERR_INVALID_CAPTCHA = 405;

    protected static DateFormat dateFormatFrom = new SimpleDateFormat( "dd.MM.yyyy" );

    private final FileUploadBase fileUpload;

    protected CaptchaService captchaService;

    private UserServicesRedirectUrlResolver userServicesRedirectUrlResolver;

    private UserServicesAccessManager userServicesAccessManager;

    private SiteService siteService;

    private SitePathResolver sitePathResolver;

    protected VerticalProperties verticalProperties;

    private SiteRedirectHelper siteRedirectHelper;

    protected SiteDao siteDao;

    protected CategoryDao categoryDao;

    protected ContentDao contentDao;

    protected SecurityService securityService;

    protected UserStoreService userStoreService;

    protected SendMailService sendMailService;

    protected ContentParserService contentParserService;

    protected ContentService contentService;

    protected SiteCachesService siteCachesService;

    private UserServicesService userServicesService;

    protected UserStoreParser userStoreParser;

    public AbstractUserServicesHandlerController()
    {
        fileUpload = new DiskFileUpload();
        fileUpload.setHeaderEncoding( "UTF-8" );
    }

    public void setUserStoreParser( UserStoreParser userStoreParser )
    {
        this.userStoreParser = userStoreParser;
    }

    public void setUserServicesRedirectHelper( UserServicesRedirectUrlResolver value )
    {
        this.userServicesRedirectUrlResolver = value;
    }

    public void setCaptchaService( CaptchaService service )
    {
        captchaService = service;
    }

    public void setUserServicesService( UserServicesService userServicesService )
    {
        this.userServicesService = userServicesService;
    }

    public void setSiteCachesService( SiteCachesService value )
    {
        this.siteCachesService = value;
    }

    public void setContentDao( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    public void setContentParserService( ContentParserService contentParserService )
    {
        this.contentParserService = contentParserService;
    }

    public void setContentService( ContentService contentService )
    {
        this.contentService = contentService;
    }

    public void setSiteDao( SiteDao siteDao )
    {
        this.siteDao = siteDao;
    }

    public void setSiteRedirectHelper( SiteRedirectHelper value )
    {
        this.siteRedirectHelper = value;
    }

    public void setVerticalProperties( VerticalProperties value )
    {
        this.verticalProperties = value;
    }

    public void setSiteService( SiteService value )
    {
        this.siteService = value;
    }

    public void setSitePathResolver( SitePathResolver value )
    {
        this.sitePathResolver = value;
    }

    public void setCategoryDao( CategoryDao categoryDao )
    {
        this.categoryDao = categoryDao;
    }

    public void setSecurityService( SecurityService value )
    {
        this.securityService = value;
    }

    public void setUserStoreService( UserStoreService userStoreService )
    {
        this.userStoreService = userStoreService;
    }

    public void setSendMailService( SendMailService sendMailService )
    {
        this.sendMailService = sendMailService;
    }

    protected void handlerCreate( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey )
        throws VerticalUserServicesException, VerticalCreateException, VerticalSecurityException, RemoteException
    {
        String message = "OperationWrapper CREATE not implemented.";
        VerticalUserServicesLogger.error(message, null );
    }

    protected void handlerRemove( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey )
        throws VerticalUserServicesException, VerticalRemoveException, VerticalSecurityException, RemoteException
    {
        String message = "OperationWrapper REMOVE not implemented.";
        VerticalUserServicesLogger.error(message, null );
    }

    protected void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey, String operation )
        throws VerticalUserServicesException, VerticalEngineException, IOException, ClassNotFoundException, IllegalAccessException,
        InstantiationException, ParseException
    {
        String message = "Custom operation not implemented: {0}";
        if ( operation != null )
        {
            operation = operation.toUpperCase();
        }
        VerticalUserServicesLogger.error(message, operation, null );
    }

    protected void handlerUpdate( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey )
    {
        String message = "OperationWrapper UPDATE not implemented.";
        VerticalUserServicesLogger.error(message, null );
    }

    private UserServicesService lookupUserServices()
    {
        return userServicesService;
    }

    public boolean isArrayFormItem( Map formItems, String string )
    {
        if ( !formItems.containsKey( string ) )
        {
            return false;
        }

        return formItems.get( string ).getClass() == String[].class;
    }

    private ExtendedMap parseSimpleRequest( HttpServletRequest request )
    {

        ExtendedMap formItems = new ExtendedMap( true );
        Enumeration paramNames = request.getParameterNames();

        while ( paramNames.hasMoreElements() )
        {
            String key = paramNames.nextElement().toString();
            String[] values = request.getParameterValues( key );

            if ( values != null )
            {
                if ( values.length == 1 && values[0] != null )
                {
                    String value = values[0];
                    if ( "true".equals( value ) )
                    {
                        formItems.putBoolean( key, true );
                    }
                    else if ( "false".equals( value ) )
                    {
                        formItems.putBoolean( key, false );
                    }
                    else
                    {
                        formItems.putString( key, value );
                    }
                }
                else if ( values.length > 1 )
                {
                    formItems.put( key, values );
                }
            }
            else
            {
                formItems.put( key, "" );
            }
        }

        return formItems;
    }

    private ExtendedMap parseMultiPartRequest( HttpServletRequest request )
    {
        ExtendedMap formItems = new ExtendedMap( true );
        try
        {
            List paramList = fileUpload.parseRequest( request );
            for ( Iterator iter = paramList.iterator(); iter.hasNext(); )
            {
                FileItem fileItem = (FileItem) iter.next();

                String name = fileItem.getFieldName();

                if ( fileItem.isFormField() )
                {
                    String value = fileItem.getString( "UTF-8" );
                    if ( formItems.containsKey( name ) )
                    {
                        ArrayList<Object> values = new ArrayList<Object>();
                        Object obj = formItems.get( name );
                        if ( obj instanceof Object[] )
                        {
                            String[] objArray = (String[]) obj;
                            for ( int i = 0; i < objArray.length; i++ )
                            {
                                values.add( objArray[i] );
                            }
                        }
                        else
                        {
                            values.add( obj );
                        }
                        values.add( value );
                        formItems.put( name, values.toArray( new String[values.size()] ) );
                    }
                    else
                    {
                        formItems.put( name, value );
                    }
                }
                else
                {
                    if ( fileItem.getSize() > 0 )
                    {
                        if ( formItems.containsKey( name ) )
                        {
                            ArrayList<Object> values = new ArrayList<Object>();
                            Object obj = formItems.get( name );
                            if ( obj instanceof FileItem[] )
                            {
                                FileItem[] objArray = (FileItem[]) obj;
                                for ( int i = 0; i < objArray.length; i++ )
                                {
                                    values.add( objArray[i] );
                                }
                            }
                            else
                            {
                                values.add( obj );
                            }
                            values.add( fileItem );
                            formItems.put( name, values.toArray( new FileItem[values.size()] ) );
                        }
                        else
                        {
                            formItems.put( name, fileItem );
                        }
                    }
                }
            }
        }
        catch ( FileUploadException fue )
        {
            String message = "Error occured with file upload: %t";
            VerticalAdminLogger.error(message, fue );
        }
        catch ( UnsupportedEncodingException uee )
        {
            String message = "Character encoding not supported: %t";
            VerticalAdminLogger.error(message, uee );
        }

        // Add parameters from url
        Map paramMap = request.getParameterMap();
        for ( Iterator iter = paramMap.entrySet().iterator(); iter.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            if ( entry.getValue() instanceof String[] )
            {
                String[] values = (String[]) entry.getValue();
                for ( int i = 0; i < values.length; i++ )
                {
                    formItems.put( key, values[i] );
                }
            }
            else
            {
                formItems.put( key, entry.getValue() );
            }
        }

        return formItems;
    }

    private ExtendedMap parseForm( HttpServletRequest request )
    {
        if ( FileUpload.isMultipartContent( request ) )
        {
            return parseMultiPartRequest( request );
        }
        else
        {
            return parseSimpleRequest( request );
        }
    }

    /**
     * Process incoming HTTP requests.
     */
    private ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response, SitePath sitePath )
        throws IOException
    {

        HttpSession session = request.getSession( true );
        ExtendedMap formItems = parseForm( request );
        UserServicesService userServices = lookupUserServices();
        SiteKey siteKey = sitePath.getSiteKey();

        SitePath originalSitePath = (SitePath) request.getAttribute( Attribute.ORIGINAL_SITEPATH );
        String handler = UserServicesParameterResolver.resolveHandlerFromSitePath( originalSitePath );
        String operation = UserServicesParameterResolver.resolveOperationFromSitePath( originalSitePath );

        if ( !userServicesAccessManager.isOperationAllowed( siteKey, handler, operation ) )
        {
            String message = "Access to http service '" + handler + "." + operation + "' on site " + siteKey +
                " is not allowed by configuration. Check the settings in site-" + siteKey + ".properties";
            VerticalUserServicesLogger.warn(message, null );
            String httpErrorMsg = "Access denied to http service '" + handler + "." + operation + "' on site " + siteKey;
            response.sendError( HttpServletResponse.SC_FORBIDDEN, httpErrorMsg );
            return null;
        }

        try
        {
            if ( !( this instanceof FormHandlerController ) )
            {
                // Note: The FormHandlerController is doing its own validation.
                Boolean captchaOk = captchaService.validateCaptcha( formItems, request, handler, operation );
                if ( ( captchaOk != null ) && ( !captchaOk ) )
                {
                    VerticalSession vsession = (VerticalSession) session.getAttribute( VerticalSession.VERTICAL_SESSION_OBJECT );
                    if ( vsession == null )
                    {
                        vsession = new VerticalSession();
                        session.setAttribute( VerticalSession.VERTICAL_SESSION_OBJECT, vsession );
                    }
                    vsession.setAttribute( "error_" + handler + "_" + operation,
                                           captchaService.buildErrorXMLForSessionContext( formItems ).getAsDOMDocument() );
                    redirectToErrorPage( request, response, formItems, ERR_INVALID_CAPTCHA, null );
                    return null;
                }
            }

            if ( "create".equals( operation ) )
            {
                handlerCreate( request, response, session, formItems, userServices, siteKey );
            }
            else if ( "update".equals( operation ) )
            {
                handlerUpdate( request, response, session, formItems, userServices, siteKey );
            }
            else if ( "remove".equals( operation ) )
            {
                handlerRemove( request, response, session, formItems, userServices, siteKey );
            }
            else
            {
                handlerCustom( request, response, session, formItems, userServices, siteKey, operation );
            }
        }
        catch ( VerticalSecurityException vse )
        {
            String message = "No rights to handle request: %t";
            VerticalUserServicesLogger.warn(message, vse );
            redirectToErrorPage( request, response, formItems, ERR_SECURITY_EXCEPTION, null );
        }
        catch ( ContentAccessException vse )
        {
            String message = "No rights to handle request: %t";
            VerticalUserServicesLogger.warn(message, vse );
            redirectToErrorPage( request, response, formItems, ERR_SECURITY_EXCEPTION, null );
        }
        catch ( CategoryAccessException vse )
        {
            String message = "No rights to handle request: %t";
            VerticalUserServicesLogger.warn(message, vse );
            redirectToErrorPage( request, response, formItems, ERR_SECURITY_EXCEPTION, null );
        }
        catch ( UserServicesException use )
        {
            throw use;
        }
        catch ( Exception e )
        {
            String message = "Failed to handle request: %t";
            VerticalUserServicesLogger.error(message, e );
            redirectToErrorPage( request, response, formItems, ERR_OPERATION_BACKEND, null );
        }
        return null;
    }

    protected void redirectToPage( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems )
    {
        redirectToPage( request, response, formItems, null );
    }

    protected void redirectToPage( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems,
                                   MultiValueMap queryParams )
    {
        String redirect = formItems.getString( "_redirect", null );

        String url = userServicesRedirectUrlResolver.resolveRedirectUrlToPage( request, redirect, queryParams );

        if ( isAbsoluteUrl( url ) )
        {
            siteRedirectHelper.sendRedirectWithAbsoluteURL( response, url );
        }
        else
        {
            String decodedUrl = UrlPathDecoder.decode( url );
            siteRedirectHelper.sendRedirectWithPath( request, response, decodedUrl );
        }
    }

    private boolean isAbsoluteUrl( String url )
    {
        return url.matches( "^[a-z]{3,6}://.+" );
    }

    protected void redirectToErrorPage( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems, int code,
                                        MultiValueMap queryParams )
    {
        redirectToErrorPage(request, response, formItems, new int[]{code}, queryParams);
    }

    protected void redirectToErrorPage( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems, int[] codes,
                                        MultiValueMap queryParams )
    {
        String url = userServicesRedirectUrlResolver.resolveRedirectUrlToErrorPage( request, formItems, codes, queryParams );
        siteRedirectHelper.sendRedirect(request, response, url);
    }

    protected static String createMissingParametersMessage( String operation, List<String> missingParameters )
    {
        StringBuffer message = new StringBuffer();
        message.append( operation ).append( " : Missing " ).append( missingParameters.size() ).append( " parameters: " );

        boolean isFirst = true;

        for ( String missingParameter : missingParameters )
        {
            if ( isFirst )
            {
                isFirst = false;
            }
            else
            {
                message.append( ", " );
            }
            message.append( missingParameter );
        }
        return message.toString();
    }

    protected static List<String> findMissingRequiredParameters( String[] requiredParameters, ExtendedMap formItems, boolean allowEmpty )
    {
        List<String> missingParameters = new ArrayList<String>();

        for ( String requiredParameter : requiredParameters )
        {
            if ( !formItems.containsKey( requiredParameter ) )
            {
                missingParameters.add( requiredParameter );
                continue;
            }

            String submittedValue = formItems.getString( requiredParameter );

            if ( StringUtils.isEmpty( submittedValue ) && !allowEmpty )
            {
                missingParameters.add( requiredParameter );
            }
        }

        return missingParameters;
    }

    public void setUserServicesAccessManager( UserServicesAccessManager userServicesAccessManager )
    {
        this.userServicesAccessManager = userServicesAccessManager;
    }

    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {

        // Get check and eventually set original sitePath
        SitePath originalSitePath = (SitePath) request.getAttribute( Attribute.ORIGINAL_SITEPATH );
        if ( originalSitePath == null )
        {
            originalSitePath = sitePathResolver.resolveSitePath( request );
            request.setAttribute( Attribute.ORIGINAL_SITEPATH, originalSitePath );
        }

        // Get and set the current sitePath
        SitePath currentSitePath = sitePathResolver.resolveSitePath( request );
        request.setAttribute( Attribute.CURRENT_SITEPATH, currentSitePath );

        return handleRequestInternal( request, response, currentSitePath );
    }

    protected SiteContext getSiteContext( SiteKey siteKey )
    {
        return siteService.getSiteContext( siteKey );
    }

    protected SitePath getSitePath( HttpServletRequest request )
    {
        SitePath sitePath = (SitePath) request.getAttribute( Attribute.ORIGINAL_SITEPATH );
        if ( sitePath == null )
        {
            sitePath = sitePathResolver.resolveSitePath( request );
        }
        return sitePath;
    }
}
