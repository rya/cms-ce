/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.link;

public abstract class LinkTransformer
{
    private final LinkScanner scanner = new LinkScanner();

    public final String transform( String value )
    {
        int pos = 0;
        StringBuffer str = new StringBuffer();

        for ( LinkMatch match : this.scanner.scan( value ) )
        {
            str.append( value.substring( pos, match.getStart() ) );
            str.append( transformLink( match.getLink() ) );
            pos = match.getEnd();
        }

        str.append( value.substring( pos ) );
        return str.toString();
    }

    protected abstract String transformLink( String link );
}
