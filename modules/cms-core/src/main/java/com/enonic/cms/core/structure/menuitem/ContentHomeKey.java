/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.menuitem;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.core.content.ContentKey;

public class ContentHomeKey
    implements Serializable
{
    private ContentKey contentKey;

    private SiteKey siteKey;

    public ContentHomeKey()
    {
        // need default constructor for Hibernate.
    }

    public ContentHomeKey( SiteKey siteKey, ContentKey contentKey )
    {
        this.siteKey = siteKey;
        this.contentKey = contentKey;
    }

    public ContentKey getContentKey()
    {
        return contentKey;
    }

    public SiteKey getSiteKey()
    {
        return siteKey;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ContentHomeKey ) )
        {
            return false;
        }

        ContentHomeKey that = (ContentHomeKey) o;

        if ( contentKey != null ? !contentKey.equals( that.getContentKey() ) : that.getContentKey() != null )
        {
            return false;
        }
        if ( siteKey != null ? !siteKey.equals( that.getSiteKey() ) : that.getSiteKey() != null )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 791, 643 ).append( contentKey ).append( siteKey ).toHashCode();
    }
}