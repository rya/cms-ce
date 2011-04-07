/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.country;

import java.util.Collection;

/**
 * Aug 4, 2009
 */
public interface CountryService
{
    Collection<Country> getCountries();

    Country getCountry( CountryCode countryCode );
}
