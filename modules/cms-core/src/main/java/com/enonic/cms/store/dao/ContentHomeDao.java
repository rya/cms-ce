/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import com.enonic.cms.core.structure.menuitem.ContentHomeEntity;
import com.enonic.cms.core.structure.menuitem.ContentHomeKey;


public interface ContentHomeDao
    extends EntityDao<ContentHomeEntity>
{
    ContentHomeEntity findByKey( ContentHomeKey key );
}
