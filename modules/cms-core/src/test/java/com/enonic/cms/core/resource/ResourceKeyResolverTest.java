/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource;

import java.util.HashMap;

import org.junit.Test;

import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.Path;
import com.enonic.cms.core.SitePath;

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
