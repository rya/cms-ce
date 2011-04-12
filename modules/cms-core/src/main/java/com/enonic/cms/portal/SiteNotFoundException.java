/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal;

import com.enonic.cms.domain.NotFoundErrorType;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.StacktraceLoggingUnrequired;

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
