/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.business;

import java.util.Date;
import java.util.List;

import com.enonic.cms.core.content.*;
import com.enonic.cms.core.content.binary.BinaryDataAndBinary;
import com.enonic.cms.core.content.binary.BinaryDataEntity;
import com.enonic.cms.core.content.category.*;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import org.jdom.Document;
import org.jdom.transform.JDOMSource;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.orm.hibernate3.HibernateTemplate;

import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.sxpath.XPathEvaluator;
import net.sf.saxon.sxpath.XPathExpression;

import com.enonic.cms.framework.util.JDOMUtil;
import com.enonic.cms.framework.xml.XMLBytes;

import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentVersionDao;
import com.enonic.cms.store.dao.GroupEntityDao;

import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.domain.LanguageKey;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentHandlerEntity;
import com.enonic.cms.core.content.ContentHandlerKey;
import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.UnitEntity;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserType;


public abstract class AbstractPersistContentTest
{
    private static int lastUsedId = -1;

    @Autowired
    protected ContentStorer contentStorer;

    @Autowired
    protected ContentService contentService;

    @Autowired
    protected ContentDao contentDao;

    @Autowired
    private GroupEntityDao groupEntityDao;

    @Autowired
    protected ContentVersionDao contentVersionDao;

    @Autowired
    protected HibernateTemplate hibernateTemplate;


    protected void initSystemData()
    {
        groupEntityDao.invalidateCachedKeys();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );

        hibernateTemplate.save( createLanguage( "en" ) );
        hibernateTemplate.save( createUserStore( "testuserstore" ) );
        hibernateTemplate.save( createGroup( GroupType.ENTERPRISE_ADMINS.getName(), GroupType.ENTERPRISE_ADMINS ) );
        hibernateTemplate.save( createGroup( GroupType.ADMINS.getName(), GroupType.ADMINS ) );
        hibernateTemplate.save( createGroup( GroupType.DEVELOPERS.getName(), GroupType.DEVELOPERS ) );
        hibernateTemplate.save( createGroup( GroupType.EXPERT_CONTRIBUTORS.getName(), GroupType.EXPERT_CONTRIBUTORS ) );
        hibernateTemplate.save( createGroup( GroupType.CONTRIBUTORS.getName(), GroupType.CONTRIBUTORS ) );
        hibernateTemplate.save( createGroup( GroupType.ANONYMOUS.getName(), GroupType.ANONYMOUS ) );
        hibernateTemplate.save(
            createGroupInUserstore( GroupType.AUTHENTICATED_USERS.getName(), GroupType.AUTHENTICATED_USERS, "testuserstore" ) );

