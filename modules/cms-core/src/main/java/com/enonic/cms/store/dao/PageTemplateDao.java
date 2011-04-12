/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.Collection;
import java.util.List;

import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateType;
import com.enonic.cms.domain.EntityPageList;

public interface PageTemplateDao
    extends EntityDao<PageTemplateEntity>
{
    PageTemplateEntity findByKey( int pageTemplateKey );

    /**
     * Find first by content type.
     */
    PageTemplateEntity findFirstByContentType( int siteKey, int contentType, PageTemplateType pageTemplateType );

    /**
     * Find first by content type.
     */
    PageTemplateEntity findFirstByContentType( int siteKey, int contentType, PageTemplateType[] pageTemplateTypes );

    List getResourceUsageCountStyle();

    List getResourceUsageCountCSS();

    List<PageTemplateEntity> findByStyle( ResourceKey resourceKey );

    List<PageTemplateEntity> findByCSS( ResourceKey resourceKey );

    Collection<PageTemplateEntity> findByContentObjectKeys( List<Integer> contentObjectKeys );

    List<PageTemplateEntity> findByTypes( List<PageTemplateType> types );

    List<PageTemplateEntity> findBySiteKey( int key );

    void updateResourceStyleReference( ResourceKey oldResourceKey, ResourceKey newResourceKey );

    void updateResourceCSSReference( ResourceKey oldResourceKey, ResourceKey newResourceKey );

    void updateResourceStyleReferencePrefix( String oldPrefix, String newPrefix );

    void updateResourceCSSReferencePrefix( String oldPrefix, String newPrefix );

    List<PageTemplateEntity> findAll();

    EntityPageList<PageTemplateEntity> findAll( int index, int count );
}
