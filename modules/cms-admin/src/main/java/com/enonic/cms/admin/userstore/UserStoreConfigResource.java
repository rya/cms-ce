package com.enonic.cms.admin.userstore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.core.InjectParam;

import com.enonic.esl.containers.ExtendedMap;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.admin.common.LoadStoreRequest;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.userstore.StoreNewUserStoreCommand;
import com.enonic.cms.core.security.userstore.UpdateUserStoreCommand;
import com.enonic.cms.core.security.userstore.UserStoreConnectorManager;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.core.security.userstore.config.UserStoreConfigParser;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;


@Component
@Path("/admin/data/userstore")
@Produces("application/json")
public final class UserStoreConfigResource
{
    @Autowired
    private UserStoreService userStoreService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    UserStoreConnectorManager userStoreConnectorManager;


    @GET
    @Path("list")
    public UserStoreConfigsModel getAll( @InjectParam final LoadStoreRequest req )
    {
        final List<UserStoreEntity> list = userStoreService.findAll();
        return UserStoreConfigModelTranslator.toModel( list );
    }

    @GET
    @Path("detail")
    public UserStoreConfigModel getDetail( @QueryParam("name") @DefaultValue("") final String name,
                                           @InjectParam final LoadStoreRequest req )
    {
        final UserStoreEntity store = userStoreService.findByName( name );
        return UserStoreConfigModelTranslator.toModelWithFields( store );
    }

    @GET
    @Path("config")
    public UserStoreConfigModel getConfig( @QueryParam("name") @DefaultValue("") final String name,
                                           @InjectParam final LoadStoreRequest req )
    {
        final UserStoreEntity store = userStoreService.findByName( name );
        return UserStoreConfigModelTranslator.toModelWithXML( store );
    }

    @POST
    @Path("config")
    public Map<String, Object> postConfig( @FormParam("key") final String key, @FormParam("name") final String name,
                                           @FormParam("connectorName") @DefaultValue("") final String connectorName,
                                           @FormParam("defaultStore") final String defaultStore,
                                           @FormParam("configXML") final String configXML )
    {
        Map<String, Object> out = new HashMap<String, Object>();

        //User user = securityService.getLoggedInPortalUser();
        User user = securityService.getUser( new QualifiedUsername( "", "admin" ) );

        if ( user == null )
        {
            out.put( "msg", "Anonymous users can't create or update userstores." );
            out.put( "success", false );
            return out;
        }

        UserStoreEntity duplicate = userStoreService.findByName( name );
        if ( duplicate != null && ( StringUtils.isEmpty( key ) ||
                StringUtils.isNotEmpty( key ) && !key.equals( duplicate.getKey().toString() ) ) )
        {

            out.put( "msg", "The userstore with such name already exists." );
            out.put( "success", false );
            return out;
        }

        ExtendedMap formItems = new ExtendedMap( 5 );
        formItems.putString( "key", key );
        formItems.putString( "name", name );
        formItems.putBoolean( "defaultUserstore", BooleanUtils.toBoolean( defaultStore ) );
        formItems.putString( "config", configXML );
        formItems.putString( "connectorName", connectorName );

        if ( StringUtils.isNotEmpty( key ) )
        {
            updateUserstore( user, formItems );
        }
        else
        {
            createUserstore( user, formItems );
        }
        out.put( "success", true );
        return out;
    }


    @GET
    @Path("connectors")
    public UserStoreConnectorsModel getConnectors( @InjectParam final LoadStoreRequest req ) {
        Map<String, UserStoreConnectorConfig> map
                = userStoreConnectorManager.getUserStoreConnectorConfigs();
        return UserStoreConfigModelTranslator.toModel( map );
    }


    private void updateUserstore( User user, ExtendedMap formItems )
    {
        final UserStoreKey userStoreKey = new UserStoreKey( formItems.getString( "key" ) );
        final boolean newDefaultUserStore = formItems.getBoolean( "defaultUserstore", false );
        final String connectorName = formItems.getString( "connectorName", null );
        final String configXmlString = formItems.getString( "config", null );

        UserStoreConfig config = new UserStoreConfig();
        if ( configXmlString != null && configXmlString.trim().length() > 0 )
        {
            config = UserStoreConfigParser.parse( XMLDocumentFactory.create( configXmlString ).getRootElement(),
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

    private void createUserstore( User user, ExtendedMap formItems )
    {
        final String name = formItems.getString( "name", null );
        final boolean defaultUserStore = formItems.getBoolean( "defaultUserstore", false );
        final String connectorName = formItems.getString( "connectorName", null );
        final String configXmlString = formItems.getString( "config", null );

        UserStoreConfig config = new UserStoreConfig();
        if ( configXmlString != null && configXmlString.trim().length() > 0 )
        {
            config = UserStoreConfigParser.parse( XMLDocumentFactory.create( configXmlString ).getRootElement(),
                                                  connectorName != null );
        }

        final StoreNewUserStoreCommand command = new StoreNewUserStoreCommand();
        if ( defaultUserStore )
        {
            command.setDefaultStore( defaultUserStore );
        }
        command.setStorer( user.getKey() );
        command.setName( name );
        command.setDefaultStore( formItems.getBoolean( "defaultUserstore", false ) );
        command.setConnectorName( connectorName );
        command.setConfig( config );

        userStoreService.storeNewUserStore( command );
    }

}
