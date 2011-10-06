/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.client;

import com.enonic.cms.api.client.ClientFactory;

/**
 * This class implements the internal client accessor.
 */
public final class InternalClientAccessor
{
    /**
     * Return the internal client.
     */
    public static InternalClient getInternalClient()
    {
        return (InternalClient) ClientFactory.getLocalClient();
    }
}
