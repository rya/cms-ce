/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.timezone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Component;

@Component("timeZoneService")
public class TimeZoneServiceImpl
    implements TimeZoneService
{

    private final List<DateTimeZone> timeZones = new ArrayList<DateTimeZone>();

    @SuppressWarnings({"unchecked"})
    public TimeZoneServiceImpl()
    {
        Set<String> ids = DateTimeZone.getAvailableIDs();
        this.timeZones.add( DateTimeZone.UTC );
        for ( final String id : ids )
        {
            if ( !id.equals( "UTC" ) )
            {
                this.timeZones.add( DateTimeZone.forID( id ) );
            }
        }
    }

    public List<DateTimeZone> getTimeZones()
    {
        return Collections.unmodifiableList( this.timeZones );
    }
}
