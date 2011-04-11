/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.locale;

import java.util.ArrayList;
import java.util.Locale;

import org.springframework.beans.factory.InitializingBean;

public class LocaleServiceImpl
    implements LocaleService, InitializingBean
{
    private final ArrayList<Locale> locales = new ArrayList<Locale>();

    public Locale[] getLocales()
    {
        return this.locales.toArray( new Locale[this.locales.size()] );
    }

    public void afterPropertiesSet()
        throws Exception
    {
        for ( Locale locale : Locale.getAvailableLocales() )
        {
            String country = locale.getCountry();
            if ( country != null && !country.equals( "" ) )
            {
                this.locales.add( locale );
            }
        }
    }
}
