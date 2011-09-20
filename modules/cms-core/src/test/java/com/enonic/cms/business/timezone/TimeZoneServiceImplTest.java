/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.timezone;

import java.util.List;

import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;

public class TimeZoneServiceImplTest
{
    @Test
    public void testGetTimeZones()
    {
        TimeZoneService timeZoneService = new TimeZoneServiceImpl();
        List<DateTimeZone> zones = timeZoneService.getTimeZones();
        Assert.assertNotNull( zones );
        Assert.assertTrue( zones.size() > 0 );
    }
}
