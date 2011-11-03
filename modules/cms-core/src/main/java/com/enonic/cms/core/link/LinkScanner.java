/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.link;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LinkScanner
{
    private final ArrayList<Pattern> patterns;

    public LinkScanner()
    {
        this.patterns = new ArrayList<Pattern>();
        this.patterns.add( buildElementPattern( "a", "href" ) );
        this.patterns.add( buildElementPattern( "img", "src" ) );
        this.patterns.add( buildElementPattern( "table", "background" ) );
    }

    public List<LinkMatch> scan( String input )
    {
        ArrayList<LinkMatch> list = new ArrayList<LinkMatch>();
        for ( Pattern pattern : this.patterns )
        {
            scan( list, input, pattern );
        }

        Collections.sort( list );
        return list;
    }

    private void scan( List<LinkMatch> result, String input, Pattern pattern )
    {
        Matcher matcher = pattern.matcher( input );
        while ( matcher.find() )
        {
            result.add( new LinkMatch( matcher.group( 1 ), matcher.start( 1 ), matcher.end( 1 ) ) );
        }
    }

    private Pattern buildElementPattern( String elem, String attr )
    {
        StringBuffer str = new StringBuffer();
        str.append( "<" ).append( elem ).append( "\\s+" );
        str.append( "[^>]*" ).append( attr );
        str.append( "\\s*=\\s*" ).append( "[\"']([^\"']+)[\"']" );
        return Pattern.compile( str.toString(), Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL );
    }
}
