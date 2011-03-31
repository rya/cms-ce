/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil
{

    private static DateFormat dateFormat = new SimpleDateFormat( "dd.MM.yyyy" );

    private static DateFormat dateFormatWithTime = new SimpleDateFormat( "dd.MM.yyyy HH:mm" );

    private static DateFormat dateFormatWithTimeSeconds = new SimpleDateFormat( "dd.MM.yyyy HH:mm:ss" );

    private static DateFormat isoDateFormatNoTime = new SimpleDateFormat( "yyyy-MM-dd" );

    private static DateFormat isoDateFormatWithTime = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
    //private static DateFormat isoDateFormatWithTimeSeconds = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

    public static Date parseDate( String date )
        throws ParseException
    {
        return dateFormat.parse( date );
    }

    public static Date parseDateTime( String date )
        throws ParseException
    {
        return parseDateTime( date, false );
    }

    public static Date parseDateTime( String date, boolean includeSeconds )
        throws ParseException
    {
        if ( includeSeconds )
        {
            return dateFormatWithTimeSeconds.parse( date );
        }
        else
        {
            return dateFormatWithTime.parse( date );
        }
    }

    public static Date parseISODate( String date )
        throws ParseException
    {
        return isoDateFormatNoTime.parse( date );
    }

    public static Date parseISODateTime( String date )
        throws ParseException
    {
        return parseISODateTime( date, false );
    }

    public static Date parseISODateTime( String date, boolean includeSeconds )
        throws ParseException
    {
        return isoDateFormatWithTime.parse( date );
    }

    public static String formatDate( Date date )
    {
        return dateFormat.format( date );
    }

    public static String formatDateTime( Date date )
    {
        return dateFormatWithTime.format( date );
    }

    public static String formatISODate( Date date )
    {
        return isoDateFormatNoTime.format( date );
    }

    public static String formatISODateTime( Date date )
    {
        return isoDateFormatWithTime.format( date );
    }

    public static String formatDate( String dateStr, String inputFormat, String outputFormat )
        throws ParseException
    {
        DateFormat in = new SimpleDateFormat( inputFormat );
        DateFormat out = new SimpleDateFormat( outputFormat );
        Date date = in.parse( dateStr );
        return out.format( date );
    }

}
