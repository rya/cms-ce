/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.vhost;

public class InvalidVirtualHostPatternException
    extends RuntimeException
{

    private String pattern;


    public InvalidVirtualHostPatternException( String pattern, String detail )
    {
        super( "Invalid virtual host pattern: '" + pattern + "', " + detail );
        this.pattern = pattern;
    }

    public String getPattern()
    {
        return pattern;
    }
}
