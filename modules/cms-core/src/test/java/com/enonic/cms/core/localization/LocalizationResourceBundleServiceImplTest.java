/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.localization;

import java.util.Locale;
import java.util.Properties;

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.framework.cache.CacheFacade;

import com.enonic.cms.core.localization.resource.LocalizationResourceBundleServiceImpl;
import com.enonic.cms.core.resource.ResourceService;

import com.enonic.cms.domain.resource.ResourceKey;
import com.enonic.cms.domain.structure.SiteEntity;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.*;

/**
 * Created by rmy - Date: Apr 23, 2009
 */
public class LocalizationResourceBundleServiceImplTest
    extends LocalizationResourceBundleServiceImpl
{
    private LocalizationResourceBundleServiceImpl resourceBundleService;

    private ResourceService resourceService;

    private CacheFacade propertiesCache;


    @Before
    public void setUp()
    {
        resourceBundleService = new LocalizationResourceBundleServiceImpl();

        propertiesCache = createMock( CacheFacade.class );

        resourceBundleService.setPropertiesCache( propertiesCache );

        resourceService = createMock( ResourceService.class );

        resourceBundleService.setLocalizationResourceFileService( resourceService );

    }

    @Test
    public void testCache()
    {
        setUpFetchFromCache( new Properties() );
        replay( propertiesCache );

        SiteEntity site = LocalizationTestUtils.createSite( "phrases.properties" );
        Locale locale = new Locale( "no" );

        LocalizationResourceBundle resourceBundle = resourceBundleService.getResourceBundle( site, locale );

        assertNotNull( "Should fetch empty properties from cache and create ResourceBundle", resourceBundle );
    }

    @Test
    public void testNoCache()
    {
        setUpFetchFromCache( null );
        setUpPutInCache();
        replay( propertiesCache );

        setUpResourceService();

        SiteEntity site = LocalizationTestUtils.createSite( "phrases.properties" );
        Locale locale = new Locale( "no" );

        LocalizationResourceBundle resourceBundle = resourceBundleService.getResourceBundle( site, locale );

        assertNotNull( "Should fetch empty properties from cache and create ResourceBundle", resourceBundle );
    }


    private void setUpResourceService()
    {
        expect( resourceService.getResourceFile( isA( ResourceKey.class ) ) ).andReturn( null ).anyTimes();
        replay( resourceService );
    }

    private void setUpFetchFromCache( Properties properties )
    {
        expect( propertiesCache.get( isA( String.class ), isA( String.class ) ) ).andReturn( properties ).anyTimes();

    }

    private void setUpPutInCache()
    {
        propertiesCache.put( isA( String.class ), isA( String.class ), isA( Properties.class ) );
        EasyMock.expectLastCall().anyTimes();
    }

}
