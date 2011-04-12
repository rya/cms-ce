/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import com.enonic.cms.core.content.category.CategoryAccessEntity;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.store.dao.GroupDao;

import com.enonic.cms.core.security.group.GroupEntity;


public class InheritContentAccessRightsAlgorithm
{
    private GroupDao groupDao;

    public void inherit( ContentEntity inheritor, CategoryEntity inheritance )
    {
        GroupEntity eaGroup = groupDao.findBuiltInEnterpriseAdministrator();

        for ( CategoryAccessEntity categoryAccess : inheritance.getAccessRights().values() )
        {
            if ( categoryAccess.getGroup().equals( eaGroup ) )
            {
                // do not add the explicit ea group
                continue;
            }

            ContentAccessEntity contentAccess = new ContentAccessEntity();
            contentAccess.setGroup( categoryAccess.getGroup() );
            contentAccess.setReadAccess( inheritReadAccess( categoryAccess ) );
            contentAccess.setUpdateAccess( inheritUpdateAccess( categoryAccess ) );
            contentAccess.setDeleteAccess( inheritDeleteAccess( categoryAccess ) );

            inheritor.addContentAccessRight( contentAccess );
        }

        inheritor.addOwnerAccessRight();
    }

    private boolean inheritReadAccess( CategoryAccessEntity categoryAccess )
    {
        return categoryAccess.givesContentReadAccess();
    }

    private boolean inheritUpdateAccess( CategoryAccessEntity categoryAccess )
    {
        return categoryAccess.givesContentUpdateAccess();
    }

    private boolean inheritDeleteAccess( CategoryAccessEntity categoryAccess )
    {
        return categoryAccess.givesContentDeleteAccess();
    }

    public void setGroupDao( GroupDao value )
    {
        this.groupDao = value;
    }
}
