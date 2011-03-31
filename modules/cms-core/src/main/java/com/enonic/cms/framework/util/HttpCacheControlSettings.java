/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

public class HttpCacheControlSettings
{
    public static int CACHE_FOREVER_SECONDS = 31536000;

    public Long maxAgeSecondsToLive = null;

    public boolean publicAccess = false;
}
