/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client;

import com.enonic.cms.api.client.binrpc.BinRpcRemoteClient;

/**
 * This class implements the factory for client implementations.
 */
public final class ClientFactory
{
    private static LocalClient LOCAL_CLIENT;

    /**
     * Returns the local client.
     */
    public static LocalClient getLocalClient()
    {
        return LOCAL_CLIENT;
    }

    /**
     * Set the local client.
     */
    public static void setLocalClient( LocalClient client )
    {
        LOCAL_CLIENT = client;
    }

    /**
     * Returns a new remote client (that is not using a global session).
     */
    public static RemoteClient getRemoteClient( String url )
    {
        return getRemoteClient( url, false );
    }

    /**
     * Returns a remote client. When useGlobalSession is true,
     * the http session is reused for each thread that uses the returned client instance.
     * When false, a new session will be created for
     * each Thread-instance invoking methods on the return client instance.
     */
    public static RemoteClient getRemoteClient( String url, boolean useGlobalSession )
    {
        return new BinRpcRemoteClient( url, useGlobalSession );
    }
}
