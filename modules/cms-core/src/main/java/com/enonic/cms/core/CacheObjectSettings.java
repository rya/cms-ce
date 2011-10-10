/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import org.joda.time.DateTime;
import org.joda.time.Interval;

public class CacheObjectSettings
{

    public static String TYPE_DEFAULT = "default";

    public static String TYPE_SPECIFIED = "specified";

    public static String TYPE_FOREVER = "forever";

    /**
     * Ten years in seconds.
     */
    private final static int FAR_FUTURE_IN_SECONDS =
        (int) ( new Interval( new DateTime( 2000, 1, 1, 1, 1, 1, 1 ), new DateTime( 2010, 1, 1, 1, 1, 1, 1 ) ).toDurationMillis() / 1000 );

    private Integer secondsToLive;

    private boolean liveForever = false;

    private boolean useDefaultSettings = false;

    public static CacheObjectSettings createFrom( CacheSettings menuItemCacheSettings )
    {
        return new CacheObjectSettings( menuItemCacheSettings.getType(), menuItemCacheSettings.getSpecifiedSecondsToLive() );
    }

    public static CacheObjectSettings createTypeDefault()
    {
        return new CacheObjectSettings( TYPE_DEFAULT, null );
    }

    public static CacheObjectSettings createTypeSpecified( int secondsToLive )
    {
        return new CacheObjectSettings( TYPE_SPECIFIED, secondsToLive );
    }

    public static CacheObjectSettings createTypeForever()
    {
        return new CacheObjectSettings( TYPE_FOREVER, null );
    }

    public CacheObjectSettings( String cacheType, Integer secondsToLive )
    {

        if ( TYPE_DEFAULT.equals( cacheType ) )
        {
            this.useDefaultSettings = true;
        }
        else if ( TYPE_SPECIFIED.equals( cacheType ) )
        {
            this.secondsToLive = secondsToLive;

            if ( secondsToLive < 1 )
            {
                throw new IllegalArgumentException( "Given secondsToLive cant be less than 1 second" );
            }
        }
        else if ( TYPE_FOREVER.equals( cacheType ) )
        {
            liveForever = true;
            this.secondsToLive = FAR_FUTURE_IN_SECONDS;
        }
    }

    public int getSecondsToLive()
    {
        return secondsToLive;
    }

    public boolean liveForever()
    {
        return liveForever;
    }

    public boolean useDefaultSettings()
    {
        return useDefaultSettings;
    }
}
