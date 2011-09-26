/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.resolver.locale;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 9/26/11
 * Time: 12:55 PM
 */
public class LocaleParserTest
{


    @Before
    public void setUp()
    {

    }

    @Test
    public void testLocalParsing()
    {
        Locale locale = LocaleParser.parseLocale( "no" );
        assertNotNull( locale );

        locale = LocaleParser.parseLocale( "no_NO" );
        assertNotNull( locale );

        locale = LocaleParser.parseLocale( "no_NO_NN" );
        assertNotNull( locale );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullLocalString()
    {
        LocaleParser.parseLocale( null );

    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidLocalString()
    {
        LocaleParser.parseLocale( "no_no_no_no" );

    }

}
