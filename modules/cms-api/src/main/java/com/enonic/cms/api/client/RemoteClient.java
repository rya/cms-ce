/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client;

/**
 * This interface defines the remote client.
 */
public interface RemoteClient
    extends Client
{
    /**
     * Return the url.
     */
    public String getServiceUrl();
}
