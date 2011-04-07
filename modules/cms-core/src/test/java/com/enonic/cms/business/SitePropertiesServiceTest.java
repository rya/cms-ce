/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;

import com.enonic.vertical.VerticalProperties;

import com.enonic.cms.core.home.HomeService;

import com.enonic.cms.domain.SiteKey;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;

/**
 * Author: rmy, 25/02/2009
 */
public class SitePropertiesServiceTest
{
    private static final int TEST_SITE_KEY_ID = 0;

    private static final String BOOLEAN_TRUE = "boolean.true";

    private static final String BOOLEAN_FALSE = "boolean.false";

    private static final String BOOLEAN_TRUE_WHITESPACE = "boolean.true.whitespaces";

    private static final String BOOLEAN_FALSE_WHITESPACE = "boolean.false.whitespaces";

    private static final String INTEGER_0 = "integer.0";

    private static final Integer INTEGER_POSITIVE_VALUE = 100;

    private static final String INTEGER_POSITIVE_WHITESPACES = "integer.positive.whitespaces";

    private static final String INTEGER_POSITIVE = "integer.positive";

    private static final String TEXT_PROPERTY = "text.property";

    private static final String NONEXISTING_PROPERTY = "apples.and.fish";

    private static final String EMPTY_PROPERTY = "empty.property";

    private static final String EMPTY_PROPERTY_WHITESPACES = "empty.property.whitespaces";

    private SitePropertiesServiceImpl sitePropertiesService;

    private HomeService homeService = createMock( HomeService.class );

    private ResourceLoader resourceLoader = createMock( ResourceLoader.class );

    private SiteKey siteKey = new SiteKey( TEST_SITE_KEY_ID );

    @Before
    public void setUp()
        throws Exception
    {
        setupVerticalProperties();
        mockAndLoadTestProperties();
        setupSitePropertiesService();
    }

    private void setupVerticalProperties()
    {
        VerticalProperties verticalProperties = new VerticalProperties();

        Properties properties = new Properties();
        properties.setProperty( "cms.url.characterEncoding", "UTF-8" );

        verticalProperties.setProperties( properties );
    }

    private void setupSitePropertiesService()
        throws Exception
    {
        sitePropertiesService = new SitePropertiesServiceImpl();
        sitePropertiesService.setHomeService( homeService );
        sitePropertiesService.setResourceLoader( resourceLoader );
        sitePropertiesService.afterPropertiesSet();
    }

    private void mockAndLoadTestProperties()
    {
        try
        {
            Resource homeResource = new UrlResource( "http:mockURL" );
            expect( homeService.getHomeDir() ).andReturn( homeResource );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            fail( "Could not mock homeService.getHomeDir" );
        }
        replay( homeService );

        expect( resourceLoader.getResource( isA( String.class ) ) ).andReturn( getLocalTestDefaultPropertyResouce() );
        expect( resourceLoader.getResource( isA( String.class ) ) ).andReturn( getLocalTestSitePropertyResouce() );
        replay( resourceLoader );
    }

    private Resource getLocalTestSitePropertyResouce()
    {
        ResourceLoader testResourceLoader = new FileSystemResourceLoader();
        Resource testResource = testResourceLoader.getResource( "classpath:com/enonic/cms/business/test.site.properties" );

        if ( !testResource.exists() )
        {
            fail( "Could not load test resource: " + testResource );
        }
        return testResource;
    }

    private Resource getLocalTestDefaultPropertyResouce()
    {
        ResourceLoader testResourceLoader = new FileSystemResourceLoader();
        Resource testResource = testResourceLoader.getResource( "classpath:com/enonic/cms/business/test.default.site.properties" );

        if ( !testResource.exists() )
        {
            fail( "Could not load test resource: " + testResource );
        }
        return testResource;
    }

    /* Shorthands */

    private String getProp( String propertyName )
    {
        return sitePropertiesService.getProperty( propertyName, siteKey );
    }

