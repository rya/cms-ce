/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import com.enonic.cms.core.preferences.PreferenceKey;
import com.enonic.cms.core.preferences.PreferenceSpecification;
import com.enonic.cms.core.preferences.PreferenceEntity;


public interface PreferenceDao
    extends EntityDao<PreferenceEntity>
{
    PreferenceEntity findByKey( PreferenceKey key );

    List<PreferenceEntity> findBy( PreferenceSpecification spec );

    void removeBy( PreferenceSpecification spec );
}
