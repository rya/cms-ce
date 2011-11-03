/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import com.enonic.cms.core.language.LanguageEntity;
import com.enonic.cms.core.language.LanguageKey;


public interface LanguageDao
    extends EntityDao<LanguageEntity>
{
    List<LanguageEntity> findAll();

    LanguageEntity findByKey( LanguageKey key );

    LanguageEntity findByCode( String code );
}
