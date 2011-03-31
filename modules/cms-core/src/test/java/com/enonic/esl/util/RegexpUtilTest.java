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
        String result = RegexpUtil.substituteAll( "\\%passord\\%", "balle", message );
        assertEquals( "passordet er balle ikke sant", result );

    }

    public void testSubstituteAll2()
    {

        String message = "Balle Klorin\\n er en luring";
        String result = RegexpUtil.substituteAll( "\\\\n", "\n", message );

        assertEquals( "Balle Klorin\n er en luring", result );
    }

    public void testSubstituteAll3()
    {

        String message = "Balle Klorin\\r er en luring";
        String result = RegexpUtil.substituteAll( "\\\\r", "", message );

        assertEquals( "Balle Klorin er en luring", result );
    }

    public void testSubstituteAll4()
    {

        String message = "Balle Klorin er en %key1%luring";
        String result = RegexpUtil.substituteAll( "%.+%", "", message );

        assertEquals( "Balle Klorin er en luring", result );
    }
}
