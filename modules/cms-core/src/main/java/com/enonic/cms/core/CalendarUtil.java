/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import java.util.Calendar;
import java.util.Date;

public class CalendarUtil
{

    private CalendarUtil()
    {
    }

    public static String formatCurrentDate()
    {
        return formatCurrentDate( false );
    }

    private static String formatCurrentDate( boolean timestampFormat )
    {
        return formatDate( System.currentTimeMillis(), timestampFormat );
    }

    private static synchronized String formatDate( long time, boolean timestampFormat )
    {

        if ( timestampFormat )
        {
            return CmsDateAndTimeFormats.printAs_STORE_TIMESTAMP( time );
        }
        else
        {
            return CmsDateAndTimeFormats.printAs_STORE_DATE( time );
        }
    }

    public static String formatTimestamp( Date timestamp )
    {
        return formatTimestamp( timestamp, false );
    }

    public static synchronized String formatTimestamp( Date timestamp, boolean timestampFormat )
    {

        final long time = timestamp.getTime();

        if ( timestampFormat )
        {
            return CmsDateAndTimeFormats.printAs_STORE_TIMESTAMP( time );
        }
        else
        {
            return CmsDateAndTimeFormats.printAs_STORE_DATE( time );
        }

    }
}
