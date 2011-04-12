/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.portal.rendering;

import java.util.Locale;

import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.portal.rendering.PageCacheKey;
import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;


public class PageCacheKeyEqualsTest
    extends AbstractEqualsTest
{

    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        return createPageCacheKey( "ABC", "1", "q", "a", "no" );
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        return new Object[]{createPageCacheKey( "ABC", "1", "x", "a", "no" ), createPageCacheKey( "ABC", "2", "q", "a", "no" ),
            createPageCacheKey( "XXX", "2", "q", "a", "no" ), createPageCacheKey( "ABC", "1", "q", "b", "no" ),
            createPageCacheKey( "ABC", "1", "q", null, "no" )};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        return createPageCacheKey( "ABC", "1", "q", "a", "no" );
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        return createPageCacheKey( "ABC", "1", "q", "a", "no" );
    }

    private PageCacheKey createPageCacheKey( String userKey, String menuItemKey, String queryString, String deviceClass, String locale )
    {
        PageCacheKey key = new PageCacheKey();
        key.setUserKey( userKey );
        key.setMenuItemKey( new MenuItemKey( menuItemKey ) );
        key.setQueryString( queryString );
        key.setDeviceClass( deviceClass );
        key.setLocale( new Locale( locale ) );
        return key;
    }
}
