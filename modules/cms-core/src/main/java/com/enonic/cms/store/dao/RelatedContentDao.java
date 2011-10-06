/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import com.enonic.cms.core.content.RelatedContentEntity;
import com.enonic.cms.core.content.RelatedContentKey;


public interface RelatedContentDao
    extends EntityDao<RelatedContentEntity>
{
    RelatedContentEntity findByKey( RelatedContentKey key );

}