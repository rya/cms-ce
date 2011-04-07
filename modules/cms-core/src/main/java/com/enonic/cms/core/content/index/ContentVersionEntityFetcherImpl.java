/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.enonic.cms.store.dao.ContentVersionDao;

import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.ContentVersionKey;
import com.enonic.cms.domain.content.index.ContentVersionEntityFetcher;

public final class ContentVersionEntityFetcherImpl
    implements ContentVersionEntityFetcher
{

    private final ContentVersionDao contentVersionDao;


    public ContentVersionEntityFetcherImpl( ContentVersionDao dao )
    {
        this.contentVersionDao = dao;
    }

    public Map<ContentVersionKey, ContentVersionEntity> fetch( List<ContentVersionKey> keys )
    {
        Map<ContentVersionKey, ContentVersionEntity> map = new LinkedHashMap<ContentVersionKey, ContentVersionEntity>();
        if ( keys != null && keys.size() > 0 )
        {

            // performance: fetching one and one will go faster when content is cached
            for ( ContentVersionKey key : keys )
            {
                map.put( key, contentVersionDao.findByKey( key ) );
            }
        }

        return map;
    }
}
