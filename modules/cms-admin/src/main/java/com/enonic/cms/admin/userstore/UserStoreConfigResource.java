package com.enonic.cms.admin.userstore;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.core.InjectParam;

import com.enonic.cms.admin.common.LoadStoreRequest;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreService;


@Component
@Path("/admin/data/userstore")
@Produces("application/json")
public final class UserStoreConfigResource
{
    @Autowired
    private UserStoreService userStoreService;


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

}
