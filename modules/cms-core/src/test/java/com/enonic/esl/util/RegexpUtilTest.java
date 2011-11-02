/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.util;

import junit.framework.TestCase;

public class RegexpUtilTest
    extends TestCase
{


    public void testSubstituteAll1()
    {

        String message = "passordet er %passord% ikke sant";
        String result = message.replaceAll("\\%passord\\%", "balle");
        assertEquals( "passordet er balle ikke sant", result );

    }

    public void testSubstituteAll2()
    {

        String message = "Balle Klorin\\n er en luring";
        String result = message.replaceAll("\\\\n", "\n");

        assertEquals( "Balle Klorin\n er en luring", result );
    }

    public void testSubstituteAll3()
    {

        String message = "Balle Klorin\\r er en luring";
        String result = message.replaceAll("\\\\r", "");

        assertEquals( "Balle Klorin er en luring", result );
    }

    public void testSubstituteAll4()
    {

        String message = "Balle Klorin er en %key1%luring";
        String result = message.replaceAll("%.+%", "");

        assertEquals( "Balle Klorin er en luring", result );
    }
}
