/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.core.security.user.UserKey;

/**
 * Jul 10, 2009
 */
public class RememberedLoginKey
    implements Serializable
{

    private SiteKey siteKey;

    private UserKey userKey;

    public SiteKey getSiteKey()
    {
        return siteKey;
    }

    public void setSiteKey( SiteKey siteKey )
    {
        this.siteKey = siteKey;
    }

    public UserKey getUserKey()
    {
        return userKey;
    }

    public void setUserKey( UserKey userKey )
    {
        this.userKey = userKey;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        RememberedLoginKey that = (RememberedLoginKey) o;

        if ( siteKey != null ? !siteKey.equals( that.getSiteKey() ) : that.getSiteKey() != null )
        {
            return false;
        }
        if ( userKey != null ? !userKey.equals( that.getUserKey() ) : that.getUserKey() != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        final int initialNonZeroOddNumber = 865;
        final int multiplierNonZeroOddNumber = 769;
        return new HashCodeBuilder( initialNonZeroOddNumber, multiplierNonZeroOddNumber ).append( siteKey ).append( userKey ).toHashCode();
    }
}
