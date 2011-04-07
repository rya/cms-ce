/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.timezone;

import java.util.List;

import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.enonic.cms.core.timezone.TimeZoneService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TimeZoneServiceImplTest
{
    @Autowired
    private TimeZoneService timeZoneService;

    @Test
    public void testGetTimeZones()
    {
        List<DateTimeZone> zones = timeZoneService.getTimeZones();
        Assert.assertNotNull( zones );
        Assert.assertTrue( zones.size() > 0 );
    }

}
