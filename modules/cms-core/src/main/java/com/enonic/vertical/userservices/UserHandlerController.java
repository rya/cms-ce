/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.userservices;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.naming.NameNotFoundException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.enonic.cms.core.preferences.*;
import com.enonic.cms.core.security.InvalidCredentialsException;
import com.enonic.cms.core.security.userstore.*;
import com.enonic.cms.portal.PortalInstanceKey;
import com.enonic.cms.portal.PortalInstanceKeyResolver;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.ESLException;
import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.net.Mail;
import com.enonic.esl.servlet.http.CookieUtil;
import com.enonic.esl.util.RegexpUtil;
import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.VerticalProperties;
import com.enonic.vertical.VerticalRuntimeException;
import com.enonic.vertical.engine.VerticalCreateException;
import com.enonic.vertical.engine.VerticalEngineException;
import com.enonic.vertical.engine.VerticalSecurityException;
import com.enonic.vertical.engine.VerticalUpdateException;

import com.enonic.cms.core.DeploymentPathResolver;
import com.enonic.cms.core.SiteContext;
import com.enonic.cms.core.log.LogType;
import com.enonic.cms.core.login.LoginService;
import com.enonic.cms.core.mail.MessageSettings;
import com.enonic.cms.core.security.PasswordGenerator;
import com.enonic.cms.core.security.SecurityHolder;
import com.enonic.cms.core.security.userstore.connector.UserAlreadyExistsException;
import com.enonic.cms.core.service.UserServicesService;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.SitePath;
import com.enonic.cms.core.security.group.AbstractMembershipsCommand;
import com.enonic.cms.core.security.group.AddMembershipsCommand;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupSpecification;
import com.enonic.cms.core.security.group.RemoveMembershipsCommand;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.StoreNewUserCommand;
import com.enonic.cms.core.security.user.UpdateUserCommand;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserNotFoundException;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.security.user.UserStorageExistingEmailException;
import com.enonic.cms.core.security.user.UserStorageInvalidArgumentException;
import com.enonic.cms.domain.user.UserInfo;
import com.enonic.cms.domain.user.field.UserFieldMap;
import com.enonic.cms.domain.user.field.UserFieldTransformer;
import com.enonic.cms.domain.user.field.UserInfoTransformer;

