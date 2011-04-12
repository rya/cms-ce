/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure;

import java.io.Serializable;

import com.enonic.cms.core.security.group.GroupKey;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.domain.SiteKey;

public class DefaultSiteAccessKey
    implements Serializable
{
    private SiteKey siteKey;

    private GroupKey groupKey;

    public DefaultSiteAccessKey()
    {
        // Required by Hiernate
    }

    public DefaultSiteAccessKey( SiteKey siteKey, GroupKey groupKey )
    {
        this.siteKey = siteKey;
        this.groupKey = groupKey;
    }

    public SiteKey getSiteKey()
    {
        return siteKey;
    }

    public GroupKey getGroupKey()
    {
        return groupKey;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof DefaultSiteAccessKey ) )
        {
            return false;
        }

        DefaultSiteAccessKey that = (DefaultSiteAccessKey) o;

        if ( !siteKey.equals( that.getSiteKey() ) )
        {
            return false;
        }
        if ( !groupKey.equals( that.getGroupKey() ) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 315, 473 ).append( siteKey ).append( groupKey ).toHashCode();
    }
}
