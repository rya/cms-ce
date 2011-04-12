/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.domain.EntityPageList;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;

public interface ContentTypeDao
    extends EntityDao<ContentTypeEntity>
{
    ContentTypeEntity findByKey( int key );

    ContentTypeEntity findByName( String name );

    List<ContentTypeEntity> getAll();

    List getResourceUsageCountCSS();

    List<ContentTypeEntity> findByCSS( ResourceKey resourceKey );

    void updateResourceCSSReference( ResourceKey oldResourceKey, ResourceKey newResourceKey );

    void updateResourceCSSReferencePrefix( String oldPrefix, String newPrefix );

    EntityPageList<ContentTypeEntity> findAll( int index, int count );
}
