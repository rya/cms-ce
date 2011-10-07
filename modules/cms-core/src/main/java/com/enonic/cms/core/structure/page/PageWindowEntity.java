/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.page;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.core.structure.page.template.PageTemplateRegionEntity;
import com.enonic.cms.core.structure.portlet.PortletEntity;

public class PageWindowEntity
    implements Serializable
{
    private PageWindowKey key;

    private int order;

    private Date timestamp;

    private PageEntity page;

    private PortletEntity portlet;

    private PageTemplateRegionEntity pageTemplateRegion;

    public PageWindowKey getKey()
    {
        return key;
    }

    public int getOrder()
    {
        return order;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public PageEntity getPage()
    {
        return page;
    }

    public PortletEntity getPortlet()
    {
        return portlet;
    }

    public PageTemplateRegionEntity getPageTemplateRegion()
    {
        return pageTemplateRegion;
    }

    public void setKey( PageWindowKey value )
    {
        this.key = value;
    }

    public void setOrder( int value )
    {
        this.order = value;
    }

    public void setTimestamp( Date value )
    {
        this.timestamp = value;
    }

    public void setPage( PageEntity value )
    {
        this.page = value;
    }

    public void setPortlet( PortletEntity value )
    {
        this.portlet = value;
    }

    public void setPageTemplateRegion( PageTemplateRegionEntity value )
    {
        this.pageTemplateRegion = value;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof PageWindowEntity ) )
        {
            return false;
        }

        PageWindowEntity that = (PageWindowEntity) o;

        if ( !key.equals( that.getKey() ) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 331, 825 ).append( key ).toHashCode();
    }
}
