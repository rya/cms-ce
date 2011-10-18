package com.enonic.cms.core.structure.menuitem;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.structure.page.template.PageTemplateKey;

/**
 * Dec 2, 2010
 */
public class SetContentHomeCommand
    implements MenuItemServiceCommand
{
    private UserKey setter;

    private ContentKey content;

    private MenuItemKey section;

    private PageTemplateKey pageTemplate;

    public UserKey getSetter()
    {
        return setter;
    }

    public void setSetter( UserKey user )
    {
        this.setter = user;
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

    public PageTemplateKey getPageTemplate()
    {
        return pageTemplate;
    }

    public void setPageTemplate( PageTemplateKey pageTemplate )
    {
        this.pageTemplate = pageTemplate;
    }
}