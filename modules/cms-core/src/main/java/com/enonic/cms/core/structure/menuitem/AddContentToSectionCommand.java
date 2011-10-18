package com.enonic.cms.core.structure.menuitem;


import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.security.user.UserKey;

/**
 * Oct 17, 2010
 */
public class AddContentToSectionCommand
    implements MenuItemServiceCommand
{
    private UserKey contributor;

    private ContentKey content;

    private MenuItemKey section;

    private boolean approveInSection = false;

    private boolean addOnTop = true;

    private OrderContentsInSectionCommand orderContentsInSectionCommand;

    public UserKey getContributor()
    {
        return contributor;
    }

    public void setContributor( UserKey contributor )
    {
        this.contributor = contributor;
    }

    public ContentKey getContent()
    {
        return content;
    }

    public void setContent( ContentKey content )
    {
        this.content = content;
    }

    public MenuItemKey getSection()
    {
        return section;
    }

    public void setSection( MenuItemKey section )
    {
        this.section = section;
    }

    public boolean isApproveInSection()
    {
        return approveInSection;
    }

    public void setApproveInSection( boolean approveInSection )
    {
        this.approveInSection = approveInSection;
    }

    public boolean isAddOnTop()
    {
        return addOnTop;
    }

    public void setAddOnTop( boolean addOnTop )
    {
        this.addOnTop = addOnTop;
    }

    public OrderContentsInSectionCommand createOrderContentsInSectionCommand()
    {
        OrderContentsInSectionCommand command = new OrderContentsInSectionCommand();
        this.orderContentsInSectionCommand = command;
        return command;
    }

    public OrderContentsInSectionCommand getOrderContentsInSectionCommand()
    {
        return orderContentsInSectionCommand;
    }
}

