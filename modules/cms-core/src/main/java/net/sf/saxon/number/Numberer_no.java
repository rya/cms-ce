/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package net.sf.saxon.number;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Numberer class for the Norwegian language
 */
public class Numberer_no
    extends AbstractNumberer
{

    private static final long serialVersionUID = 1L;

    private static String[] norwegianOrdinalUnits =
        {"", "f%C3%B8rste", "andre", "tredje", "fjerde", "femte", "sjette", "sjuende", "%C3%A5ttende", "niende", "tiende", "ellevte",
            "tolvte", "trettende", "fjortende", "femtende", "sekstende", "syttende", "attende", "nittende"};

    private static String[] norwegianOrdinalTens =
        {"", "tiende", "tjuende", "trettiende", "f%C3%B8rtiende", "femtiende", "sekstiende", "syttiende", "%C3%A5ttiende", "nittiende"};

    private static String[] norwegianUnits =
        {"", "en", "to", "tre", "fire", "fem", "seks", "sju", "%C3%A5tte", "ni", "ti", "elleve", "tolv", "tretten", "fjorten", "seksten",
            "seksten", "s%C3%B8tten", "atten", "nitten"};

    private static String[] norwegianTens = {"", "ti", "tjue", "tretti", "f%C3%B8rti", "femti", "seksti", "sytti", "%C3%A5tti", "nitti"};

    private static String[] norwegianMonths =
        {"januar", "februar", "mars", "april", "mai", "juni", "juli", "august", "september", "oktober", "november", "desember"};

    private static String[] norwegianDays = {"mandag", "tirsdag", "onsdag", "torsdag", "fredag", "l%C3%B8rdag", "s%C3%B8ndag"};

    private static String[] norwegianDayAbbreviations = {"man", "tir", "ons", "tor", "fre", "l%C3%B8r", "s%C3%B8n"};

    private static int[] minUniqueDayLength = {1, 2, 1, 2, 1, 1, 1};

    public String toOrdinalWords( String ordinalParam, long number, int wordCase )
    {

        String s;

        if ( number >= 1000000000 )
        {
            long rem = number % 1000000000;
            s = ( number / 1000000000 == 1 ? "en" : toWords( number / 1000000000 ) ) + " milliard" +
                ( number / 1000000000 == 1 || rem == 0 ? "" : "er" ) +
                ( rem == 0 ? "te" : ( rem < 100 ? " og " : " " ) + toOrdinalWords( ordinalParam, rem, wordCase ) );
        }
        else if ( number >= 1000000 )
        {
            long rem = number % 1000000;
            s = ( number / 1000000 == 1 ? "en" : toWords( number / 1000000 ) ) + " million" +
                ( number / 1000000 == 1 || rem == 0 ? "" : "er" ) +
                ( rem == 0 ? "te" : ( rem < 100 ? " og " : " " ) + toOrdinalWords( ordinalParam, rem, wordCase ) );
        }
        else if ( number >= 1000 )
        {
            long rem = number % 1000;
            s = ( number / 1000 == 1 ? "et" : toWords( number / 1000 ) ) + " tusen" +
                ( rem == 0 ? "de" : ( rem < 100 ? " og " : " " ) + toOrdinalWords( ordinalParam, rem, wordCase ) );
        }
        else if ( number >= 100 )
        {
            long rem = number % 100;
            s = ( number / 100 == 1 ? "et" : toWords( number / 100 ) ) + " hundre" +
                ( rem == 0 ? "de" : " og " + toOrdinalWords( ordinalParam, rem, wordCase ) );
        }
        else
        {
            if ( number < 20 )
            {
                s = decode( norwegianOrdinalUnits[(int) number] );
            }
            else
            {
                int rem = (int) ( number % 10 );
                if ( rem == 0 )
                {
                    s = decode( norwegianOrdinalTens[(int) number / 10] );
                }
                else
                {
                    s = decode( norwegianTens[(int) number / 10] ) + decode( norwegianOrdinalUnits[rem] );
                }
            }
        }

        if ( wordCase == UPPER_CASE )
        {
            return s.toUpperCase();
        }
        else if ( wordCase == LOWER_CASE )
        {
            return s.toLowerCase();
        }
        else
        {
            return s;
        }
    }

    public String toWords( long number )
    {
        if ( number >= 1000000000 )
        {
            long rem = number % 1000000000;
            return ( number / 1000000000 == 1 ? "en" : toWords( number / 1000000000 ) ) + " milliard" +
                ( number / 1000000000 == 1 ? "" : "er" ) + ( rem == 0 ? "" : ( rem < 100 ? " og " : " " ) + toWords( rem ) );
        }
        else if ( number >= 1000000 )
        {
            long rem = number % 1000000;
            return ( number / 1000000 == 1 ? "en" : toWords( number / 1000000 ) ) + " million" + ( number / 1000000 == 1 ? "" : "er" ) +
                ( rem == 0 ? "" : ( rem < 100 ? " og " : " " ) + toWords( rem ) );
        }
        else if ( number >= 1000 )
        {
            long rem = number % 1000;
            return ( number / 1000 == 1 ? "et" : toWords( number / 1000 ) ) + " tusen" +
                ( rem == 0 ? "" : ( rem < 100 ? " og " : " " ) + toWords( rem ) );
        }
        else if ( number >= 100 )
        {
            long rem = number % 100;
            return ( number / 100 == 1 ? "et" : toWords( number / 100 ) ) + " hundre" + ( rem == 0 ? "" : " og " + toWords( rem ) );
        }
        else
        {
            if ( number < 20 )
            {
                return decode( norwegianUnits[(int) number] );
            }
            int rem = (int) ( number % 10 );
            return decode( norwegianTens[(int) number / 10] ) + decode( norwegianUnits[rem] );
        }
    }

    public String toWords( long number, int wordCase )
    {
        String s;
        if ( number == 0 )
        {
            s = "null";
        }
        else
        {
            s = toWords( number );
        }

        if ( wordCase == UPPER_CASE )
        {
            return s.toUpperCase();
        }
        else if ( wordCase == LOWER_CASE )
        {
            return s.toLowerCase();
        }
        else
        {
            return s;
        }
    }

    public String monthName( int month, int minWidth, int maxWidth )
    {
        String name = decode( norwegianMonths[month - 1] );
        if ( maxWidth < 3 )
        {
            maxWidth = 3;
        }
        if ( name.length() > maxWidth )
        {
            name = name.substring( 0, maxWidth );
        }
        while ( name.length() < minWidth )
        {
            name = name + ' ';
        }
        return name;
    }

    public String dayName( int day, int minWidth, int maxWidth )
    {
        String name = decode( norwegianDays[day - 1] );
        if ( maxWidth < 2 )
        {
            maxWidth = 2;
        }
        if ( name.length() > maxWidth )
        {
            name = decode( norwegianDayAbbreviations[day - 1] );
            if ( name.length() > maxWidth )
            {
                name = name.substring( 0, maxWidth );
            }
        }
        while ( name.length() < minWidth )
        {
            name = name + ' ';
        }
        if ( minWidth == 1 && maxWidth == 2 )
        {
            // special case
            name = name.substring( 0, minUniqueDayLength[day - 1] );
        }
        return name;
    }

    private String decode( String s )
    {
        try
        {
            return URLDecoder.decode( s, "UTF-8" );
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new RuntimeException( e );
        }
    }
}
