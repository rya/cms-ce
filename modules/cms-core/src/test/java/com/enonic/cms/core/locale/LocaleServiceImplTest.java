/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.locale;

import java.util.Locale;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LocaleServiceImplTest
{
    private LocaleService localeService;

    @Before
    public void setUp()
    {
        this.localeService = new LocaleServiceImpl();
    }

    @Test
    public void getLocales()
    {
        Locale[] locales = localeService.getLocales();
        assertNotNull( locales );
    }
}
