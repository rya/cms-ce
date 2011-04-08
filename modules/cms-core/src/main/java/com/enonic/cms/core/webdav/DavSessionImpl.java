/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.webdav;

import java.util.HashSet;

import org.apache.jackrabbit.webdav.DavSession;

public final class DavSessionImpl
    implements DavSession
{
    private final HashSet<String> lockTokens;

    public DavSessionImpl()
    {
        this.lockTokens = new HashSet<String>();
    }

    public void addReference( Object reference )
    {
        throw new UnsupportedOperationException( "Not implemented." );
    }

    public void removeReference( Object reference )
    {
        throw new UnsupportedOperationException( "Not implemented." );
    }

    public void addLockToken( String token )
    {
        this.lockTokens.add( token );
    }

    public String[] getLockTokens()
    {
        return this.lockTokens.toArray( new String[this.lockTokens.size()] );
    }

    public void removeLockToken( String token )
    {
        this.lockTokens.remove( token );
    }
}
