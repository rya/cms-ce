/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.binary;

import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.domain.Path;

public final class AttachmentNativeLinkKeyInPathParser
{

    public static AttachmentNativeLinkKey resolveFromUrlPath( final Path path )
    {
        List<String> pathElements = getPathElementsAfterPathElementName( "_attachment", path );
        pathElements = removeAnyEmptyPathElementAtEnd( pathElements );
        return AttachmentNativeLinkKeyParser.parse( new Path( pathElements, true ) );
    }

    private static List<String> getPathElementsAfterPathElementName( String pathElementName, Path path )
    {
        List<String> pathElements = new ArrayList<String>();
        List<String> allPathElements = path.getPathElements();
        int count = path.numberOfElements();
        for ( int i = 0; i < count; i++ )
        {
            String pathElement = path.getPathElement( i );
            if ( pathElement.equals( pathElementName ) )
            {
                return allPathElements.subList( i + 1, allPathElements.size() );
            }
        }
        return new ArrayList<String>();
    }

    private static List<String> removeAnyEmptyPathElementAtEnd( List<String> pathElements )
    {
        String lastPathElement = pathElements.get( pathElements.size() - 1 );
        if ( lastPathElement.equals( "" ) )
        {
            return pathElements.subList( 0, pathElements.size() - 1 );
        }
        return pathElements;
    }
}
