/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.domain.EntityPageList;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.core.structure.portlet.PortletEntity;

public interface PortletDao
    extends EntityDao<PortletEntity>
{
    PortletEntity findByKey( int key );

    PortletEntity findBySiteKeyAndNameIgnoreCase( SiteKey siteKey, String name );

    List getResourceUsageCountStyle();

    List getResourceUsageCountBorder();

    List<PortletEntity> findByStyle( ResourceKey resourceKey );

    List<PortletEntity> findByBorder( ResourceKey resourceKey );

    void updateResourceStyleReference( ResourceKey oldResourceKey, ResourceKey newResourceKey );

    void updateResourceBorderReference( ResourceKey oldResourceKey, ResourceKey newResourceKey );

    void updateResourceStyleReferencePrefix( String oldPrefix, String newPrefix );

    void updateResourceBorderReferencePrefix( String oldPrefix, String newPrefix );

    List<PortletEntity> findAll();

    EntityPageList<PortletEntity> findAll( int index, int count );
}
