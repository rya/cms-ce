/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.client;

import java.io.IOException;

import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.api.client.model.CreateCategoryParams;
import com.enonic.cms.core.client.InternalClient;
import com.enonic.cms.core.security.SecurityHolder;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.testtools.DomainFactory;
import com.enonic.cms.testtools.DomainFixture;

import com.enonic.cms.domain.content.ContentHandlerName;
import com.enonic.cms.domain.content.category.CategoryEntity;
import com.enonic.cms.domain.content.category.CategoryKey;
import com.enonic.cms.domain.security.user.User;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration()
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class InternalClientImpl_CreateCategoryTest
{
    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private InternalClient internalClient;

    private DomainFactory factory;

    private DomainFixture fixture;

    @Before
    public void before()
        throws IOException, JDOMException
    {
        fixture = new DomainFixture( hibernateTemplate );
        factory = new DomainFactory( fixture );

        fixture.initSystemData();

        fixture.createAndStoreNormalUserWithUserGroup( "testuser", "Test user", "testuserstore" );

        fixture.save( factory.createContentHandler( "MyHandler", "com.enonic.vertical.adminweb.handlers.SimpleContentHandlerServlet" ) );
        fixture.save( factory.createContentType( "MyContentType", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );

        fixture.save( factory.createUnit( "My unit" ) );
        fixture.save( factory.createCategory( "My top category", null, "My unit", "testuser", "testuser" ) );
        fixture.save( factory.createCategoryAccessForUser( "My top category", "testuser", "read,administrate" ) );

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );

        SecurityHolder.setAnonUser( fixture.findUserByName( User.ANONYMOUS_UID ).getKey() );
        SecurityHolder.setUser( fixture.findUserByName( "testuser" ).getKey() );
        SecurityHolder.setRunAsUser( fixture.findUserByName( "testuser" ).getKey() );

    }

    @Test
    public void createCategoryWithContentTypeAsKey()
    {
        // setup
        CategoryEntity myTopCategory = fixture.findCategoryByName( "My top category" );
        CreateCategoryParams params = new CreateCategoryParams();
        params.parentCategoryKey = myTopCategory.getKey().toInt();
        params.name = "My category";
        params.contentTypeKey = fixture.findContentTypeByName( "MyContentType" ).getKey();

        // exercise
        int categoryKey = internalClient.createCategory( params );
        fixture.flushAndClearHibernateSesssion();

        // verify
        CategoryEntity createdCategory = fixture.findCategoryByKey( new CategoryKey( categoryKey ) );
        assertEquals( "My category", createdCategory.getName() );
        assertEquals( false, createdCategory.getAutoMakeAvailableAsBoolean() );
        assertEquals( myTopCategory, createdCategory.getParent() );
        assertEquals( "MyContentType", createdCategory.getContentType().getName() );
    }

    @Test
    public void createCategoryWithContentTypeAsName()
    {
        // setup
        CategoryEntity myTopCategory = fixture.findCategoryByName( "My top category" );
        CreateCategoryParams params = new CreateCategoryParams();
        params.parentCategoryKey = myTopCategory.getKey().toInt();
        params.name = "My category";
        params.contentTypeName = "MyContentType";

        // exercise
        int categoryKey = internalClient.createCategory( params );
        fixture.flushAndClearHibernateSesssion();

        // verify
        CategoryEntity createdCategory = fixture.findCategoryByKey( new CategoryKey( categoryKey ) );
        assertEquals( "My category", createdCategory.getName() );
        assertEquals( false, createdCategory.getAutoMakeAvailableAsBoolean() );
        assertEquals( myTopCategory, createdCategory.getParent() );
        assertEquals( "MyContentType", createdCategory.getContentType().getName() );
    }


    @Test
    public void createCategoryWithNoContentType()
    {
        // setup
        CategoryEntity myTopCategory = fixture.findCategoryByName( "My top category" );
        CreateCategoryParams params = new CreateCategoryParams();
        params.parentCategoryKey = myTopCategory.getKey().toInt();
        params.name = "My category";
        params.contentTypeKey = null;

        // exercise
        int categoryKey = internalClient.createCategory( params );
        fixture.flushAndClearHibernateSesssion();

        // verify
        CategoryEntity createdCategory = fixture.findCategoryByKey( new CategoryKey( categoryKey ) );
        assertEquals( "My category", createdCategory.getName() );
        assertEquals( false, createdCategory.getAutoMakeAvailableAsBoolean() );
        assertEquals( myTopCategory, createdCategory.getParent() );
        assertNull( createdCategory.getContentType() );
    }


    @Test
    public void createCategorySettingAutoApprovedToTrue()
    {
        // setup
        CategoryEntity myTopCategory = fixture.findCategoryByName( "My top category" );
        CreateCategoryParams params = new CreateCategoryParams();
        params.parentCategoryKey = myTopCategory.getKey().toInt();
        params.name = "Auto approve true";
        params.contentTypeKey = fixture.findContentTypeByName( "MyContentType" ).getKey();
        ;
        params.autoApprove = true;

        // exercise
        int categoryKey = internalClient.createCategory( params );
        fixture.flushAndClearHibernateSesssion();

        // verify
        CategoryEntity createdCategory = fixture.findCategoryByKey( new CategoryKey( categoryKey ) );
        assertEquals( "Auto approve true", createdCategory.getName() );
        assertEquals( true, createdCategory.getAutoMakeAvailableAsBoolean() );
    }


}