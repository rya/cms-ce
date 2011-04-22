/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.timezone;

import java.util.List;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TimeZoneServiceImplTest
{
    private TimeZoneService timeZoneService;

    @Before
    public void setUp()
    {
        this.timeZoneService = new TimeZoneServiceImpl();
    }

    @Test
    public void testGetTimeZones()
    {
        List<DateTimeZone> zones = this.timeZoneService.getTimeZones();
        Assert.assertNotNull( zones );
        Assert.assertTrue( zones.size() > 0 );
    }

}
