/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.resource;

import java.util.HashMap;

import com.enonic.cms.core.resource.ResourceKeyResolverForSiteLocalResources;
import org.junit.Test;

import com.enonic.cms.domain.Path;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.SitePath;

import static org.junit.Assert.*;


public class ResourceKeyResolverTest
{
    @Test
    public void testResolveWithPublicHomeSetTildeAtBegining()
    {
        ResourceKeyResolverForSiteLocalResources resolver = new ResourceKeyResolverForSiteLocalResources( "/_public/Drumming Africa" );

        assertEquals( "/_public/Drumming Africa/images/logo.gif", resolver.resolveResourceKey(
            new SitePath( new SiteKey( 1 ), new Path( "/~/images/logo.gif" ), new HashMap<String, String[]>() ) ).toString() );
    }

    @Test
    public void testResolveWithPublicHomeSetTildeInTheMiddle()
    {
        ResourceKeyResolverForSiteLocalResources resolver = new ResourceKeyResolverForSiteLocalResources( "/_public/Drumming Africa" );

        assertEquals( "/_public/Drumming Africa/images/logo.gif", resolver.resolveResourceKey(
            new SitePath( new SiteKey( 1 ), new Path( "/_public/shared/styles/~/images/logo.gif" ),
                          new HashMap<String, String[]>() ) ).toString() );
    }


}
