/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal;

import com.enonic.cms.domain.NotFoundErrorType;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.StacktraceLoggingUnrequired;

public class SiteFrontPageNotFoundException
    extends RuntimeException
    implements NotFoundErrorType, StacktraceLoggingUnrequired
{

    private SiteKey siteKey;

    private String message;


    public SiteFrontPageNotFoundException( SiteKey siteKey )
    {
        this.siteKey = siteKey;
        message = "Front page not found on site: '" + siteKey + "'";
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
