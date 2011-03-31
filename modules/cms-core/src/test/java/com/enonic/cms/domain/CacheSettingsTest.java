/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain;

import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Sep 23, 2009
 */
public class CacheSettingsTest
{
    @Test
    public void anyEnabledForverGetsSecondsToLiveSetToIntegerMax()
    {
        assertEquals( new Integer( Integer.MAX_VALUE ),
                      new CacheSettings( true, CacheSettings.TYPE_FOREVER, 0 ).getSpecifiedSecondsToLive() );
        assertEquals( new Integer( Integer.MAX_VALUE ),
                      new CacheSettings( true, CacheSettings.TYPE_FOREVER, 500 ).getSpecifiedSecondsToLive() );
        assertEquals( new Integer( Integer.MAX_VALUE ),
                      new CacheSettings( true, CacheSettings.TYPE_FOREVER, Integer.MAX_VALUE ).getSpecifiedSecondsToLive() );
    }

    @Test
    public void anyDisabledGetsSecondsToLiveSetToZero()
    {
        assertEquals( new Integer( 0 ),
                      new CacheSettings( false, CacheSettings.TYPE_FOREVER, Integer.MAX_VALUE ).getSpecifiedSecondsToLive() );
        assertEquals( new Integer( 0 ), new CacheSettings( false, CacheSettings.TYPE_DEFAULT, 500 ).getSpecifiedSecondsToLive() );
        assertEquals( new Integer( 0 ), new CacheSettings( false, CacheSettings.TYPE_SPECIFIED, 500 ).getSpecifiedSecondsToLive() );
    }

    @Test
    public void a_disabled_CacheSettings_isTighterThan_any_but_disabled()
    {
        CacheSettings a_disabled = new CacheSettings( false, CacheSettings.TYPE_DEFAULT, 0 );

        CacheSettings not_disabled_default = new CacheSettings( true, CacheSettings.TYPE_DEFAULT, 0 );
        CacheSettings not_disabled_specified = new CacheSettings( true, CacheSettings.TYPE_SPECIFIED, 500 );
        CacheSettings not_disabled_forever = new CacheSettings( true, CacheSettings.TYPE_FOREVER, 0 );

        assertTrue( a_disabled.isTighterThan( not_disabled_default ) );
        assertTrue( a_disabled.isTighterThan( not_disabled_specified ) );
        assertTrue( a_disabled.isTighterThan( not_disabled_forever ) );
    }

    @Test
    public void an_enabled_default_with_less_seconds_isTighterThan_any_default_or_specified_with_more_seconds()
    {
        CacheSettings an_enabled_default_with_500_seconds = createEnabledDefault( 500 );

        assertTrue( an_enabled_default_with_500_seconds.isTighterThan( createEnabledDefault( 501 ) ) );

        assertFalse( an_enabled_default_with_500_seconds.isTighterThan( createEnabledDefault( 499 ) ) );

        assertTrue( an_enabled_default_with_500_seconds.isTighterThan( createEnabledSpecified( 501 ) ) );

        assertFalse( an_enabled_default_with_500_seconds.isTighterThan( createEnabledSpecified( 499 ) ) );

    }

    @Test
    public void an_enabled_specified_with_less_seconds_isTighterThan_any_default_or_specified_with_more_seconds()
    {
        CacheSettings an_enabled_specified_with_500_seconds = createEnabledSpecified( 500 );

        assertTrue( an_enabled_specified_with_500_seconds.isTighterThan( createEnabledDefault( 501 ) ) );

        assertFalse( an_enabled_specified_with_500_seconds.isTighterThan( createEnabledDefault( 499 ) ) );

        assertTrue( an_enabled_specified_with_500_seconds.isTighterThan( createEnabledSpecified( 501 ) ) );

        assertFalse( an_enabled_specified_with_500_seconds.isTighterThan( createEnabledSpecified( 499 ) ) );
    }

    @Test
    public void an_enabled_forever_isNotLessThan_any()
    {
        CacheSettings an_enabled_forever = createEnabledForever();

        assertFalse( an_enabled_forever.isTighterThan( createEnabledDefault( 500 ) ) );

        assertFalse( an_enabled_forever.isTighterThan( createEnabledSpecified( 500 ) ) );

        assertFalse( an_enabled_forever.isTighterThan( createDisabled() ) );

    }

    private CacheSettings createDisabled()
    {
        return new CacheSettings( false, CacheSettings.TYPE_FOREVER, Integer.MAX_VALUE );
    }

    private CacheSettings createEnabledForever()
    {
        return new CacheSettings( true, CacheSettings.TYPE_FOREVER, Integer.MAX_VALUE );
    }

    private CacheSettings createEnabledDefault( int secondsToLive )
    {
        return new CacheSettings( true, CacheSettings.TYPE_DEFAULT, secondsToLive );
    }

    private CacheSettings createEnabledSpecified( int secondsToLive )
    {
        return new CacheSettings( true, CacheSettings.TYPE_SPECIFIED, secondsToLive );
    }
}
