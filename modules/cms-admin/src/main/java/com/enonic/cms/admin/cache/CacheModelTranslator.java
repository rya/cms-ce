package com.enonic.cms.admin.cache;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import com.enonic.cms.framework.cache.CacheFacade;

public final class CacheModelTranslator
{
    public static CacheModel toModel( final CacheFacade entity, HttpServletRequest req )
    {
        String node = req.getServerName() + ": " + req.getServerPort();
        final CacheModel model = new CacheModel();
        if ( entity != null )
        {
            model.setName( entity.getName() );
            model.setImplementationName( node );
            model.setMemoryCapacity( entity.getMemoryCapacity() );
            model.setDiskCapacity( entity.getDiskCapacity() );
            model.setDiskOverflow( entity.getDiskOverflow() );
            model.setTimeToLive( entity.getTimeToLive() );
            model.setObjectCount( entity.getCount() );
            model.setCacheHits( entity.getHitCount() );
            model.setCacheMisses( entity.getMissCount() );
        }
        return model;
    }

    public static CachesModel toModel( final Collection<CacheFacade> list, HttpServletRequest req )
    {
        final CachesModel model = new CachesModel();
        model.setTotal( list.size() );

        for ( final CacheFacade entity : list )
        {
            model.addCache( toModel( entity, req ) );
        }

        return model;
    }
}
