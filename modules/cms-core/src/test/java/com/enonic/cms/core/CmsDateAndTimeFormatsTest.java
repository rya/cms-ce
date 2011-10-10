/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.*;


public class CmsDateAndTimeFormatsTest
{

    @Test
    public void testPrintAs_STORE_DATE()
    {

        DateTime date = new DateTime( 2008, 7, 1, 14, 2, 0, 0 );
        assertEquals( "2008-07-01 14:02", CmsDateAndTimeFormats.printAs_STORE_DATE( date.toDate() ) );
    }

    @Test
    public void testPrintAs_STORE_TIMESTAMP()
    {

        DateTime date = new DateTime( 2008, 7, 1, 14, 2, 33, 0 );
        assertEquals( "2008-07-01 14:02:33", CmsDateAndTimeFormats.printAs_STORE_TIMESTAMP( date.toDate() ) );
    }

    @Test
    public void testPrintAs_XML_DATE()
    {

        DateTime date = new DateTime( 2008, 7, 1, 14, 2, 33, 0 );
        assertEquals( "2008-07-01 14:02", CmsDateAndTimeFormats.printAs_XML_DATE( date.toDate() ) );
    }

    @Test
    public void testPrintAs_XML_TIMESTAMP()
    {

        DateTime date = new DateTime( 2008, 7, 1, 14, 2, 33, 0 );
        assertEquals( "2008-07-01 14:02:33", CmsDateAndTimeFormats.printAs_XML_TIMESTAMP( date.toDate() ) );
    }
}
