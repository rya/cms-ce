/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal;

import com.enonic.cms.domain.Path;

/**
 * Jul 26, 2009
 */
public class WindowReference
{
    public final static String WINDOW_PATH_PREFIX = "_window";

    private String portletName;

    private Path pathToMenuItem;


    public static WindowReference parse( Path localPath )
    {
        int index = localPath.indexOf( WINDOW_PATH_PREFIX );
        if ( index == -1 )
        {
            return null;
        }

        if ( index >= localPath.getPathElementsCount() )
        {
            return null;
        }
        String portletName = localPath.getPathElement( index + 1 );
        String pathWithoutWindowReference = localPath.subPath( 0, index );
        if ( localPath.hasFragment() )
        {
            pathWithoutWindowReference = pathWithoutWindowReference + "#" + localPath.getFragment();
        }
        Path pathToMenuItem = new Path( pathWithoutWindowReference, true );
        return new WindowReference( portletName, pathToMenuItem );
    }

    public WindowReference( String portletName, Path pathToMenuItem )
    {
        this.portletName = portletName;
        this.pathToMenuItem = pathToMenuItem;
    }

    public String getPortletName()
    {
        return portletName;
    }

    public Path getPathToMenuItem()
    {
        return pathToMenuItem;
    }
}
