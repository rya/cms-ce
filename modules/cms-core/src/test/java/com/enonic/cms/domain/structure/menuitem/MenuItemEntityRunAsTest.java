/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.structure.menuitem;

import com.enonic.cms.core.structure.RunAsType;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.page.PageEntity;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;

/**
 *
 */
public class MenuItemEntityRunAsTest
    extends TestCase
{

    private SiteEntity site;

    private UserEntity defaultRunAsUser;

    @Before
    public void setUp()
    {
        defaultRunAsUser = createUser( UserType.NORMAL, "virtualBoss", "188A09" );

        site = new SiteEntity();
        site.setDefaultRunAsUser( defaultRunAsUser );
    }

    @Test
    public void testResolveRunAsAnonymousUser()
    {
        UserEntity user = createUser( UserType.ANONYMOUS, "anonymous", "51B0C7" );
        MenuItemEntity menuItem = createMenuItem( site, "Utenriks", RunAsType.PERSONALIZED, null, null );
        User runAsUser = menuItem.resolveRunAsUser( user, true );
        assertTrue( "The run as user should be anonymous when input is anonymous", runAsUser.isAnonymous() );

        menuItem.setRunAs( RunAsType.INHERIT );
        runAsUser = menuItem.resolveRunAsUser( user, true );
        assertTrue( "The run as user should be anonymous when input is anonymous", runAsUser.isAnonymous() );

        menuItem.setRunAs( RunAsType.DEFAULT_USER );
        runAsUser = menuItem.resolveRunAsUser( user, true );
        assertTrue( "The run as user should be anonymous when input is anonymous", runAsUser.isAnonymous() );
    }

    @Test
    public void testResolveRunAsUserNoInherit()
    {
        UserEntity loggedInUser = createUser( UserType.NORMAL, "spirrevipp", "51B0C7" );

        MenuItemEntity menuItem = createMenuItem( site, "Utenriks", RunAsType.PERSONALIZED, null, null );

        User runAsUser = menuItem.resolveRunAsUser( loggedInUser, true );
        assertEquals( "Logged in user is not run as user, despite 'Personalized' run as policy", loggedInUser, runAsUser );

        menuItem.setRunAs( RunAsType.DEFAULT_USER );
        runAsUser = menuItem.resolveRunAsUser( loggedInUser, true );
        assertEquals( "Run as user is not the default despite 'Default User' run as policy.", defaultRunAsUser, runAsUser );
    }

    @Test
    public void testResolveRunAsUserInheritFromPageTemplate()
    {
        UserEntity loggedInUser = createUser( UserType.NORMAL, "spirrevipp", "51B0C7" );
        PageEntity page = createPage( "simplePage", RunAsType.PERSONALIZED );
        MenuItemEntity menuItem = createMenuItem( site, "Utenriks", RunAsType.INHERIT, null, page );

        User runAsUser = menuItem.resolveRunAsUser( loggedInUser, true );
        assertEquals( "Logged in user is not run as user, despite 'Personalized' run as policy", loggedInUser, runAsUser );

        menuItem.setRunAs( RunAsType.DEFAULT_USER );
        runAsUser = menuItem.resolveRunAsUser( loggedInUser, true );
        assertEquals( "Run as user is not the default despite 'Default User' run as policy.", defaultRunAsUser, runAsUser );
    }

    @Test
    public void testResolveRunAsUserInheritFromHigherLevelMenuItem()
    {
        UserEntity loggedInUser = createUser( UserType.NORMAL, "spirrevipp", "51B0C7" );
        PageEntity topLevelPage = createPage( "introPage", RunAsType.DEFAULT_USER );
        PageEntity secondLevelPageEntity = createPage( "sectionPage", RunAsType.DEFAULT_USER );
        PageEntity lowLevelPage = createPage( "simplePage", RunAsType.INHERIT );
        MenuItemEntity topLevelMenuItem = createMenuItem( site, "Nyheter", RunAsType.DEFAULT_USER, null, topLevelPage );
        MenuItemEntity secondLevelMenuItem =
            createMenuItem( site, "Utenriks", RunAsType.PERSONALIZED, topLevelMenuItem, secondLevelPageEntity );
        MenuItemEntity lowLevelMenuItem = createMenuItem( site, "Opprøret i Tibet", RunAsType.INHERIT, secondLevelMenuItem, lowLevelPage );

        User runAsUser = lowLevelMenuItem.resolveRunAsUser( loggedInUser, true );
        assertEquals( "Logged in user is not run as user, despite 'Personalized' run as policy", loggedInUser, runAsUser );

        topLevelPage.getTemplate().setRunAs( RunAsType.PERSONALIZED );
        secondLevelPageEntity.getTemplate().setRunAs( RunAsType.PERSONALIZED );
        topLevelMenuItem.setRunAs( RunAsType.PERSONALIZED );
        secondLevelMenuItem.setRunAs( RunAsType.DEFAULT_USER );
        runAsUser = lowLevelMenuItem.resolveRunAsUser( loggedInUser, true );
        assertEquals( "Run as user is not the default despite 'Default User' run as policy.", defaultRunAsUser, runAsUser );

        secondLevelMenuItem.setRunAs( RunAsType.INHERIT );
        topLevelMenuItem.setRunAs( RunAsType.DEFAULT_USER );
        runAsUser = lowLevelMenuItem.resolveRunAsUser( loggedInUser, true );
        assertEquals( "Run as user is not the default despite 'Default User' run as policy.", defaultRunAsUser, runAsUser );

        topLevelPage.getTemplate().setRunAs( RunAsType.DEFAULT_USER );
        secondLevelPageEntity.getTemplate().setRunAs( RunAsType.DEFAULT_USER );
        topLevelMenuItem.setRunAs( RunAsType.PERSONALIZED );
        runAsUser = lowLevelMenuItem.resolveRunAsUser( loggedInUser, true );
        assertEquals( "Logged in user is not run as user, despite 'Personalized' run as policy", loggedInUser, runAsUser );
    }

    @Test
    public void testResolveRunAsUserInheritFromSite()
    {
        UserEntity loggedInUser = createUser( UserType.NORMAL, "spirrevipp", "51B0C7" );
        PageEntity topLevelPage = createPage( "introPage", RunAsType.PERSONALIZED );
        PageEntity page = createPage( "simplePage", RunAsType.INHERIT );
        MenuItemEntity topLevelMenuItem = createMenuItem( site, "Nyheter", RunAsType.INHERIT, null, topLevelPage );
        MenuItemEntity menuItem = createMenuItem( site, "Utenriks", RunAsType.INHERIT, topLevelMenuItem, page );

        User runAsUser = menuItem.resolveRunAsUser( loggedInUser, true );
        assertEquals( "Run as user is not the default despite 'Default User' run as policy.", defaultRunAsUser, runAsUser );
    }

    private PageEntity createPage( String name, RunAsType runAsType )
    {
        PageEntity page = new PageEntity();
        PageTemplateEntity template = new PageTemplateEntity();
        template.setRunAs( runAsType );
        template.setName( name );
        page.setTemplate( template );
        return page;
    }

    private UserEntity createUser( UserType type, String uid, String key )
    {
        UserEntity user = new UserEntity();
        user.setDeleted( 0 );
        user.setType( type );
        user.setName( uid );
        user.setKey( new UserKey( key ) );
        return user;
    }

    private MenuItemEntity createMenuItem( SiteEntity site, String name, RunAsType runAsType, MenuItemEntity parent, PageEntity page )
    {
        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setSite( site );
        menuItem.setName( name );
        menuItem.setRunAs( runAsType );
        menuItem.setParent( parent );
        menuItem.setPage( page );
        return menuItem;
    }
}
