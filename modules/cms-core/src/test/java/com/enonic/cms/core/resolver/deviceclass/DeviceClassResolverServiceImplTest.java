/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver.deviceclass;

import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.core.resolver.BaseResolverTest;
import com.enonic.cms.core.resolver.ScriptResolverService;
import com.enonic.cms.core.resolver.deviceclass.DeviceClassResolverServiceImpl;
import com.enonic.cms.core.resolver.deviceclass.DeviceClassXsltScriptResolver;

import com.enonic.cms.business.SpecialCharacterTestStrings;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.resolver.ResolverContext;
import com.enonic.cms.domain.resolver.ScriptResolverResult;
import com.enonic.cms.domain.resource.ResourceFile;
import com.enonic.cms.domain.resource.ResourceKey;
import com.enonic.cms.domain.structure.SiteEntity;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.*;

/**
 * Created by rmy - Date: Apr 2, 2009
 */
public class DeviceClassResolverServiceImplTest
    extends BaseResolverTest
{
    private DeviceClassResolverServiceImpl deviceClassResolverService;

    private ResourceFile resourceFileMock;

    private ScriptResolverService deviceClassResolverServiceMock;

    private static final String DEVICE_CLASS_SESSION_VALUE = "fromSession";

    private static final SiteKey siteKey = new SiteKey( 0 );

    private static final String RESOLVED_DEVICE_NAME = "resolvedDeviceName";

    private static final String NASTY_COOKIE_VALUE = SpecialCharacterTestStrings.NORWEGIAN;

    private static final Calendar CALENDER_INSTANSE = Calendar.getInstance();

    @Before
    public void setUp()
    {
        deviceClassResolverServiceMock = createMock( ScriptResolverService.class );

        deviceClassResolverService = new DeviceClassResolverServiceImpl();
        deviceClassResolverService.setDeviceClassScriptResolver( deviceClassResolverServiceMock );

        deviceClassResolverService.setResourceService( resourceServiceMock );
        deviceClassResolverService.setForceResolverValueService( forcedResolverValueService );
        deviceClassResolverService.setCacheResolverValueService( cacheResolverValueService );
    }

    @Test
    public void testForcedDeviceClass()
    {
        setForcedDeviceClassScenario();

        // Session variable there, but cookie should have precedence  
        setUpCachedValue( DEVICE_CLASS_SESSION_VALUE );

        ResolverContext context = new ResolverContext( request, createSite( true ) );
        String deviceClass = deviceClassResolverService.getDeviceClass( context );
        assertEquals( "Cookie-injected deviceClass should be used", NASTY_COOKIE_VALUE, deviceClass );
    }

    @Test
    public void resolveDevice()
    {
        setNoForceNoCacheScenario();

        setUpResolveDeviceScenario( true, true );

        ResolverContext context = new ResolverContext( request, createSite( true ) );
        String deviceClass = deviceClassResolverService.getDeviceClass( context );

        assertEquals( RESOLVED_DEVICE_NAME, deviceClass );
    }

    @Test
    public void testNotForcedNotCachedNoScript()
    {

        setNoForceNoCacheScenario();

        setUpResolveDeviceScenario( false, false );

        SiteEntity site = createSite( false );

        ResolverContext context = new ResolverContext( request, site );
        String deviceClass = deviceClassResolverService.getDeviceClass( context );

        assertEquals( null, deviceClass );
    }

    @After
    public void verifyDeviceClassResolverServiceMock()
    {
        //verify(deviceClassResolverServiceMock);
        // verify(resourceServiceMock);

    }

    private SiteEntity createSite( boolean hasDeviceClassResolverScript )
    {
        SiteEntity site = new SiteEntity();
        site.setKey( siteKey.toInt() );
        site.setName( "name" );

        if ( hasDeviceClassResolverScript )
        {
            site.setDeviceClassResolver( new ResourceKey( "deviceClassResolverScript" ) );
        }

        return site;
    }

    private void setNoForceNoCacheScenario()
    {
        setUpForcedResolverValue( null );
        setUpCachedValue( null );
    }

    private void setForcedDeviceClassScenario()
    {

        setUpForcedResolverValue( NASTY_COOKIE_VALUE );

        setUpResolveDeviceClassMock( true, false );

        replay( deviceClassResolverServiceMock );
        replay( resourceServiceMock );
    }

    private void setUpResolveDeviceScenario( boolean hasResolverScript, boolean timeStampChanged )
    {
        setUpGetResourceFileMock( hasResolverScript, timeStampChanged );

        setUpResolveDeviceClassMock( hasResolverScript, timeStampChanged );

        replay( deviceClassResolverServiceMock );
        replay( resourceServiceMock );
    }


    private void setUpResolveDeviceClassMock( boolean hasResolverScript, boolean timeStampChanged )
    {
        if ( timeStampChanged && hasResolverScript )
        {
            expect( deviceClassResolverServiceMock.resolveValue( isA( ResolverContext.class ), isA( ResourceFile.class ) ) ).andReturn(
                createScriptResolverReturnValue( RESOLVED_DEVICE_NAME ) ).times( 1 );
        }
    }

    private ScriptResolverResult createScriptResolverReturnValue( String deviceClass )
    {
        ScriptResolverResult result = new ScriptResolverResult();
        result.getResolverReturnValues().put( DeviceClassXsltScriptResolver.DEVICE_CLASS_RETURN_VALUE_KEY, deviceClass );

        return result;
    }

    private void setUpGetResourceFileMock( boolean hasResolverScript, boolean timeStampChanged )
    {
        resourceFileMock = hasResolverScript ? createMock( ResourceFile.class ) : null;

        if ( hasResolverScript )
        {
            expect( resourceServiceMock.getResourceFile( isA( ResourceKey.class ) ) ).andReturn( resourceFileMock ).times( 1 );
            expect( resourceFileMock.getLastModified() ).andReturn(
                timeStampChanged ? Calendar.getInstance() : CALENDER_INSTANSE ).anyTimes();
            replay( resourceFileMock );
        }
    }


}
