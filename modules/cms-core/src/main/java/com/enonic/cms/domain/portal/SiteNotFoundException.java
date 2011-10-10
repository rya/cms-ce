/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.portal;

import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.NotFoundErrorType;
import com.enonic.cms.core.StacktraceLoggingUnrequired;

public class SiteNotFoundException
    extends RuntimeException
    implements NotFoundErrorType, StacktraceLoggingUnrequired
{
    private SiteKey siteKey;

    private String message;


    public SiteNotFoundException( SiteKey siteKey )
    {
        this.siteKey = siteKey;
        message = "Site not found: '" + siteKey + "'";
    }

    public SiteKey getSiteKey()
    {
        return siteKey;
    }

    public String getMessage()
    {
        return message;
    }
}
