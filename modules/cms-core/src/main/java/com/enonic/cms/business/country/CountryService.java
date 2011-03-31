/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.country;

import java.util.Collection;

import com.enonic.cms.domain.country.Country;
import com.enonic.cms.domain.country.CountryCode;

/**
 * Aug 4, 2009
 */
public interface CountryService
{
    Collection<Country> getCountries();

    Country getCountry( CountryCode countryCode );
}
