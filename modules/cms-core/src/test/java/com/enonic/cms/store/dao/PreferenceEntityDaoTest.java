/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import com.enonic.cms.core.preferences.PreferenceEntity;
import com.enonic.cms.core.preferences.PreferenceKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class PreferenceEntityDaoTest
{
    @Autowired
    private PreferenceDao preferenceDao;

    @Before
    public void beforeTest()
        throws Exception
    {
        addTestData();
    }

    private void addTestData()
    {
        preferenceDao.store( createPreference( "user:tlund123.global.useHtmlMail", "true" ) );
        preferenceDao.store( createPreference( "user:ssandvik425.global.useHtmlMail",
                                               "true if logged in via Firefox, Safari; no if logged in via Internet Explorer;" ) );
        preferenceDao.store( createPreference( "user:007.global.smtpServer", "philipFry" ) );
        preferenceDao.store( createPreference( "user:007.global.ftpServer", "turanga" ) );
        preferenceDao.store( createPreference( "user:tlund123.site:0.language", "NO-no" ) );
        preferenceDao.store( createPreference( "user:tlund123.site:1.language", "EN-gb" ) );
        preferenceDao.store( createPreference( "user:tlund123.global.smtpServer", "zoidberg" ) );
        preferenceDao.store( createPreference( "user:tlund123.global.httpServer", "flexo" ) );
        preferenceDao.store( createPreference( "user:007.site:0.httpServer", "kwanzaa" ) );
    }


    /**
     * Simple test to find the standard values by key.
     */
    @Test
    public void testFindByKey()
    {
        preferenceDao.store( createPreference( "user:u1.global.useHtmlMail", "true" ) );
        preferenceDao.store( createPreference( "user:u2.global.useHtmlMail", "false" ) );
        preferenceDao.store( createPreference( "user:u3.global.useHtmlMail", "false" ) );

        assertNotNull( preferenceDao.findByKey( new PreferenceKey( "user:u1.global.useHtmlMail" ) ) );
        assertNotNull( preferenceDao.findByKey( new PreferenceKey( "user:u2.global.useHtmlMail" ) ) );
        assertNull( preferenceDao.findByKey( new PreferenceKey( "user:u4.global.useHtmlMail" ) ) );
    }

    /**
     * This test, stores a new object and changes the value of an old object, then fetches the objects back out to new variables and checks
     * that their values are correct.
     */
    @Test
    public void testStorePreference()
    {
        PreferenceEntity testObj1 = preferenceDao.findByKey( new PreferenceKey( "user:tlund123.global.useHtmlMail" ) );
        testObj1.setValue( "false" );
        PreferenceEntity testObj2 = createPreference( "user:045.site:0.language", "NO-no" );
        preferenceDao.store( testObj1 );
        preferenceDao.store( testObj2 );

        testObj1 = preferenceDao.findByKey( new PreferenceKey( "user:ssandvik425.global.useHtmlMail" ) );
        testObj2 = preferenceDao.findByKey( new PreferenceKey( "user:tlund123.global.useHtmlMail" ) );
        PreferenceEntity testObj3 = preferenceDao.findByKey( new PreferenceKey( "user:045.site:0.language" ) );
        assertEquals( testObj1.getValue(), "true if logged in via Firefox, Safari; no if logged in via Internet Explorer;" );
        assertEquals( testObj2.getValue(), "false" );
        assertEquals( testObj3.getValue(), "NO-no" );
    }

    @Test
    public void testFindByPrefix()
    {

        /*List<PreferenceEntity> result1 = dao.findByPrefix("user:tlund123");
        List<PreferenceEntity> result2 = dao.findByPrefix("user:007");
        List<PreferenceEntity> result3 = dao.findByPrefix("user:007.global");
        assertEquals(5, result1.size());
        assertEquals(3, result2.size());
        assertEquals(2, result3.size());*/
    }

    @Test
    public void testRemove()
    {

        PreferenceKey key = new PreferenceKey( "user:tlund123.site:0.language" );

        PreferenceEntity preference = preferenceDao.findByKey( key );
        assertNotNull( preference );

        preferenceDao.delete( preference );
        preference = preferenceDao.findByKey( key );
        assertNull( preference );

    }


    private PreferenceEntity createPreference( String keyStr, String value )
    {

        PreferenceKey key = new PreferenceKey( keyStr );
        PreferenceEntity pref = new PreferenceEntity();
        pref.setKey( key );
        pref.setValue( value );
        return pref;
    }

}
