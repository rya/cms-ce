/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal;

import com.enonic.cms.domain.NotFoundErrorType;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.StacktraceLoggingUnrequired;

public class LoginPageNotFoundException
    extends RuntimeException
    implements NotFoundErrorType, StacktraceLoggingUnrequired
{

    private SiteKey siteKey;

    private String message;


    public LoginPageNotFoundException( SiteKey siteKey )
    {
        this.siteKey = siteKey;
        message = "Login page not found for site '" + siteKey + "'";
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
