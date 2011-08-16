package com.enonic.cms.admin.cache;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheManager;

@Component
@Path("/admin/data/system/cache")
@Produces("application/json")
public final class CacheResource
{
    @Autowired
    private CacheManager cacheManager;

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

}
