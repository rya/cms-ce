/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;
import java.util.Map;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentIndexEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;

public interface ContentIndexDao
    extends EntityDao<ContentIndexEntity>
{
    ContentIndexEntity findByKey( String key );

    int removeAll();

    int removeByContentKey( ContentKey contentKey );

    int removeByCategoryKey( CategoryKey categoryKey );

    int removeByContentTypeKey( ContentTypeKey contentTypeKey );

    int findCountByContentKey( ContentKey contentKey );

    List<Object[]> findIndexValues( String query );

    List<ContentKey> findContentKeysByQuery( String hqlQuery, Map<String, Object> parameters, boolean cacheable );

    List<ContentIndexEntity> findByContentKey( ContentKey contentKey );

    void remove( List<ContentIndexEntity> entities );
}
