/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.client;

import com.enonic.cms.api.client.ClientFactory;
import com.enonic.cms.api.client.LocalClient;

/**
 * This class registers the local client into the client factory.
 */
public final class LocalClientSetter
{
    /**
     * Set the local client.
     */
    public void setLocalClient( LocalClient client )
    {
        ClientFactory.setLocalClient( client );
    }
}
