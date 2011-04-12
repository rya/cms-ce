/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.menuitem;

import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.security.user.UserKey;


public class UnapproveSectionContentCommand
{
    private UserKey user;

    private MenuItemKey sectionKey;

    private List<ContentKey> unapprovedContentToUpdate = new ArrayList<ContentKey>();

    public UserKey getUpdater()
    {
        return user;
    }

    public void setUpdater( UserKey user )
    {
        this.user = user;
    }

    public MenuItemKey getSection()
    {
        return sectionKey;
    }

    public void setSection( MenuItemKey sectionKey )
    {
        this.sectionKey = sectionKey;
    }

    public List<ContentKey> getUnapprovedContentToUpdate()
    {
        return unapprovedContentToUpdate;
    }

    public void addUnapprovedContentToUpdate( ContentKey key )
    {
        unapprovedContentToUpdate.add( key );
    }
}
