package com.enonic.cms.admin.user;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.domain.EntityPageList;
import com.enonic.cms.store.dao.UserDao;
import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.core.InjectParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.ws.rs.*;

@Component
@Path("/rest/users")
@Produces("application/json")
public final class UsersResource
{
    @Autowired
    private UserDao userDao;

    @Autowired
    private UserPhotoService photoService;

    @GET
    public UsersModel getAll(@InjectParam final UserLoadRequest req)
    {
        final EntityPageList<UserEntity> list = this.userDao.findAll(req.getIndex(), req.getCount(),
                req.buildHqlQuery(), req.buildHqlOrder());
        return UserModelHelper.toModel(list);
    }

    @GET
    @Path("{key}")
    public UserModel getUser(@PathParam("key") final String key)
    {
        final UserEntity entity = findEntity(key);
        return UserModelHelper.toModel(entity);
    }

    @GET
    @Path("{key}/photo")
    @Produces("image/png")
    public byte[] getPhoto(@PathParam("key") final String key)
        throws Exception
    {
        final UserEntity entity = findEntity(key);
        return this.photoService.renderPhoto(entity, 100);
    }

    @GET
    @Path("{key}/photo/thumb")
    @Produces("image/png")
    public byte[] getPhotoThumbnail(@PathParam("key") final String key)
        throws Exception
    {
        final UserEntity entity = findEntity(key);
        return this.photoService.renderPhoto(entity, 40);
    }

    private UserEntity findEntity(final String key)
    {
        final UserEntity entity = this.userDao.findByKey(key);
        if (entity == null) {
            throw new NotFoundException();
        }

        return entity;
    }
}
