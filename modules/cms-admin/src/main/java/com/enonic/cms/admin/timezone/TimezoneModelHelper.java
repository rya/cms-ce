package com.enonic.cms.admin.timezone;

import java.util.Collection;

import org.joda.time.DateTimeZone;

public final class TimezoneModelHelper
{
    public static TimezoneModel toModel(final DateTimeZone entity)
    {
        final TimezoneModel model = new TimezoneModel();
        if (entity != null) {
            model.setId( entity.getID() );
            model.setShortName( entity.getShortName( 0 ) );
            model.setName( entity.getName( 0 ) );
            model.setOffset( entity.getStandardOffset( 0 ) );
        }
        return model;
    }

    public static TimezonesModel toModel(final Collection<DateTimeZone> list)
    {
        final TimezonesModel model = new TimezonesModel();
        model.setTotal(list.size());

        for (final DateTimeZone entity : list) {
            model.addTimezone(toModel(entity));
        }
        
        return model;
    }
}
