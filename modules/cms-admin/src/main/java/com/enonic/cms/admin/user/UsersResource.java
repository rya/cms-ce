package com.enonic.cms.admin.user;

import java.util.HashMap;
import java.util.Map;

import com.enonic.cms.core.security.user.UserEntity;

import com.enonic.cms.domain.EntityPageList;

import com.enonic.cms.store.dao.UserDao;

import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.core.InjectParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;

@Component
@Path("/admin/data/user")
@Produces("application/json")
public final class UsersResource
{
    @Autowired
    private UserDao userDao;

    @Autowired
    private UserPhotoService photoService;

    @GET
    @Path("list")
    public UsersModel getAll( @InjectParam final UserLoadRequest req )
    {
        final EntityPageList<UserEntity> list =
                this.userDao.findAll( req.getStart(), req.getLimit(), req.buildHqlQuery(), req.buildHqlOrder() );
        return UserModelHelper.toModel( list );
    }

    @GET
    @Path("detail")
    public UserModel getUser( @QueryParam("key") final String key )
    {
        final UserEntity entity = findEntity( key );
        return UserModelHelper.toModel( entity );
    }

    @GET
    @Path("photo")
    @Produces("image/png")
    public byte[] getPhoto( @QueryParam("key") final String key,
                            @QueryParam("thumb") @DefaultValue("false") final boolean thumb )
            throws Exception
    {
        final UserEntity entity = findEntity( key );
        return this.photoService.renderPhoto( entity, thumb ? 40 : 100 );
    }

    @POST
    @Path("changepassword")
    public Map<String, Object> changePassword( @QueryParam("pwd") final String password,
                                               @QueryParam("repeatpwd") final String repeatPassword )
    {

        Map<String, Object> res = new HashMap<String, Object>();
        res.put( "success", true );
        return res;
    }

    private UserEntity findEntity( final String key )
    {
        if ( key == null )
        {
            throw new NotFoundException();
        }

        final UserEntity entity = this.userDao.findByKey( key );
        if ( entity == null )
        {
            throw new NotFoundException();
        }

        return entity;
    }

}
