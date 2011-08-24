package com.enonic.cms.admin.group;

import com.enonic.cms.core.security.group.GroupEntity;

public class GroupModelHelper
{
    public static GroupModel toAModel( final GroupEntity entity )
    {
        final GroupModel model = new GroupModel();
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
}
