/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content.index;

import java.util.Collection;

import com.enonic.cms.core.content.index.BigText;
import org.junit.Test;

import junit.framework.TestCase;

import static org.junit.Assert.*;

public class BigTextTest
    extends TestCase
{

    @Test
    public void testFullText()
    {

        String stringWithNull = new String( new char[]{'T', 'e', 0x00, 's', 't'} );
        String result = "Te st";

        assertFalse( result.equals( stringWithNull ) );

        BigText bigText = new BigText( stringWithNull );

        assertTrue( result.equals( bigText.getText() ) );
    }

    @Test
    public void testGetWords()
    {

        BigText bigText = new BigText( "Klippfisk var vikingenes favorittmat. I dag er det folket nordpaa som liker det best:)" );

        String[] expectedWords =
            new String[]{"klippfisk", "var", "vikingenes", "favorittmat", "i", "dag", "er", "det", "folket", "nordpaa", "som", "liker",
                "best",};
        Collection<String> words = bigText.getWords();
        assertArrayEquals( expectedWords, words.toArray( new String[words.size()] ) );
    }

    @Test
    public void testStrippingOfIllegalASCIIChars()
    {
        final BigText test1 = new BigText( "ABC\u0000DEF\u0001\u0003\u0014GHI" );
        final String result1 = "ABC DEF   GHI";
        final BigText test2 = new BigText( "\u0007Kaffe\u0009Te\u0009\u0013Kakao\u0009\u0011Solbærtoddy\u0004" );
        final String result2 = "Kaffe\u0009Te\u0009 Kakao\u0009 Solbærtoddy";
        final BigText test3 = new BigText( "<fulltext>rydde i fruktkurv  og legge den som evnt er igjen i en mindre bolle</fulltext>" );
        final String result3 = "fulltext rydde i fruktkurv  og legge den som evnt er igjen i en mindre bolle  fulltext";
        final BigText test4 =
            new BigText( "\u001A\u007f\u00c6\u00d8\u00c5\u00e6\u00f8\u00e5\u306d\u304e\u30de\u30e8\u713c\u304d\u0082\u0099\u009f\u001A" );
        final String result4 = "\u007f\u00c6\u00d8\u00c5\u00e6\u00f8\u00e5\u306d\u304e\u30de\u30e8\u713c\u304d\u0082\u0099\u009f";
        assertEquals( "Ascii characters 0x0, 0x1, 0x3 and 0x4 should have been removed.", result1, test1.getText() );
        assertEquals( "Ascii character 0x7, 0x13, 0x11 and 0x4 should have been removed.", result2, test2.getText() );
        assertEquals( "There should be no changes in this test string", result3, test3.getText() );
        assertEquals( "There should be no changes in this test string", result4, test4.getText() );

    }
}
