/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.upgrade.task.link;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.binary.BinaryDataKey;
import com.enonic.cms.domain.link.ContentKeyResolver;
import com.enonic.cms.domain.link.LinkTransformer;

public final class OldLinkTransformer
    extends LinkTransformer
{
    private ContentKeyResolver contentKeyResolver;

    public void setContentKeyResolver( ContentKeyResolver contentKeyResolver )
    {
        this.contentKeyResolver = contentKeyResolver;
    }

    protected String transformLink( String link )
    {
        if ( this.contentKeyResolver == null )
        {
            throw new IllegalArgumentException( "ContentKeyResolver not set" );
        }
        link = transformBinaryLinkWithMissingContentKey( link );
        link = transformBinaryLink( link );
        link = transformPageLink( link );
        return link;
    }

    private String transformBinaryLinkWithMissingContentKey( String link )
    {
        final String[] patterns = {"^[/.//]*?_?binary\\?id=([0-9]+)$", "^[/.//]*?_?binary\\?id=([0-9]+)&(.+)$"};

        for ( String pattern : patterns )
        {
            final StringBuffer str = new StringBuffer();
            final Matcher m = Pattern.compile( pattern ).matcher( link );
            while ( m.find() )
            {
                final BinaryDataKey binaryKey = new BinaryDataKey( Integer.valueOf( m.group( 1 ) ) );
                final ContentKey contentKey = contentKeyResolver.resolvFromBinaryKey( binaryKey );
                final String parameters = m.groupCount() > 1 ? "?" + m.group( 2 ) : "";
                m.appendReplacement( str, "attachment://" + ( contentKey == null ? "unknown" : contentKey ) + "/binary/" + binaryKey +
                    parameters );
            }
            m.appendTail( str );
            link = str.toString();
        }
        return link;
    }

    private String transformBinaryLink( String link )
    {
        link = link.replaceAll( "^[/.//]*?_?binary/([0-9]+)/file$", "attachment://$1" );
        link = link.replaceAll( "^[/.//]*?_?binary/([0-9]+)/file\\?(.+)$", "attachment://$1?$2" );
        return link;
    }

    private String transformPageLink( String link )
    {
        link = link.replaceAll( "^page\\?id=([0-9]+)$", "page://$1" );
        link = link.replaceAll( "^page\\?id=([0-9]+)&(.+)$", "page://$1?$2" );
        return link;
    }
}

