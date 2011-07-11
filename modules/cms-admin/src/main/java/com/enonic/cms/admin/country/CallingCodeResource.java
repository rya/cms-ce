package com.enonic.cms.admin.country;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.country.Country;
import com.enonic.cms.core.country.CountryService;

/**
 * Created by IntelliJ IDEA.
 * User: vfiodarau
 * Date: 7/8/11
 * Time: 5:07 PM
 * To change this template use File | Settings | File Templates.
 */

@Component
@Path("/admin/data/misc/callingcodes")
@Produces("application/json")
public class CallingCodeResource
{

    @Autowired
    public CountryService countryService;

    @GET
    @Path("list")
    public CallingCodesModel getAll()
    {
        List<CallingCodeModel> list = new ArrayList<CallingCodeModel>(  );
        for ( Country c : countryService.getCountries() ){
            list.add(CallingCodeModelHelper.toModel( c ));
        }
        CallingCodesModel codes = new CallingCodesModel();
        codes.setCodes( list );
        return codes;
    }
}
