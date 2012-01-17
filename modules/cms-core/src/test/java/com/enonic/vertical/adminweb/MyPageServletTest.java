package com.enonic.vertical.adminweb;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.core.content.category.CategoryAccessEntity;
import com.enonic.cms.core.content.category.CategoryAccessType;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.store.dao.GroupDao;

public class MyPageServletTest
{
    public static final CategoryAccessType[] CREATE_BROWSE = new CategoryAccessType[] {
        CategoryAccessType.ADMIN_BROWSE, CategoryAccessType.CREATE
    };

    private MyPageServlet servlet;
    private CategoryAccessEntity accessEntity;
    private UserEntity userEntity;
    private ContentTypeEntity contentType;

    @Before
    public void setUp()
        throws Exception
    {
        final GroupEntity adminGroupEntity = new GroupEntity();
        final GroupKey adminGroupKey = new GroupKey( "admin" );
        adminGroupEntity.setKey( adminGroupKey );

        final GroupEntity anonGroupEntity = new GroupEntity();
        final GroupKey anonGroupKey = new GroupKey( "anonymous" );
        anonGroupEntity.setKey( anonGroupKey );

        servlet = new MyPageServlet();
        servlet.groupDao = Mockito.mock( GroupDao.class );
        Mockito.when( servlet.groupDao.findBuiltInAnonymous() ).thenReturn( anonGroupEntity );
        Mockito.when( servlet.groupDao.findBuiltInEnterpriseAdministrator() ).thenReturn( adminGroupEntity );

        final GroupEntity groupEntity = new GroupEntity();
        final GroupKey groupKey = new GroupKey( "user_group" );
        groupEntity.setKey( groupKey );

        userEntity = new UserEntity(  );
        userEntity.setName( "user" );
        userEntity.setType( UserType.NORMAL );
        userEntity.setUserGroup( groupEntity );

        final Map<GroupKey, CategoryAccessEntity> accessRights = new HashMap<GroupKey, CategoryAccessEntity>();

        accessEntity = new CategoryAccessEntity();
        accessRights.put( groupKey, accessEntity );

        contentType = Mockito.mock( ContentTypeEntity.class );

        final CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setAccessRights( accessRights );
        Mockito.when( contentType.getCategories(false) ).thenReturn( Arrays.<CategoryEntity>asList( categoryEntity ) );
    }

    @Test
    public void testUserHasAccessOnCategoriesOfContentType_create()
    {
        accessEntity.setCreateAccess( true );

        boolean hasAccess = servlet.userHasAccessOnCategoriesOfContentType( userEntity, contentType, CREATE_BROWSE );

        Assert.assertFalse( hasAccess );
    }


    @Test
    public void testUserHasAccessOnCategoriesOfContentType_browse()
    {
        accessEntity.setAdminBrowseAccess( true );

        boolean hasAccess = servlet.userHasAccessOnCategoriesOfContentType( userEntity, contentType, CREATE_BROWSE );

        Assert.assertFalse( hasAccess );
    }


    @Test
    public void testUserHasAccessOnCategoriesOfContentType_create_browse()
    {
        accessEntity.setCreateAccess( true );
        accessEntity.setAdminBrowseAccess( true );

        boolean hasAccess = servlet.userHasAccessOnCategoriesOfContentType( userEntity, contentType, CREATE_BROWSE );

        Assert.assertTrue( hasAccess );
    }

    @Test
    public void testUserHasAccessOnCategoriesOfContentType_admin()
    {
        accessEntity.setAdminAccess( true );

        boolean hasAccess = servlet.userHasAccessOnCategoriesOfContentType( userEntity, contentType, CREATE_BROWSE );

        Assert.assertTrue( hasAccess );
    }

    @Test
    public void testUserHasAccessOnCategoriesOfContentType_read()
    {
        accessEntity.setReadAccess( true );

        boolean hasAccess = servlet.userHasAccessOnCategoriesOfContentType( userEntity, contentType, CREATE_BROWSE );

        Assert.assertFalse( hasAccess );
    }

    @Test
    public void testUserHasAccessOnCategoriesOfContentType_read_browse()
    {
        accessEntity.setReadAccess( true );
        accessEntity.setAdminBrowseAccess( true );

        boolean hasAccess = servlet.userHasAccessOnCategoriesOfContentType( userEntity, contentType, CREATE_BROWSE );

        Assert.assertFalse( hasAccess );
    }

    @Test
    public void testUserHasAccessOnCategoriesOfContentType_read_only()
    {
        accessEntity.setReadAccess( true );

        boolean hasAccess = servlet.userHasAccessOnCategoriesOfContentType( userEntity, contentType, CategoryAccessType.READ );

        Assert.assertTrue( hasAccess );
    }

}
