/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.link;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LinkScannerTest
    extends AbstractLinkTest
{
    private LinkScanner scanner;

    @Before
    public void setUp()
    {
        this.scanner = new LinkScanner();
    }

    @Test
    public void testScanner()
        throws Exception
    {
        Assert.assertArrayEquals( new String[]{"http://www.enonic.com", "/images/image.gif", "../images/background.gif",
            "image://55/label/source?size=full&filter=something", "attachment://66?download=true", "content://77"}, scan( "sample.html" ) );
    }

    private String[] scan( String file )
        throws Exception
    {
        ArrayList<String> list = new ArrayList<String>();
        for ( LinkMatch match : this.scanner.scan( readFile( file ) ) )
        {
            list.add( match.getLink() );
        }

        return list.toArray( new String[list.size()] );
    }
}
