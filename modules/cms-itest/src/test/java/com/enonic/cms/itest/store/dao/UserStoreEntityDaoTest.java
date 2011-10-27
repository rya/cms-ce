/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.store.dao;

import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.core.security.userstore.config.UserStoreConfigParser;
import com.enonic.cms.core.security.userstore.config.UserStoreUserFieldConfig;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.store.dao.UserStoreDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserStoreEntityDaoTest
    extends AbstractSpringTest
{
    @Autowired
    private UserStoreDao userStoreDao;

    @Test
    public void testStoreUserStore()
    {
        final UserStoreEntity userStore = new UserStoreEntity();

        userStore.setDefaultStore( false );
        userStore.setDeleted( false );
        userStore.setName( "TestName" );
        userStore.setConnectorName( "TestConnectorName" );

        final String configAsString = "<config><user-fields><first-name required=\"true\"/></user-fields></config>";
        final XMLDocument configXmlDoc = XMLDocumentFactory.create( configAsString );
        final UserStoreConfig config = UserStoreConfigParser.parse( configXmlDoc.getAsJDOMDocument().getRootElement() );
        userStore.setConfig( config );

        userStoreDao.storeNew( userStore );

        final UserStoreKey userStoreKey = userStore.getKey();

        userStoreDao.getHibernateTemplate().flush();
        userStoreDao.getHibernateTemplate().clear();

        // Excercise

        final UserStoreEntity storedEntity = userStoreDao.findByKey( userStoreKey );

        // Verify
        assertEquals( userStore, storedEntity );
        assertEquals( storedEntity.getConnectorName(), userStore.getConnectorName() );
        assertEquals( storedEntity.getName(), userStore.getName() );

        final UserStoreConfig userStoreConfig = storedEntity.getConfig();
        assertNotNull( userStoreConfig );

        final Collection<UserStoreUserFieldConfig> userFieldConfigs = userStoreConfig.getUserFieldConfigs();
        assertNotNull( userFieldConfigs );
        assertEquals( 1, userFieldConfigs.size() );
    }
}