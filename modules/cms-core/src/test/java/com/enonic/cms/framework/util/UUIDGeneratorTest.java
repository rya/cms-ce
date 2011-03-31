/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

public class UUIDGeneratorTest
{
    @Test
    public void testUnique()
    {
        String id1 = UUIDGenerator.randomUUID();
        String id2 = UUIDGenerator.randomUUID();
        assertFalse( id1.equals( id2 ) );
    }

    @Test
    public void testLength()
    {
        String id = UUIDGenerator.randomUUID();
        assertEquals( 32, id.length() );
    }
}
