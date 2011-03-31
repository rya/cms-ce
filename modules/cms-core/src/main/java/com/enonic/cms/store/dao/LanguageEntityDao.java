/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.domain.LanguageKey;

public class LanguageEntityDao
    extends AbstractBaseEntityDao<LanguageEntity>
    implements LanguageDao
{
    public List<LanguageEntity> findAll()
    {
        return findByNamedQuery( LanguageEntity.class, "LanguageEntity.findAll" );
    }

    public LanguageEntity findByKey( LanguageKey key )
    {
        return get( LanguageEntity.class, key );
    }

    public LanguageEntity findByCode( String code )
    {
        return findSingleByNamedQuery( LanguageEntity.class, "LanguageEntity.findByCode", "code", code );
    }
}
