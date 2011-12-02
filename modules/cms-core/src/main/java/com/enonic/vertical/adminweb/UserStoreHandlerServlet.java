/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.lang.StringUtils;
import org.jdom.transform.JDOMSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.net.URL;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.adminweb.wizard.Wizard;
import com.enonic.vertical.adminweb.wizard.WizardException;
import com.enonic.vertical.adminweb.wizard.WizardLogger;
import com.enonic.vertical.engine.VerticalEngineException;

import com.enonic.cms.framework.util.JDOMUtil;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.security.user.DeleteUserStoreCommand;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.StoreNewUserStoreCommand;
import com.enonic.cms.core.security.userstore.UpdateUserStoreCommand;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.security.userstore.UserStoreXmlCreator;
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.core.security.userstore.config.UserStoreConfigParser;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfigXmlCreator;
import com.enonic.cms.core.security.userstore.connector.synchronize.SynchronizeUserStoreJob;
import com.enonic.cms.core.security.userstore.connector.synchronize.SynchronizeUserStoreType;
import com.enonic.cms.core.service.AdminService;

import com.enonic.cms.core.security.userstore.DeleteUserStoreJob;

import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;


public class UserStoreHandlerServlet
    extends AdminHandlerBaseServlet
{
    private static final String WIZARD_CONFIG_CREATE_UPDATE = "wizardconfig_create_update_userstore.xml";

    private static final String ERROR_XMLPARSING = "2";

    private static final String ERROR_NAMEILLEGALCHARS = "19";

    private static final String ERROR_CONNECTOR = "20";

    public static class CreateUpdateUserStoreWizard
        extends Wizard
    {
        @Autowired
        private UserStoreService userStoreService;

        public CreateUpdateUserStoreWizard()
        {
            super();
        }


        protected void initialize( AdminService admin, Document wizardConfigDoc )
            throws WizardException
        {
        }


        protected boolean validateState( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems )
        {
            boolean state = true;
            final String userStoreType = formItems.getString( "userStoreType" );
            String connectorName = null;
            if ( userStoreType.equals( "remote" ) )
            {
                connectorName = formItems.getString( "remoteUserStoreConnector", null );

                final String errorMessageConnector = verifyConnector( connectorName );
                if ( errorMessageConnector != null )
                {
                    wizardState.addError( ERROR_CONNECTOR, "connector", errorMessageConnector );
                    state = false;
                }

            }
            if ( state == true )
            {
                final String errorMessageConfig = verifyConfig( formItems.getString( "config", null ), connectorName );
                if ( errorMessageConfig != null )
                {
                    wizardState.addError( ERROR_XMLPARSING, "config", errorMessageConfig );
                    state = false;
                }
            }

            final String userStoreName = formItems.getString( "name" );
            final String userStoreNameRegExp = "[a-zA-Z0-9_-]+";
            if ( !userStoreName.matches( userStoreNameRegExp ) )
            {
                wizardState.addError( ERROR_NAMEILLEGALCHARS, "name" );
                state = false;

            }
            return state;
        }


        protected boolean evaluate( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems,
                                    String testCondition )
            throws WizardException
        {

            return true;
        }


        protected void processWizardData( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems,
                                          User user, Document dataDoc )
            throws WizardException, VerticalEngineException
        {
            if ( StringUtils.isNotEmpty( formItems.getString( "key", null ) ) )
            {
                updateUserStore( formItems, user, userStoreService );
            }
            else
            {
                storeNewUserStore( formItems, user, userStoreService );
            }
        }

        protected void appendCustomData( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems,
                                         ExtendedMap parameters, User user, Document dataconfigDoc, Document wizarddataDoc )
            throws WizardException
        {
            Element wizarddataElem = wizarddataDoc.getDocumentElement();

            Step currentStep = wizardState.getCurrentStep();
            if ( "step0".equals( currentStep.getName() ) )
            {
                Map<String, UserStoreConnectorConfig> userStoreConnectorConfigs = userStoreService.getUserStoreConnectorConfigs();

                if ( formItems.containsKey( "key" ) )
                {
                    int key = formItems.getInt( "key" );
                    final UserStoreXmlCreator userStoreXmlCreator = new UserStoreXmlCreator( userStoreConnectorConfigs );
                    UserStoreEntity userStore = userStoreService.getUserStore( new UserStoreKey( key ) );
                    XMLDocument userStoresXmlDoc = XMLDocumentFactory.create( userStoreXmlCreator.createUserStoresDocument( userStore ) );
                    wizarddataElem.appendChild(
                        wizarddataDoc.importNode( userStoresXmlDoc.getAsDOMDocument().getDocumentElement(), true ) );
                }

                // Default User Store
                Element defaultUserStoreElem = XMLTool.createElement( wizarddataElem, "defaultuserstore" );
                UserStoreEntity defaultUserStore = userStoreService.getDefaultUserStore();
                if ( defaultUserStore != null )
                {
                    defaultUserStoreElem.setAttribute( "key", defaultUserStore.getKey().toString() );
                }
                wizarddataElem.appendChild( wizarddataDoc.importNode( defaultUserStoreElem, true ) );

                // UserStore connector config names

                final XMLDocument userStoreConnectorConfigNamesXmlDoc = XMLDocumentFactory.create(
                    UserStoreConnectorConfigXmlCreator.createUserStoreConnectorConfigsDocument(
                            userStoreConnectorConfigs.values() ) );
                wizarddataElem.appendChild(
                    wizarddataDoc.importNode( userStoreConnectorConfigNamesXmlDoc.getAsDOMDocument().getDocumentElement(), true ) );
            }
            else
            {
                String message = "Unknown step: {0}";
                VerticalAdminLogger.error(message, currentStep.getName(), null );
            }
        }


        protected void saveState( WizardState wizardState, HttpServletRequest request, HttpServletResponse response, AdminService admin,
                                  User user, ExtendedMap formItems )
            throws WizardException
        {
            super.saveState( wizardState, request, response, admin, user, formItems );

            // get step state document
            StepState stepState = wizardState.getCurrentStepState();
            Document stepstateDoc = stepState.getStateDoc();
            Element rootElem = stepstateDoc.getDocumentElement();

            Step currentStep = wizardState.getCurrentStep();
            if ( "step0".equals( currentStep.getName() ) )
            {
                final Element userStoreElem = XMLTool.createElement( stepstateDoc, rootElem, "userstore" );

                if ( formItems.containsKey( "key" ) )
                {
                    userStoreElem.setAttribute( "key", formItems.getString( "key" ) );
                }
                userStoreElem.setAttribute( "name", formItems.getString( "name" ) );
                userStoreElem.setAttribute( "default", formItems.getString( "defaultuserstore", "false" ) );

                final String userStoreType = formItems.getString( "userStoreType" );
                userStoreElem.setAttribute( "remote", userStoreType.equals( "remote" ) ? "true" : "false" );
                if ( userStoreType.equals( "remote" ) )
                {
                    userStoreElem.setAttribute( "connector", formItems.getString( "remoteUserStoreConnector", null ) );
                }
                XMLTool.createElement( stepstateDoc, userStoreElem, "configRaw", formItems.getString( "config", "" ) );
            }
            else
            {
                String message = "Unknown step: {0}";
                WizardLogger.errorWizard(message, currentStep );
            }
        }

        private String verifyConnector( final String connectorName )
        {
            try
            {
                userStoreService.verifyUserStoreConnector( connectorName );
            }
            catch ( Exception e )
            {
                return e.getMessage();
            }
            return null;
        }

        private String verifyConfig( final String xmlConfigData, final String connectorName )
        {
            try
            {
                org.jdom.Element configEl = null;
                if ( StringUtils.isNotEmpty( xmlConfigData.trim() ) )
                {
                    configEl = JDOMUtil.parseDocument( xmlConfigData ).getRootElement();
                }
                final UserStoreConfig config = UserStoreConfigParser.parse( configEl, connectorName != null );
                if ( connectorName != null )
                {
                    userStoreService.verifyUserStoreConnectorConfig( config, connectorName );
                }
            }
            catch ( Exception e )
            {
                return e.getMessage();
            }
            return null;
        }

        private void updateUserStore( ExtendedMap formItems, User user, UserStoreService userStoreService )
        {
            final UserStoreKey userStoreKey = new UserStoreKey( formItems.getString( "key" ) );

            final boolean newDefaultUserStore = formItems.getBoolean( "defaultuserstore", false );
            final String userStoreType = formItems.getString( "userStoreType" );
            String connectorName = null;
            if ( userStoreType.equals( "remote" ) )
            {
                connectorName = formItems.getString( "remoteUserStoreConnector", null );
            }
            final String configXmlString = formItems.getString( "config", null );
            UserStoreConfig config = new UserStoreConfig();
            if ( configXmlString != null && configXmlString.trim().length() > 0 )
            {
                config = UserStoreConfigParser.parse( XMLDocumentFactory.create( configXmlString ).getAsJDOMDocument().getRootElement(),
                                                      connectorName != null );
            }

            final UpdateUserStoreCommand command = new UpdateUserStoreCommand();
            command.setUpdater( user.getKey() );
            command.setKey( userStoreKey );
            command.setName( formItems.getString( "name", null ) );
            if ( newDefaultUserStore )
            {
                command.setAsNewDefaultStore();
            }
            command.setConnectorName( connectorName );
            command.setConfig( config );

            userStoreService.updateUserStore( command );

            userStoreService.invalidateUserStoreCachedConfig( command.getKey() );
        }

        private void storeNewUserStore( ExtendedMap formItems, User user, UserStoreService userStoreService )
        {
            final String userStoreType = formItems.getString( "userStoreType" );
            String connectorName = null;
            if ( userStoreType.equals( "remote" ) )
            {
                connectorName = formItems.getString( "remoteUserStoreConnector", null );
            }
            final String configXmlString = formItems.getString( "config", null );
            UserStoreConfig config = new UserStoreConfig();
            if ( configXmlString != null && configXmlString.trim().length() > 0 )
            {
                config = UserStoreConfigParser.parse(
                        XMLDocumentFactory.create( configXmlString ).getAsJDOMDocument().getRootElement(),
                        connectorName != null );
            }

            final StoreNewUserStoreCommand command = new StoreNewUserStoreCommand();
            command.setStorer( user.getKey() );
            command.setName( formItems.getString( "name", null ) );
            command.setDefaultStore( formItems.getBoolean( "defaultuserstore", false ) );
            command.setConnectorName( connectorName );
            command.setConfig( config );

            userStoreService.storeNewUserStore( command );
        }
    }

    private User verifyAccess( UserStoreKey userStoreKey )
        throws VerticalAdminException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        UserEntity userEntity = userDao.findByKey( user.getKey() );
        if ( userEntity.isEnterpriseAdmin() || userEntity.isAdministrator() )
        {
            return user;
        }

        UserStoreEntity userStore = userStoreDao.findByKey( userStoreKey );
        if ( userStoreKey != null && userEntity.isUserstoreAdmin( userStore ) )
        {
            return user;
        }

        VerticalAdminLogger.errorAdmin("Not authorized." );
        return null;
    }


    public void handlerBrowse( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException
    {
//        verifyAccess( null );
        User user = securityService.getLoggedInAdminConsoleUser();
        UserEntity userEntity = userDao.findByKey( user.getKey() );

        final UserStoreXmlCreator xmlCreator = new UserStoreXmlCreator( userStoreService.getUserStoreConnectorConfigs() );

        final List<UserStoreEntity> userStores = securityService.getUserStores();

        final List<UserStoreEntity> validUserStores = new ArrayList<UserStoreEntity>();

        for ( UserStoreEntity userStoreEntity : userStores )
        {
            if ( memberOfResolver.hasUserStoreAdministratorPowers( userEntity, userStoreEntity.getKey() ) )
            {
                validUserStores.add( userStoreEntity );
            }
        }

        final org.jdom.Document userStoresXml = xmlCreator.createPagedDocument( validUserStores, 0, 100 );

        Source xslSource = AdminStore.getStylesheet( session, "userstore_browse.xsl" );

        // parameters
        ExtendedMap xslParams = new ExtendedMap();
        xslParams.put( "page", formItems.getString( "page" ) );
        addSortParamteres( "@name", "ascending", formItems, session, xslParams );
        addAccessLevelParameters( user, xslParams );

        if ( formItems.containsKey( "reload" ) )
        {
            xslParams.put( "reload", "true" );
        }
        try
        {
            transformXML( session, response.getWriter(), new JDOMSource( userStoresXml ), xslSource, xslParams );
        }
        catch ( IOException e )
        {
            VerticalAdminLogger.errorAdmin("I/O error: %t", e );
        }
        catch ( TransformerException e )
        {
            VerticalAdminLogger.errorAdmin("XSLT error: %t", e );
        }
    }

    public void handlerForm( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        verifyAccess( UserStoreKey.parse( formItems.getString( "key", null ) ) );

        URL url = new URL( request.getHeader( "referer" ) );
        url.setParameter( "reload", "true" );
        formItems.put( "redirect", url.toString() );
        ExtendedMap parameters = new ExtendedMap();
        User user = securityService.getLoggedInAdminConsoleUser();

        Wizard createUpdateWizard = Wizard.getInstance( admin, applicationContext, this, session, formItems, WIZARD_CONFIG_CREATE_UPDATE );
        createUpdateWizard.processRequest( request, response, session, admin, formItems, parameters, user );
    }

    public void handlerRemove( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, int key )
        throws VerticalAdminException, VerticalEngineException
    {
        User loggedInUser = securityService.getLoggedInAdminConsoleUser();

        Assert.isTrue( StringUtils.isNotEmpty( formItems.getString( "key", null ) ), "UserStore key required" );

        final DeleteUserStoreCommand command = new DeleteUserStoreCommand();
        command.setKey( new UserStoreKey( formItems.getString( "key" ) ) );
        command.setDeleter( loggedInUser.getKey() );

        final int batchSize = 20;
        final DeleteUserStoreJob job = new DeleteUserStoreJob( userStoreService, command, batchSize );
        job.start();

        if ( formItems.containsKey( "redirect_to" ) )
        {
            redirectClientToAdminPath( formItems.getString( "redirect_to" ), request, response );
        }
        else
        {
            ExtendedMap params = new ExtendedMap();
            params.put( "page", formItems.getString( "page" ) );
            params.put( "op", "browse" );
            params.put( "reload", "true" );
            redirectClientToAdminPath( "adminpage", params, request, response );
        }
    }

    public void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, String operation )
        throws VerticalEngineException, VerticalAdminException
    {

        User user = securityService.getLoggedInAdminConsoleUser();

        SynchronizeUserStoreType syncType = null;

        if ( "synchronize_all".equals( operation ) )
        {
            syncType = SynchronizeUserStoreType.USERS_AND_GROUPS;
        }
        else if ( "synchronize_groups".equals( operation ) )
        {
            syncType = SynchronizeUserStoreType.GROUPS_ONLY;
        }
        else if ( "synchronize_users".equals( operation ) )
        {
            syncType = SynchronizeUserStoreType.USERS_ONLY;
        }

        if ( syncType != null )
        {
            final UserStoreKey userStoreKey = new UserStoreKey( formItems.getInt( "domainkey" ) );
            final SynchronizeUserStoreJob job = synchronizeUserStoreJobFactory.createSynchronizeUserStoreJob( userStoreKey, syncType, 5 );
            job.start();

            if ( formItems.containsKey( "redirect_to" ) )
            {
                String path = (String) formItems.get( "redirect_to" );
                redirectClientToAdminPath( path, request, response );
            }
            else
            {
                ExtendedMap params = new ExtendedMap();
                params.put( "page", formItems.getString( "page" ) );
                params.put( "op", "browse" );
                redirectClientToAdminPath( "adminpage", params, request, response );
            }
        }

        else if ( "page".equals( operation ) )
        {
            handlerPage( request, response, session, admin, formItems, operation );
        }
    }

    private void handlerPage( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                              ExtendedMap formItems, String operation )
        throws VerticalEngineException, VerticalAdminException
    {
        User user = securityService.getLoggedInAdminConsoleUser();

        UserStoreKey userStoreKey = new UserStoreKey( formItems.getInt( "key" ) );

        final UserStoreXmlCreator userStoreXmlCreator = new UserStoreXmlCreator( userStoreService.getUserStoreConnectorConfigs() );
        UserStoreEntity userStore = userStoreService.getUserStore( userStoreKey );
        XMLDocument userStoresXmlDoc = XMLDocumentFactory.create( userStoreXmlCreator.createUserStoresDocument( userStore ) );

        Document dataDoc = userStoresXmlDoc.getAsDOMDocument();
        try
        {
            UserEntity userEntity = userDao.findByKey( user.getKey() );
            boolean isUserStoreAdministrator = userEntity.isUserstoreAdmin( userStore ) || userEntity.isEnterpriseAdmin();
            // parameters
            ExtendedMap xslParams = new ExtendedMap();
            xslParams.put( "page", formItems.getString( "page" ) );
            xslParams.put( "key", String.valueOf( userStoreKey ) );
            xslParams.put( "reload", formItems.getString( "reload", "" ) );
            xslParams.put( "userstorekey", userStoreKey.toString() );
            xslParams.put( "userstorename", userStore.getName() );
            addCommonParameters( admin, user, request, xslParams, -1, -1 );
            addAccessLevelParameters( user, xslParams );
            xslParams.put( "userstoreadmin", isUserStoreAdministrator );

            boolean canSyncUsers = false;
            boolean canSyncGroups = false;
            try
            {
                canSyncUsers = userStoreService.canSynchronizeUsers( userStoreKey );
                canSyncGroups = userStoreService.canSynchronizeGroups( userStoreKey );
            }
            catch ( final Exception e )
            {
                xslParams.put( "userStoreConfigError", e.getMessage() );
            }
            xslParams.put( "synchronizeUsers", canSyncUsers );
            xslParams.put( "synchronizeGroups", canSyncGroups );

            Source xslSource = AdminStore.getStylesheet( session, "userstore_page.xsl" );
            Source xmlSource = new DOMSource( dataDoc );
            transformXML( session, response.getWriter(), xmlSource, xslSource, xslParams );
        }
        catch ( TransformerException e )
        {
            VerticalAdminLogger.errorAdmin("XSLT error: %t", e );
        }
        catch ( IOException e )
        {
            VerticalAdminLogger.errorAdmin("I/O error: %t", e );
        }
    }
}
