package com.enonic.cms.admin.user;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.domain.EntityPageList;

import java.util.List;

public final class UserModelHelper
{
    public static UserModel toModel(final UserEntity entity)
    {
        final UserModel model = new UserModel();
        model.setKey(entity.getKey().toString());
        model.setName(entity.getName());
        model.setQualifiedName(entity.getQualifiedName().toString());
        model.setDisplayName(entity.getDisplayName());
        model.setLastModified(entity.getLastModified());

        if (entity.getUserStore() != null) {
            model.setUserStore(entity.getUserStore().getName());
        } else {
            model.setUserStore("system");
        }

        return model;
    }

    public static UsersModel toModel(final EntityPageList<UserEntity> list)
    {
        final UsersModel model = new UsersModel();
        model.setTotal(list.getTotal());

        for (final UserEntity entity : list.getList()) {
            model.addUser(toModel(entity));
        }
        
        return model;
    }
}
