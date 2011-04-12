/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content;

import com.enonic.cms.core.content.ContentStatus;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Dec 15, 2009
 */
public class ContentStatusTest
{
    @Test
    public void test()
    {
        assertTrue( ContentStatus.APPROVED == ContentStatus.get( 2 ) );
        assertTrue( ContentStatus.APPROVED == ContentStatus.APPROVED );
        assertFalse( ContentStatus.APPROVED == ContentStatus.get( 1 ) );
        assertFalse( ContentStatus.APPROVED == ContentStatus.SNAPSHOT );
    }
}