        hibernateTemplate.save( createUser( User.ANONYMOUS_UID, "Anonmymous User", UserType.ANONYMOUS, null ) );
        hibernateTemplate.save( createUser( User.ROOT_UID, "Enterprise Admin", UserType.ADMINISTRATOR, null ) );
    }

    protected LanguageEntity createLanguage( String code )
    {
        LanguageEntity language = new LanguageEntity();
        language.setKey( new LanguageKey( ++lastUsedId ) );
        language.setCode( code );
        language.setTimestamp( new Date() );
        return language;
    }

    protected UserStoreEntity createUserStore( String name )
    {
        UserStoreEntity userStore = new UserStoreEntity();
        userStore.setKey( new UserStoreKey( ++lastUsedId ) );
        userStore.setName( name );
        userStore.setDeleted( false );
        return userStore;
    }

    protected UserEntity createAndStoreUserAndUserGroup( String uid, String displayName, UserType type, String userStoreName )
    {
        GroupEntity userGroup = new GroupEntity();
        userGroup.setName( uid );
        userGroup.setSyncValue( uid );
        userGroup.setDeleted( 0 );
        userGroup.setType( GroupType.resolveAssociate( type ) );
        userGroup.setRestricted( 1 );
        hibernateTemplate.save( userGroup );

        UserEntity user = createUser( uid, displayName, type, userStoreName );
        user.setUserGroup( userGroup );

        hibernateTemplate.save( user );
        hibernateTemplate.flush();

        return user;
    }


    private UserEntity createUser( String uid, String displayName, UserType type, String userStoreName )
    {
        return createUser( uid, displayName, type, userStoreName, null );
    }

    private UserEntity createUser( String uid, String displayName, UserType type, String userStoreName, GroupEntity group )
    {
        UserEntity user = new UserEntity();
        user.setName( uid );
        user.setDisplayName( displayName );
        user.setSyncValue( uid );
        user.setTimestamp( new DateTime() );
        user.setType( type );
        user.setDeleted( 0 );
        if ( userStoreName != null )
        {
            user.setUserStore( findUserStoreByName( userStoreName ) );
        }
        if ( group != null )
        {
            user.setUserGroup( group );
        }

        return user;
    }

    protected GroupEntity createGroup( String name, GroupType groupType )
    {
        GroupEntity group = new GroupEntity();
        group.setName( name );
        group.setSyncValue( "sync_" + name );
        group.setDeleted( 0 );
        group.setRestricted( 1 );
        group.setType( groupType );
        return group;
    }

    protected GroupEntity createGroupInUserstore( String name, GroupType groupType, String userstoreName )
    {
        GroupEntity group = new GroupEntity();
        group.setName( name );
        group.setSyncValue( "sync_" + name );
        group.setDeleted( 0 );
        group.setRestricted( 1 );
        group.setType( groupType );
        group.setUserStore( findUserStoreByName( userstoreName ) );
        return group;
    }

    protected ContentHandlerEntity createContentHandler( String name, String handlerClassName )
    {
        ContentHandlerName contentHandlerName = ContentHandlerName.parse( handlerClassName );
        ContentHandlerEntity contentHandler = new ContentHandlerEntity();
        contentHandler.setKey( new ContentHandlerKey( ++lastUsedId ) );
        contentHandler.setName( name );
        contentHandler.setClassName( contentHandlerName.getHandlerClassShortName() );
        contentHandler.setTimestamp( new Date() );
        return contentHandler;
    }

    protected ContentTypeEntity createContentType( String name, String contentHandlerClassName )
    {
        return createContentType( name, contentHandlerClassName, null );
    }

    protected ContentTypeEntity createContentType( String name, String contentHandlerClassName, XMLBytes data )
    {
        ContentTypeEntity contenType = new ContentTypeEntity();
        contenType.setKey( ++lastUsedId );
        contenType.setName( name );
        contenType.setHandler( findContentHandlerByClassName( contentHandlerClassName ) );
        contenType.setTimestamp( new Date() );
        contenType.setData( data );
        return contenType;
    }

    protected UnitEntity createUnit( String name )
    {
        return createUnit( name, null );
    }

    protected UnitEntity createUnit( String name, String languageCode )
    {
        UnitEntity unit = new UnitEntity();
        unit.setKey( ++lastUsedId );
        unit.setName( name );
        unit.setLanguage( findLanguageByCode( languageCode ) );
        return unit;
    }

    protected CategoryEntity createCategory( String name, String contentTypeName, String unitName, String ownerUid, String modifierUid )
    {
        return createCategory( name, contentTypeName, unitName, ownerUid, modifierUid, false );
    }

    protected CategoryEntity createCategory( String name, String contentTypeName, String unitName, String ownerUid, String modifierUid,
                                             boolean autoApprove )
    {
        CategoryEntity category = new CategoryEntity();
        category.setKey( new CategoryKey( ++lastUsedId ) );
        category.setName( name );
        category.setContentType( findContentTypeByName( contentTypeName ) );
        category.setUnit( findUnitByName( unitName ) );
        category.setCreated( new Date() );
        category.setTimestamp( new Date() );
        category.setOwner( findUserByName( ownerUid ) );
        category.setModifier( findUserByName( modifierUid ) );
        category.setAutoMakeAvailable( autoApprove );
        category.setDeleted( false );
        return category;
    }

    protected CategoryAccessEntity createCategoryAccess( String categoryName, String groupName, String read, String adminBrowse,
                                                         String create, String publish, String administrate )
    {
        CategoryEntity category = findCategoryByName( categoryName );
        GroupEntity group = findGroupByName( groupName );

        CategoryAccessEntity access = new CategoryAccessEntity();
        access.setKey( new CategoryAccessKey( category.getKey(), group.getGroupKey() ) );
        access.setReadAccess( Boolean.valueOf( read ) );
        access.setAdminBrowseAccess( Boolean.valueOf( adminBrowse ) );
        access.setCreateAccess( Boolean.valueOf( create ) );
        access.setPublishAccess( Boolean.valueOf( publish ) );
        access.setAdminAccess( Boolean.valueOf( administrate ) );
        return access;
    }

    protected ContentEntity createContent( String categoryName, String languageCode, String ownerQualifiedName, String priority )
    {
        ContentEntity content = new ContentEntity();
        content.setLanguage( findLanguageByCode( languageCode ) );
        content.setCategory( findCategoryByName( categoryName ) );
        content.setOwner( findUserByName( ownerQualifiedName ) );
        content.setPriority( Integer.valueOf( priority ) );
        content.setName( "testcontent" );
        return content;
    }

    protected ContentVersionEntity createContentVersion( String status, String modiferQualifiedName )
    {
        ContentVersionEntity version = new ContentVersionEntity();
        version.setStatus( ContentStatus.get( Integer.valueOf( status ) ) );
        version.setModifiedBy( findUserByName( modiferQualifiedName ) );
        return version;
    }

    protected BinaryDataAndBinary createBinaryDataAndBinary( String name, byte[] data )
    {
        BinaryDataEntity binaryData = createBinaryData( name, data.length );
        return new BinaryDataAndBinary( binaryData, data );
    }

    protected BinaryDataEntity createBinaryData( String name, int size )
    {
        BinaryDataEntity binaryData = new BinaryDataEntity();
        binaryData.setName( name );
        binaryData.setCreatedAt( new Date() );
        binaryData.setSize( size );
        return binaryData;
    }

    protected LanguageEntity findLanguageByCode( String value )
    {
        LanguageEntity example = new LanguageEntity();
        example.setCode( value );
        return (LanguageEntity) findFirstByExample( example );
    }

    protected UserStoreEntity findUserStoreByName( String userStoreName )
    {
        UserStoreEntity example = new UserStoreEntity();
        example.setName( userStoreName );
        return (UserStoreEntity) findFirstByExample( example );
    }

    protected UserEntity findUserByName( String value )
    {
        UserEntity example = new UserEntity();
        example.setName( value );
        return (UserEntity) findFirstByExample( example );
    }

    protected GroupEntity findGroupByKey( String groupKey )
    {
        GroupEntity example = new GroupEntity();
        example.setKey( groupKey );
        return (GroupEntity) findFirstByExample( example );
    }

    protected GroupEntity findGroupByName( String groupName )
    {
        GroupEntity example = new GroupEntity();
        example.setName( groupName );
        return (GroupEntity) findFirstByExample( example );
    }

    protected ContentHandlerEntity findContentHandlerByClassName( String value )
    {
        ContentHandlerEntity example = new ContentHandlerEntity();
        example.setClassName( value );
        return (ContentHandlerEntity) findFirstByExample( example );
    }

    protected ContentTypeEntity findContentTypeByName( String value )
    {
        ContentTypeEntity example = new ContentTypeEntity();
        example.setName( value );
        return (ContentTypeEntity) findFirstByExample( example );
    }

    protected UnitEntity findUnitByName( String value )
    {
        UnitEntity example = new UnitEntity();
        example.setName( value );
        return (UnitEntity) findFirstByExample( example );
    }

    protected CategoryEntity findCategoryByName( String value )
    {
        CategoryEntity example = new CategoryEntity();
        example.setName( value );
        return (CategoryEntity) findFirstByExample( example );
    }

    protected ContentEntity findFirstContentByCategory( CategoryEntity category )
    {
        ContentEntity example = new ContentEntity();
        example.setCategory( category );
        return (ContentEntity) findFirstByExample( example );
    }

    protected Object findFirstByExample( Object example )
    {
        List list = hibernateTemplate.findByExample( example );
        if ( list.isEmpty() )
        {
            return null;
        }

        return list.get( 0 );
    }

    protected void assertXPathEquals( String xpathString, Document doc, String[] expectedValues )
    {

        try
        {
            XPathEvaluator xpathEvaluator = new XPathEvaluator();
            XPathExpression expr = xpathEvaluator.createExpression( xpathString );

            final JDOMSource docAsDomSource = new JDOMSource( doc );

            List nodes = expr.evaluate( docAsDomSource );

            if ( nodes.size() != expectedValues.length )
            {
                Assert.fail( "expected " + expectedValues.length + " values at xpath" );
            }

            for ( int i = 0; i < expectedValues.length; i++ )
            {
                Object node = nodes.get( i );
                if ( node instanceof NodeInfo )
                {
                    NodeInfo nodeInfo = (NodeInfo) node;
                    Assert.assertEquals( xpathString, expectedValues[i], nodeInfo.getStringValue() );
                }
                else
                {
                    Assert.assertEquals( xpathString, expectedValues[i], node );
                }
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    protected void assertXPathEquals( String xpathString, Document doc, String expectedValue )
    {
        String actualValue = JDOMUtil.evaluateSingleXPathValueAsString( xpathString, doc );
        Assert.assertEquals( xpathString, expectedValue, actualValue );

    }
}
