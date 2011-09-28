/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import com.enonic.cms.domain.structure.menuitem.ContentHomeEntity;
import com.enonic.cms.domain.structure.menuitem.ContentHomeKey;
import org.springframework.stereotype.Repository;

@Repository("contentHomeDao")
public final class ContentHomeEntityDao
    extends AbstractBaseEntityDao<ContentHomeEntity>
    implements ContentHomeDao
{

    public ContentHomeEntity findByKey( ContentHomeKey key )
    {
        return get( ContentHomeEntity.class, key );
    }
}
