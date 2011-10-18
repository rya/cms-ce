package com.enonic.cms.core.structure.menuitem;


import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.security.user.UserKey;

public class UnapproveContentsInSectionCommand
    implements MenuItemServiceCommand
{
    private UserKey unapprover;

    private MenuItemKey sectionKey;

    private List<ContentKey> contentToUnapprove = new ArrayList<ContentKey>();

    public UserKey getUnapprover()
    {
        return unapprover;
    }

    public void setUnapprover( UserKey user )
    {
        this.unapprover = user;
    }

    public MenuItemKey getSection()
    {
        return sectionKey;
    }

    public void setSection( MenuItemKey sectionKey )
    {
        this.sectionKey = sectionKey;
    }

    public List<ContentKey> getContentToUnapprove()
    {
        return contentToUnapprove;
    }

    public void addContentToUnapprove( ContentKey key )
    {
        contentToUnapprove.add( key );
    }

    public boolean hasContentToUnapprove()
    {
        return !contentToUnapprove.isEmpty();
    }
}
