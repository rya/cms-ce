/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import com.enonic.cms.core.content.RelatedContentEntity;
import com.enonic.cms.core.content.RelatedContentKey;
import org.springframework.stereotype.Repository;

@Repository
public class RelatedContentEntityDao
    extends AbstractBaseEntityDao<RelatedContentEntity>
    implements RelatedContentDao
{
    public RelatedContentEntity findByKey( RelatedContentKey key )
    {
        return get( RelatedContentEntity.class, key );
    }


}