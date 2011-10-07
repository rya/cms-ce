package com.enonic.cms.admin.user;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import com.enonic.cms.core.security.user.StoreNewUserCommand;
import com.enonic.cms.core.security.user.UpdateUserCommand;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.domain.EntityPageList;

@Component
@Path("/admin/data/user")
@Produces("application/json")
public final class UsersResource
{

    private static final Logger LOG = LoggerFactory.getLogger( UsersResource.class );

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserStoreService userStoreService;

    @Autowired
    private UserPhotoService photoService;

    @Autowired
    private UserModelTranslator userModelTranslator;

    @GET
    @Path("list")
    public UsersModel getAll( @InjectParam final UserLoadRequest req )
    {
        final EntityPageList<UserEntity> list =
                this.userDao.findAll( req.getStart(), req.getLimit(), req.buildHqlQuery(), req.buildHqlOrder() );
        return userModelTranslator.toModel( list );
    }

    @GET
    @Path("userinfo")
    public UserModel getUserInfo( @QueryParam("key") final String key )
    {
        final UserEntity entity = findEntity( key );
        return userModelTranslator.toUserInfoModel( entity );
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
    @Path("/photo")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces("text/html")
    public String uploadPhoto( @FormDataParam("photo") InputStream fileInputStream,
                               @FormDataParam("photo") FormDataContentDisposition fileDetail,
                               @Context ServletContext context )
    {
        String response;
        try
        {
            File folder = new File( context.getRealPath( "/admin/resources/uploads/" ) );
            if ( !folder.exists() )
            {
                folder.mkdirs();
            }
            File file = new File( folder.getPath() + "/" + fileDetail.getFileName() );
            if ( file.exists() )
            {
                file.delete();
            }
            int read;
            byte[] bytes = new byte[1024];
            OutputStream out = new FileOutputStream( file );
            while ( ( read = fileInputStream.read( bytes ) ) != -1 )
            {
                out.write( bytes, 0, read );
            }
            out.flush();
            out.close();
            response = "{ src: \"/admin/resources/uploads/" + fileDetail.getFileName() + "\", success : true }";
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            response = "{ success: false }";
        }
        // fileupload expects textarea because of using iframe
        // on line 20732 of ext-all-debug.js
        return "<textarea>" + response + "</textarea>";
    }

    @POST
    @Path("changepassword")
    public Map<String, Object> changePassword( @FormParam("pwd") final String password,
                                               @FormParam("userKey") final String userKey )
    {

        Map<String, Object> res = new HashMap<String, Object>();
        if ( password.length() <= User.MAX_PASSWORD_LENGTH && password.length() >= User.MIN_PASSWORD_LENGTH )
        {
            LOG.info( "Password has been changed for user " + userKey );
            res.put( "success", true );
        }
        else
        {
            res.put( "success", false );
            res.put( "errorMsg", "Password is out of possible length" );
        }
        return res;
    }

    @POST
    @Path("delete")
    public Map<String, Object> deleteUser( @FormParam("userKey") final String userKey )
    {
        Map<String, Object> res = new HashMap<String, Object>();
        LOG.info( "User was deleted: " + userKey );
        res.put( "success", true );
        return res;
    }

    @POST
    @Path("update")
    @Consumes("application/json")
    public Map<String, Object> saveUser( UserModel userData )
    {
        boolean isValid =
                StringUtils.isNotBlank( userData.getDisplayName() ) && StringUtils.isNotBlank( userData.getName() ) &&
                        StringUtils.isNotBlank( userData.getEmail() );
        Map<String, Object> res = new HashMap<String, Object>();
        if ( isValid )
        {
            if ( userData.getKey() == null )
            {
                StoreNewUserCommand command = userModelTranslator.toNewUserCommand( userData );
                userStoreService.storeNewUser( command );
            }
            else
            {
                UpdateUserCommand command = userModelTranslator.toUpdateUserCommand( userData );
                userStoreService.updateUser( command );
            }
            res.put( "success", true );
        }
        else
        {
            res.put( "success", false );
            res.put( "error", "Validation was failed" );
        }
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
