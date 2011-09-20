/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.locale;

import org.junit.Test;

import java.util.Locale;
import static org.junit.Assert.*;

public class LocaleServiceImplTest
{
    @Test
    public void getLocales()
    {
        final Locale[] locales = new LocaleServiceImpl().getLocales();
        assertNotNull( locales );
    }
}
