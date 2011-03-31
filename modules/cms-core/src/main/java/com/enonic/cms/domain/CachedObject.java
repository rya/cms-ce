/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain;

import java.io.Serializable;

import org.joda.time.DateTime;

public class CachedObject
    implements Serializable
{

    private Object object;

    private DateTime FAR_PAST = new DateTime( 0 );

    private DateTime expirationTime = FAR_PAST;

    private boolean cached = true;

    public CachedObject( Object object )
    {
        if ( object == null )
        {
            throw new IllegalArgumentException( "Given object cannot be null: No sense in caching null objects" );
        }
        this.object = object;
    }

    public CachedObject( Object object, boolean isCached )
    {
        this.object = object;
        this.cached = isCached;
    }

    public CachedObject( Object object, DateTime expirationTime )
    {
        this.object = object;
        this.expirationTime = expirationTime;
    }

    public Object getObject()
    {
        return object;
    }

    public void setExpirationTime( DateTime value )
    {
        this.expirationTime = value;
    }

    public DateTime getExpirationTime()
    {
        return expirationTime;
    }

    public boolean isCached()
    {
        return cached;
    }

    public boolean isExpired()
    {
        return expirationTime.isBeforeNow();
    }
}

