/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.link;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class NativeLinkListener
{
    private final LinkScanner scanner = new LinkScanner();

    private final static Pattern PATTERN = Pattern.compile( "^([a-z]+)://([0-9]+).*" );

    public void process( String input )
    {
        for ( LinkMatch match : this.scanner.scan( input ) )
        {
            processLink( match.getLink() );
        }
    }

    private void processLink( String link )
    {
        Matcher matcher = PATTERN.matcher( link );
        if ( matcher.find() )
        {
            String scheme = matcher.group( 1 );
            Integer key = toInteger( matcher.group( 2 ) );

            if ( ( scheme == null ) || ( key == null ) )
            {
                return;
            }

            if ( "image".equalsIgnoreCase( scheme ) )
            {
                onImageLink( key, link );
            }
            else if ( "attachment".equalsIgnoreCase( scheme ) )
            {
                onBinaryLink( key, link );
            }
            else if ( "content".equalsIgnoreCase( scheme ) )
            {
                onContentLink( key, link );
            }
        }
    }

    private Integer toInteger( String value )
    {
        try
        {
            return new Integer( value );
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    public abstract void onImageLink( int key, String link );

    public abstract void onBinaryLink( int key, String link );

    public abstract void onContentLink( int key, String link );
}