@Controller
@RequestMapping(value = "/site/**/_services/user")
public class UserHandlerController
    extends AbstractUserServicesHandlerController
{
    private static final Logger LOG = LoggerFactory.getLogger( UserHandlerController.class.getName() );

    public static final int ERR_EMAIL_ALREADY_EXISTS = 100;

    public static final int ERR_UID_ALREADY_EXISTS = 101;

    public static final int ERR_USER_NOT_LOGGED_IN = 102;

    public static final int ERR_USER_NOT_FOUND = 103;

    public static final int ERR_MISSING_UID_PASSWD = 104;

    public static final int ERR_USER_PASSWD_WRONG = 106;

    public static final int ERR_PASSWORD_MISMATCH = 107;

    public static final int ERR_NEW_PASSWORD_INVALID = 108;

    public static final int ERR_JOIN_GROUP_FAILED = 110;

    public static final int ERR_JOIN_GROUP_NOT_ALLOWED = 111;

    public static final int ERR_SET_PREFERENCES_INVALID_PARAMS = 112;

    public static final int ERR_NOT_ALLOWED = 113;

    public static final int ERR_UID_WRONG_FORMAT = 114;

    public static final int ERR_USERSTORE_NOT_FOUND = 115;

    @Resource
    private PreferenceService preferenceService;

    private final PortalInstanceKeyResolver portalInstanceKeyResolver = new PortalInstanceKeyResolver();

    @Resource
    private LoginService loginService;

    @Resource
    private UserDao userDao;

    protected static final String JOINGROUPKEY = "joingroupkey";

    protected static final String ALLGROUPKEYS = "allgroupkeys";

    private static final String FORMITEM_UID = "uid";

    private static final String FORMITEM_USERNAME = "username";

    private static final String FORMITEM_USERSTORE = "userstore";

    private static final String FORMITEM_PASSWORD = "password";

    private static final String FORMITEM_DISPLAYNAME = "display_name";

    private static final String FORMITEM_EMAIL = "email";

    public UserHandlerController()
    {
        super();
    }

    public void setLoginService( LoginService loginService )
    {
        this.loginService = loginService;
    }

    public void setPreferenceService( PreferenceService value )
    {
        this.preferenceService = value;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public void login( HttpServletRequest request, HttpServletResponse response )
            throws Exception
    {
        handleRequest( request, response );
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public void logout( HttpServletRequest request, HttpServletResponse response )
            throws Exception
    {
        handleRequest( request, response );
    }

    @RequestMapping(value = "/modify", method = RequestMethod.GET)
    public void modify( HttpServletRequest request, HttpServletResponse response )
            throws Exception
    {
        handleRequest( request, response );
    }

    @RequestMapping(value = "/resetpwd", method = RequestMethod.GET)
    public void resetpwd( HttpServletRequest request, HttpServletResponse response )
            throws Exception
    {
        handleRequest( request, response );
    }

    @RequestMapping(value = "/changepwd", method = RequestMethod.GET)
    public void changepwd( HttpServletRequest request, HttpServletResponse response )
            throws Exception
    {
        handleRequest( request, response );
    }

    @RequestMapping(value = "/emailexists", method = RequestMethod.GET)
    public void emailexists( HttpServletRequest request, HttpServletResponse response )
            throws Exception
    {
        handleRequest( request, response );
    }

    @RequestMapping(value = "/joingroup", method = RequestMethod.GET)
    public void joingroup( HttpServletRequest request, HttpServletResponse response )
            throws Exception
    {
        handleRequest( request, response );
    }

    @RequestMapping(value = "/leavegroup", method = RequestMethod.GET)
    public void leavegroup( HttpServletRequest request, HttpServletResponse response )
            throws Exception
    {
        handleRequest( request, response );
    }

    @RequestMapping(value = "/setgroups", method = RequestMethod.GET)
    public void setgroups( HttpServletRequest request, HttpServletResponse response )
            throws Exception
    {
        handleRequest( request, response );
    }

    @RequestMapping(value = "/setpreferences", method = RequestMethod.GET)
    public void setpreferences( HttpServletRequest request, HttpServletResponse response )
            throws Exception
    {
        handleRequest( request, response );
    }

    @RequestMapping(value = "/deletepreferences", method = RequestMethod.GET)
    public void deletepreferences( HttpServletRequest request, HttpServletResponse response )
            throws Exception
    {
        handleRequest( request, response );
    }

    @Override
    protected void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey, String operation )
        throws VerticalUserServicesException, VerticalEngineException, IOException, ClassNotFoundException, IllegalAccessException,
        InstantiationException, ParseException
    {
        SitePath sitePath = getSitePath( request );
        SiteContext siteContext = getSiteContext( sitePath.getSiteKey() );

        if ( operation.equals( "logout" ) )
        {
            processLogout( siteContext, request, response, session, formItems, userServices );
        }
        else if ( operation.equals( "login" ) )
        {
            processLogin( siteContext, request, response, session, formItems, userServices );
        }
        else if ( operation.equals( "modify" ) )
        {
            handlerModify( request, response, formItems );
        }
        else if ( operation.equals( "resetpwd" ) )
        {
            handlerResetPassword( request, response, formItems );
        }
        else if ( operation.equals( "changepwd" ) )
        {
            handlerChangePassword( request, response, formItems );
        }
        else if ( operation.equals( "emailexists" ) )
        {
            handlerEmailExists( request, response, formItems );
        }
        else if ( operation.equals( "joingroup" ) )
        {
            handlerJoinGroup( request, response, session, formItems );
        }
        else if ( operation.equals( "leavegroup" ) )
        {
            handlerLeaveGroup( request, response, session, formItems );
        }
        else if ( operation.equals( "setgroups" ) )
        {
            handlerSetGroups( request, response, session, formItems );
        }
        else if ( operation.equals( "setpreferences" ) )
        {
            handlerSetPreferences( request, response, formItems, siteKey );
        }
        else if ( operation.equals( "deletepreferences" ) )
        {
            handlerRemovePreferences( request, response, formItems, siteKey );
        }
    }


    protected void handlerEmailExists( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems )
        throws VerticalUserServicesException, RemoteException
    {
        UserStoreKey userStoreKey;
        String email;

        try
        {
            userStoreKey = parseUserStoreKeyFromUidAndUserstore( formItems );
            email = formItems.getString( "email", null );

            Assert.isTrue( StringUtils.isNotBlank( email ) );
        }
        catch ( Exception e )
        {
            handleExceptions( e, request, response, formItems );
            return;
        }

        UserSpecification userSpec = new UserSpecification();
        userSpec.setEmail( email );
        userSpec.setUserStoreKey( userStoreKey );

        List<UserEntity> usersWithEmail = userDao.findBySpecification( userSpec );
        boolean exists = !usersWithEmail.isEmpty();

        MultiValueMap queryParams = new MultiValueMap();
        queryParams.put( "exists", String.valueOf( exists ) );
        queryParams.put( "email", email );

        UserStoreEntity userStore = userStoreService.getUserStore( userStoreKey );
        queryParams.put( "userstore", userStore.getName() );

        redirectToPage( request, response, formItems, queryParams );
    }

    protected void handlerSetGroups( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems )
        throws VerticalUserServicesException, RemoteException
    {
        UserEntity loggedInUser = securityService.getLoggedInPortalUserAsEntity();

        if ( loggedInUser == null || loggedInUser.isAnonymous() )
        {
            String message = "User must be logged in.";
            LOG.warn( StringUtil.expandString( message, null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_USER_NOT_LOGGED_IN, null );
            return;
        }

        String[] requiredParameters = new String[]{ALLGROUPKEYS};

        List<String> missingParameters = findMissingRequiredParameters( requiredParameters, formItems, true );

        if ( !missingParameters.isEmpty() )
        {
            String message = createMissingParametersMessage( "Set groups", missingParameters );

            LOG.warn( StringUtil.expandString( message, null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_PARAMETERS_MISSING, null );
            return;
        }

        UpdateUserCommand updateUserCommand = createUpdateUserCommandForGroupHandling( loggedInUser );

        addGroupsFromSetGroupsConfig( formItems, updateUserCommand, loggedInUser );

        try
        {
            userStoreService.updateUser( updateUserCommand );

            updateUserInSession( session );
        }
        catch ( Exception e )
        {
            handleExceptions( e, request, response, formItems );
            return;
        }

        redirectToPage( request, response, formItems );
    }


    protected void handlerJoinGroup( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems )
        throws VerticalUserServicesException, RemoteException
    {

        UserEntity user = securityService.getLoggedInPortalUserAsEntity();
        UserEntity executor = securityService.getRunAsUser();

        if ( user == null )
        {
            String message = "User must be logged in.";
            LOG.warn( StringUtil.expandString( message, null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_USER_NOT_LOGGED_IN, null );
            return;
        }

        String[] requiredParameters = new String[]{"key"};

        List<String> missingParameters = findMissingRequiredParameters( requiredParameters, formItems, true );

        if ( !missingParameters.isEmpty() )
        {
            String message = createMissingParametersMessage( "Join group", missingParameters );

            LOG.warn( StringUtil.expandString( message, null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_PARAMETERS_MISSING, null );
            return;
        }

        List<GroupKey> groupKeysToAdd = getSubmittedGroupKeys( formItems, "key" );
        List<GroupKey> existingKeysForUser = getExistingDirectMembershipsForUser( user );
        groupKeysToAdd.removeAll( existingKeysForUser );
        if ( groupKeysToAdd.size() >= 1 )
        {

            GroupSpecification userGroupForLoggedInUser = new GroupSpecification();
            userGroupForLoggedInUser.setKey( user.getUserGroupKey() );

            AddMembershipsCommand addMembershipCommand = new AddMembershipsCommand( userGroupForLoggedInUser, executor.getKey() );
            addMembershipCommand.setUpdateOpenGroupsOnly( true );
            addMembershipCommand.setRespondWithException( true );
            for ( GroupKey groupKeyToAdd : groupKeysToAdd )
            {
                addMembershipCommand.addGroupsToAddTo( groupKeyToAdd );
            }

            try
            {
                userStoreService.addMembershipsToGroup( addMembershipCommand );
                updateUserInSession( session );
            }
            catch ( UserStoreAccessException e )
            {
                String message = "Not allowed to add user to group: %t";
                LOG.warn( StringUtil.expandString( message, null, e ), e );
                redirectToErrorPage( request, response, formItems, ERR_JOIN_GROUP_NOT_ALLOWED, null );
                return;
            }
            catch ( RuntimeException e )
            {
                String message = "Failed to add user to group: %t";
                LOG.warn( StringUtil.expandString( message, null, e ), e );
                redirectToErrorPage( request, response, formItems, ERR_JOIN_GROUP_FAILED, null );
                return;
            }
        }

        redirectToPage( request, response, formItems );
    }

    private UpdateUserCommand createUpdateUserCommandForGroupHandling( User user )
    {
        UserSpecification spec = new UserSpecification();

        spec.setKey( user.getKey() );
        spec.setDeletedStateNotDeleted();

        UpdateUserCommand updateUserCommand = new UpdateUserCommand( user.getKey(), spec );
        updateUserCommand.setUpdateStrategy( UpdateUserCommand.UpdateStrategy.REPLACE_NEW );
        updateUserCommand.setSyncMemberships( true );
        updateUserCommand.setUpdateOpenGroupsOnly( true );
        updateUserCommand.setAllowUpdateSelf( true );
        return updateUserCommand;
    }

    private void addSubmittedGroups( List<GroupKey> groupKeys, AbstractMembershipsCommand userCommand )
    {

        for ( GroupKey newGroupKey : groupKeys )
        {
            userCommand.addMembership( newGroupKey );
        }
    }

    private List<GroupKey> getExistingDirectMembershipsForUser( UserEntity user )
    {
        ArrayList<GroupKey> existingMemberships = new ArrayList<GroupKey>();

        for ( GroupEntity existingGroup : user.getDirectMemberships() )
        {
            existingMemberships.add( existingGroup.getGroupKey() );
        }

        return existingMemberships;
    }

    protected void addGroupsFromSetGroupsConfig( ExtendedMap formItems, UpdateUserCommand updateUserCommand, UserEntity user )
    {

        List<GroupKey> toBeAdded = getSubmittedGroupKeys( formItems, JOINGROUPKEY );
        List<GroupKey> toBeConsidered = getSubmittedGroupKeys( formItems, ALLGROUPKEYS );

        addSubmittedGroups( toBeAdded, updateUserCommand );

        for ( GroupEntity existingGroup : user.getDirectMemberships() )
        {
            if ( !toBeConsidered.contains( existingGroup.getGroupKey() ) )
            {
                updateUserCommand.addMembership( existingGroup.getGroupKey() );
            }
        }
    }

    private List<GroupKey> getSubmittedGroupKeys( ExtendedMap formItems, String key )
    {
        List<GroupKey> groupKeys = new ArrayList<GroupKey>();

        if ( formItems.getString( key, null ) == null )
        {
            return groupKeys;
        }

        String[] groupKeysAsStrings;

        if ( isArrayFormItem( formItems, key ) )
        {
            groupKeysAsStrings = formItems.getStringArray( key );
        }
        else
        {
            groupKeysAsStrings = StringUtil.splitString( formItems.getString( key ), "," );
        }

        for ( String groupKeyAsString : groupKeysAsStrings )
        {
            groupKeys.add( new GroupKey( groupKeyAsString ) );
        }

        return groupKeys;
    }

    protected void handlerLeaveGroup( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems )
        throws VerticalUserServicesException, RemoteException
    {
        UserEntity user = securityService.getLoggedInPortalUserAsEntity();
        UserEntity executor = securityService.getRunAsUser();
        if ( user == null )
        {
            String message = "User must be logged in.";
            LOG.warn( StringUtil.expandString( message, null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_USER_NOT_LOGGED_IN, null );
            return;
        }

        String[] requiredParameters = new String[]{"key"};

        List<String> missingParameters = findMissingRequiredParameters( requiredParameters, formItems, true );

        if ( !missingParameters.isEmpty() )
        {
            String message = createMissingParametersMessage( "Leave group", missingParameters );

            LOG.warn( StringUtil.expandString( message, null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_PARAMETERS_MISSING, null );
            return;
        }

        List<GroupKey> submittedGroupKeysToRemove = getSubmittedGroupKeys( formItems, "key" );
        List<GroupKey> existingKeysForUser = getExistingDirectMembershipsForUser( user );
        submittedGroupKeysToRemove.retainAll( existingKeysForUser );

        if ( submittedGroupKeysToRemove.size() >= 1 )
        {

            GroupSpecification userGroupForLoggedInUser = new GroupSpecification();
            userGroupForLoggedInUser.setKey( user.getUserGroupKey() );

            RemoveMembershipsCommand removeMembershipsCommand = new RemoveMembershipsCommand( userGroupForLoggedInUser, executor.getKey() );
            removeMembershipsCommand.setUpdateOpenGroupsOnly( true );
            removeMembershipsCommand.setRespondWithException( true );
            for ( GroupKey groupKeyToRemove : submittedGroupKeysToRemove )
            {
                removeMembershipsCommand.addGroupToRemoveFrom( groupKeyToRemove );
            }

            try
            {
                userStoreService.removeMembershipsFromGroup( removeMembershipsCommand );
                updateUserInSession( session );
            }
            catch ( UserStoreAccessException e )
            {
                String message = "Not allowed to remove user from group: %t";
                LOG.warn( StringUtil.expandString( message, null, e ), e );
                redirectToErrorPage( request, response, formItems, ERR_JOIN_GROUP_NOT_ALLOWED, null );
                return;
            }
            catch ( RuntimeException e )
            {
                LOG.warn( StringUtil.expandString( e.getMessage(), null, e ), e );
                redirectToErrorPage( request, response, formItems, ERR_JOIN_GROUP_FAILED, null );
                return;
            }
        }

        redirectToPage( request, response, formItems );
    }

    protected void handlerChangePassword( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems )
        throws VerticalUserServicesException, VerticalUpdateException, RemoteException
    {
        User loggedInUser = securityService.getOldUserObject();

        String uid = parseUsername( formItems );

        UserStoreKey userStoreKey;
        try
        {
            userStoreKey = parseUserStoreKeyFromUidAndUserstore( formItems );
        }
        catch ( Exception e )
        {
            handleExceptions( e, request, response, formItems );
            return;
        }

        if ( uid != null && uid.contains( "@" ) )
        {
            String message = "username is in wrong format, email not supported: " + uid;
            LOG.warn( StringUtil.expandString( message, null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_UID_WRONG_FORMAT, null );
            return;
        }

        if ( loggedInUser != null && StringUtils.isBlank( uid ) )
        {
            uid = loggedInUser.getName();
            userStoreKey = loggedInUser.getUserStoreKey();
        }

        try
        {
            if ( loggedInUser == null || loggedInUser.getName().equals( uid ) )
            {
                // Either the user is trying to update its own password, or the user is not
                // logged in. We need to check the old password

                String password = formItems.getString( FORMITEM_PASSWORD, null );
                if ( StringUtils.isBlank( password ) )
                {
                    String message = "Missing user name and/or password.";
                    LOG.warn( StringUtil.expandString( message, null, null ) );
                    redirectToErrorPage( request, response, formItems, ERR_MISSING_UID_PASSWD, null );
                    return;
                }
                securityService.loginPortalUser( new QualifiedUsername( userStoreKey, uid ), password );
                loggedInUser = securityService.getOldUserObject();
            }

            String newPassword1 = formItems.getString( "newpassword1", "" );
            String newPassword2 = formItems.getString( "newpassword2", "" );

            if ( !newPassword1.equals( newPassword2 ) )
            {
                String message = "Passwords don't match.";
                LOG.warn( StringUtil.expandString( message, null, null ) );
                redirectToErrorPage( request, response, formItems, ERR_PASSWORD_MISMATCH, null );
                return;
            }

            if ( newPassword1.length() == 0 )
            {
                String message = "New password is invalid.";
                LOG.warn( StringUtil.expandString( message, null, null ) );
                redirectToErrorPage( request, response, formItems, ERR_NEW_PASSWORD_INVALID, null );
                return;
            }

            if ( loggedInUser.isEnterpriseAdmin() || loggedInUser.getName().equals( uid ) )
            {
                securityService.changePassword( new QualifiedUsername( userStoreKey, uid ), newPassword1 );
            }
            else
            {
                String message = "Not allowed to update user password.";
                LOG.warn( StringUtil.expandString( message, null, null ) );
                redirectToErrorPage( request, response, formItems, ERR_NOT_ALLOWED, null );
                return;
            }
        }
        catch ( InvalidCredentialsException ice )
        {
            String message = "User name and/or password is wrong: %0";
            LOG.warn( StringUtil.expandString( message, uid, null ) );
            redirectToErrorPage( request, response, formItems, ERR_USER_PASSWD_WRONG, null );
            return;
        }
        catch ( VerticalUpdateException vue )
        {
            if ( vue.getCause() instanceof NameNotFoundException )
            {
                String message = "User not found: %0.";
                LOG.warn( StringUtil.expandString( message, uid, null ) );
                redirectToErrorPage( request, response, formItems, ERR_USER_NOT_FOUND, null );
                return;
            }
            else
            {
                throw vue;
            }
        }
        catch ( VerticalSecurityException vue )
        {
            String message = "User id and/or password incorrect: %t";
            LOG.warn( StringUtil.expandString( message, null, vue ), vue );
            redirectToErrorPage( request, response, formItems, ERR_USER_PASSWD_WRONG, null );
            return;
        }
        catch ( Exception e )
        {
            handleExceptions( e, request, response, formItems );
            return;
        }

        redirectToPage( request, response, formItems );
    }

    protected void handlerResetPassword( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems )
        throws VerticalUserServicesException, VerticalUpdateException, RemoteException
    {
        UserStoreKey userStoreKey;
        String uid;
        String email = null;

        try
        {
            userStoreKey = parseUserStoreKeyFromUidAndUserstore( formItems );
            uid = parseUsername( formItems );
        }
        catch ( Exception e )
        {
            handleExceptions( e, request, response, formItems );
            return;
        }

        if ( StringUtils.isBlank( uid ) )
        {
            email = parseEmailForResetPassword( formItems );
        }

        boolean neitherEmailOrUidSet = StringUtils.isBlank( uid ) && StringUtils.isBlank( email );
        if ( neitherEmailOrUidSet )
        {
            redirectToErrorPage( request, response, formItems, ERR_USER_NOT_FOUND, null );
            return;
        }

        boolean missingMailBodyOrFromEmail = !( formItems.containsKey( "mail_body" ) && formItems.containsKey( "from_email" ) );
        if ( missingMailBodyOrFromEmail )
        {
            LOG.warn( StringUtil.expandString( "Missing either 'mail_body' or 'from_email' parameter.", null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_PARAMETERS_MISSING, null );
            return;
        }

        QualifiedUsername qualifiedUsername;

        if ( StringUtils.isNotBlank( email ) )
        {
            UserSpecification userSpecification = new UserSpecification();
            userSpecification.setUserStoreKey( userStoreKey );
            userSpecification.setEmail( email );
            userSpecification.setDeletedStateNotDeleted();

            UserEntity userEntity = userDao.findSingleBySpecification( userSpecification );
            if ( userEntity == null )
            {
                redirectToErrorPage( request, response, formItems, ERR_USER_NOT_FOUND, null );
                return;
            }
            qualifiedUsername = new QualifiedUsername( userStoreKey, userEntity.getName() );
        }
        else
        {
            qualifiedUsername = new QualifiedUsername( userStoreKey, uid );
        }

        final String password = PasswordGenerator.generateNewPassword();

        try
        {
            securityService.changePassword( qualifiedUsername, password );
        }
        catch ( IllegalArgumentException e )
        {
            if ( e.getMessage() != null && e.getMessage().startsWith( "Could not find user" ) )
            {
                String message = "User not found: " + uid;
                LOG.warn( StringUtil.expandString( message, uid, null ) );
                redirectToErrorPage( request, response, formItems, ERR_USER_NOT_FOUND, null );
                return;
            }
            else
            {
                String message = "Not allowed to change password for user: " + uid;
                LOG.warn( StringUtil.expandString( message, uid, null ) );
                redirectToErrorPage( request, response, formItems, ERR_NOT_ALLOWED, null );
                return;
            }
        }
        catch ( Exception e )
        {
            handleExceptions( e, request, response, formItems );
            return;
        }

        final MessageSettings messageSettings = new MessageSettings();
        messageSettings.setFromName( formItems.getString( "from_name", null ) );
        messageSettings.setFromMail( formItems.getString( "from_email", null ) );
        messageSettings.setSubject( formItems.getString( "mail_subject", null ) );

        String mailBody = formItems.getString( "mail_body", "" );
        mailBody = fixLinebreaks( mailBody );
        messageSettings.setBody( mailBody );

        sendMailService.sendChangePasswordMail( qualifiedUsername, password, messageSettings );

        redirectToPage( request, response, formItems );
    }

    private String fixLinebreaks( String mailBody )
    {
        mailBody = RegexpUtil.substituteAll( "\\\\n", "\n", mailBody );
        return mailBody;
    }

    private String parseEmailForResetPassword( ExtendedMap formItems )
    {
        String email = parseEmail( formItems );

        if ( StringUtils.isBlank( email ) )
        {
            email = formItems.getString( "id", null );
        }

        return email;
    }


    protected void handlerModify( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems )
        throws VerticalUserServicesException, VerticalUpdateException, VerticalSecurityException, RemoteException
    {
        UserEntity loggedInUser = securityService.getLoggedInPortalUserAsEntity();

        if ( loggedInUser == null || loggedInUser.isAnonymous() )
        {
            String msg = "User not logged in.";
            LOG.warn( StringUtil.expandString( msg, null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_USER_NOT_LOGGED_IN, null );
            return;
        }

        try
        {
            final UserSpecification updateUserSpecification = createUserSpecificationForUpdate( formItems );

            final UpdateUserCommand updateUserCommand = new UpdateUserCommand( loggedInUser.getKey(), updateUserSpecification );

            final String email = parseEmail( formItems );

            if ( email != null )
            {
                updateUserCommand.setEmail( email );
            }

            final String displayName = parseDisplayName( formItems );

            if ( displayName != null )
            {
                updateUserCommand.setDisplayName( displayName );
            }

            final UserStoreKey userStoreKey = parseUserStoreKeyFromUidAndUserstore( formItems );

            final UserInfo userInfo = parseCustomUserFieldValues( userStoreKey, formItems );

            updateUserCommand.setUserInfo( userInfo );
            updateUserCommand.setAllowUpdateSelf( true );
            updateUserCommand.setUpdateOpenGroupsOnly( true );

            updateUserCommand.setUpdateStrategy( UpdateUserCommand.UpdateStrategy.REPLACE_NEW );

            updateGroupsInUpdateCommand( formItems, loggedInUser, updateUserCommand );

            userStoreService.updateUser( updateUserCommand );
            redirectToPage( request, response, formItems );
        }
        catch ( Exception e )
        {
            handleExceptions( e, request, response, formItems );
        }
    }


    private void handleExceptions( Exception e, HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems )
    {
        if ( e instanceof UserAlreadyExistsException )
        {
            String msg = "username already exists: %0";

            String userId =
                formItems.containsKey( FORMITEM_UID ) ? formItems.getString( FORMITEM_UID ) : formItems.getString( FORMITEM_USERNAME, "" );

            LOG.warn( StringUtil.expandString( msg, userId, null ) );
            redirectToErrorPage( request, response, formItems, ERR_UID_ALREADY_EXISTS, null );
        }
        else if ( e instanceof IllegalArgumentException )
        {
            String message = StringUtils.isNotBlank( e.getMessage() ) ? e.getMessage() : "Illegal arguments: %t";
            LOG.warn( StringUtil.expandString( message, null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_PARAMETERS_INVALID, null );
        }
        else if ( e instanceof UserStorageExistingEmailException )
        {
            LOG.warn( StringUtil.expandString( e.getMessage(), null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_EMAIL_ALREADY_EXISTS, null );
        }
        else if ( e instanceof UserNotFoundException )
        {
            LOG.warn( StringUtil.expandString( e.getMessage(), null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_USER_NOT_FOUND, null );
        }
        else if ( e instanceof UserStorageInvalidArgumentException )
        {
            LOG.warn( StringUtil.expandString( e.getMessage(), null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_PARAMETERS_INVALID, null );
        }
        else if ( e instanceof UserStoreAccessException )
        {
            LOG.warn( StringUtil.expandString( e.getMessage(), null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_NOT_ALLOWED, null );
        }
        else if ( e instanceof ESLException )
        {
            String message = "Not able to send mail: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, e ), e );
            redirectToErrorPage( request, response, formItems, ERR_EMAIL_SEND_FAILED, null );
        }
        else if ( e instanceof UnsupportedEncodingException )
        {
            String message = "Un-supported encoding: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, e ), e );
            redirectToErrorPage( request, response, formItems, ERR_EMAIL_SEND_FAILED, null );
        }
        else if ( e instanceof MessagingException )
        {
            String message = "Failed to send order received mail: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, e ), e );
            redirectToErrorPage( request, response, formItems, ERR_EMAIL_SEND_FAILED, null );
        }
        else if ( e instanceof UserStoreNotFoundException )
        {
            String message = "Userstore not found";
            LOG.warn( StringUtil.expandString( message, null, e ), e );
            redirectToErrorPage( request, response, formItems, ERR_USERSTORE_NOT_FOUND, null );
        }
        else if ( e instanceof UserStoreConnectorPolicyBrokenException)
        {
            String msg = e.getMessage();
            LOG.warn( StringUtil.expandString( msg, null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_SECURITY_EXCEPTION, null );
        }
        else
        {
            String message = e.getMessage() == null ? "Error" : e.getMessage();

            LOG.warn( StringUtil.expandString( message, null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_OPERATION_BACKEND, null );
        }
    }

    private void updateGroupsInUpdateCommand( ExtendedMap formItems, UserEntity user, UpdateUserCommand updateUserCommand )
    {
        if ( formItems.containsKey( JOINGROUPKEY ) || formItems.containsKey( ALLGROUPKEYS ) )
        {
            updateUserCommand.setSyncMemberships( true );
            addGroupsFromSetGroupsConfig( formItems, updateUserCommand, user );
        }
    }


    @Override
    protected void handlerCreate( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey )
        throws VerticalUserServicesException, VerticalCreateException, VerticalSecurityException, RemoteException
    {
        String[] requiredParameters = new String[]{"email"};

        List<String> missingParameters = findMissingRequiredParameters( requiredParameters, formItems, false );

        if ( !missingParameters.isEmpty() )
        {
            String message = createMissingParametersMessage( "Create User", missingParameters );

            LOG.warn( StringUtil.expandString( message, null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_PARAMETERS_MISSING, null );
            return;
        }

        try
        {
            StoreNewUserCommand storeNewUserCommand = new StoreNewUserCommand();

            final String username = formItems.getString( FORMITEM_USERNAME, null );
            storeNewUserCommand.setUsername( username );

            final UserStoreKey userStoreKey = parseUserStoreKeyFromUserstore( formItems );
            storeNewUserCommand.setUserStoreKey( userStoreKey );

            storeNewUserCommand.setEmail( parseEmail( formItems ) );
            storeNewUserCommand.setDisplayName( parseDisplayName( formItems ) );
            storeNewUserCommand.setPassword( parsePassword( formItems ) );

            storeNewUserCommand.setStorer( securityService.getLoggedInPortalUser().getKey() );

            final UserInfo userInfo = parseCustomUserFieldValues( userStoreKey, formItems );
            storeNewUserCommand.setUserInfo( userInfo );

            storeNewUserCommand.setAllowAnyUserAccess( true );

            List<GroupKey> submittedGroups = getSubmittedGroupKeys( formItems, JOINGROUPKEY );
            if ( !submittedGroups.isEmpty() )
            {
                addSubmittedGroups( submittedGroups, storeNewUserCommand );
            }

            UserKey userKey = userStoreService.storeNewUser( storeNewUserCommand );
            sendCreatedNewUserMail( formItems, userKey, storeNewUserCommand );
        }
        catch ( Exception e )
        {
            handleExceptions( e, request, response, formItems );
            return;
        }

        updateUserInSession( session );

        redirectToPage( request, response, formItems );
    }

    private void sendCreatedNewUserMail( ExtendedMap formItems, UserKey userKey, StoreNewUserCommand command )
    {
        UserSpecification userSpec = new UserSpecification();
        userSpec.setKey( userKey );
        //userSpec.setUserStoreKey( command.getUserStoreKey() );
        userSpec.setDeletedStateNotDeleted();
        UserEntity newUser = userDao.findSingleBySpecification( userSpec );

        sendMailToNewUser( formItems, newUser, command.getPassword() );
        sendMailToAdmin( formItems, newUser );
    }

    private void sendMailToAdmin( ExtendedMap formItems, UserEntity newUser )
    {
        String[] adminEmailRequiredParameters = {"admin_email", "admin_mail_body"};

        if ( formItems.containsKeys( adminEmailRequiredParameters, false ) )
        {
            formItems.put( FORMITEM_UID, newUser.getName() );
            formItems.put( FORMITEM_DISPLAYNAME, newUser.getDisplayName() );
            formItems.put( FORMITEM_USERSTORE, newUser.getUserStore().getName() );

            Mail adminMail = new Mail();
            VerticalProperties vp = VerticalProperties.getVerticalProperties();
            adminMail.setSMTPHost( vp.getSMTPHost() );

            adminMail.addRecipient( formItems.getString( "admin_name", "" ), formItems.getString( "admin_email" ), Mail.TO_RECIPIENT );

            adminMail.setFrom( formItems.getString( "from_name", "" ), formItems.getString( "from_email", "" ) );

            String mailSubject = formItems.getString( "admin_mail_subject", "" );
            mailSubject = Util.replaceKeys( formItems, mailSubject, new String[]{FORMITEM_PASSWORD} );
            mailSubject = Util.removeTokens( mailSubject );

            adminMail.setSubject( mailSubject );

            String mailBody = formItems.getString( "admin_mail_body" );
            mailBody = Util.replaceKeys( formItems, mailBody, new String[]{FORMITEM_PASSWORD} );
            mailBody = Util.removeTokens( mailBody );
            adminMail.setMessage( mailBody );

            adminMail.send();
        }
    }

    private void sendMailToNewUser( ExtendedMap formItems, UserEntity newUser, String password )
    {
        String[] sendEmailRequiredParameters = {"user_mail_body", "from_email", "email"};

        if ( formItems.containsKeys( sendEmailRequiredParameters, false ) )
        {
            formItems.put( FORMITEM_USERNAME, newUser.getName() );
            formItems.put( FORMITEM_PASSWORD, password );
            formItems.put( FORMITEM_DISPLAYNAME, newUser.getDisplayName() );
            formItems.put( FORMITEM_USERSTORE, newUser.getUserStore().getName() );

            Mail userMail = new Mail();
            userMail.setSMTPHost( verticalProperties.getSMTPHost() );

            userMail.addRecipient( newUser.getDisplayName(), formItems.getString( "email" ), Mail.TO_RECIPIENT );

            userMail.setFrom( formItems.getString( "from_name", "" ), formItems.getString( "from_email" ) );

            String mailSubject = formItems.getString( "user_mail_subject", "" );
            mailSubject = Util.replaceKeys( formItems, mailSubject, new String[]{FORMITEM_PASSWORD} );
            mailSubject = Util.removeTokens( mailSubject );
            userMail.setSubject( mailSubject );

            String mailBody = formItems.getString( "user_mail_body" );

            mailBody = Util.replaceKeys( formItems, mailBody, null );
            mailBody = Util.removeTokens( mailBody );
            userMail.setMessage( mailBody );

            userMail.send();
        }
    }

    /*
    Same as UserHandlerServlet.parseCustomUserFieldValues();
     */
    private UserInfo parseCustomUserFieldValues( final UserStoreKey userStoreKey, final ExtendedMap formItems )
    {
        final UserFieldTransformer fieldTransformer = new UserFieldTransformer();
        final UserFieldMap userFieldMap = fieldTransformer.toUserFields( formItems );

        final UserStoreEntity userStore = userStoreService.getUserStore( userStoreKey );
        userStore.getConfig().validateUserFieldMap( userFieldMap );

        final UserInfoTransformer infoTransformer = new UserInfoTransformer();

        return infoTransformer.toUserInfo( userFieldMap );
    }

    private String parseDisplayName( ExtendedMap formItems )
    {
        return formItems.containsKey( FORMITEM_DISPLAYNAME ) ? formItems.getString( FORMITEM_DISPLAYNAME ) : null;
    }

    private String parseEmail( ExtendedMap formItems )
    {
        return formItems.containsKey( "email" ) ? formItems.getString( "email" ) : null;
    }

    private String parsePassword( ExtendedMap formItems )
    {
        String password;

        if ( formItems.containsKey( FORMITEM_PASSWORD ) )
        {
            password = formItems.getString( FORMITEM_PASSWORD );

            if ( StringUtils.isNotBlank( password ) )
            {
                return password;
            }
        }

        return PasswordGenerator.generateNewPassword();
    }

    @Override
    protected void handlerUpdate( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey )
        throws VerticalUserServicesException, VerticalUpdateException, VerticalSecurityException, RemoteException
    {

        UserEntity loggedInUser = securityService.getLoggedInPortalUserAsEntity();

        if ( loggedInUser == null || loggedInUser.isAnonymous() )
        {
            String msg = "User not logged in.";
            LOG.warn( StringUtil.expandString( msg, null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_USER_NOT_LOGGED_IN, null );
            return;
        }

        try
        {
            final UserSpecification updateUserSpecification = createUserSpecificationForUpdate( formItems );

            final UpdateUserCommand updateUserCommand = new UpdateUserCommand( loggedInUser.getKey(), updateUserSpecification );

            updateUserCommand.setEmail( parseEmail( formItems ) );
            updateUserCommand.setDisplayName( parseDisplayName( formItems ) );

            final UserStoreKey userStoreKey = parseUserStoreKeyFromUidAndUserstore( formItems );

            final UserInfo userInfo = parseCustomUserFieldValues( userStoreKey, formItems );

            updateUserCommand.setUserInfo( userInfo );
            updateUserCommand.setAllowUpdateSelf( true );
            updateUserCommand.setUpdateOpenGroupsOnly( true );

            updateUserCommand.setUpdateStrategy( UpdateUserCommand.UpdateStrategy.REPLACE_ALL );

            updateGroupsInUpdateCommand( formItems, loggedInUser, updateUserCommand );

            userStoreService.updateUser( updateUserCommand );

            redirectToPage( request, response, formItems );
        }
        catch ( Exception e )
        {
            handleExceptions( e, request, response, formItems );
        }
    }

    private UserSpecification createUserSpecificationForUpdate( final ExtendedMap formItems )
    {
        final UserSpecification spec = new UserSpecification();
        spec.setDeletedStateNotDeleted();

        String username = parseUsername( formItems );

        if ( StringUtils.isNotBlank( username ) )
        {
            spec.setName( username );
            spec.setUserStoreKey( parseUserStoreKeyFromUidAndUserstore( formItems ) );
        }
        else
        {
            User loggedInUser = securityService.getLoggedInPortalUser();
            spec.setKey( loggedInUser.getKey() );
            spec.setName( loggedInUser.getName() );
        }

        return spec;
    }

    private void processLogin( SiteContext siteContext, HttpServletRequest request, HttpServletResponse response, HttpSession session,
                               ExtendedMap formItems, UserServicesService userServices )
        throws VerticalUserServicesException, RemoteException
    {
        String username = null;
        User user = null;
        UserStoreKey userStoreKey = null;

        try
        {
            userStoreKey = parseUserStoreKeyFromUidAndUserstore( formItems );
            username = parseUsername( formItems );

            if ( StringUtils.isBlank( username ) && formItems.containsKey( FORMITEM_EMAIL ) )
            {
                UserEntity foundUser = getUserFromEmail( formItems, userStoreKey );

                if ( foundUser == null )
                {
                    throw new InvalidCredentialsException( "Not able to log in user: " + formItems.getString( FORMITEM_EMAIL ) );
                }

                username = foundUser.getName();
            }

            String password = formItems.getString( FORMITEM_PASSWORD, null );

            user = null;

            if ( StringUtils.isBlank( username ) || StringUtils.isBlank( password ) )
            {
                String message = "Missing user name and/or password.";
                LOG.warn( StringUtil.expandString( message, null, null ) );
                redirectToErrorPage( request, response, formItems, ERR_MISSING_UID_PASSWD, null );
                return;
            }

            securityService.loginPortalUser( new QualifiedUsername( userStoreKey, username ), password );
            user = securityService.getOldUserObject();

            if ( user == null )
            {
                throw new InvalidCredentialsException( "Not able to log in user: " + username );
            }

            if ( siteContext.isAuthenticationLoggingEnabled() )
            {
                // Create log entry:
                boolean createdLogEntrySuccessfully =
                    createLogEntry( siteContext, user, userServices, request.getRemoteAddr(), LogType.LOGIN.asInteger(), userStoreKey );
                if ( !createdLogEntrySuccessfully )
                {
                    String message = "Failed to create log entry. Aborted login.";

                    VerticalRuntimeException.error( this.getClass(), VerticalUserServicesException.class,
                                                    StringUtil.expandString( message, (Object) null, null ) );
                }
            }

            session.setAttribute( "vertical_uid", username );
            SecurityHolder.setUser( user.getKey() );

            boolean rememberUser = parseRememberUser( formItems );

            String deploymentPath = DeploymentPathResolver.getSiteDeploymentPath( request );
            String cookieName = "guid-" + siteContext.getSiteKey();

            if ( rememberUser )
            {
                applyRememberUser( siteContext, response, formItems, user, deploymentPath, cookieName );
            }
            else
            {
                removeGuidCookie( response, deploymentPath, siteContext );
            }

            redirectToPage( request, response, formItems );
        }
        catch ( InvalidCredentialsException ice )
        {
            reportFailedLogin( siteContext, user, userServices, username, userStoreKey, request.getRemoteAddr() );
            String message = "User name and/or password is wrong: %0";
            LOG.warn( StringUtil.expandString( message, username, null ) );
            redirectToErrorPage( request, response, formItems, ERR_USER_PASSWD_WRONG, null );
        }
        catch ( VerticalSecurityException vse )
        {
            reportFailedLogin( siteContext, user, userServices, username, userStoreKey, request.getRemoteAddr() );
            String message = "No rights to handle request: " + vse.getMessage();
            LOG.warn( StringUtil.expandString( message, null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_SECURITY_EXCEPTION, null );
        }
        catch ( Exception e )
        {
            handleExceptions( e, request, response, formItems );
        }
    }

    private void applyRememberUser( SiteContext siteContext, HttpServletResponse response, ExtendedMap formItems, User user,
                                    String deploymentPath, String cookieName )
    {
        boolean resetGuid = false;
        if ( formItems.getString( "resetguid", "false" ).equals( "true" ) || formItems.getString( "resetguid", "off" ).equals( "on" ) )
        {
            resetGuid = true;
        }

        String guid = loginService.rememberLogin( user.getKey(), siteContext.getSiteKey(), resetGuid );

        long maxAge = 60L * 60 * 24 * verticalProperties.getAutologinTimeout();
        if ( maxAge > Integer.MAX_VALUE )
        {
            maxAge = Integer.MAX_VALUE;
        }

        CookieUtil.setCookie( response, cookieName, guid, (int) maxAge, deploymentPath );
    }

    private boolean parseRememberUser( ExtendedMap formItems )
    {
        boolean rememberUser = false;

        if ( formItems.getString( "rememberme", "false" ).equals( "true" ) || formItems.getString( "rememberme", "off" ).equals( "on" ) )
        {
            rememberUser = true;
        }
        return rememberUser;
    }


    private UserEntity getUserFromEmail( ExtendedMap formItems, UserStoreKey userStoreKey )
    {
        String email = formItems.getString( FORMITEM_EMAIL, null );

        if ( StringUtils.isBlank( email ) )
        {
            return null;
        }

        UserSpecification userSpecification = new UserSpecification();
        userSpecification.setEmail( email );
        userSpecification.setUserStoreKey( userStoreKey );
        userSpecification.setDeletedStateNotDeleted();

        return userDao.findSingleBySpecification( userSpecification );
    }

    private String parseUsername( ExtendedMap formItems )
    {
        String submittedUid = formItems.getString( FORMITEM_UID, null );

        if ( StringUtils.isNotBlank( submittedUid ) )
        {
            QualifiedUsername qualifiedUserName = QualifiedUsername.parse( submittedUid );

            if ( qualifiedUserName != null )
            {
                return qualifiedUserName.getUsername();
            }
        }

        return null;
    }

    private UserStoreKey parseUserStoreKeyFromUidAndUserstore( ExtendedMap formItems )
    {
        return doParseUserStoreKey( formItems, true );
    }

    private UserStoreKey parseUserStoreKeyFromUserstore( ExtendedMap formItems )
    {
        return doParseUserStoreKey( formItems, false );
    }

    private UserStoreKey doParseUserStoreKey( ExtendedMap formItems, boolean parseUserstoreFromItem )
    {
        if ( formItems.containsKey( FORMITEM_UID ) && parseUserstoreFromItem )
        {
            QualifiedUsername qualifiedUsername = QualifiedUsername.parse( formItems.getString( FORMITEM_UID ) );
            if ( qualifiedUsername.hasUserStoreNameSet() )
            {
                UserStoreEntity userStoreEntity = userStoreParser.parseUserStore( qualifiedUsername.getUserStoreName() );
                if ( userStoreEntity != null )
                {
                    return userStoreEntity.getKey();
                }
            }
        }

        if ( formItems.containsKey( FORMITEM_USERSTORE ) && StringUtils.isNotBlank( formItems.getString( FORMITEM_USERSTORE ) ) )
        {
            UserStoreEntity userStoreEntity = userStoreParser.parseUserStore( formItems.getString( FORMITEM_USERSTORE ) );
            if ( userStoreEntity != null )
            {
                return userStoreEntity.getKey();
            }
        }

        return userStoreService.getDefaultUserStore().getKey();
    }

    private void removeGuidCookie( HttpServletResponse response, String deploymentPath, SiteContext siteContext )
    {
        String cookieName = "guid-" + siteContext.getSiteKey();
        CookieUtil.setCookie( response, cookieName, null, 0, deploymentPath );
    }

    private boolean createLogEntry( SiteContext siteContext, User user, UserServicesService userServices, String remoteIP, int type,
                                    UserStoreKey userStoreKey )
        throws RemoteException
    {
        try
        {
            Document doc = XMLTool.createDocument( "logentry" );
            Element rootElement = doc.getDocumentElement();
            rootElement.setAttribute( "typekey", String.valueOf( type ) );
            rootElement.setAttribute( "inetaddress", remoteIP );
            rootElement.setAttribute( "menukey", String.valueOf( siteContext.getSiteKey() ) );
//			Element logDataElement = XMLTool.createElement(doc, rootElement, "data");
            if ( userStoreKey != null )
            {
                /*
                    String domainName = userServices.getDomainName(domainKey);
                    Element elem = XMLTool.createElement(doc, logDataElement, "domain", domainName);
                    elem.setAttribute("key", String.valueOf(domainKey));
                    */
            }

            userServices.createLogEntries( user, XMLTool.documentToString( doc ) );
        }
        catch ( VerticalCreateException vce )
        {
            String message = "Failed to create log entry: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, vce ), vce );
            return false;
        }
        catch ( VerticalSecurityException vse )
        {
            String message = "Failed to create log entry: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, vse ), vse );
            return false;
        }

        return true;
    }

    private void processLogout( SiteContext siteContext, HttpServletRequest request, HttpServletResponse response, HttpSession session,
                                ExtendedMap formItems, UserServicesService userServices )
        throws VerticalUserServicesException, RemoteException
    {

        UserStoreKey userStoreKey = parseUserStoreKeyFromUidAndUserstore( formItems );

        if ( session != null )
        {
            // Create log entry:
            User user = securityService.getOldUserObject();
            if ( user != null && !user.isAnonymous() )
            {
                if ( siteContext.isAuthenticationLoggingEnabled() )
                {
                    createLogEntry( siteContext, user, userServices, request.getRemoteAddr(), LogType.LOGOUT.asInteger(), userStoreKey );
                }
            }
            else
            {
                String message = "User is not logged in.";
                LOG.warn( StringUtil.expandString( message, null, null ) );
                redirectToErrorPage( request, response, formItems, ERR_USER_NOT_LOGGED_IN, null );
                return;
            }

            // Remove GUID cookie if present
            String cookieName = "guid-" + siteContext.getSiteKey();
            Cookie cookie = CookieUtil.getCookie( request, cookieName );
            if ( cookie != null )
            {
                cookie.setValue( null );
                response.addCookie( cookie );
            }

            removeGuidCookie( response, DeploymentPathResolver.getSiteDeploymentPath( request ), siteContext );
            securityService.logoutPortalUser();

            redirectToPage( request, response, formItems );
        }
    }

    private void reportFailedLogin( SiteContext siteContext, User user, UserServicesService userServices, String uid,
                                    UserStoreKey userStoreKey, String remoteIP )
        throws RemoteException
    {
        try
        {
            Document doc = XMLTool.createDocument( "logentry" );
            Element rootElement = doc.getDocumentElement();
            rootElement.setAttribute( "typekey", String.valueOf( LogType.LOGIN_FAILED.asInteger() ) );
            rootElement.setAttribute( "inetaddress", remoteIP );
            rootElement.setAttribute( "menukey", String.valueOf( siteContext.getSiteKey() ) );
            XMLTool.createElement( doc, rootElement, "title", uid );
//			Element logDataElement = XMLTool.createElement(doc, rootElement, "data");
            if ( userStoreKey != null )
            {
                /*
                    String domainName = userServices.getDomainName(domainKey);
                    Element elem = XMLTool.createElement(doc, logDataElement, "domain", domainName);
                    rootElement.setAttribute("userstorekey", String.valueOf(userStoreKey));
                    elem.setAttribute("key", String.valueOf(domainKey));
                    */
            }

            userServices.createLogEntries( user, XMLTool.documentToString( doc ) );
        }
        catch ( VerticalCreateException vce )
        {
            String message = "Failed to create log entry for failed login: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, vce ), vce );
        }
        catch ( VerticalSecurityException vse )
        {
            String message = "Failed to create log entry for failed login: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, vse ), vse );
        }
    }

    private void updateUserInSession( HttpSession session )
        throws RemoteException
    {
        // It is only needed to update logged in user in session when we store the whole user object in the session
        // this we do not do anymore...
        // Still, we leave this method here as a reminder in case we change the strategy around this.

        /*
        UserEntity user = securityService.getUser();
        if ( user != null )
        {
            session.setAttribute( "vertical_user", user.getKey() );
        }
        */
    }

    private void handlerSetPreferences( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems, SiteKey siteKey )
    {

        User olduser = securityService.getOldUserObject();
        if ( olduser == null )
        {
            String message = "User is not logged in.";
            LOG.warn( StringUtil.expandString( message, null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_USER_NOT_LOGGED_IN, null );
            return;
        }

        PortalInstanceKey instanceKey = portalInstanceKeyResolver.resolvePortalInstanceKey( formItems.getString( "_instanceKey", null ) );
        instanceKey.setSite( siteKey );
        UserEntity user = securityService.getUser( olduser.getKey() );

        String defaultScope = getDefaultScope( instanceKey );

        for ( Object keyObj : formItems.keySet() )
        {
            String key = (String) keyObj;
            if ( key.startsWith( "_" ) )
            {
                // System parameters
                continue;
            }
            String value = formItems.getString( key );

            try
            {
                PreferenceKey preferenceKey = resolvePreferenceKey( user.getKey(), instanceKey, key, defaultScope );

                if ( preferenceKey != null )
                {
                    PreferenceEntity preference = new PreferenceEntity();
                    preference.setKey( preferenceKey );
                    preference.setValue( value );
                    preferenceService.setPreference( preference );
                }
            }
            catch ( IllegalArgumentException iae )
            {
                String message = "Illegal arguments to setPreferences: " + iae.getMessage();
                LOG.warn( StringUtil.expandString( message, null, null ) );
                redirectToErrorPage( request, response, formItems, ERR_SET_PREFERENCES_INVALID_PARAMS, null );
                return;
            }
            catch ( PreferenceAccessException e )
            {
                String message = e.getMessage();
                LOG.warn( StringUtil.expandString( message, null, null ) );
                redirectToErrorPage( request, response, formItems, ERR_NOT_ALLOWED, null );
                return;
            }
        }

        redirectToPage( request, response, formItems );

    }

    private String getDefaultScope( PortalInstanceKey instanceKey )
    {
        if ( instanceKey.isWindow() )
        {
            return PreferenceScopeType.WINDOW.name();
        }
        if ( instanceKey.isMenuItem() )
        {
            return PreferenceScopeType.PAGE.name();
        }
        if ( instanceKey.getSiteKey() != null )
        {
            return PreferenceScopeType.SITE.name();
        }

        return "";
    }

    private void handlerRemovePreferences( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems,
                                           SiteKey siteKey )
    {

        User olduser = securityService.getOldUserObject();
        if ( olduser == null )
        {
            String message = "User is not logged in.";
            LOG.warn( StringUtil.expandString( message, null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_USER_NOT_LOGGED_IN, null );
            return;
        }

        PortalInstanceKey instanceKey = portalInstanceKeyResolver.resolvePortalInstanceKey( formItems.getString( "_instanceKey", null ) );
        instanceKey.setSite( siteKey );
        UserEntity user = securityService.getUser( olduser.getKey() );

        String defaultScope = getDefaultScope( instanceKey );

        String[] preferenceKeys = formItems.getStringArray( "preferenceKey" );

        for ( String preferenceKeyStr : preferenceKeys )
        {
            try
            {
                PreferenceKey preferenceKey = resolvePreferenceKey( user.getKey(), instanceKey, preferenceKeyStr, defaultScope );

                if ( preferenceKey != null )
                {
                    PreferenceEntity preferenceEntity = preferenceService.getPreference( preferenceKey );
                    preferenceService.removePreference( preferenceEntity );
                }
            }
            catch ( IllegalArgumentException iae )
            {
                String message = "Illegal arguments to removePreferences: " + iae.getMessage();
                LOG.warn( StringUtil.expandString( message, null, null ) );
                redirectToErrorPage( request, response, formItems, ERR_SET_PREFERENCES_INVALID_PARAMS, null );
                return;
            }
            catch ( PreferenceAccessException e )
            {
                String message = e.getMessage();
                LOG.warn( StringUtil.expandString( message, null, null ) );
                redirectToErrorPage( request, response, formItems, ERR_NOT_ALLOWED, null );
                return;
            }
        }

        redirectToPage( request, response, formItems );
    }

    private PreferenceKey resolvePreferenceKey( UserKey userKey, PortalInstanceKey instanceKey, String key, String defaultScope )
    {
        if ( key == null || key.length() == 0 )
        {
//            return null;
            throw new IllegalArgumentException( "Preference cannot be null or empty" );
        }

        String preferenceKeyStr;
        String scopeName;

        if ( key.contains( "$" ) )
        {
            scopeName = key.substring( 0, key.indexOf( "$" ) );
            preferenceKeyStr = key.substring( key.indexOf( "$" ) + 1 );
        }
        else
        {
            preferenceKeyStr = key;
            scopeName = defaultScope;
        }

        PreferenceScopeType scopeType = PreferenceScopeType.parse( scopeName );
        if ( scopeType == null )
        {
            throw new IllegalArgumentException( "Scope " + scopeName + " is not valid" );
        }

        PreferenceScopeKey scopeKey = resolveScopeKey( instanceKey, scopeType );

        return new PreferenceKey( userKey, scopeType, scopeKey, preferenceKeyStr );
    }

    private PreferenceScopeKey resolveScopeKey( PortalInstanceKey instanceKey, PreferenceScopeType scopeType )
    {
        boolean submitFromPageTemplate = instanceKey.getPortletKey() == null;
        if ( submitFromPageTemplate && ( scopeType == PreferenceScopeType.WINDOW || scopeType == PreferenceScopeType.PORTLET ) )
        {
            throw new IllegalArgumentException( "Scope " + scopeType.getName() + " can only be used from a portlet" );
        }
        return PreferenceScopeKeyResolver.resolve( scopeType, instanceKey, instanceKey.getSiteKey() );

    }

    public void setUserDao( final UserDao userDao )
    {
        this.userDao = userDao;
    }


}
