/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.servlet.http.CookieUtil;
import com.enonic.esl.util.StringUtil;

import com.enonic.cms.api.Version;
import com.enonic.cms.core.AdminConsoleTranslationService;
import com.enonic.cms.core.DeploymentPathResolver;
import com.enonic.cms.core.admin.AdminConsoleAccessDeniedException;
import com.enonic.cms.core.log.LogType;
import com.enonic.cms.core.log.StoreNewLogEntryCommand;
import com.enonic.cms.core.security.InvalidCredentialsException;
import com.enonic.cms.core.security.LoginAdminUserCommand;
import com.enonic.cms.core.security.PasswordGenerator;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreXmlCreator;

/**
 * Administration login servlet.
 */
public final class AdminLogInServlet
    extends AdminHandlerBaseServlet
{

    private static final int COOKIE_TIMEOUT = 60 * 60 * 24 * 365 * 50;   // 50 years

    private static final int SESSION_TIMEOUT_ERROR = 2 * 60;

    // error codes
    //  500_unexpected_error   : an unexpected error occurred during login
    //  401_missing_user_passwd: missing user id and/or password
    //  401_user_passwd_wrong  : user id and/or password is wrong for this domain
    //                           (or enterprise administrator user id and/or password is incorrect)
    //  401_access_denied      : user doesn't have access to the administration console

    private static final String EC_401_MISSING_USER_PASSWD = "401_missing_user_passwd";

    private static final String EC_401_USER_PASSWD_WRONG = "401_user_passwd_wrong";

    private static final String EC_401_ACCESS_DENIED = "401_access_denied";

    private static final String EC_400_MISSING_UID = "400_missing_uid";

    private static final Pattern PATTERN = Pattern.compile( "^.*editContent=([\\d]+).*$" );

    /**
     * @see com.enonic.vertical.adminweb.AdminHandlerBaseServlet#doGet(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public void doGet( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {

        try
        {
            HashMap<String, Object> parameters = new HashMap<String, Object>();
            ExtendedMap formItems = parseForm( request );
            response.setContentType( "text/html;charset=UTF-8" );

            // Make IE 9 behave like IE 8
            // http://msdn.microsoft.com/en-us/library/cc288325%28v=vs.85%29.aspx#Servers
            response.setHeader( "X-UA-Compatible", "IE=EmulateIE8" );

            if ( isRequestForAdminPath( "/login", request ) )
            {
                if ( formItems.getBoolean( "login", false ) )
                {
                    handlerLogin( request, response, formItems );
                }
                else
                {
                    HttpSession session = request.getSession( true );
                    org.jdom.Document doc = new org.jdom.Document( new org.jdom.Element( "data" ) );
                    handlerLoginForm( request, response, session, parameters, doc );
                }
            }
            else if ( isRequestForAdminPath( "/logout", request ) )
            {
                HttpSession session = request.getSession( false );
                handlerLogout( request, response, session );
            }
            else if ( isRequestForAdminPath( "/forgotpassword", request ) )
            {
                HttpSession session = request.getSession( true );
                org.jdom.Document doc = new org.jdom.Document( new org.jdom.Element( "data" ) );
                handlerForgotPasswordForm( request, response, session, parameters, doc );
            }
            else
            {
                super.doGet( request, response );
            }
        }
        catch ( Exception vae )
        {
            try
            {
                HttpSession session = request.getSession( true );
                ErrorPageServlet.Error error = new ErrorPageServlet.ThrowableError( vae );
                session.setAttribute( "com.enonic.vertical.error", error );
                redirectClientToAdminPath( "errorpage", (MultiValueMap) null, request, response );
            }
            catch ( VerticalAdminException vae2 )
            {
                String message = "Failed to redirect to error page: %t";
                VerticalAdminLogger.errorAdmin( message, vae2 );
            }
        }
    }

    /**
     * @see com.enonic.vertical.adminweb.AdminHandlerBaseServlet#doPost(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public void doPost( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {

        try
        {
            ExtendedMap formItems = parseForm( request );
            //ren: VS-1970
            int editContent = resolveEditContentParam( request.getHeader( "Referer" ) );
            if ( editContent > -1 )
            {
                formItems.put( "editContent", editContent );
            }
            //end: VS-1970

            if ( isRequestForAdminPath( "/login", request ) )
            {
                handlerLogin( request, response, formItems );
            }
            else if ( isRequestForAdminPath( "/forgotpassword", request ) )
            {
                handlerForgotPassword( request, response, formItems );
            }
            else
            {
                super.doPost( request, response );
            }
        }
        catch ( Exception e )
        {
            try
            {
                HttpSession session = request.getSession( true );
                ErrorPageServlet.Error error = new ErrorPageServlet.ThrowableError( e );
                session.setAttribute( "com.enonic.vertical.error", error );
                redirectClientToAdminPath( "errorpage", (MultiValueMap) null, request, response );
            }
            catch ( VerticalAdminException vae2 )
            {
                String message = "Failed to redirect to error page: %t";
                VerticalAdminLogger.errorAdmin( message, vae2 );
            }
        }
    }

    private int resolveEditContentParam( String referer )
    {

        int editContent = -1;
        Matcher matcher = PATTERN.matcher( referer );
        Boolean matches = matcher.matches();
        if ( matches )
        {
            return Integer.parseInt( matcher.group( 1 ) );
        }
        return editContent;
    }

    private void logLoginFailed( final QualifiedUsername user, final String remoteIp )
    {
        final StoreNewLogEntryCommand command = new StoreNewLogEntryCommand();

        command.setType( LogType.LOGIN_FAILED );
        command.setInetAddress( remoteIp );
        command.setTitle( user.getUsername() );
        command.setXmlData( createUserStoreDataDoc( user ) );
        command.setUser( this.securityService.getAnonymousUserKey() );

        this.logService.storeNew( command );
    }

    private void logLogin( final User user, final String remoteIp )
    {
        final StoreNewLogEntryCommand command = new StoreNewLogEntryCommand();
        command.setType( LogType.LOGIN );
        command.setInetAddress( remoteIp );
        command.setTitle( user.getDisplayName() + " (" + user.getName() + ")" );
        command.setXmlData( createUserStoreDataDoc( user.getQualifiedName() ) );
        command.setUser( user.getKey() );

        this.logService.storeNew( command );
    }

    private void logLogout( final User user, final String remoteIp )
    {
        final StoreNewLogEntryCommand command = new StoreNewLogEntryCommand();
        command.setType( LogType.LOGOUT );
        command.setInetAddress( remoteIp );
        command.setTitle( user.getDisplayName() + " (" + user.getName() + ")" );
        command.setUser( user.getKey() );

        this.logService.storeNew( command );
    }

    private Document createUserStoreDataDoc( final QualifiedUsername user )
    {
        final Element rootElem = new Element( "data" );

        if ( user.getUserStoreKey() != null )
        {
            final Element userStoreElem = new Element( "userstorekey" );
            userStoreElem.setAttribute( "key", user.getUserStoreKey().toString() );
            userStoreElem.setText( user.getUserStoreName() );
            rootElem.addContent( userStoreElem );
        }

        return new Document( rootElem );
    }

    private void handlerLoginForm( HttpServletRequest request, HttpServletResponse response, HttpSession session,
                                   HashMap<String, Object> parameters, org.jdom.Document doc )
        throws VerticalAdminException
    {
        final UserStoreXmlCreator xmlCreator = new UserStoreXmlCreator( userStoreService.getUserStoreConnectorConfigs() );
        List<UserStoreEntity> userStores = securityService.getUserStores();
        org.jdom.Document tempDoc = xmlCreator.createPagedDocument( userStores, 0, 100 );

        org.jdom.Element dataElem = doc.getRootElement();
        dataElem.addContent( tempDoc.getRootElement().detach() );

        // set correct language and get languages xml
        AdminConsoleTranslationService languageMap = AdminConsoleTranslationService.getInstance();
        String languageCode = request.getParameter( "lang" );
        Boolean cookieSet = false;
        if ( languageCode == null )
        {
            Cookie cookie = CookieUtil.getCookie( request, "languageCode" );
            if ( cookie == null )
            {
                languageCode = languageMap.getDefaultLanguageCode();
            }
            else
            {
                languageCode = cookie.getValue();
                cookieSet = true;
            }
        }

        languageMap.toDoc( doc, languageCode );
        session.setAttribute( "languageCode", languageCode );
        parameters.put( "languagecode", languageCode );

        if ( !cookieSet )
        {
            String deploymentPath = DeploymentPathResolver.getAdminDeploymentPath( request );
            CookieUtil.setCookie( response, "languageCode", languageCode, COOKIE_TIMEOUT, deploymentPath );
        }

        String userStoreKeyStr = request.getParameter( "userStorekey" );
        if ( userStoreKeyStr != null )
        {
            parameters.put( "userStorekey", userStoreKeyStr );
        }
        String username = request.getParameter( "username" );
        if ( username != null )
        {
            parameters.put( "username", username );
        }
        String password = request.getParameter( "password" );
        if ( password != null )
        {
            parameters.put( "password", password );
        }

        String errorCode = (String) session.getAttribute( "passworderrorcode" );
        if ( errorCode != null )
        {
            session.removeAttribute( "passworderrorcode" );
            session.removeAttribute( "passworderror" );
        }

        errorCode = (String) session.getAttribute( "loginerrorcode" );
        if ( errorCode != null )
        {
            parameters.put( "errorcode", errorCode );
            parameters.put( "errormessage", session.getAttribute( "loginerror" ) );
        }

        // version and copyright info
        parameters.put( "version", Version.getVersion() );
        parameters.put( "copyright", Version.getCopyright() );

        String selectedUserStore = (String) session.getAttribute( "selectedloginuserstore" );
        if ( StringUtils.isNotEmpty( selectedUserStore ) )
        {
            parameters.put( "selectedloginuserstore", selectedUserStore );
        }

        transformXML( request, response, doc, "login_form.xsl", parameters );
    }

    private void handlerLogin( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems )
        throws VerticalAdminException
    {
        String uid = formItems.getString( "username", null );
        String passwd = formItems.getString( "password", null );
        UserStoreKey userStoreKey;
        String userStoreKeyStr = formItems.getString( "userstorekey", null );

        if ( userStoreKeyStr != null )
        {
            userStoreKey = new UserStoreKey( userStoreKeyStr );
        }
        else
        {
            userStoreKey = userStoreService.getDefaultUserStore().getKey();
        }

        securityService.logoutAdminUser();
        HttpSession session = request.getSession( true );

        session.setAttribute( "selectedloginuserstore", userStoreKey.toString() );

        // language
        AdminConsoleTranslationService languageMap = AdminConsoleTranslationService.getInstance();
        String languageCode;
        Cookie cookie = CookieUtil.getCookie( request, "languageCode" );
        if ( cookie == null )
        {
            languageCode = languageMap.getDefaultLanguageCode();
        }
        else
        {
            languageCode = cookie.getValue();
        }
        session.setAttribute( "languageCode", languageCode );

        User user = null;
        String errorCode = null;
        QualifiedUsername qualifiedUsername = null;

        try
        {
            if ( uid == null || passwd == null )
            {
                String message = "User and/or password not set.";
                VerticalAdminLogger.error( message );
                session.setAttribute( "loginerrorcode", EC_401_MISSING_USER_PASSWD );
                session.setAttribute( "loginerror", message );
                session.setMaxInactiveInterval( SESSION_TIMEOUT_ERROR );
                errorCode = EC_401_MISSING_USER_PASSWD;
            }
            else
            {
                // authenticate user
                if ( UserEntity.isBuiltInUser( uid ) )
                {
                    qualifiedUsername = new QualifiedUsername( uid );
                }
                else
                {
                    qualifiedUsername = new QualifiedUsername( userStoreKey, uid );
                }
                user = securityService.loginAdminUser( new LoginAdminUserCommand( qualifiedUsername, passwd ) );
            }
        }
        catch ( InvalidCredentialsException vse )
        {
            String message = "Failed to authenticate user (domain key: {0}): {1}";
            Object[] msgData = {userStoreKey, uid};
            VerticalAdminLogger.warn( message, msgData );
            message = StringUtil.expandString( message, msgData, vse );
            session.setAttribute( "loginerrorcode", EC_401_USER_PASSWD_WRONG );
            session.setAttribute( "loginerror", message );
            session.setMaxInactiveInterval( SESSION_TIMEOUT_ERROR );
            errorCode = EC_401_USER_PASSWD_WRONG;
            String remoteAdr = request.getRemoteAddr();
            logLoginFailed( qualifiedUsername, remoteAdr );
        }
        catch ( AdminConsoleAccessDeniedException e )
        {
            String message = "User is not authorized to use administration console.";
            VerticalAdminLogger.error( message );
            session.setAttribute( "loginerrorcode", EC_401_ACCESS_DENIED );
            session.setAttribute( "loginerror", message );
            session.setMaxInactiveInterval( SESSION_TIMEOUT_ERROR );
            errorCode = EC_401_ACCESS_DENIED;
        }

        if ( errorCode != null )
        {
            if ( formItems.containsKey( "editContent" ) )
            {
                ExtendedMap parameters = new ExtendedMap();
                parameters.put( "editContent", formItems.getInt( "editContent" ) );
                redirectClientToAdminPath( "login", parameters, request, response );
                return;
            }
            redirectClientToAdminPath( "login", request, response );
            return;
        }

        // no errors occured during authentication and authorization of user

        String remoteAdr = request.getRemoteAddr();
        user.setSelectedLanguageCode( languageCode );

        try
        {

            logLogin( user, remoteAdr );

            // Reset some cookie data:
            String deploymentPath = DeploymentPathResolver.getAdminDeploymentPath( request );

            if ( userStoreKey != null )
            {
                CookieUtil.setCookie( response, user.getKey() + "userstorekey", userStoreKey.toString(), -1, deploymentPath );
            }

            CookieUtil.setCookie( response, user.getKey() + "selectedunitkey", "-1", -1, deploymentPath );
            // If the enterpriseadmin user did'nt select a domain,
            // show system tab page, else show domain tab page.
            Cookie tabPageCookie = CookieUtil.getCookie( request, user.getKey() + "mainmenu_selectedTabPage" );
            int tabPage = -1;
            if ( tabPageCookie != null )
            {
                tabPage = Integer.parseInt( tabPageCookie.getValue() );
            }

            CookieUtil.setCookie( response, user.getKey() + "mainmenu_selectedTabPage", String.valueOf( tabPage ), -1, deploymentPath );
            session.setAttribute( "selectedunitkey", "-1" );

            ExtendedMap parameters = new ExtendedMap();
            parameters.put( "page", "0" );
            if ( formItems.containsKey( "rightframe" ) )
            {
                parameters.put( "rightframe", formItems.getString( "rightframe" ) );
            }
            if ( formItems.containsKey( "referer" ) )
            {
                parameters.put( "referer", formItems.getString( "referer", "" ) );
            }

            //ren: VS-1970
            if ( formItems.containsKey( "editContent" ) )
            {
                parameters.put( "editContent", formItems.getInt( "editContent" ) );
            }
            //end: VS-1970
            session.removeAttribute( "loginerrorcode" );
            session.removeAttribute( "loginerror" );
            redirectClientToAdminPath( "adminpage", parameters, request, response );

        }
        catch ( VerticalAdminException vae )
        {
            String message = "Failed to redirect to admin page: %t";
            VerticalAdminLogger.errorAdmin( message, vae );
        }

    }

    private void handlerLogout( HttpServletRequest request, HttpServletResponse response, HttpSession session )
    {
        User user = securityService.getLoggedInAdminConsoleUser();

        if ( session != null && user != null )
        {
            String remoteAddr = request.getRemoteAddr();
            logLogout( user, remoteAddr );

            try
            {
                securityService.logoutAdminUser();
                redirectClientToAdminPath( "login", (MultiValueMap) null, request, response );
            }
            catch ( VerticalAdminException vae )
            {
                String page = "login page";
                String message = "Failed to redirect to {0}: %t";
                VerticalAdminLogger.errorAdmin( message, page, vae );
            }
        }
        else
        {
            try
            {
                redirectClientToAdminPath( "login", (MultiValueMap) null, request, response );
            }
            catch ( VerticalAdminException vae )
            {
                String message = "Failed to redirect to {0}: %t";
                VerticalAdminLogger.errorAdmin( message, "login page", vae );
            }
        }
    }

    private void handlerForgotPasswordForm( HttpServletRequest request, HttpServletResponse response, HttpSession session,
                                            HashMap<String, Object> parameters, org.jdom.Document doc )
        throws VerticalAdminException
    {
        final UserStoreXmlCreator xmlCreator = new UserStoreXmlCreator( userStoreService.getUserStoreConnectorConfigs() );
        List<UserStoreEntity> userStores = securityService.getUserStores();
        org.jdom.Document tempDoc = xmlCreator.createPagedDocument( userStores, 0, 100 );

        org.jdom.Element dataElem = doc.getRootElement();
        dataElem.addContent( tempDoc.getRootElement().detach() );

        String errorCode = (String) session.getAttribute( "loginerrorcode" );
        if ( errorCode != null )
        {
            session.removeAttribute( "loginerrorcode" );
            session.removeAttribute( "loginerror" );
        }

        errorCode = (String) session.getAttribute( "passworderrorcode" );
        if ( errorCode != null )
        {
            parameters.put( "errorcode", errorCode );
            parameters.put( "errormessage", session.getAttribute( "passworderror" ) );
        }

        // version and copyright info
        parameters.put( "version", Version.getVersion() );
        parameters.put( "copyright", Version.getCopyright() );

        String selectedUserStore = (String) session.getAttribute( "selectedloginuserstore" );
        if ( StringUtils.isNotEmpty( selectedUserStore ) )
        {
            parameters.put( "selectedloginuserstore", selectedUserStore );
        }

        transformXML( request, response, doc, "forgotpwd_form.xsl", parameters );
    }

    private void handlerForgotPassword( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems )
        throws VerticalAdminException
    {
        final String uid = formItems.getString( "uid", null );

        HttpSession session = request.getSession( true );

        if ( uid == null || uid.length() == 0 )
        {
            session.setAttribute( "passworderrorcode", EC_400_MISSING_UID );
            session.setAttribute( "passworderror", "No user id specified!" );
            session.setMaxInactiveInterval( SESSION_TIMEOUT_ERROR );
            redirectClientToAdminPath( "forgotpassword", request, response );
            return;
        }

        UserStoreKey userStoreKey = null;
        final String userStoreKeyStr = formItems.getString( "userstorekey", null );
        if ( userStoreKeyStr != null )
        {
            userStoreKey = new UserStoreKey( userStoreKeyStr );
        }

        final QualifiedUsername qualifiedUsername = new QualifiedUsername( userStoreKey, uid );
        final String password = PasswordGenerator.generateNewPassword();

        try
        {
            securityService.changePassword( qualifiedUsername, password );
        }
        catch ( Exception ex )
        {
            session.setAttribute( "passworderrorcode", EC_401_ACCESS_DENIED );
            session.setAttribute( "passworderror", ex.getMessage() );
            session.setMaxInactiveInterval( SESSION_TIMEOUT_ERROR );
            redirectClientToAdminPath( "forgotpassword", request, response );
        }

        sendMailService.sendChangePasswordMail( qualifiedUsername, password );

        redirectClientToAdminPath( "login", request, response );
    }
}