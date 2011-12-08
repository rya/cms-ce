package com.enonic.cms.itest.content;


import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.content.category.CategoryAccessEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.category.CategoryService;
import com.enonic.cms.core.content.category.StoreNewCategoryCommand;
import com.enonic.cms.core.content.category.access.CategoryAccessRights;
import com.enonic.cms.core.content.category.access.CreateCategoryAccessException;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;

import static org.junit.Assert.*;

public class CategoryServiceImplTest
    extends AbstractSpringTest
{
    @Autowired
    protected CategoryService categoryService;

    @Autowired
    protected CategoryDao categoryDao;

    @Autowired
    protected UserDao userDao;

    @Autowired
    protected GroupDao groupDao;

    private DomainFactory factory;

    @Autowired
    private DomainFixture fixture;


    @Before
    public void setUp()
    {
        factory = fixture.getFactory();

        // setup needed common data for each test
        fixture.initSystemData();

        fixture.createAndStoreUserAndUserGroup( "MyUser", "MyUser fullname", UserType.NORMAL, "testuserstore" );
    }

    @Test
    public void usual_category_is_created()
    {
        // setup
        fixture.save( factory.createUnit( "MyUnit", "by" ) );
        fixture.save( factory.createCategory( "ParentCategory", null, "MyUnit", "MyUser", "MyUser" ) );
        fixture.save(
            factory.createCategoryAccessForUser( "ParentCategory", "MyUser", "administrate, read, create, approve, admin_browse" ) );
        fixture.flushAndClearHibernateSesssion();

        // exercise
        StoreNewCategoryCommand command = new StoreNewCategoryCommand();
        command.setName( "Test category" );
        command.setCreator( fixture.findUserByName( "MyUser" ).getKey() );
        command.setUnitKey( fixture.findUnitByName( "MyUnit" ).getUnitKey() );
        command.setParentCategory( fixture.findCategoryByName( "ParentCategory" ).getKey() );
        CategoryKey key = categoryService.storeNewCategory( command );

        assertEquals( "Test category", categoryDao.findByKey( key ).getName() );
    }

    @Test(expected = CreateCategoryAccessException.class)
    public void create_category_without_access_rights()
    {
        // setup
        fixture.save( factory.createUnit( "MyUnit", "by" ) );
        fixture.save( factory.createCategory( "ParentCategory", null, "MyUnit", "MyUser", "MyUser" ) );
        fixture.save( factory.createCategoryAccessForUser( "ParentCategory", "MyUser", "read" ) );
        fixture.flushAndClearHibernateSesssion();

        // exercise
        StoreNewCategoryCommand command = new StoreNewCategoryCommand();
        command.setName( "Test category" );
        command.setCreator( fixture.findUserByName( "MyUser" ).getKey() );
        command.setUnitKey( fixture.findUnitByName( "MyUnit" ).getUnitKey() );
        command.setParentCategory( fixture.findCategoryByName( "ParentCategory" ).getKey() );
        categoryService.storeNewCategory( command );
    }

    @Test(expected = CreateCategoryAccessException.class)
    public void create_top_category_without_access_rights()
    {
        // setup
        fixture.save( factory.createUnit( "MyUnit", "by" ) );
        fixture.flushAndClearHibernateSesssion();

        // exercise
        StoreNewCategoryCommand command = new StoreNewCategoryCommand();
        command.setName( "Test category" );
        command.setCreator( fixture.findUserByName( "MyUser" ).getKey() );
        command.setUnitKey( fixture.findUnitByName( "MyUnit" ).getUnitKey() );
        categoryService.storeNewCategory( command );
    }

    @Test
    public void unit_is_set_on_when_creating_top_category_and_unit_is_given()
    {
        // setup
        fixture.save( factory.createUnit( "MyCommandUnit", "by" ) );
        fixture.flushAndClearHibernateSesssion();

        // exercise
        StoreNewCategoryCommand command = new StoreNewCategoryCommand();
        command.setName( "Test category" );
        command.setCreator( fixture.findUserByName( User.ROOT_UID ).getKey() );
        command.setUnitKey( fixture.findUnitByName( "MyCommandUnit" ).getUnitKey() );
        CategoryKey key = categoryService.storeNewCategory( command );

        assertNotNull( categoryDao.findByKey( key ).getUnit() );
        assertEquals( "MyCommandUnit", categoryDao.findByKey( key ).getUnit().getName() );
    }

    @Test
    public void accessrights_for_administrator_is_persisted_when_creating_top_category_and_accessrights_are_not_given()
    {
        // setup
        fixture.save( factory.createUnit( "MyCommandUnit", "by" ) );
        fixture.flushAndClearHibernateSesssion();

        // exercise
        StoreNewCategoryCommand command = new StoreNewCategoryCommand();
        command.setName( "Test category" );
        command.setCreator( fixture.findUserByName( User.ROOT_UID ).getKey() );
        command.setUnitKey( fixture.findUnitByName( "MyCommandUnit" ).getUnitKey() );

        CategoryKey key = categoryService.storeNewCategory( command );

        assertNotNull( categoryDao.findByKey( key ).getAccessRights().values() );
        assertTrue( categoryDao.findByKey( key ).getAccessRights().size() == 1 );

        CategoryAccessEntity categoryAccess = categoryDao.findByKey( key ).getAccessRights().values().iterator().next();
        assertEquals( groupDao.findBuiltInAdministrator().getGroupKey(), categoryAccess.getKey().getGroupKey() );
        assertTrue( categoryAccess.isAdminAccess() );
        assertTrue( categoryAccess.isAdminBrowseAccess() );
        assertTrue( categoryAccess.isCreateAccess() );
        assertTrue( categoryAccess.isPublishAccess() );
        assertTrue( categoryAccess.isReadAccess() );
    }

    @Test
    public void accessrights_is_persisted_when_creating_top_category_and_accessrights_are_given()
    {
        // setup
        fixture.save( factory.createUnit( "MyCommandUnit", "by" ) );
        fixture.flushAndClearHibernateSesssion();

        // exercise
        StoreNewCategoryCommand command = new StoreNewCategoryCommand();
        command.setName( "Test category" );
        command.setCreator( fixture.findUserByName( User.ROOT_UID ).getKey() );
        command.setUnitKey( fixture.findUnitByName( "MyCommandUnit" ).getUnitKey() );

        CategoryAccessRights accessRights = new CategoryAccessRights();
        accessRights.setGroupKey( groupDao.findBuiltInDeveloper().getGroupKey() );
        accessRights.setAdminAccess( false );
        accessRights.setAdminBrowseAccess( false );
        accessRights.setCreateAccess( false );
        accessRights.setPublishAccess( true );
        accessRights.setReadAccess( true );
        command.addAccessRight( accessRights );

        CategoryKey key = categoryService.storeNewCategory( command );

        assertNotNull( categoryDao.findByKey( key ).getAccessRights().values() );
        assertTrue( categoryDao.findByKey( key ).getAccessRights().size() == 2 );

        assertNotNull( categoryDao.findByKey( key ).getAccessRights().get( groupDao.findBuiltInAdministrator().getGroupKey() ) );

        CategoryAccessEntity categoryAccess =
            categoryDao.findByKey( key ).getAccessRights().get( groupDao.findBuiltInDeveloper().getGroupKey() );

        assertFalse( categoryAccess.isAdminAccess() );
        assertFalse( categoryAccess.isAdminBrowseAccess() );
        assertFalse( categoryAccess.isCreateAccess() );
        assertTrue( categoryAccess.isPublishAccess() );
        assertTrue( categoryAccess.isReadAccess() );
    }

    @Test
    public void inherited_accessrights_from_top_category_is_persisted_when_creating_child_category_without_given_accessrights()
    {
        // setup
        fixture.save( factory.createUnit( "MyCommandUnit", "by" ) );
        fixture.flushAndClearHibernateSesssion();

        // create top category with defined access rights
        StoreNewCategoryCommand topCategoryCommand = new StoreNewCategoryCommand();
        topCategoryCommand.setName( "Test category" );
        topCategoryCommand.setCreator( fixture.findUserByName( User.ROOT_UID ).getKey() );
        topCategoryCommand.setUnitKey( fixture.findUnitByName( "MyCommandUnit" ).getUnitKey() );

        CategoryAccessRights accessRights = new CategoryAccessRights();
        accessRights.setGroupKey( groupDao.findBuiltInDeveloper().getGroupKey() );
        accessRights.setAdminAccess( false );
        accessRights.setAdminBrowseAccess( false );
        accessRights.setCreateAccess( false );
        accessRights.setPublishAccess( true );
        accessRights.setReadAccess( true );
        topCategoryCommand.addAccessRight( accessRights );

        CategoryKey topCategoryKey = categoryService.storeNewCategory( topCategoryCommand );

        //create child category under the top category
        StoreNewCategoryCommand childCategoryCommand = new StoreNewCategoryCommand();
        childCategoryCommand.setName( "Child category" );
        childCategoryCommand.setCreator( fixture.findUserByName( User.ROOT_UID ).getKey() );
        childCategoryCommand.setUnitKey( fixture.findUnitByName( "MyCommandUnit" ).getUnitKey() );
        childCategoryCommand.setParentCategory( topCategoryKey );

        CategoryKey childCategoryKey = categoryService.storeNewCategory( childCategoryCommand );

        assertEquals( "Child category", categoryDao.findByKey( childCategoryKey ).getName() );

        // verify same accessrights from parent is persisted
        assertNotNull( categoryDao.findByKey( childCategoryKey ).getAccessRights().values() );
        assertTrue( categoryDao.findByKey( childCategoryKey ).getAccessRights().size() == 2 );

        CategoryAccessEntity categoryDeveloperAccess =
            categoryDao.findByKey( childCategoryKey ).getAccessRights().get( groupDao.findBuiltInDeveloper().getGroupKey() );

        assertEquals( groupDao.findBuiltInDeveloper().getGroupKey(), categoryDeveloperAccess.getKey().getGroupKey() );
        assertFalse( categoryDeveloperAccess.isAdminAccess() );
        assertFalse( categoryDeveloperAccess.isAdminBrowseAccess() );
        assertFalse( categoryDeveloperAccess.isCreateAccess() );
        assertTrue( categoryDeveloperAccess.isPublishAccess() );
        assertTrue( categoryDeveloperAccess.isReadAccess() );

        CategoryAccessEntity categoryAdministratorAccess =
            categoryDao.findByKey( childCategoryKey ).getAccessRights().get( groupDao.findBuiltInAdministrator().getGroupKey() );

        assertEquals( groupDao.findBuiltInAdministrator().getGroupKey(), categoryAdministratorAccess.getKey().getGroupKey() );
        assertTrue( categoryAdministratorAccess.isAdminAccess() );
        assertTrue( categoryAdministratorAccess.isAdminBrowseAccess() );
        assertTrue( categoryAdministratorAccess.isCreateAccess() );
        assertTrue( categoryAdministratorAccess.isPublishAccess() );
        assertTrue( categoryAdministratorAccess.isReadAccess() );
    }

}
