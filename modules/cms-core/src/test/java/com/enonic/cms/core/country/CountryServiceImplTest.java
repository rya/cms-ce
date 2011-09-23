/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.country;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class CountryServiceImplTest
{
    private CountryServiceImpl service;

    @Before
    public void setUp()
        throws Exception
    {
        this.service = new CountryServiceImpl();
        this.service.setResource(
            new ClassPathResource( "com/enonic/cms/core/country/test-countries.xml", getClass().getClassLoader() ) );
        this.service.afterPropertiesSet();
    }

    @Test
    public void testGetCountry()
    {
        Assert.assertNull( this.service.getCountry( new CountryCode( "NO" ) ) );

        Country country = this.service.getCountry( new CountryCode( "BB" ) );
        Assert.assertNotNull( country );
        Assert.assertEquals( "BB", country.getCode().toString() );
        Assert.assertEquals( "BARBADOS", country.getEnglishName() );
    }

    @Test
    public void testGetCountries()
    {
        Collection<Country> countries = this.service.getCountries();
        Assert.assertEquals( 3, countries.size() );

        Iterator<Country> it = countries.iterator();
        Assert.assertEquals( "BB", it.next().getCode().toString() );
        Assert.assertEquals( "GY", it.next().getCode().toString() );
        Assert.assertEquals( "UG", it.next().getCode().toString() );
        Assert.assertFalse( it.hasNext() );
    }

}
