/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.page.template;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.core.structure.portlet.PortletEntity;

public class PageTemplatePortletEntity
    implements Serializable
{
    private PageTemplatePortletKey key;

    private int order;

    private Date timestamp;

    private PageTemplateEntity pageTemplate;

    private PortletEntity portlet;

    private PageTemplateRegionEntity pageTemplateRegion;

    public PageTemplatePortletKey getKey()
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

    public PageTemplateEntity getPageTemplate()
    {
        return pageTemplate;
    }

    public PortletEntity getPortlet()
    {
        return portlet;
    }

    public PageTemplateRegionEntity getPageTemplateRegion()
    {
        return pageTemplateRegion;
    }

    public void setKey( PageTemplatePortletKey key )
    {
        this.key = key;
    }

    public void setOrder( int order )
    {
        this.order = order;
    }

    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
    }

    public void setPageTemplate( PageTemplateEntity value )
    {
        this.pageTemplate = value;
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
        if ( !( o instanceof PageTemplatePortletEntity ) )
        {
            return false;
        }

        PageTemplatePortletEntity that = (PageTemplatePortletEntity) o;

        if ( !key.equals( that.getKey() ) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 375, 237 ).append( key ).toHashCode();
    }

    public String toString()
    {
        StringBuffer str = new StringBuffer();
        str.append( "id = " ).append( getKey() ).append( ", pageTemplateRegion.name = '" ).append(
            getPageTemplateRegion().getName() ).append( "'" ).append( ", portlet.name = '" ).append( getPortlet().getName() ).append( "'" );
        return str.toString();
    }
}
