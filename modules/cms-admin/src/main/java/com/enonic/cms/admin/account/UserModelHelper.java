package com.enonic.cms.admin.account;

import com.enonic.cms.core.security.user.UserEntity;
import java.util.ArrayList;
import java.util.List;

final class UserModelHelper
{
    public static UserModel toModel(final UserEntity entity)
    {
        final UserModel model = new UserModel();
        model.setKey(entity.getKey().toString());
        model.setName(entity.getName());
        model.setEmail(entity.getEmail());
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

    public static List<UserModel> toModel(final List<UserEntity> list)
    {
        final  List<UserModel> model = new ArrayList<UserModel>();

        for (final UserEntity entity : list) {
            model.add(toModel(entity));
        }
        
        return model;
    }
}
