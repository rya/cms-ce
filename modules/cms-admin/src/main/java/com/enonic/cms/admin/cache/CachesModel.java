package com.enonic.cms.admin.cache;

import java.util.ArrayList;
import java.util.List;

public class CachesModel
{
    private int total;

    private List<CacheModel> caches;

    public CachesModel()
    {
        this.caches = new ArrayList<CacheModel>();
    }

    public int getTotal()
    {
        return total;
    }

    public void setTotal( int total )
    {
        this.total = total;
    }

    public List<CacheModel> getCaches()
    {
        return caches;
    }

    public void setCaches( List<CacheModel> caches )
    {
        this.caches = caches;
    }

    public void addCache( CacheModel cache )
    {
        this.caches.add( cache );
    }
}
