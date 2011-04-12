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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.enonic.cms.core.content.category.CategoryAccessException;
import com.enonic.cms.portal.VerticalSession;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import com.octo.captcha.service.CaptchaServiceException;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.util.ArrayUtil;
import com.enonic.esl.util.RegexpUtil;
import com.enonic.esl.util.StringUtil;
import com.enonic.vertical.engine.VerticalCreateException;
import com.enonic.vertical.engine.VerticalEngineException;
import com.enonic.vertical.engine.VerticalRemoveException;
import com.enonic.vertical.engine.VerticalSecurityException;
import com.enonic.vertical.engine.VerticalUpdateException;

import com.enonic.cms.framework.util.UrlPathDecoder;

import com.enonic.cms.core.internal.service.CmsCoreServicesSpringManagedBeansBridge;
import com.enonic.cms.core.service.UserServicesService;

import com.enonic.cms.core.captcha.CaptchaService;
import com.enonic.cms.core.security.UserStoreParser;

import com.enonic.cms.domain.Attribute;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.SitePath;
import com.enonic.cms.core.content.ContentAccessException;
import com.enonic.cms.portal.httpservices.UserServicesException;

public class AbstractUserServicesHandlerController
    extends AbstractPresentationController
{
    private static final Logger LOG = LoggerFactory.getLogger( AbstractUserServicesHandlerController.class.getName() );

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

    protected static DateFormat isoDateFormatNoTime = new SimpleDateFormat( "yyyy-MM-dd" );

    private static final FileUploadBase fileUpload;

    protected CaptchaService captchaService;

    private UserServicesRedirectUrlResolver userServicesRedirectUrlResolver;

    private UserServicesAccessManager userServicesAccessManager;

    static
    {
        fileUpload = new DiskFileUpload();
        fileUpload.setHeaderEncoding( "UTF-8" );
    }

    protected UserStoreParser userStoreParser;

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

    protected void handlerCreate( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey )
        throws VerticalUserServicesException, VerticalCreateException, VerticalSecurityException, RemoteException
    {
        LOG.error( StringUtil.expandString( "OperationWrapper CREATE not implemented.", (Object) null, null ) );
    }

    protected void handlerRemove( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey )
        throws VerticalUserServicesException, VerticalRemoveException, VerticalSecurityException, RemoteException
    {
        LOG.error( StringUtil.expandString( "OperationWrapper REMOVE not implemented.", (Object) null, null ) );
    }

    protected void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey, String operation )
        throws VerticalUserServicesException, VerticalEngineException, IOException, ClassNotFoundException, IllegalAccessException,
        InstantiationException, ParseException
    {
        String message = "Custom operation not implemented: ";
        if ( operation != null )
        {
            operation = operation.toUpperCase();
        }
        LOG.error( StringUtil.expandString( message + operation, (Object) null, null ) );
    }

    protected void handlerUpdate( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey )
        throws VerticalUserServicesException, VerticalUpdateException, VerticalSecurityException, RemoteException
    {
        String message = "OperationWrapper UPDATE not implemented.";
        LOG.error( StringUtil.expandString( message, (Object) null, null ) );
    }

    protected UserServicesService lookupUserServices()
    {
        return CmsCoreServicesSpringManagedBeansBridge.getUserServicesService();
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
            LOG.error( StringUtil.expandString( message, (Object) null, fue ), fue );
        }
        catch ( UnsupportedEncodingException uee )
        {
            String message = "Character encoding not supported: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, uee ), uee );
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

    protected ExtendedMap parseForm( HttpServletRequest request )
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
    public ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response, SitePath sitePath )
        throws ServletException, IOException
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
            LOG.warn( StringUtil.expandString( message, null, null ) );
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
        catch ( CaptchaServiceException e )
        {
            String message = "Failed during captcha validation: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, e ), e );
            redirectToErrorPage( request, response, formItems, ERR_OPERATION_BACKEND, null );
        }
        catch ( VerticalUserServicesException vuse )
        {
            String message = "Failed to handle request: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, vuse ), vuse );
            redirectToErrorPage( request, response, formItems, ERR_OPERATION_HANDLER, null );
        }
        catch ( VerticalUpdateException vue )
        {
            String message = "Failed to handle update request: %t";
            LOG.warn( StringUtil.expandString( message, null, vue ), vue );
            redirectToErrorPage( request, response, formItems, ERR_OPERATION_BACKEND, null );
        }
        catch ( VerticalRemoveException vre )
        {
            String message = "Failed to handle remove request: %t";
            LOG.warn( StringUtil.expandString( message, null, vre ), vre );
            redirectToErrorPage( request, response, formItems, ERR_OPERATION_BACKEND, null );
        }
        catch ( VerticalSecurityException vse )
        {
            String message = "No rights to handle request: %t";
            LOG.warn( StringUtil.expandString( message, null, vse ), vse );
            redirectToErrorPage( request, response, formItems, ERR_SECURITY_EXCEPTION, null );
        }
        catch ( ContentAccessException vse )
        {
            String message = "No rights to handle request: %t";
            LOG.warn( StringUtil.expandString( message, null, vse ), vse );
            redirectToErrorPage( request, response, formItems, ERR_SECURITY_EXCEPTION, null );
        }
        catch ( CategoryAccessException vse )
        {
            String message = "No rights to handle request: %t";
            LOG.warn( StringUtil.expandString( message, null, vse ), vse );
            redirectToErrorPage( request, response, formItems, ERR_SECURITY_EXCEPTION, null );
        }
        catch ( VerticalEngineException vee )
        {
            String message = "Failed to handle engine request: %t";
            LOG.warn( StringUtil.expandString( message, null, vee ), vee );
            redirectToErrorPage( request, response, formItems, ERR_OPERATION_BACKEND, null );
        }
        catch ( UserServicesException use )
        {
            throw use;
        }
        catch ( Exception e )
        {
            String message = "Failed to handle request: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, e ), e );
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
        redirectToErrorPage( request, response, formItems, new int[]{code}, queryParams );
    }

    protected void redirectToErrorPage( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems, int[] codes,
                                        MultiValueMap queryParams )
    {
        String url = userServicesRedirectUrlResolver.resolveRedirectUrlToErrorPage( request, formItems, codes, queryParams );
        siteRedirectHelper.sendRedirect( request, response, url );
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

    protected static class Util
    {

        protected static String replaceKeys( ExtendedMap formItems, String inText, String[] excludeKeys )
        {

            String outText = inText;

            for ( Object o : formItems.keySet() )
            {
                String key = (String) o;

                Pattern p = Pattern.compile( ".*%" + key + "%.*", Pattern.DOTALL );
                Matcher m = p.matcher( outText );

                if ( ( excludeKeys == null || !ArrayUtil.arrayContains( key, excludeKeys ) ) && m.matches() &&
                    formItems.containsKey( key ) )
                {

                    String regexp = "%" + key + "%";
                    outText = RegexpUtil.substituteAll( regexp, formItems.getString( key, "" ), outText );
                }
            }

            return outText;
        }

        protected static String removeTokens( String inText )
        {
            return inText.replaceAll( "%[^%]+%", "" );
        }

    }

}
