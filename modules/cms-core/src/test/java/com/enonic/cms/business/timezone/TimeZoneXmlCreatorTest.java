/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.business.timezone;

import java.util.ArrayList;
import java.util.List;

import org.custommonkey.xmlunit.Diff;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.timezone.TimeZoneXmlCreator;

public class TimeZoneXmlCreatorTest
    extends TestCase
{
    DateTime now = new DateTime( 2011, 1, 1, 1, 1, 1, 1 );

    TimeZoneXmlCreator xmlCreator = new TimeZoneXmlCreator( now );

    @Before
    public void setUp()
    {
    }

    @Test
    public void testCreateTimeZoneXmlDocument()
        throws Exception
    {

        List<DateTimeZone> timeZones = new ArrayList<DateTimeZone>();

        timeZones.add( DateTimeZone.UTC );

        Document doc = xmlCreator.createTimeZonesDocument( timeZones );

        StringBuffer buf = new StringBuffer();
        buf.append( "<time-zones>" );
        buf.append( "<time-zone ID=\"UTC\">" );
        buf.append( "<display-name>UTC</display-name>" );
        buf.append( "<hours-from-utc-as-human-readable>+00:00</hours-from-utc-as-human-readable>" );
        buf.append( "</time-zone>" );
        buf.append( "</time-zones>" );

        XMLOutputter outputter = new XMLOutputter();
        Diff myDiff = new Diff( buf.toString(), outputter.outputString( doc ) );

        assertTrue( "XML should be equal", myDiff.identical() );
    }


    @Test
    public void testMinusOffset()
        throws Exception
    {

        List<DateTimeZone> timeZones = new ArrayList<DateTimeZone>();

        timeZones.add( DateTimeZone.forOffsetHours( -1 ) );
        timeZones.add( DateTimeZone.forOffsetHoursMinutes( -1, 30 ) );
        timeZones.add( DateTimeZone.forOffsetMillis( -360000 ) );

        Document doc = xmlCreator.createTimeZonesDocument( timeZones );

        List<Element> timeZonesElements = JDOMUtil.getChildren( doc.getRootElement(), "time-zone" );

        for ( Element el : timeZonesElements )
        {
            Element toCheck = el.getChild( "hours-from-utc-as-human-readable" );
            assertEquals( el.getAttribute( "ID" ).getValue(), toCheck.getText() );
        }
    }

    @Test
    public void testTwoDigitOffset()
        throws Exception
    {

        List<DateTimeZone> timeZones = new ArrayList<DateTimeZone>();

        timeZones.add( DateTimeZone.forOffsetHours( 10 ) );
        timeZones.add( DateTimeZone.forOffsetHoursMinutes( 10, 30 ) );
        timeZones.add( DateTimeZone.forOffsetMillis( 720000 ) );

        Document doc = xmlCreator.createTimeZonesDocument( timeZones );

        List<Element> timeZonesElements = JDOMUtil.getChildren( doc.getRootElement(), "time-zone" );

        for ( Element el : timeZonesElements )
        {
            Element toCheck = el.getChild( "hours-from-utc-as-human-readable" );
            assertEquals( el.getAttribute( "ID" ).getValue(), toCheck.getText() );
        }
    }
}


