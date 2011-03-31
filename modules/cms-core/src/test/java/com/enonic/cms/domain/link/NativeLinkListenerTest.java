/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.link;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

public class NativeLinkListenerTest
    extends AbstractLinkTest
{
    @Test
    public void testListener()
        throws Exception
    {
        Assert.assertArrayEquals( new String[]{"image-55", "attachment-66", "content-77"}, processListener( "sample.html" ) );
    }

    private String[] processListener( String file )
        throws Exception
    {
        final ArrayList<String> list = new ArrayList<String>();
        new NativeLinkListener()
        {
            public void onImageLink( int key, String link )
            {
                list.add( "image-" + key );
            }

            public void onBinaryLink( int key, String link )
            {
                list.add( "attachment-" + key );
            }

            public void onContentLink( int key, String link )
            {
                list.add( "content-" + key );
            }
        }.process( readFile( file ) );

        return list.toArray( new String[list.size()] );
    }
}
