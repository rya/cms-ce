/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;

/**
 * May 6, 2009
 */
public class ContentPageRequest
{
    private MenuItemEntity menuItem;

    private PageTemplateEntity pageTemplate;

    private ContentEntity content;

    private LanguageEntity language;

    public ContentPageRequest( MenuItemEntity menuItem, PageTemplateEntity pageTemplate, ContentEntity content, LanguageEntity language )
    {
        this.menuItem = menuItem;
        this.pageTemplate = pageTemplate;
        this.content = content;
        this.language = language;
    }

    public MenuItemEntity getMenuItem()
    {
        return menuItem;
    }

    public PageTemplateEntity getPageTemplate()
    {
        return pageTemplate;
    }

    public ContentEntity getContent()
    {
        return content;
    }

    public LanguageEntity getLanguage()
    {
        return language;
    }
}


