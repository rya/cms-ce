package com.enonic.cms.itest.client;


import java.io.IOException;

import org.jdom.Document;
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

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.api.client.ClientException;
import com.enonic.cms.api.client.model.CreateCategoryParams;
import com.enonic.cms.api.client.model.CreateFileContentParams;
import com.enonic.cms.api.client.model.DeleteCategoryParams;
import com.enonic.cms.api.client.model.content.ContentStatus;
import com.enonic.cms.api.client.model.content.file.FileBinaryInput;
import com.enonic.cms.api.client.model.content.file.FileContentDataInput;
import com.enonic.cms.api.client.model.content.file.FileDescriptionInput;
import com.enonic.cms.api.client.model.content.file.FileKeywordsInput;
import com.enonic.cms.api.client.model.content.file.FileNameInput;
import com.enonic.cms.core.client.InternalClient;
import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.UnitEntity;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.security.SecurityHolder;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.itest.DomainFactory;
import com.enonic.cms.itest.DomainFixture;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration()
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class InternalClientImpl_DeleteCategoryTest
{
    @Autowired
    private InternalClient internalClient;

    private Document contentTypeConfig;

    @Autowired
    private HibernateTemplate hibernateTemplate;

    private DomainFactory factory;

    private DomainFixture fixture;

    private byte[] dummyBinary = new byte[]{1, 2, 3};

    @Before
    public void before()
        throws IOException, JDOMException
    {
        fixture = new DomainFixture( hibernateTemplate );
        factory = new DomainFactory( fixture );

        fixture.initSystemData();

        fixture.createAndStoreNormalUserWithUserGroup( "deleter", "Test user", "testuserstore" );
        fixture.createAndStoreNormalUserWithUserGroup( "deleter-noaccess", "deleter-noaccess", "testuserstore" );

        //prepare data for content creation
        StringBuffer contentTypeConfigXml = new StringBuffer();
        contentTypeConfigXml.append( "<moduledata/>" );
        contentTypeConfig = XMLDocumentFactory.create( contentTypeConfigXml.toString() );
        hibernateTemplate.flush();

        fixture.save( factory.createContentHandler( "MyHandler", ContentHandlerName.FILE.getHandlerClassShortName() ) );
        fixture.save( factory.createContentType( "MyContentType", ContentHandlerName.FILE.getHandlerClassShortName(), contentTypeConfig ) );
        fixture.save(
            factory.createContentType( "MyOtherContentType", ContentHandlerName.FILE.getHandlerClassShortName(), contentTypeConfig ) );

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );

        loginPortalUser( "deleter" );

        fixture.flushAndClearHibernateSesssion();
    }

    @Test
    public void deleteCategory_that_is_top_category()
    {
        // prepare data
        UnitEntity myUnit = factory.createUnit( "MyUnit" );
        myUnit.addContentType( fixture.findContentTypeByName( "MyContentType" ) );
        myUnit.addContentType( fixture.findContentTypeByName( "MyOtherContentType" ) );
        fixture.save( myUnit );
        fixture.save( factory.createCategory( "World", null, "MyUnit", "deleter", "deleter" ) );
        fixture.save( factory.createCategoryAccessForUser( "World", "deleter", "read,administrate" ) );

        fixture.flushAndClearHibernateSesssion();

        // verify setup
        assertEquals( false, fixture.findUnitByName( "MyUnit" ).isDeleted() );
        assertEquals( 2, fixture.findUnitByName( "MyUnit" ).getContentTypes().size() );
        assertEquals( false, fixture.findCategoryByName( "World" ).isDeleted() );

        fixture.flushAndClearHibernateSesssion();

        // delete
        DeleteCategoryParams params = new DeleteCategoryParams();
        params.key = fixture.findCategoryByName( "World" ).getKey().toInt();
        params.includeContent = false;
        params.recursive = false;
        internalClient.deleteCategory( params );

        fixture.flushAndClearHibernateSesssion();

        assertTrue( fixture.findContentTypeByName( "MyContentType" ) != null );
        assertTrue( fixture.findContentTypeByName( "MyOtherContentType" ) != null );

        assertEquals( true, fixture.findCategoryByName( "World" ).isDeleted() );
        assertEquals( true, fixture.findCategoryByName( "World" ).getUnit().isDeleted() );
        assertEquals( true, fixture.findCategoryByName( "World" ).getUnit().getContentTypes().isEmpty() );
        assertEquals( true, fixture.findUnitByName( "MyUnit" ).isDeleted() );
        assertEquals( true, fixture.findUnitByName( "MyUnit" ).getContentTypes().isEmpty() );
    }

    @Test
    public void deleteCategory_that_is_top_category_and_has_content()
    {
        // prepare data
        UnitEntity myUnit = factory.createUnit( "MyUnit" );
        myUnit.addContentType( fixture.findContentTypeByName( "MyContentType" ) );
        myUnit.addContentType( fixture.findContentTypeByName( "MyOtherContentType" ) );
        fixture.save( myUnit );
        fixture.save( factory.createCategory( "World", "MyContentType", "MyUnit", "deleter", "deleter" ) );
        fixture.save( factory.createCategoryAccessForUser( "World", "deleter", "read,administrate" ) );

        createAndStoreContent( "content1", "World" );

        fixture.flushAndClearHibernateSesssion();

        // verify setup
        assertEquals( false, fixture.findUnitByName( "MyUnit" ).isDeleted() );
        assertEquals( 2, fixture.findUnitByName( "MyUnit" ).getContentTypes().size() );
        assertEquals( false, fixture.findCategoryByName( "World" ).isDeleted() );
        assertEquals( 1, fixture.countAllContent() );

        fixture.flushAndClearHibernateSesssion();

        // delete
        DeleteCategoryParams params = new DeleteCategoryParams();
        params.key = fixture.findCategoryByName( "World" ).getKey().toInt();
        params.includeContent = true;
        params.recursive = false;
        internalClient.deleteCategory( params );

        fixture.flushAndClearHibernateSesssion();

        assertTrue( fixture.findContentTypeByName( "MyContentType" ) != null );
        assertTrue( fixture.findContentTypeByName( "MyOtherContentType" ) != null );

        assertEquals( true, fixture.findCategoryByName( "World" ).isDeleted() );
        assertEquals( true, fixture.findCategoryByName( "World" ).getUnit().isDeleted() );
        assertEquals( true, fixture.findCategoryByName( "World" ).getUnit().getContentTypes().isEmpty() );
        assertEquals( true, fixture.findUnitByName( "MyUnit" ).isDeleted() );
        assertEquals( true, fixture.findUnitByName( "MyUnit" ).getContentTypes().isEmpty() );

        assertEquals( true, fixture.findContentByName( "content1" ).isDeleted() );
    }

    @Test
    public void deleteCategory_that_is_top_category_and_has_sub_categories()
    {
        // prepare data
        UnitEntity myUnit = factory.createUnit( "MyUnit" );
        myUnit.addContentType( fixture.findContentTypeByName( "MyContentType" ) );
        myUnit.addContentType( fixture.findContentTypeByName( "MyOtherContentType" ) );
        fixture.save( myUnit );
        fixture.save( factory.createCategory( "World", "MyContentType", "MyUnit", "deleter", "deleter" ) );
        fixture.save( factory.createCategoryAccessForUser( "World", "deleter", "read,administrate" ) );

        createAndStoreCategory( "Europe", "World" );
        createAndStoreCategory( "Belarus", "Europe" );

        fixture.flushAndClearHibernateSesssion();

        // verify setup
        assertEquals( false, fixture.findUnitByName( "MyUnit" ).isDeleted() );
        assertEquals( 2, fixture.findUnitByName( "MyUnit" ).getContentTypes().size() );
        assertEquals( false, fixture.findCategoryByName( "World" ).isDeleted() );
        assertEquals( false, fixture.findCategoryByName( "Europe" ).isDeleted() );
        assertEquals( false, fixture.findCategoryByName( "Belarus" ).isDeleted() );
        assertEquals( 0, fixture.countAllContent() );

        fixture.flushAndClearHibernateSesssion();

        // delete
        DeleteCategoryParams params = new DeleteCategoryParams();
        params.key = fixture.findCategoryByName( "World" ).getKey().toInt();
        params.includeContent = false;
        params.recursive = true;
        internalClient.deleteCategory( params );

        fixture.flushAndClearHibernateSesssion();

        assertTrue( fixture.findContentTypeByName( "MyContentType" ) != null );
        assertTrue( fixture.findContentTypeByName( "MyOtherContentType" ) != null );

        assertEquals( true, fixture.findCategoryByName( "Belarus" ).isDeleted() );
        assertEquals( true, fixture.findCategoryByName( "Europe" ).isDeleted() );
        assertEquals( true, fixture.findCategoryByName( "World" ).isDeleted() );
        assertEquals( true, fixture.findUnitByName( "MyUnit" ).isDeleted() );
        assertEquals( true, fixture.findUnitByName( "MyUnit" ).getContentTypes().isEmpty() );

    }

    @Test
    public void deleteCategory_that_is_category_without_content_and_sub_categories()
    {
        // prepare data
        fixture.save( factory.createUnit( "MyUnit" ) );
        fixture.save( factory.createCategory( "World", null, "MyUnit", "deleter", "deleter" ) );
        fixture.save( factory.createCategoryAccessForUser( "World", "deleter", "read,administrate" ) );

        createAndStoreCategory( "Europe", "World" );

        fixture.flushAndClearHibernateSesssion();

        // verify setup
        assertEquals( false, fixture.findCategoryByName( "Europe" ).isDeleted() );

        fixture.flushAndClearHibernateSesssion();

        // delete
        DeleteCategoryParams params = new DeleteCategoryParams();
        params.key = fixture.findCategoryByName( "Europe" ).getKey().toInt();
        params.includeContent = false;
        params.recursive = false;
        internalClient.deleteCategory( params );

        fixture.flushAndClearHibernateSesssion();

        assertEquals( true, fixture.findCategoryByName( "Europe" ).isDeleted() );
        assertEquals( false, fixture.findUnitByName( "MyUnit" ).isDeleted() );
    }

    @Test
    public void deleteCategory_that_is_category_with_sub_category()
    {
        // prepare data
        fixture.save( factory.createUnit( "MyUnit" ) );
        fixture.save( factory.createCategory( "World", null, "MyUnit", "deleter", "deleter" ) );
        fixture.save( factory.createCategoryAccessForUser( "World", "deleter", "read,administrate" ) );

        createAndStoreCategory( "Europe", "World" );

        fixture.flushAndClearHibernateSesssion();

        // verify setup
        assertEquals( false, fixture.findCategoryByName( "Europe" ).isDeleted() );

        fixture.flushAndClearHibernateSesssion();

        // delete
        DeleteCategoryParams params = new DeleteCategoryParams();
        params.key = fixture.findCategoryByName( "Europe" ).getKey().toInt();
        params.includeContent = false;
        params.recursive = true;
        internalClient.deleteCategory( params );

        fixture.flushAndClearHibernateSesssion();

        assertEquals( 0, fixture.findCategoryByName( "World" ).getChildren().size() );
        assertEquals( true, fixture.findCategoryByName( "Europe" ).isDeleted() );
        assertEquals( 0, fixture.findCategoryByName( "Europe" ).getChildren().size() );

        assertEquals( false, fixture.findCategoryByName( "World" ).isDeleted() );
        assertEquals( false, fixture.findUnitByName( "MyUnit" ).isDeleted() );
    }

    @Test
    public void deleteCategory_that_is_category_when_recursive_xxx()
    {
        // setup
        fixture.save( factory.createUnit( "MyUnit" ) );
        fixture.save( factory.createCategory( "World", null, "MyUnit", "deleter", "deleter" ) );
        fixture.save( factory.createCategoryAccessForUser( "World", "deleter", "read,administrate" ) );

        createAndStoreCategory( "Europe", "World" );

        fixture.flushAndClearHibernateSesssion();

        // verify setup
        assertEquals( false, fixture.findCategoryByName( "Europe" ).isDeleted() );

        fixture.flushAndClearHibernateSesssion();

        // delete
        DeleteCategoryParams params = new DeleteCategoryParams();
        params.key = fixture.findCategoryByName( "Europe" ).getKey().toInt();
        params.includeContent = true;
        params.recursive = true;
        internalClient.deleteCategory( params );

        hibernateTemplate.flush();

        assertEquals( 0, fixture.findCategoryByName( "World" ).getChildren().size() );
        assertEquals( true, fixture.findCategoryByName( "Europe" ).isDeleted() );
    }

    @Test
    public void deleteCategory_that_is_category_which_contains_content_in_sub_categories_when_recursive_is_true_and_include_content_is_true()
    {
        // setup
        fixture.save( factory.createUnit( "MyUnit" ) );
        fixture.save( factory.createCategory( "World", null, "MyUnit", "deleter", "deleter" ) );
        fixture.save( factory.createCategoryAccessForUser( "World", "deleter", "read,administrate" ) );

        createAndStoreCategory( "Europe", "World" );
        createAndStoreCategory( "Belarus", "Europe" );
        createAndStoreCategory( "Minsk", "Belarus" );
        createAndStoreCategory( "Babrujsk", "Belarus" );
        createAndStoreContent( "content1", "Europe" );
        createAndStoreContent( "content2", "Belarus" );

        fixture.flushAndClearHibernateSesssion();

        // verify setup
        assertEquals( false, fixture.findCategoryByName( "Europe" ).isDeleted() );
        assertEquals( false, fixture.findCategoryByName( "Belarus" ).isDeleted() );
        assertEquals( false, fixture.findCategoryByName( "Minsk" ).isDeleted() );
        assertEquals( 0, fixture.findCategoryByName( "Europe" ).getContents().iterator().next().getDeleted().intValue() );
        assertEquals( 0, fixture.findCategoryByName( "Belarus" ).getContents().iterator().next().getDeleted().intValue() );

        fixture.flushAndClearHibernateSesssion();

        // delete
        DeleteCategoryParams params = new DeleteCategoryParams();
        params.key = fixture.findCategoryByName( "Europe" ).getKey().toInt();
        params.includeContent = true;
        params.recursive = true;
        internalClient.deleteCategory( params );

        fixture.flushAndClearHibernateSesssion();

        assertEquals( 0, fixture.findCategoryByName( "World" ).getChildren().size() );
        assertEquals( true, fixture.findCategoryByName( "Europe" ).isDeleted() );
        assertEquals( 0, fixture.findCategoryByName( "Europe" ).getChildren().size() );
        assertEquals( true, fixture.findCategoryByName( "Belarus" ).isDeleted() );
        assertEquals( 0, fixture.findCategoryByName( "Belarus" ).getChildren().size() );
        assertEquals( true, fixture.findCategoryByName( "Minsk" ).isDeleted() );

        assertEquals( 1, fixture.findCategoryByName( "Europe" ).getContents().iterator().next().getDeleted().intValue() );
        assertEquals( 1, fixture.findCategoryByName( "Belarus" ).getContents().iterator().next().getDeleted().intValue() );

        assertEquals( true, fixture.findContentByName( "content1" ).isDeleted() );
        assertEquals( true, fixture.findContentByName( "content2" ).isDeleted() );
    }

    @Test
    public void deleteCategory_that_is_category_which_contains_many_content_in_many_sub_categories_when_recursive_is_true_and_include_content_is_true()
    {
        // setup
        fixture.save( factory.createUnit( "MyUnit" ) );
        fixture.save( factory.createCategory( "World", null, "MyUnit", "deleter", "deleter" ) );
        fixture.save( factory.createCategoryAccessForUser( "World", "deleter", "read,administrate" ) );

        createAndStoreCategory( "Europe", "World" );
        createAndStoreCategory( "Belarus", "Europe" );
        for ( int i = 1; i <= 10; i++ )
        {
            createAndStoreCategory( "BelarusCity-" + i, "Belarus" );
            for ( int j = 1; j <= 10; j++ )
            {
                createAndStoreContent( "belarus-city-" + i + "-content-" + j, "BelarusCity-" + i );
            }
        }

        fixture.flushAndClearHibernateSesssion();

        // verify setup
        assertEquals( false, fixture.findCategoryByName( "Europe" ).isDeleted() );
        assertEquals( false, fixture.findCategoryByName( "Belarus" ).isDeleted() );
        assertEquals( 100, fixture.countAllContent() );

        fixture.flushAndClearHibernateSesssion();

        // delete
        DeleteCategoryParams params = new DeleteCategoryParams();
        params.key = fixture.findCategoryByName( "Europe" ).getKey().toInt();
        params.includeContent = true;
        params.recursive = true;
        internalClient.deleteCategory( params );

        fixture.flushAndClearHibernateSesssion();

        assertEquals( true, fixture.findCategoryByName( "Europe" ).isDeleted() );
        assertEquals( true, fixture.findCategoryByName( "Belarus" ).isDeleted() );

        for ( int i = 1; i <= 10; i++ )
        {
            assertEquals( true, fixture.findCategoryByName( "BelarusCity-" + i ).isDeleted() );
            for ( int j = 1; j <= 10; j++ )
            {
                assertEquals( true, fixture.findContentByName( "belarus-city-" + i + "-content-" + j ).isDeleted() );
            }
        }
    }

    @Test
    public void deleteCategory_that_is_category_which_has_sub_category_throws_exception_when_recursive_is_false()
    {
        // setup
        fixture.save( factory.createUnit( "MyUnit" ) );
        fixture.save( factory.createCategory( "World", null, "MyUnit", "deleter", "deleter" ) );
        fixture.save( factory.createCategoryAccessForUser( "World", "deleter", "read,administrate" ) );

        createAndStoreCategory( "Europe", "World" );
        createAndStoreCategory( "Belarus", "Europe" );

        fixture.flushAndClearHibernateSesssion();

        // verify setup
        assertEquals( false, fixture.findCategoryByName( "Europe" ).isDeleted() );
        assertEquals( false, fixture.findCategoryByName( "Belarus" ).isDeleted() );

        fixture.flushAndClearHibernateSesssion();

        // delete
        DeleteCategoryParams params = new DeleteCategoryParams();
        params.key = fixture.findCategoryByName( "Europe" ).getKey().toInt();
        params.includeContent = false;
        params.recursive = false;
        try
        {
            internalClient.deleteCategory( params );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof ClientException );
            assertTrue( e.getMessage().contains(
                "Category [/World/Europe] contains categories. Deleting a category that contains categories is not allowed when recursive flag is false." ) );
        }
    }

    @Test
    public void deleteCategory_that_is_category_which_contains_content_throws_exception_when_includeContent_is_false_and_category_has_content()
    {
        // setup
        fixture.save( factory.createUnit( "MyUnit" ) );
        fixture.save( factory.createCategory( "World", null, "MyUnit", "deleter", "deleter" ) );
        fixture.save( factory.createCategoryAccessForUser( "World", "deleter", "read,administrate" ) );

        createAndStoreCategory( "Europe", "World" );
        createAndStoreCategory( "Belarus", "Europe" );
        createAndStoreContent( "Content-1", "Europe" );

        fixture.flushAndClearHibernateSesssion();

        // verify setup
        assertEquals( false, fixture.findCategoryByName( "Europe" ).isDeleted() );
        assertEquals( false, fixture.findCategoryByName( "Belarus" ).isDeleted() );

        fixture.flushAndClearHibernateSesssion();

        // delete
        DeleteCategoryParams params = new DeleteCategoryParams();
        params.key = fixture.findCategoryByName( "Europe" ).getKey().toInt();
        params.includeContent = false;
        params.recursive = true;
        try
        {
            internalClient.deleteCategory( params );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof ClientException );
            assertTrue( e.getMessage().contains(
                "Category [/World/Europe] contains content. Deleting a category that contains content is not allowed when includeContent is false." ) );
        }
    }

    @Test
    public void deleteCategory_that_is_category_which_have_sub_category_with_content_throws_exception_when_includeContent_is_false()
    {
        // setup
        fixture.save( factory.createUnit( "MyUnit" ) );
        fixture.save( factory.createCategory( "World", null, "MyUnit", "deleter", "deleter" ) );
        fixture.save( factory.createCategoryAccessForUser( "World", "deleter", "read,administrate" ) );

        createAndStoreCategory( "Europe", "World" );
        createAndStoreCategory( "Belarus", "Europe" );
        createAndStoreCategory( "Minsk", "Belarus" );
        createAndStoreContent( "Content-1", "Minsk" );

        fixture.flushAndClearHibernateSesssion();

        // verify setup
        assertEquals( false, fixture.findCategoryByName( "Europe" ).isDeleted() );
        assertEquals( false, fixture.findCategoryByName( "Belarus" ).isDeleted() );
        assertEquals( false, fixture.findCategoryByName( "Minsk" ).isDeleted() );

        fixture.flushAndClearHibernateSesssion();

        // exercise deleteCategory
        DeleteCategoryParams params = new DeleteCategoryParams();
        params.key = fixture.findCategoryByName( "Europe" ).getKey().toInt();
        params.includeContent = false;
        params.recursive = true;
        try
        {
            internalClient.deleteCategory( params );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof ClientException );
            assertTrue( e.getMessage().contains(
                "Failed to delete category: Category [/World/Europe/Belarus/Minsk] contains content. Deleting a category that contains content is not allowed when includeContent is false." ) );
        }
    }

    @Test
    public void deleteCategory_that_is_category_throws_exception_when_deleter_has_no_access()
    {
        // setup
        fixture.save( factory.createUnit( "MyUnit" ) );
        fixture.save( factory.createCategory( "World", null, "MyUnit", "deleter", "deleter" ) );
        fixture.save( factory.createCategoryAccessForUser( "World", "deleter", "read, administrate" ) );
        fixture.save( factory.createCategoryAccessForUser( "World", "deleter-noaccess", "read, admin_browse" ) );

        createAndStoreCategory( "Europe", "World" );

        fixture.flushAndClearHibernateSesssion();

        // verify setup
        assertEquals( false, fixture.findCategoryByName( "Europe" ).isDeleted() );

        fixture.flushAndClearHibernateSesssion();

        // exercise deleteCategory
        loginPortalUser( "deleter-noaccess" );
        DeleteCategoryParams params = new DeleteCategoryParams();
        params.key = fixture.findCategoryByName( "Europe" ).getKey().toInt();
        params.includeContent = false;
        params.recursive = true;
        try
        {
            internalClient.deleteCategory( params );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof ClientException );
            assertTrue( e.getMessage().contains(
                "Cannot delete category User testuserstore\\deleter-noaccess do not have ADMINISTRATE access on category" ) );
        }
    }

    private CategoryKey createAndStoreCategory( String categoryName, String parentCategoryName )
    {
        CategoryEntity myTopCategory = fixture.findCategoryByName( parentCategoryName );
        CreateCategoryParams params = new CreateCategoryParams();
        params.parentCategoryKey = myTopCategory.getKey().toInt();
        params.name = categoryName;
        params.contentTypeKey = fixture.findContentTypeByName( "MyContentType" ).getKey();

        int categoryKey = internalClient.createCategory( params );
        fixture.flushAndClearHibernateSesssion();

        return new CategoryKey( categoryKey );
    }

    private ContentKey createAndStoreContent( String contentName, String categoryName )
    {
        FileContentDataInput fileContentData = new FileContentDataInput();
        fileContentData.binary = new FileBinaryInput( dummyBinary, contentName );
        fileContentData.description = new FileDescriptionInput( "Dummy description." );
        fileContentData.keywords = new FileKeywordsInput().addKeyword( "keyword1" ).addKeyword( "keyword2" );
        fileContentData.name = new FileNameInput( contentName );

        CreateFileContentParams params = new CreateFileContentParams();
        params.categoryKey = fixture.findCategoryByName( categoryName ).getKey().toInt();
        params.publishFrom = null;
        params.publishTo = null;
        params.status = ContentStatus.STATUS_DRAFT;
        params.fileContentData = fileContentData;
        int contentKey = internalClient.createFileContent( params );
        fixture.flushAndClearHibernateSesssion();

        return new ContentKey( contentKey );
    }

    private void loginPortalUser( String userName )
    {
        SecurityHolder.setUser( fixture.findUserByName( userName ).getKey() );
        SecurityHolder.setRunAsUser( fixture.findUserByName( userName ).getKey() );
    }
}
