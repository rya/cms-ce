/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.calendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.esl.util.StringUtil;
import com.enonic.vertical.VerticalRuntimeException;

/**
 *
 */
public class CalendarService
{
    /**
     * Get todays date with the standard Norwegian format.
     *
     * @return A JDom XML Document with information about today.
     */
    public Document getFormattedDate()
    {
        return getFormattedDate( 0, "dd.MM.yyyy HH:mm:ss", "no", "NO" );
    }

    /**
     * Generates an XML for a given day, relative to today.
     *
     * @param offset     The number of days to add to today, to set the date the XML should be generated for.
     * @param dateformat The format in the field <code>datetimestring</code>.
     * @param language   The language of the locale used in the dateformat.
     * @param country    The country of the locale used in the dateformat.
     * @return A JDom xml <code>Document</code> with information about the specified date.
     */
    public Document getFormattedDate( int offset, String dateformat, String language, String country )
    {
        Locale loc = new Locale( language, country );
        SimpleDateFormat sdf = new SimpleDateFormat( dateformat, loc );
        return getFormattedDate( offset, sdf );
    }


    /**
     * Generates an XML for a given day, relative to today.
     *
     * @param offset The number of days to add to today, to set the date the XML should be generated for.
     * @param sdf    A format for the presentation of the date in the <code>datetimestring</code> field.
     * @return A JDom xml <code>Document</code> with information about the specified date.
     */
    public Document getFormattedDate( int offset, SimpleDateFormat sdf )
    {
        Calendar today = new GregorianCalendar();
        if ( offset != 0 )
        {
            today.add( Calendar.DATE, offset );
        }

        org.jdom.Element root = new org.jdom.Element( "formatteddate" );
        org.jdom.Document doc = new org.jdom.Document( root );
        root.addContent( new Element( "datetimestring" ).setText( sdf.format( today.getTime() ) ) );
        root.addContent( new Element( "day" ).setText( Integer.toString( today.get( Calendar.DATE ) ) ) );
        root.addContent( new Element( "month" ).setText( Integer.toString( today.get( Calendar.MONTH ) ) ) );
        root.addContent( new Element( "monthofyear" ).setText(
            Integer.toString( today.get( Calendar.MONTH ) + ( today.getActualMinimum( Calendar.MONTH ) == 0 ? 1 : 0 ) ) ) );
        root.addContent( new Element( "year" ).setText( Integer.toString( today.get( Calendar.YEAR ) ) ) );
        root.addContent( new Element( "hour" ).setText( Integer.toString( today.get( Calendar.HOUR_OF_DAY ) ) ) );
        root.addContent( new Element( "minute" ).setText( Integer.toString( today.get( Calendar.MINUTE ) ) ) );
        root.addContent( new Element( "second" ).setText( Integer.toString( today.get( Calendar.SECOND ) ) ) );
        sdf.applyPattern( "EEEE" );
        root.addContent( new Element( "weekday" ).setText( sdf.format( today.getTime() ) ) );
        return doc;
    }

