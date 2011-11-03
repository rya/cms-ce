/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.user.remote;

public abstract class RemotePrincipal
{
    private final String id;

    private String sync;

    public RemotePrincipal( String id )
    {
        this.id = id;
    }

    public final String getId()
    {
        return this.id;
    }

    public final String getSync()
    {
        return sync;
    }

    public final void setSync( String sync )
    {
        this.sync = sync;
    }
}
