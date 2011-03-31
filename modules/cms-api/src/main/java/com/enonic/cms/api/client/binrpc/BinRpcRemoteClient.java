/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.binrpc;

import java.lang.reflect.Proxy;

import com.enonic.cms.api.client.Client;
import com.enonic.cms.api.client.ClientWrapper;
import com.enonic.cms.api.client.RemoteClient;

/**
 * This class implements the remote client using binrpc.
 */
public final class BinRpcRemoteClient
    extends ClientWrapper
    implements RemoteClient
{

    private final String serviceUrl;

    public BinRpcRemoteClient( String serviceUrl )
    {
        this( serviceUrl, false );
    }


    public BinRpcRemoteClient( String serviceUrl, boolean useGlobalSession )
    {
        super( createProxy( serviceUrl, useGlobalSession ) );
        this.serviceUrl = serviceUrl;
    }


    public String getServiceUrl()
    {
        return this.serviceUrl;
    }


    public int hashCode()
    {
        return this.serviceUrl.hashCode();
    }

    public String toString()
    {
        return getClass().getName() + "[" + this.serviceUrl + "]";
    }

    private static Client createProxy( String serviceUrl, boolean useGlobalSession )
    {
        return (Client) Proxy.newProxyInstance( BinRpcInvocationHandler.class.getClassLoader(), new Class[]{Client.class},
                                                new BinRpcInvocationHandler( serviceUrl, useGlobalSession ) );
    }
}
