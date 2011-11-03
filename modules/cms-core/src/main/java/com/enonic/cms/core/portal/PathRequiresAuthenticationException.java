/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal;

import com.enonic.cms.core.SitePath;

public class PathRequiresAuthenticationException
    extends RuntimeException
    implements UnauthorizedErrorType
{

    private SitePath sitePath;

    private String message;

    public PathRequiresAuthenticationException( SitePath sitePath )
    {
        this.sitePath = sitePath;
        this.message = "Path requires authentication: '" + sitePath.getLocalPath() + "'";
    }

    public SitePath getSitePath()
    {
        return sitePath;
    }

    public String getMessage()
    {
        return message;
    }
}
