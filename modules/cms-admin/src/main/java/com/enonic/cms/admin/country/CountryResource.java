package com.enonic.cms.admin.country;

import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.core.InjectParam;

import com.enonic.cms.admin.common.LoadStoreRequest;
import com.enonic.cms.core.country.Country;
import com.enonic.cms.core.country.CountryCode;
import com.enonic.cms.core.country.CountryService;
import com.enonic.cms.core.country.Region;

@Component
@Path("/admin/data/misc")
@Produces("application/json")
public final class CountryResource
{

    @Autowired
    private CountryService countryService;

    @GET
    @Path("country/list")
    public CountriesModel getCountries( @QueryParam("query") final String query,
                                        @InjectParam final LoadStoreRequest req )
    {
        Collection<Country> list = this.countryService.getCountries();
        if ( query != null )
        {
            list = filterCountries( list, query );
        }
        return CountryModelHelper.toModel( list );
    }

    @GET
    @Path("country")
    public CountryModel getCountry( @QueryParam("countryCode") @DefaultValue("") final String countryCode,
                                    @InjectParam final LoadStoreRequest req )
    {
        Country country = this.countryService.getCountry( new CountryCode( countryCode ) );
        return CountryModelHelper.toModel( country );
    }

    @GET
    @Path("region/list")
    public RegionsModel getRegions( @QueryParam("query") final String query,
                                    @QueryParam("countryCode") @DefaultValue("") final String countryCode,
                                    @InjectParam final LoadStoreRequest req )
    {
        Country country = this.countryService.getCountry( new CountryCode( countryCode ) );
        RegionsModel model;
        if ( country != null )
        {
            Collection<Region> list = country.getRegions();
            if ( query != null )
            {
                list = filterRegions( list, query );
            }
            model = CountryModelHelper.toModel( list );
        } else {
            model = new RegionsModel();
        }
        return model;
    }

    @GET
    @Path("region")
    public RegionModel getRegion( @QueryParam("countryCode") @DefaultValue("") final String countryCode,
                                  @QueryParam("regionCode") @DefaultValue("") final String regionCode,
                                  @InjectParam final LoadStoreRequest req )
    {
        final Country country = this.countryService.getCountry( new CountryCode( countryCode ) );
        return country != null ? CountryModelHelper.toModel( country.getRegion( regionCode ) ) : new RegionModel();
    }


    private Collection<Country> filterCountries( Collection<Country> list, String query )
    {
        Collection<Country> result = new ArrayList<Country>();
        for ( Country country : list )
        {
            if ( country.getEnglishName().toLowerCase().contains( query.toLowerCase() ) )
            {
                result.add( country );
            }
        }
        return result;
    }

    private Collection<Region> filterRegions( Collection<Region> list, String query )
    {
        Collection<Region> result = new ArrayList<Region>();
        for ( Region region : list )
        {
            if ( region.getEnglishName().toLowerCase().contains( query.toLowerCase() ) )
            {
                result.add( region );
            }
        }
        return result;
    }

}
