/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import com.enonic.cms.core.content.ContentAccessEntity;
import com.enonic.cms.domain.security.group.GroupKey;
import org.springframework.stereotype.Repository;

@Repository("contentAccessDao")
public final class ContentAccessEntityDao
    extends AbstractBaseEntityDao<ContentAccessEntity>
    implements ContentAccessDao
{
    public void deleteByGroupKey( GroupKey groupKey )
    {
        deleteByNamedQuery( "ContentAccessEntity.deleteByGroupKey", "groupKey", groupKey );
    }
}
