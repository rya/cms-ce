package com.enonic.vertical.adminweb;

import junit.framework.TestCase;

import com.enonic.cms.core.resource.ResourceKey;

public class ResourceHandlerServletTest
        extends TestCase
{

    private ResourceHandlerServlet servlet;

    @Override
    protected void setUp()
            throws Exception
    {
        servlet = new ResourceHandlerServlet();
    }

    /*
     * Case:
     *      source:      "/libraries/resolvers"
     *      destination: "/sites/stuff"
     *  we move folder "resolvers"
     *      result: "/sites/stuff/resolvers"
     */
    public void testResolvePathForNewFolderComputePath()
    {

        ResourceKey sourceFolderPath = new ResourceKey( "/libraries/resolvers" );
        ResourceKey destinationFolderPath = new ResourceKey( "/sites/stuff" );

        String newPath = servlet.resolvePathForNewFolder( sourceFolderPath, destinationFolderPath );
        assertEquals( "/sites/stuff/resolvers", newPath );
    }

    /*
     * Case:
     *      source:      "/libraries/resolvers"
     *      destination: "/"
     *  we move folder "resolvers"
     *      result: "/resolvers"
     */
    public void testResolvePathForNewFolderRootFolder()
    {

        ResourceKey sourceFolderPath = new ResourceKey( "/libraries/resolvers" );
        ResourceKey destinationFolderPath = new ResourceKey( "/" );

        String newPath = servlet.resolvePathForNewFolder( sourceFolderPath, destinationFolderPath );
        assertEquals( "/resolvers", newPath );
    }

    /*
     * Case:
     *      source:      "resolvers"  - wrong, regexp matcher doesn't match
     *      destination: "/sites/stuff"
     *  we move folder "resolvers"
     *      result: "/sites/stuff"
     */
    public void testResolvePathForNewFolder()
    {

        ResourceKey sourceFolderPath = new ResourceKey( "resolvers" );
        ResourceKey destinationFolderPath = new ResourceKey( "/sites/stuff" );

        String newPath = servlet.resolvePathForNewFolder( sourceFolderPath, destinationFolderPath );
        assertEquals( "/sites/stuff", newPath );
    }

}
