/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import com.enonic.cms.core.structure.page.PageEntity;


public class PageEntityDao
    extends AbstractBaseEntityDao<PageEntity>
    implements PageDao
{
    public PageEntity findByKey( int pageKey )
    {
        return get( PageEntity.class, pageKey );
    }

    public List<PageEntity> findByTemplateKeys( List<Integer> pageTemplateKeys )
    {
        return findByNamedQuery( PageEntity.class, "PageEntity.findByTemplateKeys", "keys", pageTemplateKeys );
    }
}
