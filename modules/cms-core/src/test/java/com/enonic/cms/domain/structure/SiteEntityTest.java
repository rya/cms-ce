/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.structure;

import java.util.LinkedHashMap;
import java.util.Map;

import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import junit.framework.TestCase;

import com.enonic.cms.domain.CaseInsensitiveString;
import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.domain.Path;

public class SiteEntityTest
    extends TestCase
{

    private MenuItemEntity mi_forside;

    private MenuItemEntity mi_nyheter;

    private MenuItemEntity mi_innenriks;

    public void testGetMenuItemByPath()
    {

        SiteEntity site = createSiteFixture1();

        MenuItemEntity actual;

        actual = site.resolveMenuItemByPath( new Path( "" ) );
        assertEquals( mi_forside, actual );

        actual = site.resolveMenuItemByPath( new Path( "/" ) );
        assertEquals( mi_forside, actual );

        actual = site.resolveMenuItemByPath( new Path( "/forside/" ) );
        assertEquals( mi_forside, actual );

        actual = site.resolveMenuItemByPath( new Path( "/forside/nyheter" ) );
        assertEquals( mi_nyheter, actual );

        actual = site.resolveMenuItemByPath( new Path( "/forside/nyheter/innenriks/" ) );
        assertEquals( mi_innenriks, actual );
    }

    private SiteEntity createSiteFixture1()
    {

        SiteEntity siteEntity = new SiteEntity();
        siteEntity.setKey( 1 );
        siteEntity.setLanguage( createLanguage( "no", "Norwegian" ) );
        mi_forside = createMenuItem( "1", "forside", null );
        mi_nyheter = createMenuItem( "2", "nyheter", mi_forside );
        mi_innenriks = createMenuItem( "3", "innenriks", mi_nyheter );
        siteEntity.setFirstPage( mi_forside );

        Map<CaseInsensitiveString, MenuItemEntity> topMenuItems = new LinkedHashMap<CaseInsensitiveString, MenuItemEntity>();
        topMenuItems.put( new CaseInsensitiveString( mi_forside.getName() ), mi_forside );
        siteEntity.setTopMenuItems( topMenuItems );
        return siteEntity;
    }

    private MenuItemEntity createMenuItem( String key, String name, MenuItemEntity parent )
    {
        MenuItemEntity mi = new MenuItemEntity();
        mi.setKey( Integer.parseInt( key ) );
        mi.setName( name );
        if ( parent != null )
        {
            mi.setParent( parent );
            parent.addChild( mi );
        }
        return mi;
    }

    private LanguageEntity createLanguage( String code, String description )
    {
        LanguageEntity language = new LanguageEntity();
        language.setCode( code );
        language.setDescription( description );
        return language;
    }
}

