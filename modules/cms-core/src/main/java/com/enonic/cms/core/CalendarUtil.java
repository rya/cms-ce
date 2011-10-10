/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.xml.XMLTool;

public class CalendarUtil
{

    private CalendarUtil()
    {
    }

    public static String formatCurrentDate()
    {
        return formatCurrentDate( false );
    }

    public static String formatCurrentDate( boolean timestampFormat )
    {
        return formatDate( System.currentTimeMillis(), timestampFormat );
    }

    public static String formatDate( long time )
    {
        return formatDate( time, false );
    }

    public static synchronized String formatDate( long time, boolean timestampFormat )
    {

        if ( timestampFormat )
        {
            return CmsDateAndTimeFormats.printAs_STORE_TIMESTAMP( time );
        }
        else
        {
            return CmsDateAndTimeFormats.printAs_STORE_DATE( time );
        }
    }

    public static String formatTimestamp( Date timestamp )
    {
        return formatTimestamp( timestamp, false );
    }

    public static synchronized String formatTimestamp( Date timestamp, boolean timestampFormat )
    {

        final long time = timestamp.getTime();

        if ( timestampFormat )
        {
            return CmsDateAndTimeFormats.printAs_STORE_TIMESTAMP( time );
        }
        else
        {
            return CmsDateAndTimeFormats.printAs_STORE_DATE( time );
        }

    }

    /**
     * Quick hack to get day of week. Can't get Calendar.get(Calendar.DAY_OF_WEEK) to work...
     *
     * @param cal Calendar
     * @return int
     */
    private static int getDayOfWeek( Calendar cal )
    {
        if ( cal.get( Calendar.DAY_OF_WEEK ) > 1 )
        {
            return cal.get( Calendar.DAY_OF_WEEK ) - 1;
        }

        // sunday is last day of week
        return 7;
    }

    public static String getMonth( int month, int year )
    {
        Calendar today = new GregorianCalendar();
        Calendar cal, calFollow;
        SimpleDateFormat dateformat = new SimpleDateFormat( "yyyy-MM-dd" );
        SimpleDateFormat followdateformat = new SimpleDateFormat( "MMMM", new Locale( "no", "NO" ) );
        Document doc = null;
        //XMLTool XMLTool;

        if ( year == -1 )
        {
            year = today.get( Calendar.YEAR );
        }

        if ( month < 0 || month > 11 )
        {
            month = today.get( Calendar.MONTH );
        }

        cal = new GregorianCalendar( year, month, 1 );
        dateformat.setCalendar( cal );

        // following months
        calFollow = new GregorianCalendar();
        followdateformat.setCalendar( calFollow );

        // create document root and weeks
        doc = XMLTool.createDocument( "month" );
        Element root = doc.getDocumentElement();
        root.setAttribute( "number", Integer.toString( cal.get( Calendar.MONTH ) ) );
        root.setAttribute( "currentnumber", Integer.toString( today.get( Calendar.MONTH ) ) );
        root.setAttribute( "year", Integer.toString( cal.get( Calendar.YEAR ) ) );
        root.setAttribute( "currentyear", Integer.toString( today.get( Calendar.YEAR ) ) );

        // set 3 following months
        Element followingMonthsElem = XMLTool.createElement( doc, root, "followingmonths" );
        int x = 0;
        for ( int i = 0; i < 3; i++ )
        {
            calFollow.add( Calendar.MONTH, x );
            if ( x == 0 )
            {
                x++;
            }
            Element followingMonthElem = XMLTool.createElement( doc, followingMonthsElem, "followingmonth",
                                                                followdateformat.format( calFollow.getTime() ) + " " +
                                                                    Integer.toString( calFollow.get( Calendar.YEAR ) ) );
            followingMonthElem.setAttribute( "number", Integer.toString( calFollow.get( Calendar.MONTH ) ) );

        }

        // set weeks in month
        Element weeksElem = XMLTool.createElement( doc, root, "weeks" );

        // maximum six weeks in one month
        for ( int i = 0; i < 6; i++ )
        {
            // create week and days element, set week number attribute
            Element weekElem = XMLTool.createElement( doc, weeksElem, "week" );
            weekElem.setAttribute( "number", Integer.toString( cal.get( Calendar.WEEK_OF_YEAR ) ) );
            weekElem.setAttribute( "startdate", dateformat.format( cal.getTime() ) );

            Element daysElem = XMLTool.createElement( doc, weekElem, "days" );

            // create day element for the seven days
            for ( int j = 1; j < 8; j++ )
            {
                if ( getDayOfWeek( cal ) == j && cal.get( Calendar.MONTH ) == month )
                {
                    // create day element and set attributes
                    Element dayElem = XMLTool.createElement( doc, daysElem, "day", Integer.toString( cal.get( Calendar.DAY_OF_MONTH ) ) );
                    dayElem.setAttribute( "date", dateformat.format( cal.getTime() ) );

                    if ( today.get( Calendar.DAY_OF_MONTH ) == cal.get( Calendar.DAY_OF_MONTH ) &&
                        today.get( Calendar.MONTH ) == cal.get( Calendar.MONTH ) )
                    {
                        dayElem.setAttribute( "today", "true" );
                    }

                    // next day
                    cal.add( Calendar.DATE, 1 );

                }
                else
                {
                    // create empty day element
                    XMLTool.createElement( doc, daysElem, "day" );
                }
            }
            // set end of week date
            cal.add( Calendar.DATE, -1 );
            weekElem.setAttribute( "enddate", dateformat.format( cal.getTime() ) );
            cal.add( Calendar.DATE, 1 );

            // quit if not in this month
            if ( month != cal.get( Calendar.MONTH ) )
            {
                break;
            }
        }

        return XMLTool.documentToString( doc );
    }
}