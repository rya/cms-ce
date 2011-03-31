/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.io;

import junit.framework.TestCase;

public class ByteOrderMarkTest
    extends TestCase
{
    public void testInvalid()
    {
        assertNull( ByteOrderMark.resolve( new byte[0] ) );
        assertNull( ByteOrderMark.resolve( new byte[]{(byte) 0xef, (byte) 0xbb} ) );
        assertNull( ByteOrderMark.resolve( new byte[]{(byte) 0xef, (byte) 0xbb, (byte) 0x11} ) );
    }

    public void testValid()
    {
        assertEquals( ByteOrderMark.UTF8, ByteOrderMark.resolve( new byte[]{(byte) 0xef, (byte) 0xbb, (byte) 0xbf} ) );
        assertEquals( ByteOrderMark.UTF16LE, ByteOrderMark.resolve( new byte[]{(byte) 0xff, (byte) 0xfe} ) );
        assertEquals( ByteOrderMark.UTF16BE, ByteOrderMark.resolve( new byte[]{(byte) 0xfe, (byte) 0xff} ) );
    }
}
