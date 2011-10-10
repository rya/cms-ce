/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.resolver;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.core.structure.SiteEntity;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 9/26/11
 * Time: 1:16 PM
 */
public class CacheResolverValueServiceImplTest
{


    @Before
    public void setUp()
    {

    }

    @Test
    public void setSetAndGetValuesInCache()
    {
        CacheResolverValueServiceImpl service = new CacheResolverValueServiceImpl();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute( "deviceClassKey", "test" );
        SiteEntity site = new SiteEntity();

        ResolverContext context = new ResolverContext( request, site );

        final String setDeviceClass = "testDeviceClass";
        final String cacheKey = "cacheKey1";

        service.setCachedResolverValue( context, setDeviceClass, cacheKey );

        String resolvedValue = service.getCachedResolverValue( context, cacheKey );

        assertEquals( setDeviceClass, resolvedValue );

        service.clearCachedResolverValue( context, cacheKey );

        assertNull( "Cached value should have been removed", service.getCachedResolverValue( context, cacheKey ) );

        assertNull( "Cached value should have been removed from session", request.getSession().getAttribute( cacheKey ) );
    }
}
