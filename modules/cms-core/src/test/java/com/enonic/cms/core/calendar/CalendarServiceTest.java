/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.calendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.Test;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.enonic.cms.core.calendar.CalendarService;

/**
 *
 */
public class CalendarServiceTest
    extends AbstractDependencyInjectionSpringContextTests
{

    private CalendarService service;

    XMLOutputter output = new XMLOutputter( Format.getPrettyFormat() );

    public void setCalendarService( CalendarService service )
    {
        this.service = service;
    }

    public String[] getConfigLocations()
    {
        return ( new String[]{"classpath:" + getClass().getName().replace( '.', '/' ) + ".xml"} );
    }

    public void testTodayFormat()
    {
        Calendar today = new GregorianCalendar();
        int day = today.get( Calendar.DAY_OF_MONTH );
        int month = today.get( Calendar.MONTH );
        int year = today.get( Calendar.YEAR );
        SimpleDateFormat norwegianDateFormat = new SimpleDateFormat( "dd.MM.yyyy" );
        SimpleDateFormat americanDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        String norwegianTodayString = norwegianDateFormat.format( today.getTime() );
        today.add( Calendar.DAY_OF_MONTH, 8 );
        int offsetDay = today.get( Calendar.DAY_OF_MONTH );
        int offsetMonth = today.get( Calendar.MONTH );
        int offsetYear = today.get( Calendar.YEAR );
        String americanOffsetTodayString = americanDateFormat.format( today.getTime() );

        Document doc = service.getFormattedDate();

        String xmlDoc = output.outputString( doc );
        assertTrue( xmlDoc.contains( "<day>" + day + "</day>" ) );
        assertTrue( xmlDoc.contains( "<month>" + month + "</month>" ) );
        assertTrue( xmlDoc.contains( "<year>" + year + "</year>" ) );
        assertTrue( xmlDoc.contains( norwegianTodayString ) );
        Document doc3 = service.getFormattedDate( 8, "yyyy-MM-dd hh:mm", "en", "US" );
        xmlDoc = output.outputString( doc3 );
        assertTrue( xmlDoc.contains( "<day>" + offsetDay + "</day>" ) );
        assertTrue( xmlDoc.contains( "<month>" + offsetMonth + "</month>" ) );
        assertTrue( xmlDoc.contains( "<year>" + offsetYear + "</year>" ) );
        assertTrue( xmlDoc.contains( americanOffsetTodayString ) );
    }

    @Test
    public void testGetCalendar()
    {
        // The next 3 months, no matter when in the year:
        Document doc = service.getCalendar( true, 0, 0, 3, true, false, "en", "Norway" );
        checkNumberOfMonthsInDoc( doc, 3 );
        // December 2008, and January 2009:
        doc = service.getCalendar( false, 2008, 12, 2, true, false, "en", "no" );
        checkNumberOfMonthsInDoc( doc, 2 );
        checkMonthNamesInDoc( doc, new String[]{"December", "January"} );
        // The next 3 months, no matter when in the year, including days.
        doc = service.getCalendar( true, 0, 0, 3, true, true, "en", "Norway" );
        checkNumberOfMonthsInDoc( doc, 3 );  // This should be checked a little better.
        // December 2008, and January 2009, including days:
        doc = service.getCalendar( false, 2008, 11, 5, true, true, "no", "no" );
        checkNumberOfMonthsInDoc( doc, 5 );  // This should be checked a little better.
        checkMonthNamesInDoc( doc, new String[]{"November", "Desember", "Januar", "Februar", "Mars"} );
    }

    private void checkNumberOfMonthsInDoc( Document doc, int expectedNumberOfMonths )
    {
        int months = 0;
        List years = doc.getRootElement().getChildren();
        for ( Object year : years )
        {
            months += ( (Element) year ).getChildren().size();
        }
        assertEquals( "There must be some children", expectedNumberOfMonths, months );
    }

    private void checkMonthNamesInDoc( Document doc, String[] names )
    {
        List<String> monthNames = new ArrayList<String>( names.length );
        List years = doc.getRootElement().getChildren();

        for ( Object year : years )
        {
            List months = ( (Element) year ).getChildren();
            for ( Object month : months )
            {
                monthNames.add( ( (Element) month ).getAttributeValue( "name" ) );
            }
        }
        for ( int i = 0; i < names.length; i++ )
        {
            assertTrue( "Expected month name: " + names[i] + ", but got: " + monthNames.get( i ),
                        names[i].compareToIgnoreCase( monthNames.get( i ) ) == 0 );
        }
    }


}
