/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.binary;

import java.util.List;

import com.enonic.cms.core.content.ContentKey;
import org.apache.commons.lang.StringUtils;

import com.enonic.cms.domain.Path;

/**
 * Feb 15, 2010
 */
public class AttachmentNativeLinkKeyParser
{
    public static AttachmentNativeLinkKey parse( Path path )
        throws InvalidAttachmentNativeLinkKeyException
    {
        if ( path == null )
        {
            throw new IllegalArgumentException( "Given path cannot be null" );
        }

        List<String> pathElements = path.getPathElements();

        if ( pathElements.size() == 0 )
        {
            throw new InvalidAttachmentNativeLinkKeyException( path.getPathAsString(), "Path is empty" );
        }

        final String firstPathElement = pathElements.get( 0 );
        final ContentKey contentKey = parseContentKey( firstPathElement );

        if ( pathElements.size() == 1 )
        {
            // handling: <contentkey>*
            String extension = parseExtension( firstPathElement );
            //AttachmentNativeLinkKey linkKey = new AttachmentNativeLinkKeyWithLabel( contentKey, "source" );
            AttachmentNativeLinkKey linkKey = new AttachmentNativeLinkKey( contentKey );
            if ( extension != null )
            {
                linkKey.setExtension( extension );
            }
            return linkKey;
        }

        final String secondPathElement = pathElements.get( 1 );
        final boolean hasLabelPathElement = secondPathElement.equals( "label" );
        final boolean hasBinaryPathElement = secondPathElement.equals( "binary" );

        if ( hasLabelPathElement )
        {
            // handling: <contentkey>*/label/<label>
            return parseWithLabel( pathElements, contentKey, path );
        }
        else if ( hasBinaryPathElement )
        {
            // handling: <contentkey>*/binary/<binarydatakey>
            return parseWithBinaryDataKey( pathElements, contentKey, path );
        }

        throw new InvalidAttachmentNativeLinkKeyException( path.getPathAsString(), "Unknown format" );
    }

    private static AttachmentNativeLinkKey parseWithBinaryDataKey( final List<String> pathElements, final ContentKey contentKey, Path path )
    {
        if ( pathElements.size() < 3 )
        {
            throw new InvalidAttachmentNativeLinkKeyException( path.getPathAsString(), "Missing binary key" );
        }

        BinaryDataKey binaryDataKey = null;
        String extension = null;

        final String thirdPathElement = pathElements.get( 2 );
        binaryDataKey = parseBinaryDataKey( thirdPathElement );
        extension = parseExtension( thirdPathElement );

        AttachmentNativeLinkKeyWithBinaryKey linkKey = new AttachmentNativeLinkKeyWithBinaryKey( contentKey, binaryDataKey );
        if ( extension != null )
        {
            linkKey.setExtension( extension );
        }
        return linkKey;
    }

    private static AttachmentNativeLinkKey parseWithLabel( final List<String> pathElements, final ContentKey contentKey, Path path )
    {
        if ( pathElements.size() < 3 )
        {
            throw new InvalidAttachmentNativeLinkKeyException( path.getPathAsString(), "Missing label" );
        }

        String label = null;
        String extension = null;

        final String thirdPathElement = pathElements.get( 2 );
        label = parseStringBeforeDot( thirdPathElement );
        extension = parseExtension( thirdPathElement );

        if ( StringUtils.isBlank( label ) )
        {
            label = "source";
        }
        AttachmentNativeLinkKeyWithLabel linkKey = new AttachmentNativeLinkKeyWithLabel( contentKey, label );
        if ( extension != null )
        {
            linkKey.setExtension( extension );
        }
        return linkKey;
    }

    private static ContentKey parseContentKey( final String pathElement )
    {
        return new ContentKey( parseStringBeforeDot( pathElement ) );
    }

    private static BinaryDataKey parseBinaryDataKey( final String pathElement )
    {
        return new BinaryDataKey( parseStringBeforeDot( pathElement ) );
    }

    private static String parseStringBeforeDot( final String pathElement )
    {
        String extension = null;
        final int dotPos = pathElement.indexOf( "." );
        if ( dotPos == -1 )
        {
            return pathElement;
        }

        return pathElement.substring( 0, dotPos );
    }

    private static String parseExtension( final String pathElement )
    {
        final int dotPos = pathElement.indexOf( "." );
        if ( dotPos == -1 )
        {
            return null;
        }

        return pathElement.substring( dotPos + 1, pathElement.length() );
    }

}
