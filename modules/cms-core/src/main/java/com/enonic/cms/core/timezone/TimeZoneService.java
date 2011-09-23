/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.timezone;

import java.util.List;

import org.joda.time.DateTimeZone;

/**
 * Sep 8, 2009
 */
public interface TimeZoneService
{
    /**
     * Returns an unmodifyable list of available time zones.
     */
    List<DateTimeZone> getTimeZones();
}
