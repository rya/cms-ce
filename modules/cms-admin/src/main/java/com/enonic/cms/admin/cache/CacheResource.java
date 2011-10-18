package com.enonic.cms.admin.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheManager;

import com.enonic.cms.portal.cache.SiteCachesService;

@Component
@Path("/admin/data/system/cache")
@Produces("application/json")
public final class CacheResource
{
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private SiteCachesService siteCachesService;

    @GET
    @Path("list")
    public CachesModel getAll( @Context HttpServletRequest request )
    {
        List<CacheFacade> list = new ArrayList<CacheFacade>();
        for ( String cacheName : cacheManager.getCacheNames() )
        {
            list.add( cacheManager.getCache( cacheName ) );
        }
        return CacheModelTranslator.toModel( list );
    }

    @GET
    @Path("info")
    public CacheModel getCache( @QueryParam("name") @DefaultValue("") final String name,
                                @Context HttpServletRequest request )
    {
        return CacheModelTranslator.toModel( cacheManager.getCache( name ) );
    }

    @POST
    @Path("clear")
    public Map<String, Object> clearCache( @FormParam("name") @DefaultValue("") final String name )
    {
        Map<String, Object> res = new HashMap<String, Object>();
        if ( name.length() > 0 ) {
            siteCachesService.clearCache( name );
            res.put( "msg", "Cache '" + name + "' has been cleared." );
        } else {
            res.put( "msg", "Incorrect cache name." );
        }
        res.put( "success", true );
        return  res;
    }

}
