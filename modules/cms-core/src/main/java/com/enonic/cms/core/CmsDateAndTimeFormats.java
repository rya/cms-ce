/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import java.util.Date;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class CmsDateAndTimeFormats
{

    public static final String STORE_DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm";

    public static final String STORE_TIMESTAMP_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String XML_TIMESTAMP_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String XML_DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm";

    /**
     * Format used for storing dates.
     */
    private static final DateTimeFormatter STORE_DATE_FORMAT = DateTimeFormat.forPattern( STORE_DATE_FORMAT_PATTERN );

    /**
     * Format used for storing timestamps.
     */
    private static final DateTimeFormatter STORE_TIMESTAMP_FORMAT = DateTimeFormat.forPattern( STORE_TIMESTAMP_FORMAT_PATTERN );

    /**
     * Format used for xml timestamps.
     */
    private static final DateTimeFormatter XML_TIMESTAMP_FORMAT = DateTimeFormat.forPattern( XML_TIMESTAMP_FORMAT_PATTERN );

    /**
     * Format used for xml dates.
     */
    private static final DateTimeFormatter XML_DATE_FORMAT = DateTimeFormat.forPattern( XML_DATE_FORMAT_PATTERN );


    public static Date parseFrom_STORE_DATE( String dateTime )
    {
        return STORE_DATE_FORMAT.parseDateTime( dateTime ).toDate();
    }

    public static String printAs_STORE_DATE( Date date )
    {
        return STORE_DATE_FORMAT.print( date.getTime() );
    }

    public static String printAs_STORE_DATE( long time )
    {

        return STORE_DATE_FORMAT.print( time );
    }

    public static String printAs_STORE_TIMESTAMP( Date date )
    {

        return STORE_TIMESTAMP_FORMAT.print( date.getTime() );
    }

    public static String printAs_STORE_TIMESTAMP( long time )
    {

        return STORE_TIMESTAMP_FORMAT.print( time );
    }

    public static String printAs_XML_DATE( Date date )
    {

        return XML_DATE_FORMAT.print( date.getTime() );
    }

    public static String printAs_XML_TIMESTAMP( Date date )
    {

        return XML_TIMESTAMP_FORMAT.print( date.getTime() );
    }
}
