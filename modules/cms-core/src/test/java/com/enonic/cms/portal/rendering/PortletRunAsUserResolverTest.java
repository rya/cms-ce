/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering;

import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.domain.security.user.User;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserType;
import com.enonic.cms.domain.structure.RunAsType;
import com.enonic.cms.domain.structure.SiteEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;
import com.enonic.cms.domain.structure.portlet.PortletEntity;

import static org.junit.Assert.*;

/**
 *
 */
public class PortletRunAsUserResolverTest
{

    private SiteEntity site;

    private UserEntity defaultRunAsUser;

    @Before
    public void setUp()
    {
        defaultRunAsUser = createUser( UserType.NORMAL, "virtualBoss" );

        site = new SiteEntity();
        site.setDefaultRunAsUser( defaultRunAsUser );
    }

    @Test
    public void testResolveRunAsAnonymousUser()
    {
        UserEntity user = createUser( UserType.ANONYMOUS, "anonymous" );
        User runAsUser = PortletRunAsUserResolver.resolveRunAsUser( null, user, null );
        assertTrue( "The run as user should be anonymous when input is anonymous", runAsUser.isAnonymous() );
    }

    @Test
    public void testResolveRunAsUserNoInherit()
    {
        UserEntity loggedInUser = createUser( UserType.NORMAL, "spirrevipp" );

        PortletEntity portlet = createPortlet( site, RunAsType.PERSONALIZED );

        User runAsUser = PortletRunAsUserResolver.resolveRunAsUser( portlet, loggedInUser, null );
        assertEquals( "Logged in user is not run as user, despite 'Personalized' run as policy", loggedInUser, runAsUser );

        portlet.setRunAs( RunAsType.DEFAULT_USER );
        runAsUser = PortletRunAsUserResolver.resolveRunAsUser( portlet, loggedInUser, null );
        assertEquals( "Run as user is not the default despite 'Default User' run as policy.", defaultRunAsUser, runAsUser );
    }

    @Test
    public void testResolveRunAsUserInheritFromMenu()
    {
        PortletEntity portlet = createPortlet( site, RunAsType.INHERIT );
        UserEntity loggedInUser = createUser( UserType.NORMAL, "spirrevipp" );
        MenuItemEntity menuItem = createMenuItem( site, "Opprøret i Tibet", RunAsType.PERSONALIZED, null );

        User runAsUser = PortletRunAsUserResolver.resolveRunAsUser( portlet, loggedInUser, menuItem );
        assertEquals( "Logged in user is not run as user, despite 'Personalized' run as policy", loggedInUser, runAsUser );

        menuItem.setRunAs( RunAsType.DEFAULT_USER );
        runAsUser = PortletRunAsUserResolver.resolveRunAsUser( portlet, loggedInUser, menuItem );
        assertEquals( "Run as user is not the default despite 'Default User' run as policy.", defaultRunAsUser, runAsUser );
    }

    @Test
    public void testResolveRunAsUserInheritFromTopLevelMenu()
    {
        PortletEntity portlet = createPortlet( site, RunAsType.INHERIT );
        UserEntity loggedInUser = createUser( UserType.NORMAL, "spirrevipp" );
        MenuItemEntity topLevelMenuItem = createMenuItem( site, "Nyheter", RunAsType.PERSONALIZED, null );
        MenuItemEntity menuItem = createMenuItem( site, "Utenriks", RunAsType.INHERIT, topLevelMenuItem );

        User runAsUser = PortletRunAsUserResolver.resolveRunAsUser( portlet, loggedInUser, menuItem );
        assertEquals( "Logged in user is not run as user, despite 'Personalized' run as policy", loggedInUser, runAsUser );

        topLevelMenuItem.setRunAs( RunAsType.DEFAULT_USER );
        runAsUser = PortletRunAsUserResolver.resolveRunAsUser( portlet, loggedInUser, menuItem );
        assertEquals( "Run as user is not the default despite 'Default User' run as policy.", defaultRunAsUser, runAsUser );
    }

    @Test
    public void testResolveRunAsUserInheritFromSite()
    {
        PortletEntity portlet = createPortlet( site, RunAsType.INHERIT );
        UserEntity loggedInUser = createUser( UserType.NORMAL, "spirrevipp" );
        MenuItemEntity topLevelMenuItem = createMenuItem( site, "Nyheter", RunAsType.INHERIT, null );
        MenuItemEntity menuItem = createMenuItem( site, "Utenriks", RunAsType.INHERIT, topLevelMenuItem );

        User runAsUser = PortletRunAsUserResolver.resolveRunAsUser( portlet, loggedInUser, menuItem );
        assertEquals( "Run as user is not the default despite 'Default User' run as policy.", defaultRunAsUser, runAsUser );
    }

    private UserEntity createUser( UserType type, String uid )
    {
        UserEntity user = new UserEntity();
        user.setDeleted( 0 );
        user.setType( type );
        user.setName( uid );
        return user;
    }

    private PortletEntity createPortlet( SiteEntity site, RunAsType runAsType )
    {
        PortletEntity portlet = new PortletEntity();
        portlet.setSite( site );
        portlet.setRunAs( runAsType );
        return portlet;
    }

    private MenuItemEntity createMenuItem( SiteEntity site, String name, RunAsType runAsType, MenuItemEntity parent )
    {
        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setSite( site );
        menuItem.setName( name );
        menuItem.setRunAs( runAsType );
        menuItem.setParent( parent );
        return menuItem;
    }
}
