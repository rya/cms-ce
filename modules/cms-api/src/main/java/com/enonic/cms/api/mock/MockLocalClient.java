/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.mock;

import java.util.HashMap;
import java.util.Map;

import com.enonic.cms.api.client.LocalClient;

/**
 * Mock implementation of the LocalClient interface.
 */
public class MockLocalClient
    extends MockClient
    implements LocalClient
{
    public Map<String, String> getConfiguration()
    {
        return new HashMap<String, String>();
    }

    public Map<String, String> getSiteConfiguration( int siteKey )
    {
        return new HashMap<String, String>();
    }
}
