/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.ReadableDateTime;

/**
 * This class implements the index value.
 */
public final class ValueConverter
{

    private static final long SIGN_MASK = 0x8000000000000000L;

    private static final int STRING_DOUBLE_LEN = Long.toString( Long.MAX_VALUE, Character.MAX_RADIX ).length() + 1;

    private final static DateFormat FULL_DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss" );

    private final static DateFormat DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd" );

    private final static DateFormat DATETIME_WITH_SECS_FORMAT = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

    private final static DateFormat DATETIME_WITHOUT_SECS_FORMAT = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );

    /**
     * Private constructor.
     */
    private ValueConverter()
    {
    }

    public static String toString( long value )
    {
        return Long.toString( value );
    }

    public static String toString( double value )
    {
        return Double.toString( value );
    }

    public static String toString( Date value )
    {
        return value != null ? FULL_DATE_FORMAT.format( value ) : null;
    }

    public static String toTypedString( Date value )
    {
        return value != null ? toTypedString( value.getTime() ) : null;
    }

    public static String toTypedString( double value )
    {
        long longValue = Double.doubleToLongBits( value );
        StringBuffer sb = new StringBuffer( STRING_DOUBLE_LEN );
        if ( ( longValue & SIGN_MASK ) == 0 )
        {
            String s = Long.toString( longValue, Character.MAX_RADIX );
            sb.append( '1' );
            while ( ( sb.length() + s.length() ) < STRING_DOUBLE_LEN )
            {
                sb.append( '0' );
            }
            sb.append( s );
        }
        else
        {
            longValue = -longValue;
            String s = Long.toString( longValue, Character.MAX_RADIX );
            while ( ( sb.length() + s.length() ) < STRING_DOUBLE_LEN )
            {
                sb.append( '0' );
            }
            sb.append( s );
        }

        return sb.toString().toLowerCase();
    }

    public static Double toDouble( String value )
    {
        try
        {
            Double num = new Double( value );
            if ( num.isNaN() || num.isInfinite() )
            {
                return null;
            }
            else
            {
                return num;
            }
        }
        catch ( NumberFormatException e )
        {
            return null;
        }
    }

    public static ReadableDateTime toDate( String value )
    {

        value = value.toUpperCase();

        Date dateTimeByFullFormat = toDate( value, FULL_DATE_FORMAT );
        if ( dateTimeByFullFormat != null )
        {
            return new DateTime( dateTimeByFullFormat );
        }

        Date dateTimeByDateTimeWithSecsFormat = toDate( value, DATETIME_WITH_SECS_FORMAT );
        if ( dateTimeByDateTimeWithSecsFormat != null )
        {
            return new DateTime( dateTimeByDateTimeWithSecsFormat );
        }

        Date dateTimeByDateTimeWithoutSecsFormat = toDate( value, DATETIME_WITHOUT_SECS_FORMAT );
        if ( dateTimeByDateTimeWithoutSecsFormat != null )
        {
            return new DateTime( dateTimeByDateTimeWithoutSecsFormat );
        }

        Date dateByDateFormat = toDate( value, DATE_FORMAT );
        if ( dateByDateFormat != null )
        {
            // We use DateMidnight to later recognise that user have not specified time
            return new DateMidnight( dateByDateFormat );
        }

        return null;
    }

    private static Date toDate( String value, DateFormat format )
    {
        try
        {
            return format.parse( value );
        }
        catch ( ParseException e )
        {
            // Do nothing
            return null;
        }
    }
}
