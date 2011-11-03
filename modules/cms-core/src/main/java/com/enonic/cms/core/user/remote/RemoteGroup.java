/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.user.remote;

public final class RemoteGroup
    extends RemotePrincipal
{
    public RemoteGroup( String id )
    {
        super( id );
    }

    public int hashCode()
    {
        return this.getId().hashCode();
    }

    public boolean equals( Object o )
    {
        return ( o instanceof RemoteGroup ) && ( (RemoteGroup) o ).getId().equals( getId() );
    }
}
