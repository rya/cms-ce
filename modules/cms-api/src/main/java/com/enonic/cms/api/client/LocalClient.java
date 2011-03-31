/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client;

import java.util.Map;

/**
 * This interface defines the local client service.
 */
public interface LocalClient
    extends Client
{
    /**
     * Return the global configuration.
     */
    public Map<String, String> getConfiguration();

    /**
     * Return the configuration for a site. Returns null if site not found.
     */
    public Map<String, String> getSiteConfiguration( int siteKey );
}
