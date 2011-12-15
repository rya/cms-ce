/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.client;

import java.io.IOException;

import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.api.client.model.CreateCategoryParams;
import com.enonic.cms.core.client.InternalClient;
import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.security.PortalSecurityHolder;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;

import static org.junit.Assert.*;

public class InternalClientImpl_CreateCategoryTest
    extends AbstractSpringTest
{
    @Autowired
    private InternalClient internalClient;

    private DomainFactory factory;

    @Autowired
    private DomainFixture fixture;

    @Before
    public void before()
        throws IOException, JDOMException
    {
        factory = fixture.getFactory();

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

        PortalSecurityHolder.setAnonUser( fixture.findUserByName( User.ANONYMOUS_UID ).getKey() );
        PortalSecurityHolder.setLoggedInUser( fixture.findUserByName( "testuser" ).getKey() );
        PortalSecurityHolder.setImpersonatedUser( fixture.findUserByName( "testuser" ).getKey() );

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
        params.autoApprove = true;

        // exercise
        int categoryKey = internalClient.createCategory( params );
        fixture.flushAndClearHibernateSesssion();

        // verify
        CategoryEntity createdCategory = fixture.findCategoryByKey( new CategoryKey( categoryKey ) );
        assertEquals( "Auto approve true", createdCategory.getName() );
        assertEquals( true, createdCategory.getAutoMakeAvailableAsBoolean() );
    }

    @Test
    public void create_category_then_create_sub_category()
    {
        // exercise
        CreateCategoryParams params = new CreateCategoryParams();
        params.parentCategoryKey = fixture.findCategoryByName( "My top category" ).getKey().toInt();
        params.name = "My category";
        params.contentTypeKey = null;
        internalClient.createCategory( params );

        params.parentCategoryKey = fixture.findCategoryByName( "My category" ).getKey().toInt();
        params.name = "My sub category";
        params.contentTypeKey = null;
        internalClient.createCategory( params );

        // verify
        assertNotNull( fixture.findCategoryByName( "My sub category" ) );
        assertEquals( "My category", fixture.findCategoryByName( "My sub category" ).getParent().getName() );
        assertNotNull( fixture.findCategoryByName( "My category" ) );
        assertEquals( "My category", fixture.findCategoryByName( "My category" ).getName() );
        assertEquals( 1, fixture.findCategoryByName( "My top category" ).getChildren().size() );
        assertEquals( 1, fixture.findCategoryByName( "My category" ).getChildren().size() );
    }


}