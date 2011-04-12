/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.resource;

import com.enonic.cms.core.resource.FileResourceName;
import org.junit.Assert;
import org.junit.Test;

public class FileResourceNameTest
{
    @Test
    public void testName()
    {
        FileResourceName name = new FileResourceName( null );
        Assert.assertEquals( "", name.getName() );

        name = new FileResourceName( "" );
        Assert.assertEquals( "", name.getName() );

        name = new FileResourceName( "/" );
        Assert.assertEquals( "", name.getName() );

        name = new FileResourceName( "somename" );
        Assert.assertEquals( "somename", name.getName() );

        name = new FileResourceName( "/parentname/somename" );
        Assert.assertEquals( "somename", name.getName() );
    }

    @Test
    public void testPath()
    {
        FileResourceName name = new FileResourceName( null );
        Assert.assertEquals( "/", name.getPath() );

        name = new FileResourceName( "" );
        Assert.assertEquals( "/", name.getPath() );

        name = new FileResourceName( "/" );
        Assert.assertEquals( "/", name.getPath() );

        name = new FileResourceName( "somename" );
        Assert.assertEquals( "/somename", name.getPath() );

        name = new FileResourceName( "/parentname/somename" );
        Assert.assertEquals( "/parentname/somename", name.getPath() );
    }

    @Test
    public void testIsRoot()
    {
        FileResourceName name = new FileResourceName( null );
        Assert.assertTrue( name.isRoot() );

        name = new FileResourceName( "" );
        Assert.assertTrue( name.isRoot() );

        name = new FileResourceName( "/" );
        Assert.assertTrue( name.isRoot() );

        name = new FileResourceName( "somename" );
        Assert.assertFalse( name.isRoot() );

        name = new FileResourceName( "/parentname/somename" );
        Assert.assertFalse( name.isRoot() );
    }

    @Test
    public void testGetParent()
    {
        FileResourceName name = new FileResourceName( null );
        Assert.assertNull( name.getParent() );

        name = new FileResourceName( "" );
        Assert.assertNull( name.getParent() );

        name = new FileResourceName( "/" );
        Assert.assertNull( name.getParent() );

        name = new FileResourceName( "somename" );
        Assert.assertNotNull( name.getParent() );
        Assert.assertEquals( "/", name.getParent().getPath() );

        name = new FileResourceName( "/parentname/somename" );
        Assert.assertNotNull( name.getParent() );
        Assert.assertEquals( "/parentname", name.getParent().getPath() );
    }

    @Test
    public void testToString()
    {
        FileResourceName name = new FileResourceName( "somename" );
        Assert.assertEquals( "/somename", name.toString() );

        name = new FileResourceName( "/parentname/somename" );
        Assert.assertEquals( "/parentname/somename", name.toString() );
    }

    @Test
    public void testEquals()
    {
        Assert.assertTrue( new FileResourceName( "somename" ).equals( new FileResourceName( "somename" ) ) );
        Assert.assertTrue( new FileResourceName( "somename" ).equals( new FileResourceName( "//somename" ) ) );
        Assert.assertFalse( new FileResourceName( "somename" ).equals( new FileResourceName( "/" ) ) );
    }

    @Test
    public void testPathsWithDots()
    {

        Assert.assertEquals( new FileResourceName( "path/to/somename" ), new FileResourceName( "./path/to/somename" ) );
        Assert.assertEquals( new FileResourceName( "/path/to/somename" ), new FileResourceName( "/path/to/./somename" ) );
        Assert.assertEquals( new FileResourceName( "/somename" ), new FileResourceName( "./somename" ) );
        Assert.assertEquals( new FileResourceName( "/path/to/somename" ), new FileResourceName( "/path/to/./././somename" ) );

        Assert.assertEquals( new FileResourceName( "path/somename" ), new FileResourceName( "path/to/../somename" ) );
        Assert.assertEquals( new FileResourceName( "/path/to/somename" ), new FileResourceName( "/path/to/../to/somename" ) );
        Assert.assertEquals( new FileResourceName( "somename" ), new FileResourceName( "path/to/../../somename" ) );
        Assert.assertEquals( new FileResourceName( "/somename" ), new FileResourceName( "/path/to/../../../../somename" ) );
        Assert.assertEquals( new FileResourceName( "/path/to" ), new FileResourceName( "/path/to/somename/.." ) );
        Assert.assertEquals( new FileResourceName( "/" ), new FileResourceName( "../../.." ) );

    }

}
