/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.menuitem;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;

public class ContentHomeEntity
    implements Serializable
{
    private ContentHomeKey key;

    private MenuItemEntity menuItem;

    private PageTemplateEntity pageTemplate;

    private ContentEntity content;

    private SiteEntity site;

    public ContentHomeKey getKey()
    {
        return key;
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

    public SiteEntity getSite()
    {
        return site;
    }

    public void setKey( ContentHomeKey key )
    {
        this.key = key;
    }

    public void setMenuItem( MenuItemEntity menuItem )
    {
        this.menuItem = menuItem;
    }

    public void setPageTemplate( PageTemplateEntity pageTemplate )
    {
        this.pageTemplate = pageTemplate;
    }

    public void setContent( ContentEntity content )
    {
        this.content = content;
    }

    public void setSite( SiteEntity site )
    {
        this.site = site;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ContentHomeEntity ) )
        {
            return false;
        }

        ContentHomeEntity that = (ContentHomeEntity) o;

        if ( !key.equals( that.getKey() ) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 791, 643 ).append( key ).toHashCode();
    }
}
