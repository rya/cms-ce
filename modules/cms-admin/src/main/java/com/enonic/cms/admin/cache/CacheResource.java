package com.enonic.cms.admin.cache;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.core.InjectParam;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheManager;

import com.enonic.cms.admin.common.LoadStoreRequest;
import com.enonic.cms.core.timezone.TimeZoneService;
import com.enonic.cms.portal.cache.BaseCacheService;
import com.enonic.cms.portal.cache.PageCacheService;
import com.enonic.cms.portal.cache.SiteCachesService;

import com.enonic.cms.domain.CachedObject;
import com.enonic.cms.domain.SiteKey;

@Component
@Path("/admin/data/system")
@Produces("application/json")
public final class CacheResource
{
    @Autowired
    private CacheManager cacheManager;

    @GET
    @Path("caches")
    public CachesModel getAll(@InjectParam final LoadStoreRequest req)
    {
        List<CacheFacade> list = new ArrayList<CacheFacade>();
        for ( String cacheName : cacheManager.getCacheNames() )
        {
            list.add( cacheManager.getCache( cacheName ) );
        }
        return CacheModelTranslator.toModel( list );
    }

    @GET
    @Path("cache")
    public CacheModel getCache( @QueryParam("name") @DefaultValue("") final String name,
                                @InjectParam final LoadStoreRequest req)
    {
        return CacheModelTranslator.toModel( cacheManager.getCache( name ) );
    }

}