    public Document getCalendar( boolean relative, int year, int month, int count, boolean includeWeeks, boolean includeDays,
                                 String language, String country )
    {

        org.jdom.Element root = new org.jdom.Element( "calendar" );
        org.jdom.Document doc = new org.jdom.Document( root );

        Locale locale = new Locale( language, country );

        // check for valid count value
        if ( count < 0 )
        {

            VerticalRuntimeException.error( this.getClass(), VerticalRuntimeException.class, StringUtil.expandString(
                    "Parameter 'count' must be 0 or a positive integer.", (Object) null, null ) );
        }

        // set current date
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd", locale );
        root.setAttribute( "currentdate", dateFormat.format( new Date() ) );

        SimpleDateFormat dayName = new SimpleDateFormat( "EEEE", locale );
        SimpleDateFormat shortDayName = new SimpleDateFormat( "EE", locale );

        SimpleDateFormat monthName = new SimpleDateFormat( "MMMM", locale );
        SimpleDateFormat shortMonthName = new SimpleDateFormat( "MMM", locale );

        // return calendar data relative to the current date
        Calendar calendar = Calendar.getInstance( locale );

        // Setting the DAY_OF_MONTH to 1 is necessary to avoid month problems on the 31st of a month.  For instance:
        // The month is changed to November, on October 31st.  There is no November 31st, so the internal date will be December 1st.
        calendar.set( Calendar.DAY_OF_MONTH, 1 );
        if ( relative )
        {
            if ( year != 0 )
            {
                calendar.add( Calendar.YEAR, year );
            }
            if ( month != 0 )
            {
                calendar.add( Calendar.MONTH, month );
            }
        }
        else
        {
            calendar.set( Calendar.YEAR, year );
            if ( month > 1 )
            {
                calendar.set( Calendar.MONTH, month - 1 );
            }
            else
            {
                calendar.set( Calendar.MONTH, 0 );
            }
        }

        int currentMonth = calendar.get( Calendar.MONTH );
        int currentDay = calendar.get( Calendar.DAY_OF_YEAR );
        int currentYear = calendar.get( Calendar.YEAR );

        Element yearElement = new Element( "year" );
        root.addContent( yearElement );
        yearElement.setAttribute( "number", String.valueOf( currentYear ) );

        // set enddate
        calendar.set( Calendar.MONTH, calendar.getActualMaximum( Calendar.MONTH ) );
        calendar.set( Calendar.DAY_OF_MONTH, calendar.getActualMaximum( Calendar.DAY_OF_MONTH ) );
        yearElement.setAttribute( "enddate", dateFormat.format( calendar.getTime() ) );

        // set startdate
        calendar.set( Calendar.MONTH, calendar.getActualMinimum( Calendar.MONTH ) );
        calendar.set( Calendar.DAY_OF_MONTH, calendar.getActualMinimum( Calendar.DAY_OF_MONTH ) );
        yearElement.setAttribute( "startdate", dateFormat.format( calendar.getTime() ) );

        // reset date
        calendar.set( Calendar.MONTH, currentMonth );
        calendar.set( Calendar.DAY_OF_YEAR, currentDay );

        for ( int i = 0; i < count; ++i )
        {
            if ( calendar.get( Calendar.YEAR ) != currentYear )
            {
                currentYear = calendar.get( Calendar.YEAR );

                yearElement = new Element( "year" );
                root.addContent( yearElement );
                yearElement.setAttribute( "number", String.valueOf( currentYear ) );

                // set enddate
                calendar.set( Calendar.MONTH, calendar.getActualMaximum( Calendar.MONTH ) );
                calendar.set( Calendar.DAY_OF_MONTH, calendar.getActualMaximum( Calendar.DAY_OF_MONTH ) );
                yearElement.setAttribute( "enddate", dateFormat.format( calendar.getTime() ) );

                // set startdate
                calendar.set( Calendar.MONTH, calendar.getActualMinimum( Calendar.MONTH ) );
                calendar.set( Calendar.DAY_OF_MONTH, calendar.getActualMinimum( Calendar.DAY_OF_MONTH ) );
                yearElement.setAttribute( "startdate", dateFormat.format( calendar.getTime() ) );
            }

            Element monthElement = new Element( "month" );
            yearElement.addContent( monthElement );
            monthElement.setAttribute( "name", monthName.format( calendar.getTime() ) );
            monthElement.setAttribute( "shortname", shortMonthName.format( calendar.getTime() ) );
            monthElement.setAttribute( "number", String.valueOf( calendar.get( Calendar.MONTH ) + 1 ) );

            // set enddate
            calendar.set( Calendar.DAY_OF_MONTH, calendar.getActualMaximum( Calendar.DAY_OF_MONTH ) );
            monthElement.setAttribute( "enddate", dateFormat.format( calendar.getTime() ) );

            // set startdate
            calendar.set( Calendar.DAY_OF_MONTH, calendar.getActualMinimum( Calendar.DAY_OF_MONTH ) );
            monthElement.setAttribute( "startdate", dateFormat.format( calendar.getTime() ) );

            // weeks
            Calendar weekCal = (Calendar) calendar.clone();
            if ( includeWeeks || includeDays )
            {
                weekCal.set( Calendar.DAY_OF_MONTH, weekCal.getActualMinimum( Calendar.DAY_OF_MONTH ) );
                int thisMonth = weekCal.get( Calendar.MONTH );

                Element dayParentElement = monthElement;

                int j = 0;
                while ( weekCal.get( Calendar.MONTH ) == thisMonth && weekCal.get( Calendar.YEAR ) == currentYear )
                {
                    Element weekElement = null;

                    // set startdate
                    if ( j > 0 )
                    {
                        weekCal.set( Calendar.DAY_OF_WEEK, weekCal.getFirstDayOfWeek() );
                    }

                    if ( includeWeeks )
                    {
                        weekElement = new Element( "week" );
                        monthElement.addContent( weekElement );
                        weekElement.setAttribute( "number", String.valueOf( weekCal.get( Calendar.WEEK_OF_YEAR ) ) );
                        weekElement.setAttribute( "startdate", dateFormat.format( weekCal.getTime() ) );

                        dayParentElement = weekElement;
                    }

                    int thisWeek = weekCal.get( Calendar.WEEK_OF_MONTH );

                    // days
                    if ( includeDays )
                    {
                        while ( weekCal.get( Calendar.WEEK_OF_MONTH ) == thisWeek && weekCal.get( Calendar.MONTH ) == thisMonth &&
                            weekCal.get( Calendar.YEAR ) == currentYear )
                        {
                            Element dayElement = new Element( "day" );
                            dayParentElement.addContent( dayElement );
                            dayElement.setAttribute( "number", String.valueOf( weekCal.get( Calendar.DAY_OF_MONTH ) ) );
                            dayElement.setAttribute( "date", dateFormat.format( weekCal.getTime() ) );
                            dayElement.setAttribute( "name", dayName.format( weekCal.getTime() ) );
                            dayElement.setAttribute( "shortname", shortDayName.format( weekCal.getTime() ) );

                            int day = weekCal.get( Calendar.DAY_OF_WEEK );
                            day = day - 1;
                            if ( day == 0 )
                            {
                                day = 7;
                            }

                            String id = String.valueOf( day );

                            dayElement.setAttribute( "id", id );

                            weekCal.add( Calendar.DAY_OF_MONTH, 1 );
                        }

                        weekCal.add( Calendar.DAY_OF_MONTH, -1 );
                    }

                    // set enddate
                    if ( !includeDays )
                    {
                        while ( weekCal.get( Calendar.WEEK_OF_MONTH ) < ( thisWeek + 1 ) &&
                            weekCal.get( Calendar.MONTH ) < ( thisMonth + 1 ) && weekCal.get( Calendar.YEAR ) < ( currentYear + 1 ) )
                        {
                            weekCal.add( Calendar.DAY_OF_MONTH, 1 );
                        }
                        weekCal.add( Calendar.DAY_OF_MONTH, -1 );
                    }

                    if ( includeWeeks )
                    {
                        weekElement.setAttribute( "enddate", dateFormat.format( weekCal.getTime() ) );
                    }

                    // increment week
                    weekCal.add( Calendar.DAY_OF_MONTH, 1 );

                    ++j;
                }
            }  // if (includeWeeks || includeDays)

            // increment month
            calendar.add( Calendar.MONTH, 1 );
        }

        return doc;
    }
}
