/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import com.enonic.cms.core.structure.page.PageEntity;


public interface PageDao
    extends EntityDao<PageEntity>
{
    /**
     * Find page by key.
     */
    PageEntity findByKey( int pageKey );

    /**
     * Find by template.
     */
    List<PageEntity> findByTemplateKeys( List<Integer> pageTemplateKeys );
}
