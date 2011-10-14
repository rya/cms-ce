/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.country;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.jdom.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.enonic.cms.framework.util.JDOMUtil;
import org.springframework.stereotype.Component;

/**
 * This implements the country service. It load country codes from an xml file. It tries to find the first resource that exists and load
 * it.
 */
@Component("countryService")
public final class CountryServiceImpl
        implements CountryService, InitializingBean
{
    private final static Logger LOG = LoggerFactory.getLogger( CountryServiceImpl.class );

    private final LinkedHashMap<CountryCode, Country> countriesMapByCode;

    private File countryFile;

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
        final Resource res = findCountryResource();
        for ( final Country country : readCountries( res ) )
        {
            this.countriesMapByCode.put( country.getCode(), country );
        }
    }

    private Resource findCountryResource()
    {
        if ((this.countryFile != null) && this.countryFile.exists() && this.countryFile.isFile()) {
            return new FileSystemResource(this.countryFile);
        } else {
            return new ClassPathResource("com/enonic/cms/core/country/countries.xml");
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

    @Value("#{config.countriesFile}")
    public void setCountriesFile( final File file )
    {
        this.countryFile = file;
    }
}
