/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.localization;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import com.enonic.cms.core.structure.SiteEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.core.localization.resource.LocalizationResourceBundleService;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.core.resource.ResourceKey;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;

/**
 * Created by rmy - Date: Apr 22, 2009
 */
public class LocalizationServiceImplTest
{

    private LocalizationServiceImpl localizationService;

    private LocalizationResourceBundleService resourceBundleServiceMock;

    private final SiteKey siteKey = new SiteKey( 0 );

    public static List<String> supportedPhrases = new ArrayList();

    private static final String SITE_LOCALIZATION_RESOURCE_KEY = "test";

    public static final String LOCALIZED_ADDON = "Localized";

    @Before
    public void setUp()
    {
        localizationService = new LocalizationServiceImpl();

        resourceBundleServiceMock = createMock( LocalizationResourceBundleService.class );

        localizationService.setLocalizationResourceBundleService( resourceBundleServiceMock );
    }

    @Test
    public void getLocalizedPhrase()
    {
        createResourceBundleExpectance( getNorwegianTestResourceBundle() );

        setUpSupportedPhrases();

        for ( String phrase : supportedPhrases )
        {
            String localizedPhrase = localizationService.getLocalizedPhrase( createSite(), phrase, new Locale( "no" ) );

            assertEquals( phrase + LOCALIZED_ADDON + "_no", localizedPhrase );
        }
    }

    @After
    public void tearDown()
    {
        verify( resourceBundleServiceMock );
    }

    private void createResourceBundleExpectance( LocalizationResourceBundle resouceBundle )
    {
        expect( resourceBundleServiceMock.getResourceBundle( isA( SiteEntity.class ), isA( Locale.class ) ) ).andReturn(
            resouceBundle ).anyTimes();
        replay( resourceBundleServiceMock );

    }


    private void setUpSupportedPhrases()
    {
        supportedPhrases.add( "test" );
        supportedPhrases.add( "ost" );
        supportedPhrases.add( "fisk" );
    }

    private SiteEntity createSite()
    {
        SiteEntity site = new SiteEntity();
        site.setKey( siteKey.toInt() );
        site.setDefaultLocalizationResource( new ResourceKey( SITE_LOCALIZATION_RESOURCE_KEY ) );

        return site;
    }


    private LocalizationResourceBundle getNorwegianTestResourceBundle()
    {
        Properties properties = new Properties();
        properties.put( "test", "testLocalized_no" );
        properties.put( "ost", "ostLocalized_no" );
        properties.put( "fisk", "fiskLocalized_no" );

        LocalizationResourceBundle bundle = new LocalizationResourceBundle( properties );

        return bundle;
    }

}
