/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content;

import java.util.List;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentNativeLink;
import com.enonic.cms.core.content.ContentNativeLinkCollector;
import com.enonic.cms.core.content.ContentNativeLinkType;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * May 8, 2009
 */

public class ContentNativeLinkCollectorTest
{
    @Test
    public void testCollectContentLinks()
    {
        String str = "rubbish rubbish <a href='content://123'>link</a>";
        str += "rubbish rubbish <a href='content://124shallnotbecollected'>link</a>";
        str += "rubbish rubbish <a href='content://125shallnotbecollected/'>link</a>";
        str += "rubbish rubbish <a href='content://126/'>link</a>";

        List<ContentNativeLink> list = new ContentNativeLinkCollector().collect( str );
        assertEquals( 2, list.size() );
        assertEquals( new ContentKey( 123 ), list.get( 0 ).getContentKey() );
        assertEquals( ContentNativeLinkType.CONTENT, list.get( 0 ).getType() );
        assertEquals( new ContentKey( 126 ), list.get( 1 ).getContentKey() );
        assertEquals( ContentNativeLinkType.CONTENT, list.get( 1 ).getType() );
    }

    @Test
    public void testCollectContentBinaryLinks()
    {
        String str = "rubbish rubbish <a href='attachment://123/more'>link</a>";
        str += "rubbish rubbish <a href='attachment://124shallnotbecollected'>link</a>";
        str += "rubbish rubbish <a href='attachment://125shallnotbecollected/'>link</a>";
        str += "rubbish rubbish <a href='attachment://126/more'>link</a>";

        List<ContentNativeLink> list = new ContentNativeLinkCollector().collect( str );
        assertEquals( 2, list.size() );
        assertEquals( new ContentKey( 123 ), list.get( 0 ).getContentKey() );
        assertEquals( ContentNativeLinkType.ATTACHMENT, list.get( 0 ).getType() );
        assertEquals( new ContentKey( 126 ), list.get( 1 ).getContentKey() );
        assertEquals( ContentNativeLinkType.ATTACHMENT, list.get( 1 ).getType() );
    }

    @Test
    public void testCollectContentImageLinks()
    {
        String str = "rubbish rubbish <a href='image://123/more'>link</a>";
        str += "rubbish rubbish <a href='image://83?_size=thumbnail&amp;_format=jpg'>link</a>rubbish rubbish";
        str += "rubbish rubbish <a href='image://124shallnotbecollected'>link</a>";
        str += "rubbish rubbish <a href='image://125shallnotbecollected/'>link</a>";
        str += "rubbish rubbish <a href='image://126/more'>link</a>";

        List<ContentNativeLink> list = new ContentNativeLinkCollector().collect( str );
        assertEquals( 3, list.size() );

        assertEquals( new ContentKey( 123 ), list.get( 0 ).getContentKey() );
        assertEquals( ContentNativeLinkType.IMAGE, list.get( 0 ).getType() );

        assertEquals( new ContentKey( 83 ), list.get( 1 ).getContentKey() );
        assertEquals( ContentNativeLinkType.IMAGE, list.get( 1 ).getType() );

        assertEquals( new ContentKey( 126 ), list.get( 2 ).getContentKey() );
        assertEquals( ContentNativeLinkType.IMAGE, list.get( 2 ).getType() );
    }

}
