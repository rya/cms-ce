/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security;

import java.util.Random;

public class PasswordGenerator
{
    public static String generateNewPassword()
    {
        return doGenerateNewPassword( 8 );
    }

    private static String doGenerateNewPassword( final int length )
    {
        final int[][] ranges = new int[3][];
        ranges[0] = createIntRange( 65, 90 );
        ranges[1] = createIntRange( 97, 122 );
        ranges[2] = createIntRange( 49, 57 );

        final Random rnd = new Random();

        final StringBuffer pwd = new StringBuffer( length );
        for ( int i = 0; i < length; i++ )
        {
            final int[] range = ranges[rnd.nextInt( 3 )];

            final char pwdChar = (char) range[rnd.nextInt( range.length )];
            pwd.append( pwdChar );
        }

        return pwd.toString();
    }

    private static int[] createIntRange( final int start, final int end )
    {
        final int length = end - start + 1;
        final int[] array = new int[length];

        for ( int i = 0; i <= end - start; i++ )
        {
            array[i] = start + i;
        }

        return array;
    }
}
