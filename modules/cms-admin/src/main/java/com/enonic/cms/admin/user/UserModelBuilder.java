package com.enonic.cms.admin.user;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.domain.EntityPageList;

final class UserModelBuilder
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
        }

        return model;
    }

    public static UserListModel toModel(final EntityPageList<UserEntity> list)
    {
        final UserListModel model = new UserListModel();
        model.setTotal(list.getTotal());

        for (final UserEntity entity : list.getList()) {
            model.add(toModel(entity));
        }

        return model;
    }
}
