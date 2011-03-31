/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.portal;

import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;
import com.enonic.cms.domain.structure.page.template.PageTemplateEntity;

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


