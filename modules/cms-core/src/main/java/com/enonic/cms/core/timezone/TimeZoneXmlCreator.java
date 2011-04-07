/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.timezone;

import java.util.Collection;

import org.jdom.Document;
import org.jdom.Element;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;

/**
 * Sep 8, 2009
 */
public class TimeZoneXmlCreator
{
    private DateTime now;

    public TimeZoneXmlCreator( DateTime now )
    {
        this.now = now;
    }

    public Document createTimeZonesDocument( final Collection<DateTimeZone> timeZones )
    {
        if ( timeZones == null )
        {
            throw new IllegalArgumentException( "timeZones cannot be null" );
        }

        Element timeZonesEl = new Element( "time-zones" );
        for ( DateTimeZone timeZone : timeZones )
        {
            timeZonesEl.addContent( doCreateTimeZoneElement( timeZone ) );
        }
        return new Document( timeZonesEl );
    }

    private Element doCreateTimeZoneElement( DateTimeZone timeZone )
    {

        Element timeZoneEl = new Element( "time-zone" );
        timeZoneEl.setAttribute( "ID", timeZone.getID() );
        timeZoneEl.addContent( new Element( "display-name" ).setText( timeZone.getID() ) );

        DateTime localTime = now.plus( timeZone.getOffsetFromLocal( now.getMillis() ) );
        Period offsetPeriod = new Period( now, localTime );
        //timeZoneEl.addContent( new Element( "hours-from-utc" ).setText( String.valueOf( offsetPeriod.getHours() ) ) );
        timeZoneEl.addContent( new Element( "hours-from-utc-as-human-readable" ).setText( getHoursAsHumanReadable( offsetPeriod ) ) );

        return timeZoneEl;
    }

    private String getHoursAsHumanReadable( Period offsetPeriod )
    {
        final StringBuffer s = new StringBuffer();
        if ( offsetPeriod.getMinutes() < 0 )
        {
            s.append( "-" );
        }
        else
        {
            s.append( "+" );
        }

        final int hours = offsetPeriod.getHours();

        if ( hours < 10 && hours > ( -10 ) )
        {
            s.append( "0" );
        }
        s.append( Math.abs( hours ) );
        s.append( ":" );

        final int minutes = offsetPeriod.getMinutes();
        if ( minutes < 10 )
        {
            s.append( "0" );
        }
        s.append( minutes );
        return s.toString();
    }
}
