/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.page.template;

import java.io.Serializable;
import java.util.Collection;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class PageTemplateRegionEntity
    implements Serializable
{
    private int key;

    private String name;

    private String separator;

    private boolean multiple;

    private boolean override;

    private PageTemplateEntity pageTemplate;

    private Collection<PageTemplatePortletEntity> portlets;

    public int getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public String getSeparator()
    {
        return separator;
    }

    public boolean isMultiple()
    {
        return multiple;
    }

    public boolean isOverride()
    {
        return override;
    }

    public PageTemplateEntity getPageTemplate()
    {
        return pageTemplate;
    }

    public void setKey( int key )
    {
        this.key = key;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setSeparator( String separator )
    {
        this.separator = separator;
    }

    public void setMultiple( boolean multiple )
    {
        this.multiple = multiple;
    }

    public void setOverride( boolean override )
    {
        this.override = override;
    }

    public void setPageTemplate( PageTemplateEntity pageTemplate )
    {
        this.pageTemplate = pageTemplate;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof PageTemplateRegionEntity ) )
        {
            return false;
        }

        PageTemplateRegionEntity that = (PageTemplateRegionEntity) o;

        if ( key != that.getKey() )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 433, 449 ).append( key ).toHashCode();
    }

    public String toString()
    {
        StringBuffer str = new StringBuffer();
        str.append( "id = " ).append( getKey() ).append( ", name = '" ).append( getName() ).append( "'" );
        return str.toString();
    }

    public Collection<PageTemplatePortletEntity> getPortlets()
    {
        return portlets;
    }
}
