/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.page.template;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class PageTemplatePortletKey
    implements Serializable
{
    private int pageTemplateKey;

    private int portletKey;

    public PageTemplatePortletKey()
    {
    }

    public PageTemplatePortletKey( int pageTemplateKey, int portletKey )
    {
        this.pageTemplateKey = pageTemplateKey;
        this.portletKey = portletKey;
    }

    public int getPageTemplateKey()
    {
        return pageTemplateKey;
    }

    public int getPortletKey()
    {
        return portletKey;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof PageTemplatePortletKey ) )
        {
            return false;
        }

        PageTemplatePortletKey that = (PageTemplatePortletKey) o;

        if ( portletKey != that.getPortletKey() )
        {
            return false;
        }
        if ( pageTemplateKey != that.getPageTemplateKey() )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 393, 271 ).append( pageTemplateKey ).append( portletKey ).toHashCode();
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s.append( "pageTemplateKey = " ).append( pageTemplateKey ).append( ", portletKey = " ).append( portletKey );
        return s.toString();
    }
}
