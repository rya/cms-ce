/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.country;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.jdom.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import com.enonic.cms.framework.util.JDOMUtil;

/**
 * This implements the country service. It load country codes from an xml file. It tries to find the first resource that exists and load
 * it.
 */
public final class CountryServiceImpl
        implements CountryService, InitializingBean
{
    private final static Logger LOG = LoggerFactory.getLogger( CountryServiceImpl.class );

    private final LinkedHashMap<CountryCode, Country> countriesMapByCode;

    private Resource resource;

    public CountryServiceImpl()
    {
        this.countriesMapByCode = new LinkedHashMap<CountryCode, Country>();
    }

    public Collection<Country> getCountries()
    {
        return this.countriesMapByCode.values();
    }

    public Country getCountry( CountryCode countryCode )
    {
        return this.countriesMapByCode.get( countryCode );
    }

    public void afterPropertiesSet()
            throws Exception
    {
        if ( this.resource == null )
        {
            throw new IllegalArgumentException( "No country code resource is set" );
        }

        for ( Country country : readCountries( this.resource ) )
        {
            this.countriesMapByCode.put( country.getCode(), country );
        }
    }

    private List<Country> readCountries( Resource resource )
            throws Exception
    {
        if ( !resource.exists() )
        {
            throw new IllegalArgumentException(
                    "Country code resource [" + resource.getDescription() + "] was not found" );
        }

        Document doc = JDOMUtil.parseDocument( resource.getInputStream() );
        List<Country> list = CountryXmlParser.parseCountriesXml( doc );
        LOG.info( "Loaded country codes from [" + resource.getDescription() + "]" );
        return list;
    }

    public void setResource( Resource resource )
    {
        this.resource = resource;
    }


}
