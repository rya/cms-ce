/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package net.sf.saxon.number;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import junit.framework.Assert;
import junit.framework.TestCase;

public class Numberer_noTest
    extends TestCase
{

    public void testToWords()
    {
        Numberer_no numberer = new Numberer_no();

        Assert.assertEquals( decode( "" ), numberer.toWords( 0 ) );
        Assert.assertEquals( decode( "en" ), numberer.toWords( 1 ) );
        Assert.assertEquals( decode( "to" ), numberer.toWords( 2 ) );
        Assert.assertEquals( decode( "tre" ), numberer.toWords( 3 ) );
        Assert.assertEquals( decode( "fire" ), numberer.toWords( 4 ) );

        Assert.assertEquals( decode( "ti" ), numberer.toWords( 10 ) );
        Assert.assertEquals( decode( "elleve" ), numberer.toWords( 11 ) );
        Assert.assertEquals( decode( "tolv" ), numberer.toWords( 12 ) );
        Assert.assertEquals( decode( "tretten" ), numberer.toWords( 13 ) );
        Assert.assertEquals( decode( "fjorten" ), numberer.toWords( 14 ) );

        Assert.assertEquals( decode( "tjue" ), numberer.toWords( 20 ) );
        Assert.assertEquals( decode( "tjueen" ), numberer.toWords( 21 ) );
        Assert.assertEquals( decode( "tjueto" ), numberer.toWords( 22 ) );
        Assert.assertEquals( decode( "tjuetre" ), numberer.toWords( 23 ) );
        Assert.assertEquals( decode( "tjuefire" ), numberer.toWords( 24 ) );

        Assert.assertEquals( decode( "et hundre og tjue" ), numberer.toWords( 120 ) );
        Assert.assertEquals( decode( "et hundre og tjueen" ), numberer.toWords( 121 ) );
        Assert.assertEquals( decode( "et hundre og tjueto" ), numberer.toWords( 122 ) );
        Assert.assertEquals( decode( "et hundre og tjuetre" ), numberer.toWords( 123 ) );
        Assert.assertEquals( decode( "et hundre og tjuefire" ), numberer.toWords( 124 ) );

        Assert.assertEquals( decode( "to tusen et hundre og tjue" ), numberer.toWords( 2120 ) );
        Assert.assertEquals( decode( "to tusen et hundre og tjueen" ), numberer.toWords( 2121 ) );
        Assert.assertEquals( decode( "to tusen et hundre og tjueto" ), numberer.toWords( 2122 ) );
        Assert.assertEquals( decode( "to tusen et hundre og tjuetre" ), numberer.toWords( 2123 ) );
        Assert.assertEquals( decode( "to tusen et hundre og tjuefire" ), numberer.toWords( 2124 ) );

        Assert.assertEquals( decode( "to tusen og tjue" ), numberer.toWords( 2020 ) );
        Assert.assertEquals( decode( "to tusen og tjueen" ), numberer.toWords( 2021 ) );
        Assert.assertEquals( decode( "to tusen og tjueto" ), numberer.toWords( 2022 ) );
        Assert.assertEquals( decode( "to tusen og tjuetre" ), numberer.toWords( 2023 ) );
        Assert.assertEquals( decode( "to tusen og tjuefire" ), numberer.toWords( 2024 ) );

        Assert.assertEquals( decode( "ti" ), numberer.toWords( 10 ) );
        Assert.assertEquals( decode( "et hundre" ), numberer.toWords( 100 ) );
        Assert.assertEquals( decode( "et tusen" ), numberer.toWords( 1000 ) );
        Assert.assertEquals( decode( "ti tusen" ), numberer.toWords( 10000 ) );
        Assert.assertEquals( decode( "et hundre tusen" ), numberer.toWords( 100000 ) );
        Assert.assertEquals( decode( "en million" ), numberer.toWords( 1000000 ) );
        Assert.assertEquals( decode( "ti millioner" ), numberer.toWords( 10000000 ) );
        Assert.assertEquals( decode( "et hundre millioner" ), numberer.toWords( 100000000 ) );
        Assert.assertEquals( decode( "en milliard" ), numberer.toWords( 1000000000 ) );
        Assert.assertEquals( decode( "ti milliarder" ), numberer.toWords( 10000000000L ) );
        Assert.assertEquals( decode( "et hundre milliarder" ), numberer.toWords( 100000000000L ) );

        Assert.assertEquals( decode( "tjue" ), numberer.toWords( 20 ) );
        Assert.assertEquals( decode( "to hundre" ), numberer.toWords( 200 ) );
        Assert.assertEquals( decode( "to tusen" ), numberer.toWords( 2000 ) );
        Assert.assertEquals( decode( "tjue tusen" ), numberer.toWords( 20000 ) );
        Assert.assertEquals( decode( "to hundre tusen" ), numberer.toWords( 200000 ) );
        Assert.assertEquals( decode( "to millioner" ), numberer.toWords( 2000000 ) );
        Assert.assertEquals( decode( "tjue millioner" ), numberer.toWords( 20000000 ) );
        Assert.assertEquals( decode( "to hundre millioner" ), numberer.toWords( 200000000 ) );
        Assert.assertEquals( decode( "to milliarder" ), numberer.toWords( 2000000000 ) );
        Assert.assertEquals( decode( "tjue milliarder" ), numberer.toWords( 20000000000L ) );
        Assert.assertEquals( decode( "to hundre milliarder" ), numberer.toWords( 200000000000L ) );

        Assert.assertEquals( decode( "fire hundre og tolv millioner fire hundre og tolv tusen fire hundre og tolv" ),
                             numberer.toWords( 412412412 ) );
        Assert.assertEquals( decode( "to milliarder og to" ), numberer.toWords( 2000000002 ) );
        Assert.assertEquals( decode( "en milliard et hundre og elleve millioner et hundre og elleve tusen et hundre og elleve" ),
                             numberer.toWords( 1111111111 ) );
        Assert.assertEquals( decode(
            "tre hundre og trettitre milliarder tre hundre og trettitre millioner tre hundre og trettitre tusen tre hundre og trettitre" ),
                             numberer.toWords( 333333333333L ) );
    }

    public void testToOrdinalWords()
    {
        Numberer_no numberer = new Numberer_no();

        Assert.assertEquals( decode( "" ), numberer.toOrdinalWords( "", 0, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "f%C3%B8rste" ), numberer.toOrdinalWords( "", 1, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "andre" ), numberer.toOrdinalWords( "", 2, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "tredje" ), numberer.toOrdinalWords( "", 3, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "fjerde" ), numberer.toOrdinalWords( "", 4, Numberer_no.LOWER_CASE ) );

        Assert.assertEquals( decode( "tiende" ), numberer.toOrdinalWords( "", 10, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "ellevte" ), numberer.toOrdinalWords( "", 11, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "tolvte" ), numberer.toOrdinalWords( "", 12, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "trettende" ), numberer.toOrdinalWords( "", 13, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "fjortende" ), numberer.toOrdinalWords( "", 14, Numberer_no.LOWER_CASE ) );

        Assert.assertEquals( decode( "tjuende" ), numberer.toOrdinalWords( "", 20, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "tjuef%C3%B8rste" ), numberer.toOrdinalWords( "", 21, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "tjueandre" ), numberer.toOrdinalWords( "", 22, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "tjuetredje" ), numberer.toOrdinalWords( "", 23, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "tjuefjerde" ), numberer.toOrdinalWords( "", 24, Numberer_no.LOWER_CASE ) );

        Assert.assertEquals( decode( "et hundre og tjuende" ), numberer.toOrdinalWords( "", 120, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "et hundre og tjuef%C3%B8rste" ), numberer.toOrdinalWords( "", 121, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "et hundre og tjueandre" ), numberer.toOrdinalWords( "", 122, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "et hundre og tjuetredje" ), numberer.toOrdinalWords( "", 123, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "et hundre og tjuefjerde" ), numberer.toOrdinalWords( "", 124, Numberer_no.LOWER_CASE ) );

        Assert.assertEquals( decode( "to tusen et hundre og tjuende" ), numberer.toOrdinalWords( "", 2120, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "to tusen et hundre og tjuef%C3%B8rste" ),
                             numberer.toOrdinalWords( "", 2121, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "to tusen et hundre og tjueandre" ), numberer.toOrdinalWords( "", 2122, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "to tusen et hundre og tjuetredje" ), numberer.toOrdinalWords( "", 2123, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "to tusen et hundre og tjuefjerde" ), numberer.toOrdinalWords( "", 2124, Numberer_no.LOWER_CASE ) );

        Assert.assertEquals( decode( "to tusen og tjuende" ), numberer.toOrdinalWords( "", 2020, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "to tusen og tjuef%C3%B8rste" ), numberer.toOrdinalWords( "", 2021, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "to tusen og tjueandre" ), numberer.toOrdinalWords( "", 2022, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "to tusen og tjuetredje" ), numberer.toOrdinalWords( "", 2023, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "to tusen og tjuefjerde" ), numberer.toOrdinalWords( "", 2024, Numberer_no.LOWER_CASE ) );

        Assert.assertEquals( decode( "tiende" ), numberer.toOrdinalWords( "", 10, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "et hundrede" ), numberer.toOrdinalWords( "", 100, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "et tusende" ), numberer.toOrdinalWords( "", 1000, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "ti tusende" ), numberer.toOrdinalWords( "", 10000, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "et hundre tusende" ), numberer.toOrdinalWords( "", 100000, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "en millionte" ), numberer.toOrdinalWords( "", 1000000, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "ti millionte" ), numberer.toOrdinalWords( "", 10000000, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "et hundre millionte" ), numberer.toOrdinalWords( "", 100000000, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "en milliardte" ), numberer.toOrdinalWords( "", 1000000000, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "ti milliardte" ), numberer.toOrdinalWords( "", 10000000000L, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "et hundre milliardte" ), numberer.toOrdinalWords( "", 100000000000L, Numberer_no.LOWER_CASE ) );

        Assert.assertEquals( decode( "tjuende" ), numberer.toOrdinalWords( "", 20, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "to hundrede" ), numberer.toOrdinalWords( "", 200, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "to tusende" ), numberer.toOrdinalWords( "", 2000, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "tjue tusende" ), numberer.toOrdinalWords( "", 20000, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "to hundre tusende" ), numberer.toOrdinalWords( "", 200000, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "to millionte" ), numberer.toOrdinalWords( "", 2000000, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "tjue millionte" ), numberer.toOrdinalWords( "", 20000000, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "to hundre millionte" ), numberer.toOrdinalWords( "", 200000000, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "to milliardte" ), numberer.toOrdinalWords( "", 2000000000, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "tjue milliardte" ), numberer.toOrdinalWords( "", 20000000000L, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "to hundre milliardte" ), numberer.toOrdinalWords( "", 200000000000L, Numberer_no.LOWER_CASE ) );

        Assert.assertEquals( decode( "fire hundre og tolv millioner fire hundre og tolv tusen fire hundre og tolvte" ),
                             numberer.toOrdinalWords( "", 412412412, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "to milliarder og andre" ), numberer.toOrdinalWords( "", 2000000002, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode( "en milliard et hundre og elleve millioner et hundre og elleve tusen et hundre og ellevte" ),
                             numberer.toOrdinalWords( "", 1111111111, Numberer_no.LOWER_CASE ) );
        Assert.assertEquals( decode(
            "tre hundre og trettitre milliarder tre hundre og trettitre millioner tre hundre og trettitre tusen tre hundre og trettitredje" ),
                             numberer.toOrdinalWords( "", 333333333333L, Numberer_no.LOWER_CASE ) );
    }

    public void testDayName()
    {
        Numberer_no numberer = new Numberer_no();

        Assert.assertEquals( decode( "mandag" ), numberer.dayName( 1, 0, 20 ) );
        Assert.assertEquals( decode( "tirsdag" ), numberer.dayName( 2, 0, 20 ) );
        Assert.assertEquals( decode( "onsdag" ), numberer.dayName( 3, 0, 20 ) );
        Assert.assertEquals( decode( "torsdag" ), numberer.dayName( 4, 0, 20 ) );
        Assert.assertEquals( decode( "fredag" ), numberer.dayName( 5, 0, 20 ) );
        Assert.assertEquals( decode( "l%C3%B8rdag" ), numberer.dayName( 6, 0, 20 ) );
        Assert.assertEquals( decode( "s%C3%B8ndag" ), numberer.dayName( 7, 0, 20 ) );

        Assert.assertEquals( decode( "man" ), numberer.dayName( 1, 0, 3 ) );
        Assert.assertEquals( decode( "tir" ), numberer.dayName( 2, 0, 3 ) );
        Assert.assertEquals( decode( "ons" ), numberer.dayName( 3, 0, 3 ) );
        Assert.assertEquals( decode( "tor" ), numberer.dayName( 4, 0, 3 ) );
        Assert.assertEquals( decode( "fre" ), numberer.dayName( 5, 0, 3 ) );
        Assert.assertEquals( decode( "l%C3%B8r" ), numberer.dayName( 6, 0, 3 ) );
        Assert.assertEquals( decode( "s%C3%B8n" ), numberer.dayName( 7, 0, 3 ) );

        Assert.assertEquals( decode( "ma" ), numberer.dayName( 1, 0, 2 ) );
        Assert.assertEquals( decode( "ti" ), numberer.dayName( 2, 0, 2 ) );
        Assert.assertEquals( decode( "on" ), numberer.dayName( 3, 0, 2 ) );
        Assert.assertEquals( decode( "to" ), numberer.dayName( 4, 0, 2 ) );
        Assert.assertEquals( decode( "fr" ), numberer.dayName( 5, 0, 2 ) );
        Assert.assertEquals( decode( "l%C3%B8" ), numberer.dayName( 6, 0, 2 ) );
        Assert.assertEquals( decode( "s%C3%B8" ), numberer.dayName( 7, 0, 2 ) );
    }

    public void testMonthName()
    {
        Numberer_no numberer = new Numberer_no();

        Assert.assertEquals( decode( "januar" ), numberer.monthName( 1, 0, 20 ) );
        Assert.assertEquals( decode( "februar" ), numberer.monthName( 2, 0, 20 ) );
        Assert.assertEquals( decode( "mars" ), numberer.monthName( 3, 0, 20 ) );
        Assert.assertEquals( decode( "april" ), numberer.monthName( 4, 0, 20 ) );
        Assert.assertEquals( decode( "mai" ), numberer.monthName( 5, 0, 20 ) );
        Assert.assertEquals( decode( "juni" ), numberer.monthName( 6, 0, 20 ) );
        Assert.assertEquals( decode( "juli" ), numberer.monthName( 7, 0, 20 ) );
        Assert.assertEquals( decode( "august" ), numberer.monthName( 8, 0, 20 ) );
        Assert.assertEquals( decode( "september" ), numberer.monthName( 9, 0, 20 ) );
        Assert.assertEquals( decode( "oktober" ), numberer.monthName( 10, 0, 20 ) );
        Assert.assertEquals( decode( "november" ), numberer.monthName( 11, 0, 20 ) );
        Assert.assertEquals( decode( "desember" ), numberer.monthName( 12, 0, 20 ) );

        Assert.assertEquals( decode( "jan" ), numberer.monthName( 1, 0, 3 ) );
        Assert.assertEquals( decode( "feb" ), numberer.monthName( 2, 0, 3 ) );
        Assert.assertEquals( decode( "mar" ), numberer.monthName( 3, 0, 3 ) );
        Assert.assertEquals( decode( "apr" ), numberer.monthName( 4, 0, 3 ) );
        Assert.assertEquals( decode( "mai" ), numberer.monthName( 5, 0, 3 ) );
        Assert.assertEquals( decode( "jun" ), numberer.monthName( 6, 0, 3 ) );
        Assert.assertEquals( decode( "jul" ), numberer.monthName( 7, 0, 3 ) );
        Assert.assertEquals( decode( "aug" ), numberer.monthName( 8, 0, 3 ) );
        Assert.assertEquals( decode( "sep" ), numberer.monthName( 9, 0, 3 ) );
        Assert.assertEquals( decode( "okt" ), numberer.monthName( 10, 0, 3 ) );
        Assert.assertEquals( decode( "nov" ), numberer.monthName( 11, 0, 3 ) );
        Assert.assertEquals( decode( "des" ), numberer.monthName( 12, 0, 3 ) );
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
