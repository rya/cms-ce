package com.enonic.cms.admin.group;

import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.security.group.GroupEntity;

public class GroupModelHelper
{
    public static GroupModel toModel( final GroupEntity entity )
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

    public static List<GroupModel> toListModel(final List<GroupEntity> groups ){
        List<GroupModel> groupModels = new ArrayList<GroupModel>(  );
        for (GroupEntity gr : groups){
            groupModels.add( toModel( gr ) );
        }
        return groupModels;
    }
}
