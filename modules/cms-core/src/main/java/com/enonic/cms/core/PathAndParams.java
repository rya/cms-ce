/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

public class PathAndParams
{

    private Path path;

    private RequestParameters params;

    public PathAndParams( Path path, RequestParameters params )
    {
        if ( path == null )
        {
            throw new IllegalArgumentException( "Given path cannot be null" );
        }
        if ( params == null )
        {
            throw new IllegalArgumentException( "Given params cannot be null" );
        }

        this.path = path;
        this.params = params;
    }

    public Path getPath()
    {
        return path;
    }

    public RequestParameters getParams()
    {
        return params;
    }

    public String getParameter( String name )
    {
        return params.getParameterValue( name );
    }
}
