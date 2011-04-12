/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal;

import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import org.junit.Test;

import com.enonic.cms.domain.InvalidKeyException;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.core.structure.portlet.PortletKey;

import static org.junit.Assert.*;

public class PortalInstanceKeyResolverTest
{
    private PortalInstanceKeyResolver resolver = new PortalInstanceKeyResolver();

    @Test
    public void testPortalInstanceKeyResolver()
    {
        PortalInstanceKey key1 = resolver.resolvePortalInstanceKey( "WINDOW:43:191" );
        PortalInstanceKey key2 = resolver.resolvePortalInstanceKey( "PAGE:812" );
        PortalInstanceKey key4 = resolver.resolvePortalInstanceKey( "SITE:11" );

        assertEquals( new MenuItemKey( 43 ), key1.getMenuItemKey() );
        assertEquals( new PortletKey( 191 ), key1.getPortletKey() );
        assertNull( key1.getSiteKey() );

        assertEquals( new MenuItemKey( 812 ), key2.getMenuItemKey() );
        assertNull( key2.getPortletKey() );
        assertNull( key2.getSiteKey() );

        assertNull( key4.getMenuItemKey() );
        assertNull( key4.getPortletKey() );
        assertEquals( new SiteKey( 11 ), key4.getSiteKey() );
    }

    @Test
    public void testPortalInstanceKeyResolverErrors()
    {
        try
        {
            resolver.resolvePortalInstanceKey( null );
            fail( "null, is not a valid instance key." );
        }
        catch ( IllegalArgumentException e )
        {
            assertEquals( "No instanceKey provided, input is empty.", e.getMessage() );
        }

        try
        {
            resolver.resolvePortalInstanceKey( "" );
            fail( "'', is not a valid instance key." );
        }
        catch ( IllegalArgumentException e )
        {
            assertEquals( "No instanceKey provided, input is empty.", e.getMessage() );
        }

        try
        {
            resolver.resolvePortalInstanceKey( "WINDOWS:81:81" );
            fail( "'WINDOWS:81:81', is not a valid instance key." );
        }
        catch ( IllegalArgumentException e )
        {
            assertEquals( "No valid instance key context in key: WINDOWS:81:81", e.getMessage() );
        }

        try
        {
            resolver.resolvePortalInstanceKey( "WINDO:81:81" );
            fail( "'WINDO:81:81', is not a valid instance key." );
        }
        catch ( IllegalArgumentException e )
        {
            assertEquals( "No valid instance key context in key: WINDO:81:81", e.getMessage() );
        }

        try
        {
            resolver.resolvePortalInstanceKey( "WINDOW:81:81:81" );
            fail( "'WINDOW:81:81:81', is not a valid instance key." );
        }
        catch ( IllegalArgumentException e )
        {
            assertEquals( "WINDOW instance key has wrong number of keys: 3", e.getMessage() );
        }

        try
        {
            resolver.resolvePortalInstanceKey( "PAGE:1:1" );
            fail( "'PAGE:1:1', is not a valid instance key." );
        }
        catch ( IllegalArgumentException e )
        {
            assertEquals( "PAGE instance key has wrong number of keys: 2", e.getMessage() );
        }

        try
        {
            resolver.resolvePortalInstanceKey( "SITE:0:1:2:3:4" );
            fail( "'SITE:0:1:2:3:4', is not a valid instance key." );
        }
        catch ( IllegalArgumentException e )
        {
            assertEquals( "SITE instance key has wrong number of keys: 5", e.getMessage() );
        }

        try
        {
            resolver.resolvePortalInstanceKey( "SITE:a" );
            fail( "'SITE:a' is not a valid instance key." );
        }
        catch ( InvalidKeyException e )
        {
            // Success!
        }

        try
        {
            resolver.resolvePortalInstanceKey( "WINDOW:-2:14" );
            fail( "'WINDOW:-2:14' is not a valid instance key." );
        }
        catch ( InvalidKeyException e )
        {
            // Success!
        }

        try
        {
            resolver.resolvePortalInstanceKey( "WINDOW:PAGE:1" );
            fail( "'WINDOW:PAGE' is not a valid instance key." );
        }
        catch ( InvalidKeyException e )
        {
            // Success!
        }

    }
}
