/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;

import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;
import com.enonic.cms.core.content.contentdata.custom.stringbased.HtmlAreaDataEntry;
import com.enonic.cms.core.content.contenttype.dataentryconfig.BinaryDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.HtmlAreaDataEntryConfig;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Mar 9, 2010
 * Time: 3:01:43 PM
 */
public class BinaryDataEntryEqualsTest
    extends AbstractEqualsTest
{
    private DataEntryConfig config1;

    private DataEntryConfig config2;

    private byte[] binary1;

    private byte[] binary2;

    private String binaryName1;

    private String binaryName2;

    @Before
    public void setUp()
    {
        config1 = new BinaryDataEntryConfig( "a config", true, "a config", "/root/entry" );
        config2 = new BinaryDataEntryConfig( "another config", true, "another config", "/root/entry2" );

        binary1 = "This is binary1".getBytes();
        binary2 = "This is binary2".getBytes();

        binaryName1 = "binary1";
        binaryName2 = "binary2";

    }

    @Test
    public void testEqualsExistingBinaryKey()
    {
        BinaryDataEntry b1 = new BinaryDataEntry( config1, 101 );
        BinaryDataEntry b2 = new BinaryDataEntry( config1, 101 );
        BinaryDataEntry b3 = new BinaryDataEntry( config1, 102 );
        BinaryDataEntry b4 = new BinaryDataEntry( config2, 102 );

        assertTrue( b1.equals( b2 ) );
        assertFalse( b1.equals( b3 ) );
        assertFalse( b3.equals( b4 ) );
    }

    @Test
    public void testEqualsFullConstructor()
    {
        BinaryDataEntry b1 = new BinaryDataEntry( config1, "%0", binary1, binaryName1 );
        BinaryDataEntry b2 = new BinaryDataEntry( config1, "%0", binary1, binaryName1 );
        BinaryDataEntry b3 = new BinaryDataEntry( config2, "%0", binary1, binaryName1 );
        BinaryDataEntry b4 = new BinaryDataEntry( config1, "%1", binary1, binaryName1 );
        BinaryDataEntry b5 = new BinaryDataEntry( config1, "%0", binary1, binaryName2 );
        BinaryDataEntry b6 = new BinaryDataEntry( config1, "%0", binary2, binaryName1 );

        assertTrue( "Differs despite all equal", b1.equals( b2 ) );
        assertFalse( "Equals despite different config", b1.equals( b3 ) );
        assertFalse( "Equals despite different placeholer", b1.equals( b4 ) );
        assertFalse( "Equals despite different binaryname", b1.equals( b5 ) );
        assertFalse( "Equals despite different binary data", b1.equals( b6 ) );
    }

    @Test
    public void testEqualsDifferentConstructors()
    {
        BinaryDataEntry b1 = new BinaryDataEntry( config1, "%0", binary1, binaryName1 );
        BinaryDataEntry b2 = new BinaryDataEntry( config1, 101 );

        assertFalse( "Equals despite different data", b1.equals( b2 ) );
        assertFalse( "Equals despite different data", b2.equals( b1 ) );

        b2.setBinaryKeyPlaceholder( "%0" );
        assertFalse( "Equals despite different data", b1.equals( b2 ) );

        b1.setExistingBinaryKey( 101 );
        assertFalse( "Equals despite different data", b1.equals( b2 ) );
    }

    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        BinaryDataEntryConfig config = new BinaryDataEntryConfig( "test", true, "test", "contentdata/test" );

        return new BinaryDataEntry( config, "%0" );
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        BinaryDataEntryConfig config = new BinaryDataEntryConfig( "test", true, "test", "contentdata/test" );
        BinaryDataEntryConfig config2 = new BinaryDataEntryConfig( "test", true, "test", "contentdata/test2" );
        BinaryDataEntryConfig config3 = new BinaryDataEntryConfig( "test2", true, "test", "contentdata/test" );
        HtmlAreaDataEntryConfig config4 = new HtmlAreaDataEntryConfig( "test", true, "test", "contentdata/test" );

        return new Object[]{new BinaryDataEntry( config, "%1" ), new BinaryDataEntry( config2, "%0" ), new BinaryDataEntry( config3, "%0" ),
            new HtmlAreaDataEntry( config4, "%0" )};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        BinaryDataEntryConfig config = new BinaryDataEntryConfig( "test", true, "test", "contentdata/test" );

        return new BinaryDataEntry( config, "%0" );
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        BinaryDataEntryConfig config = new BinaryDataEntryConfig( "test", true, "test", "contentdata/test" );

        return new BinaryDataEntry( config, "%0" );
    }
}