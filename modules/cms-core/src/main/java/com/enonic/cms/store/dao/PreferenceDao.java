/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import com.enonic.cms.domain.preference.PreferenceEntity;
import com.enonic.cms.domain.preference.PreferenceKey;
import com.enonic.cms.domain.preference.PreferenceSpecification;


public interface PreferenceDao
    extends EntityDao<PreferenceEntity>
{
    PreferenceEntity findByKey( PreferenceKey key );

    List<PreferenceEntity> findBy( PreferenceSpecification spec );

    void removeBy( PreferenceSpecification spec );
}
