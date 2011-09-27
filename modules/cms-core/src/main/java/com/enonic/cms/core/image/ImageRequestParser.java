/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enonic.cms.domain.Path;
import com.enonic.cms.domain.SitePath;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentVersionKey;
import com.enonic.cms.domain.content.binary.BinaryDataKey;
import com.enonic.cms.domain.security.user.UserKey;

public final class ImageRequestParser
{
    private final boolean allowContentVersion;

    public ImageRequestParser()
    {
        this( false );
    }

    public ImageRequestParser( boolean allowContentVersion )
    {
        this.allowContentVersion = allowContentVersion;
    }

    public ImageRequest parse( SitePath path, boolean requireEncoded )
    {
        return parse( path.getLocalPath(), toSingleValueMap( path.getParams() ), requireEncoded );
    }

    public ImageRequest parse( Path path, Map<String, String> params, boolean requireEncoded )
    {
        List<String> elem = findPathElems( path );

        ImageRequest request = new ImageRequest();
        request.setContentVersionKey( parseContentVersionKey( params ) );

        if ( params != null )
        {
            request.getParams().setParams( params, requireEncoded );
        }

        request.setFormat( parseFormat( elem ) );
        request.setUserKey( parseUserKey( elem ) );

        if ( request.getUserKey() == null )
        {
            request.setContentKey( parseContentKey( elem ) );
            request.setLabel( parseLabel( elem ) );
            request.setBinaryDataKey( parseBinaryDataKey( elem ) );
        }

        return request;
    }

    public ImageRequest parse( String path, Map<String, String> params, boolean requireEncoded )
    {
        return parse( new Path( path ), params, requireEncoded );
    }

    private ContentVersionKey parseContentVersionKey( Map<String, String> params )
    {
        String value = params.remove( "_version" );
        return ( ( value != null ) && this.allowContentVersion ) ? new ContentVersionKey( value ) : null;
    }

    private ContentKey parseContentKey( List<String> elems )
    {
        if ( elems.size() > 0 )
        {
            return new ContentKey( stripFormat( elems.get( 0 ) ) );
        }
        else
        {
            return null;
        }
    }

    private UserKey parseUserKey( List<String> elems )
    {
        if ( ( elems.size() == 2 ) && "user".equals( elems.get( 0 ) ) )
        {
            return new UserKey( stripFormat( elems.get( 1 ) ) );
        }
        else
        {
            return null;
        }
    }

    private BinaryDataKey parseBinaryDataKey( List<String> elems )
    {
        if ( ( elems.size() == 3 ) && "binary".equals( elems.get( 1 ) ) )
        {
            return new BinaryDataKey( stripFormat( elems.get( 2 ) ) );
        }
        else
        {
            return null;
        }
    }

    private String parseLabel( List<String> elems )
    {
        if ( ( elems.size() == 3 ) && "label".equals( elems.get( 1 ) ) )
        {
            return stripFormat( elems.get( 2 ) );
        }
        else
        {
            return "source";
        }
    }

    private String parseFormat( List<String> elems )
    {
        if ( elems.size() > 0 )
        {
            String elem = elems.get( elems.size() - 1 );
            int pos = elem.indexOf( '.' );

            if ( pos > -1 )
            {
                return elem.substring( pos + 1 ).trim();
            }
        }

        return null;
    }

    private String stripFormat( String str )
    {
        int pos = str.indexOf( '.' );
        if ( pos > -1 )
        {
            return str.substring( 0, pos );
        }
        else
        {
            return str;
        }
    }

    private Map<String, String> toSingleValueMap( Map<String, String[]> map )
    {
        HashMap<String, String> result = new HashMap<String, String>();
        for ( Map.Entry<String, String[]> entry : map.entrySet() )
        {
            String[] values = entry.getValue();
            if ( ( values != null ) && ( values.length > 0 ) )
            {
                result.put( entry.getKey(), values[0] );
            }
        }

        return result;
    }

    private List<String> findPathElems( Path path )
    {
        List<String> list = path.getPathElements();
        int pos = list.indexOf( "_image" );

        if ( pos > -1 )
        {
            return list.subList( pos + 1, list.size() );
        }
        else
        {
            return Collections.emptyList();
        }
    }
}
