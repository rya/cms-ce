/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver;

import java.util.List;
import java.util.Locale;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.structure.SiteEntity;
import org.jdom.Document;
import org.jdom.Element;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.framework.util.JDOMUtil;
import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.resolver.mock.ResolverHttpRequestInputCreatorMock;
import com.enonic.cms.core.resolver.mock.ResolverHttpRequestInputXMLCreatorMock;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;

/**
 * Created by rmy - Date: Aug 24, 2009
 */
public class ResolverInputXMLCreatorTest
{

    private ResolverInputXMLCreator resolverInputXMLCreator;

    private ResolverHttpRequestInputCreatorMock httpRequestInputCreatorMock;

    private ResolverHttpRequestInputXMLCreatorMock httpRequestInputXMLCreatorMock;

    private static final String USER_LOCALE = "no_nb";

    @Before
    public void setUp()
    {
        resolverInputXMLCreator = new ResolverInputXMLCreator();

        httpRequestInputCreatorMock = new ResolverHttpRequestInputCreatorMock();
        httpRequestInputXMLCreatorMock = new ResolverHttpRequestInputXMLCreatorMock();

        resolverInputXMLCreator.setResolverHttpRequestInputCreator( httpRequestInputCreatorMock );
        resolverInputXMLCreator.setResolverHttpRequestInputXMLCreator( httpRequestInputXMLCreatorMock );
    }

    @Test
    public void testDocumentStucture()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();

        ResolverContext context = new ResolverContext( request, new SiteEntity() );

        XMLDocument input = resolverInputXMLCreator.buildResolverInputXML( context );

        Document doc = input.getAsJDOMDocument();
        Element root = doc.getRootElement();

        assertEquals( root.getName(), "context" );

        assertEquals( root.getContentSize(), 2 );

        List<Element> children = root.getChildren();

        for ( Element child : children )
        {
            assertTrue( "Unexptected element found in XML", child.getName().equals( "request" ) || child.getName().equals( "user" ) );
        }
    }


    @Test
    public void testUserDocument()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();

        ResolverContext context = new ResolverContext( request, new SiteEntity() );
        context.setUser( createNormalUser() );

        XMLDocument input = resolverInputXMLCreator.buildResolverInputXML( context );

        Document doc = input.getAsJDOMDocument();
        Element root = doc.getRootElement();

        Element userElement = JDOMUtil.getElement( root, "user" );
        assertNotNull( "UserElement shold exist in XML", userElement );

        Element localeElement = JDOMUtil.getElement( userElement, "locale" );
        assertNotNull( "locale-element shold exist in XML", localeElement );

        String locale = localeElement.getValue();
        assertEquals( "locale should be in XML with correct value", USER_LOCALE, locale );
    }

    private UserEntity createNormalUser()
    {
        UserEntity user = new UserEntity();
        user.setKey( new UserKey( "1" ) );
        user.setDeleted( false );
        user.setEmail( "email@example.com" );
        user.setDisplayName( "DisplayName" );
        user.setName( "uid" );
        user.setSyncValue( "syncValue" );
        user.setType( UserType.NORMAL );
        user.setTimestamp( new DateTime() );

        user.getUserInfo().setLocale( new Locale( USER_LOCALE ) );

        UserStoreEntity userStore = new UserStoreEntity();
        userStore.setName( "myUserStore" );
        userStore.setDefaultStore( true );
        userStore.setDeleted( false );
        userStore.setKey( new UserStoreKey( "1" ) );

        user.setUserStore( userStore );

        GroupEntity userGroup = new GroupEntity();
        userGroup.setDeleted( 0 );
        userGroup.setDescription( null );
        userGroup.setName( "userGroup" + user.getKey() );
        //       userGroup.setSyncValue( user.getSyncValue() );
        userGroup.setUser( user );
        userGroup.setType( GroupType.USER );
        userGroup.setRestricted( 1 );

        user.setUserGroup( userGroup );

        return user;
    }

}
