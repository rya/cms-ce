/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.presentation;

import com.enonic.cms.domain.SitePath;

public class RenderPageException
    extends RuntimeException
{

    private String message;

    public RenderPageException( SitePath sitePath, String pageId, String referer, Throwable cause )
    {
        super( cause );
        StringBuffer s = new StringBuffer();
        s.append( "Failed to render page width id " ).append( pageId );
        s.append( ", sitePath was: " ).append( sitePath.toString() );
        if ( referer != null )
        {
            s.append( ", referer was: '" ).append( referer ).append( "'" );
        }

        this.message = s.toString();
    }

    public String getMessage()
    {
        return message;
    }
}
