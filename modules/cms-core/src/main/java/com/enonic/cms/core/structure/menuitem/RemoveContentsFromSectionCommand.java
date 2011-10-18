package com.enonic.cms.core.structure.menuitem;


import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.security.user.UserKey;

public class RemoveContentsFromSectionCommand
    implements MenuItemServiceCommand
{
    private UserKey remover;

    private MenuItemKey sectionKey;

    private List<ContentKey> contentsToRemove = new ArrayList<ContentKey>();

    public UserKey getRemover()
    {
        return remover;
    }

    public void setRemover( UserKey user )
    {
        this.remover = user;
    }

    public MenuItemKey getSection()
    {
        return sectionKey;
    }

    public void setSection( MenuItemKey sectionKey )
    {
        this.sectionKey = sectionKey;
    }

    public boolean hasContentToRemove()
    {
        return !contentsToRemove.isEmpty();
    }

    public List<ContentKey> getContentsToRemove()
    {
        return contentsToRemove;
    }

    public void addContentToRemove( ContentKey key )
    {
        contentsToRemove.add( key );
    }
}
