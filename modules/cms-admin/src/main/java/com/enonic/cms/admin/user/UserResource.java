package com.enonic.cms.admin.user;

import com.enonic.cms.core.security.user.UserEntity;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Produces("application/json")
public class UserResource
{
    private final UserEntity entity;
    private final UserPhotoService service;

    public UserResource(final UserEntity entity, final UserPhotoService service)
    {
        this.entity = entity;
        this.service = service;
    }

    @GET
    public UserModel getUser()
    {
        return UserModelHelper.toModel(this.entity);
    }

    @GET
    @Path("photo")
    @Produces("image/png")
    public byte[] getPhoto()
        throws Exception
    {
        return this.service.renderPhoto(this.entity, 100);
    }

    @GET
    @Path("photo/thumb")
    @Produces("image/png")
    public byte[] getPhotoThumbnail()
        throws Exception
    {
        return this.service.renderPhoto(this.entity, 40);
    }
}
