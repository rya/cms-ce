package com.enonic.cms.admin.account;

import java.util.List;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.user.UserEntity;

public final class AccountModelHelper
{
    private static AccountModel toAModel( final UserEntity entity )
    {
        final AccountModel model = new AccountModel();
        model.setUser( true );
        model.setKey( entity.getKey().toString() );
        model.setName( entity.getName() );
        model.setEmail( entity.getEmail() );
        model.setQualifiedName( entity.getQualifiedName().toString() );
        model.setDisplayName( entity.getDisplayName() );
        model.setLastModified( entity.getLastModified() );

        if ( entity.getUserStore() != null )
        {
            model.setUserStore( entity.getUserStore().getName() );
        }
        else
        {
            model.setUserStore( "system" );
        }

        return model;
    }

    private static AccountModel toAModel( final GroupEntity entity )
    {
        final AccountModel model = new AccountModel();
        model.setUser( false );
        model.setKey( entity.getGroupKey().toString() );
        model.setName( entity.getName() );
        model.setQualifiedName( entity.getQualifiedName().toString() );
        model.setDisplayName( entity.getDisplayName() );
        model.setLastModified( entity.getLastModified() );

        if ( entity.getUserStore() != null )
        {
            model.setUserStore( entity.getUserStore().getName() );
        }
        else
        {
            model.setUserStore( "system" );
        }

        return model;
    }

    public static AccountsModel toModel( final List<UserEntity> userList, final List<GroupEntity> groupList )
    {
        final AccountsModel model = new AccountsModel();
        model.setTotal( userList.size() + groupList.size() );

        for ( final UserEntity entity : userList )
        {
            AccountModel aModel = toAModel( entity );
            model.addAccount( aModel );
        }
        for ( final GroupEntity entity : groupList )
        {
            AccountModel aModel = toAModel( entity );
            model.addAccount( aModel );
        }

        return model;
    }
}
