package com.enonic.cms.admin.user;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.spring.PrototypeScope;
import com.enonic.cms.domain.EntityPageList;
import com.enonic.cms.store.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.ws.rs.*;

@Path("/admin/rest/users")
@PrototypeScope
@Component
@Produces("application/json")
public final class UsersResource
{
    @Autowired
    private UserDao userDao;

    @Autowired
    private UserPhotoService photoService;

    @GET
    public UsersModel getAll(
            @DefaultValue("0") @QueryParam("start") final int index,
            @DefaultValue("10") @QueryParam("limit") final int count)
    {
        final EntityPageList<UserEntity> list = this.userDao.findAll(index, count);
        return UserModelHelper.toModel(list);
    }

    @Path("{key}")
    public UserResource getUser(@PathParam("key") final String key)
    {
        final UserEntity entity = this.userDao.findByKey(key);
        if (entity == null) {
            return null;
        }

        return new UserResource(entity, this.photoService);
    }
}
