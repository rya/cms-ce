/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for regular expressions.
 */
public abstract class RegexpUtil
{
    // Use as case-insensitive

    public static Matcher match( String inputString, String regexp )
    {
        return match( inputString, regexp, 0 );
    }

    public static Matcher match( String inputString, String regexp, int patternOptions )
    {
        Pattern pattern = Pattern.compile( regexp, patternOptions );
        if ( inputString == null )
        {
            inputString = "";
        }

        return pattern.matcher( inputString );
    }

    static public String substituteAll( String regexp, String subst, String target )
    {
        return target.replaceAll( regexp, subst );
    }

}
