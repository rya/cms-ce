/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.country;

import java.util.Collection;

import org.jdom.Document;
import org.jdom.Element;

/**
 * Aug 4, 2009
 */
public class CountryXmlCreator
{
    private boolean includeRegionsInfo = true;

    public Document createCountriesDocument( final Collection<Country> countries )
    {
        if ( countries == null )
        {
            throw new IllegalArgumentException( "countries cannot be null" );
        }

        Element countriesEl = new Element( "countries" );
        for ( Country country : countries )
        {
            countriesEl.addContent( doCreateCountryElement( country ) );
        }
        return new Document( countriesEl );
    }

    public Document createCountryDocument( final Country country )
    {
        if ( country == null )
        {
            throw new IllegalArgumentException( "country cannot be null" );
        }

        Element countriesEl = new Element( "countries" );
        countriesEl.addContent( doCreateCountryElement( country ) );
        return new Document( countriesEl );
    }

    private Element doCreateCountryElement( final Country country )
    {
        Element countryEl = new Element( "country" );
        countryEl.setAttribute( "code", country.getCode().toString() );
        countryEl.addContent( new Element( "english-name" ).setText( asEmptyIfNull( country.getEnglishName() ) ) );
        countryEl.addContent( new Element( "local-name" ).setText( asEmptyIfNull( country.getLocalName() ) ) );
        countryEl.addContent( new Element( "calling-code" ).setText( asEmptyIfNull( country.getCallingCode() ) ) );
        if ( includeRegionsInfo && country.hasRegions() )
        {
            countryEl.addContent( doCreateRegionsElement( country ) );
        }
        return countryEl;
    }

    private Element doCreateRegionsElement( final Country country )
    {
        Element regionsEl = new Element( "regions" );
        regionsEl.addContent( new Element( "english-name" ).setText( asEmptyIfNull( country.getRegionsEnglishName() ) ) );
        regionsEl.addContent( new Element( "local-name" ).setText( asEmptyIfNull( country.getRegionsLocalName() ) ) );

        for ( Region region : country.getRegions() )
        {
            regionsEl.addContent( doCreateRegionElement( region ) );
        }

        return regionsEl;
    }

    private Element doCreateRegionElement( final Region region )
    {
        Element regionEl = new Element( "region" );
        regionEl.setAttribute( "code", region.getCode() );
        regionEl.addContent( new Element( "english-name" ).setText( asEmptyIfNull( region.getEnglishName() ) ) );
        regionEl.addContent( new Element( "local-name" ).setText( asEmptyIfNull( region.getLocalName() ) ) );
        return regionEl;
    }


    private String asEmptyIfNull( final String value )
    {
        return value == null ? "" : value;
    }

    public void setIncludeRegionsInfo( boolean includeRegionsInfo )
    {
        this.includeRegionsInfo = includeRegionsInfo;
    }
}
