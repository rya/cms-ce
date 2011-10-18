package com.enonic.cms.core.structure.menuitem;


import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.security.user.UserKey;

/**
 * Nov 18, 2010
 */
public class ApproveContentInSectionCommand
    implements MenuItemServiceCommand
{
    private UserKey approver;

    private MenuItemKey section;

    private ContentKey contentToApprove;

    public UserKey getApprover()
    {
        return approver;
    }

    public void setApprover( UserKey approver )
    {
        this.approver = approver;
    }

    public MenuItemKey getSection()
    {
        return section;
    }

    public void setSection( MenuItemKey section )
    {
        this.section = section;
    }

    public ContentKey getContentToApprove()
    {
        return contentToApprove;
    }

    public void setContentToApprove( ContentKey contentToApprove )
    {
        this.contentToApprove = contentToApprove;
    }
}
