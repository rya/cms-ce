/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content.index;

import com.enonic.cms.core.content.index.SimpleText;
import org.junit.Test;

import junit.framework.TestCase;

public class SimpleTextTest
    extends TestCase
{
    public SimpleTextTest()
    {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Test
    public void testFullText()
    {

        String stringWithNull = new String( new char[]{'T', 'e', 0x00, 's', 't'} );
        String result = "Te st";

        assertFalse( result.equals( stringWithNull ) );

        SimpleText bigText = new SimpleText( stringWithNull );

        assertTrue( result.equals( bigText.getText() ) );
    }

    @Test
    public void testStrippingOfIllegalASCIIChars()
    {
        final SimpleText test1 = new SimpleText( "ABC\u0000DEF\u0001\u0003\u0014" );
        final String result1 = "ABC DEF";
        final SimpleText test2 = new SimpleText( "\u0007Kaffe\u0009Te\u0009\u0013Kakao\u0009\u0011Solbærtoddy\u0004" );
        final String result2 = "Kaffe\u0009Te\u0009 Kakao\u0009 Solbærtoddy";
        final SimpleText test3 =
            new SimpleText( "<fulltext>rydde i fruktkurv  og legge den som evnt er igjen i en mindre bolle</fulltext>" );
        final String result3 = "<fulltext>rydde i fruktkurv  og legge den som evnt er igjen i en mindre bolle</fulltext>";
        final SimpleText test4 =
            new SimpleText( "\u007f\u00c6\u00d8\u00c5\u00e6\u00f8\u00e5\u306d\u304e\u30de\u30e8\u713c\u304d\u0082\u0099\u009f" );
        final String result4 = "\u007f\u00c6\u00d8\u00c5\u00e6\u00f8\u00e5\u306d\u304e\u30de\u30e8\u713c\u304d\u0082\u0099\u009f";
        assertEquals( "Ascii characters 0x0, 0x1, 0x3 and 0x4 should have been removed.", result1, test1.getText() );
        assertEquals( "Ascii character 0x7, 0x13, 0x11 and 0x4 should have been removed.", result2, test2.getText() );
        assertEquals( "There should be no changes in this test string", result3, test3.getText() );
        assertEquals( "There should be no changes in this test string", result4, test4.getText() );

    }
}
