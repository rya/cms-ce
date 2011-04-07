/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver.locale;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.core.resolver.BaseResolverTest;
import com.enonic.cms.core.resolver.locale.LocaleResolverServiceImpl;

import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.domain.resolver.ResolverContext;
import com.enonic.cms.domain.resource.ResourceKey;
import com.enonic.cms.domain.structure.SiteEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;

import static org.junit.Assert.*;

/**
 * Created by rmy - Date: Apr 28, 2009
 */
public class LocaleResolverServiceImplTest
    extends BaseResolverTest
{

    LocaleResolverServiceImpl localeResolverService;

    @Before
    public void setUp()
    {
        localeResolverService = new LocaleResolverServiceImpl();
        localeResolverService.setForceResolverValueService( forcedResolverValueService );
    }

    @Test
    public void testNoDefaultResourceExists()
    {
        setUpForcedResolverValue( null );

        ResolverContext context = new ResolverContext( request, createSite( "en-us", false ), createMenuItem( "no" ), null );

        Locale locale = localeResolverService.getLocale( context );
        assertTrue( "locale should be null since no default resource present", locale == null );

    }

    @Test
    public void testOrder()
    {
        setUpForcedResolverValue( null );

        ResolverContext context = new ResolverContext( request, createSite( "en-us", true ), createMenuItem( "no" ), null );

        Locale locale = localeResolverService.getLocale( context );
        assertEquals( "no", locale.getLanguage() );

        context = new ResolverContext( request, createSite( "en", true ) );

        locale = localeResolverService.getLocale( context );
        assertEquals( "en", locale.getLanguage() );

        context = new ResolverContext( request, createSite( "en", true ), createMenuItem( "no" ), createLanguage( "sw" ) );
        locale = localeResolverService.getLocale( context );
        assertEquals( "sw", locale.getLanguage() );
    }

    @Test
    public void testForcedValueOverride()
    {
        SiteEntity site = createSite( "no", true );

        setUpForcedResolverValue( "en" );

        ResolverContext context = new ResolverContext( request, site, createMenuItem( "no" ), createLanguage( "sw" ) );
        Locale locale = localeResolverService.getLocale( context );
        assertEquals( "en", locale.getLanguage() );
    }

    @Test
    public void testEmptyContentLanguage()
    {

        SiteEntity site = createSite( "no", true );

        setUpForcedResolverValue( null );

        ResolverContext context = new ResolverContext( request, site, createMenuItem( "en" ), new LanguageEntity() );
        Locale locale = localeResolverService.getLocale( context );
        assertEquals( "Should use menuItem language since contenLanguage doesnt contain code", "en", locale.getLanguage() );
    }

    private MenuItemEntity createMenuItem( String languageCode )
    {
        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setLanguage( createLanguage( languageCode ) );
        return menuItem;
    }

    private LanguageEntity createLanguage( String languageCode )
    {
        LanguageEntity language = new LanguageEntity();
        language.setCode( languageCode );
        return language;
    }

    public SiteEntity createSite( String language, boolean hasDefaultResource )
    {
        SiteEntity site = new SiteEntity();
        site.setKey( 0 );
        site.setLanguage( createLanguage( language ) );

        if ( hasDefaultResource )
        {
            site.setDefaultLocalizationResource( new ResourceKey( "test" ) );
        }

        return site;
    }
}

