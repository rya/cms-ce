/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.domain.link.NativeLinkListener;

public class ContentNativeLinkCollector
    extends NativeLinkListener
{
    List<ContentNativeLink> links = new ArrayList<ContentNativeLink>();

    public List<ContentNativeLink> collect( String str )
    {
        if ( str == null )
        {
            throw new IllegalArgumentException( "NULL is not a legal argument for str." );
        }
        links.clear();
        process( str );
        return links;
    }


    public void onImageLink( int key, String link )
    {
        if ( link.matches( "image://[0-9]+/.+" ) || link.matches( "image://[0-9]+\\?.+" ) )
        {
            links.add( new ContentNativeLink( new ContentKey( key ), link, ContentNativeLinkType.IMAGE ) );
        }
    }

    public void onBinaryLink( int key, String link )
    {
        if ( link.matches( "attachment://[0-9]+/.+" ) )
        {
            links.add( new ContentNativeLink( new ContentKey( key ), link, ContentNativeLinkType.ATTACHMENT ) );
        }
    }

    public void onContentLink( int key, String link )
    {
        if ( link.matches( "content://[0-9]+/?$" ) )
        {
            links.add( new ContentNativeLink( new ContentKey( key ), link, ContentNativeLinkType.CONTENT ) );
        }
    }

}
