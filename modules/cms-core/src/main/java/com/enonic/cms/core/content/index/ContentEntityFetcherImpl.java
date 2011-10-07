/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.store.dao.ContentDao;

public final class ContentEntityFetcherImpl
    implements ContentEntityFetcher
{

    private final ContentDao contentDao;


    public ContentEntityFetcherImpl( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    public Map<ContentKey, ContentEntity> fetch( List<ContentKey> keys )
    {
        Map<ContentKey, ContentEntity> map = new LinkedHashMap<ContentKey, ContentEntity>();
        if ( keys != null && keys.size() > 0 )
        {

            // performance: fetching one and one will go faster when content is cached
            for ( ContentKey key : keys )
            {
                map.put( key, contentDao.findByKey( key ) );
            }
        }

        return map;
    }
}
