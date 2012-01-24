package com.enonic.cms.core.webdav;

import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.core.vhost.VirtualHostHelper;

public class DavLocatorFactoryImplTest
{
    private MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
    private DavLocatorFactoryImpl davLocatorFactory = new DavLocatorFactoryImpl();

    @Before
    public void before()
    {
        ServletRequestAccessor.setRequest( httpServletRequest );
    }

    @Test
    public void testCreateResourceLocator_dav()
    {
        VirtualHostHelper.setBasePath( httpServletRequest, "/dav" );

        final DavResourceLocator resourceLocator = davLocatorFactory.createResourceLocator( "http://localhost:8080", "/dav/config" );

        Assert.assertEquals( "http://localhost:8080/dav", resourceLocator.getPrefix());
        Assert.assertEquals( "/config", resourceLocator.getResourcePath() );
    }

    @Test
    public void testCreateResourceLocator_gfdav()
    {
        VirtualHostHelper.setBasePath( httpServletRequest, "/gfdav" );

        final DavResourceLocator resourceLocator = davLocatorFactory.createResourceLocator( "http://localhost:8080", "/gfdav/config" );

        Assert.assertEquals( "http://localhost:8080/gfdav", resourceLocator.getPrefix());
        // before D-01768 it was "/gfdav/config" . must be  "/config"
        Assert.assertEquals( "/config", resourceLocator.getResourcePath() );
    }

    @Test
    public void testCreateResourceLocator_gfdav_dav()
    {
        VirtualHostHelper.setBasePath( httpServletRequest, "/gfdav" );

        final DavResourceLocator resourceLocator = davLocatorFactory.createResourceLocator( "http://localhost:8080", "/dav/config" );

        Assert.assertEquals( "http://localhost:8080/gfdav", resourceLocator.getPrefix());
        Assert.assertEquals( "/config", resourceLocator.getResourcePath() );
    }


}
