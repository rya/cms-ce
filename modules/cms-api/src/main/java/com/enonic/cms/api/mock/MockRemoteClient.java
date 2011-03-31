/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.mock;

import com.enonic.cms.api.client.RemoteClient;

/**
 * Mock implementation of the RemoteClient interface.
 */
public class MockRemoteClient
    extends MockClient
    implements RemoteClient
{
    public String getServiceUrl()
    {
        return null;
    }
}
