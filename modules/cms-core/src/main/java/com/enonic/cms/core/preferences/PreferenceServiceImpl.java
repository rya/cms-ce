/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.preferences;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.store.dao.PreferenceDao;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;


public class PreferenceServiceImpl
    implements PreferenceService
{

    @Autowired
    private UserDao userDao;

    @Autowired
    private PreferenceDao preferenceDao;

    private PreferenceAccessResolver preferenceAccessResolver = new PreferenceAccessResolver();

    /**
     * @inheritDoc
     */
    public PreferenceEntity getPreference( PreferenceKey key )
    {

        UserEntity user = checkUserExist( key.getUserKey() );

        if ( !preferenceAccessResolver.hasReadAccess( user ) )
        {
            throw new PreferenceAccessException( "User: '" + user.getName() + "' not allowed to read preference: " );
        }

        return preferenceDao.findByKey( key );
    }

    public List<PreferenceEntity> getPreferences( PreferenceSpecification spec )
    {

        return preferenceDao.findBy( spec );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PreferenceEntity setPreference( PreferenceEntity preference )
    {

        PreferenceKey preferenceKey = preference.getKey();
        UserEntity user = checkUserExist( preferenceKey.getUserKey() );

        if ( !preferenceAccessResolver.hasWriteAccess( user ) )
        {
            throw new PreferenceAccessException( "User '" + user.getName() + "' not allowed to write preference: " );
        }

        preferenceDao.store( preference );
        return preference;
    }

    /**
     * Removes the preference entry from data source.
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void removePreference( PreferenceEntity preference )
    {
        preferenceDao.delete( preference );
    }

    private UserEntity checkUserExist( UserKey userKey )
    {
        UserEntity user = userDao.findByKey( userKey.toString() );
        if ( user == null )
        {
            throw new IllegalArgumentException( "User does not exist: " + userKey );
        }
        return user;
    }

}
