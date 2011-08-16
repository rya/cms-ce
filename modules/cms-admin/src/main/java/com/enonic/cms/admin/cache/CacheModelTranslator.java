package com.enonic.cms.admin.cache;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import com.enonic.cms.framework.cache.CacheFacade;

public final class CacheModelTranslator
{
    public static CacheModel toModel( final CacheFacade entity )
    {
        final CacheModel model = new CacheModel();
        if ( entity != null )
        {
            model.setName( entity.getName() );
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

    public static CachesModel toModel( final Collection<CacheFacade> list )
    {
        final CachesModel model = new CachesModel();
        model.setTotal( list.size() );

        for ( final CacheFacade entity : list )
        {
            model.addCache( toModel( entity ) );
        }

        return model;
    }
}