    private Boolean getBooleanProp( String propertyName )
    {
        return sitePropertiesService.getPropertyAsBoolean( propertyName, siteKey );
    }

    private Integer getIntegerProp( String propertyName )
    {
        return sitePropertiesService.getPropertyAsInteger( propertyName, siteKey );
    }

    @Test
    public void testGetPropertyAsInteger()
    {
        assertEquals( new Integer( 0 ), getIntegerProp( INTEGER_0 ) );
        assertEquals( "INTEGER_POSITIVE should be " + INTEGER_POSITIVE_VALUE, INTEGER_POSITIVE_VALUE, getIntegerProp( INTEGER_POSITIVE ) );
        assertEquals( "INTEGER_POSITIVE_WHITESPACES should be " + INTEGER_POSITIVE_VALUE, INTEGER_POSITIVE_VALUE,
                      getIntegerProp( INTEGER_POSITIVE_WHITESPACES ) );
    }

    @Test
    public void testBooleanPropertiesGetter()
    {
        assertTrue( "BOOLEAN_TRUE expected to be true", getBooleanProp( BOOLEAN_TRUE ) );
        assertFalse( "BOOLEAN_FALSE expected to be false", getBooleanProp( BOOLEAN_FALSE ) );
        assertTrue( "BOOLEAN_TRUE_WHITESPACE expected to be true", getBooleanProp( BOOLEAN_TRUE_WHITESPACE ) );
        assertFalse( "BOOLEAN_FALSE_WHITESPACE expected to be false", getBooleanProp( BOOLEAN_FALSE_WHITESPACE ) );
    }

    @Test
    public void testNonExistingProperty()
    {
        assertNull( "NON_EXISTING_PROPERTY expected to be null", getProp( NONEXISTING_PROPERTY ) );
    }

    @Test
    public void testNonExistingBooleanProperty()
    {
        assertFalse( "NON_EXISTING_PROPERTY expected to be false", getBooleanProp( NONEXISTING_PROPERTY ) );
    }

    @Test
    public void testNonExistingIntegerProperty()
    {
        assertNull( "NON_EXISTING_PROPERTY expected to be null", getIntegerProp( NONEXISTING_PROPERTY ) );
    }

    @Test
    public void testEmptyProperty()
    {
        assertNull( "EMPTY_PROPERTY expected to be null", getProp( EMPTY_PROPERTY ) );
        assertNull( "EMPTY_PROPERTY_WHITESPACES expected to be null", getProp( EMPTY_PROPERTY_WHITESPACES ) );
    }

    @Test
    public void testEmptyBooleanProperty()
    {
        assertFalse( "EMPTY_PROPERTY expected to be false", getBooleanProp( EMPTY_PROPERTY ) );
        assertFalse( "EMPTY_PROPERTY_WHITESPACES expected to be false", getBooleanProp( EMPTY_PROPERTY_WHITESPACES ) );
    }

    @Test
    public void testEmptyIntegerProperty()
    {
        assertNull( "EMPTY_PROPERTY expected to be null", getIntegerProp( EMPTY_PROPERTY ) );
        assertNull( "EMPTY_PROPERTY_WHITESPACES expected to be null", getIntegerProp( EMPTY_PROPERTY_WHITESPACES ) );
    }

    @Test
    public void testInvalidBooleanProperty()
    {
        assertFalse( "Invalid boolean-value, expected to be false", getBooleanProp( TEXT_PROPERTY ) );
    }

    @Test
    public void testSiteOverridesDefaultProperty()
    {
        String testProp = getProp( TEXT_PROPERTY );
        assertEquals( "text.property expected to be 'bar', not 'foo'", "bar", testProp );
    }

    @Test(expected = NumberFormatException.class)
    public void testInvalidIntegerProperty()
    {
        getIntegerProp( BOOLEAN_TRUE );
    }

    @After
    public void verifyAndTeardown()
    {
        verify( homeService );
        verify( resourceLoader );
    }

}
