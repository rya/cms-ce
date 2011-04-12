/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.page;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class PageWindowKey
    implements Serializable
{
    private int pageKey;

    private int portletKey;

    public PageWindowKey()
    {
        // for Hibernate
    }

    public PageWindowKey( int pageKey, int portletKey )
    {
        this.pageKey = pageKey;
        this.portletKey = portletKey;
    }

    public int getPageKey()
    {
        return pageKey;
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
        if ( !( o instanceof PageWindowKey ) )
        {
            return false;
        }

        PageWindowKey that = (PageWindowKey) o;

        if ( portletKey != that.getPortletKey() )
        {
            return false;
        }
        if ( pageKey != that.getPageKey() )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 425, 821 ).append( pageKey ).append( portletKey ).toHashCode();
    }
}
