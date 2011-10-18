package com.enonic.cms.core.structure.menuitem;


import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.security.user.UserKey;

public class ApproveContentsInSectionCommand
    implements MenuItemServiceCommand
{
    private UserKey approver;

    private MenuItemKey sectionKey;

    private List<ContentKey> contentsToApprove = new ArrayList<ContentKey>();

    private OrderContentsInSectionCommand orderContentsInSectionCommand;

    public UserKey getApprover()
    {
        return approver;
    }

    public void setApprover( UserKey user )
    {
        this.approver = user;
    }

    public MenuItemKey getSection()
    {
        return sectionKey;
    }

    public void setSection( MenuItemKey sectionKey )
    {
        this.sectionKey = sectionKey;
    }

    public List<ContentKey> getContentsToApprove()
    {
        return contentsToApprove;
    }

    public void addContentToApprove( ContentKey key )
    {
        contentsToApprove.add( key );
    }

    public OrderContentsInSectionCommand createAndReturnOrderContentsInSectionCommand()
    {
        OrderContentsInSectionCommand command = new OrderContentsInSectionCommand();
        this.orderContentsInSectionCommand = command;
        return command;
    }

    public OrderContentsInSectionCommand getOrderContentsInSectionCommand()
    {
        return orderContentsInSectionCommand;
    }

    public boolean hasContentToApprove()
    {
        return !contentsToApprove.isEmpty();
    }
}
