/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content.contentdata.custom.contentkeybased;

import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.contenttype.dataentryconfig.ImageDataEntryConfig;

/**
 * Oct 9, 2009
 */
public class ImageDataEntryEqualsTest
    extends AbstractEqualsTest
{
    private ImageDataEntryConfig config = new ImageDataEntryConfig( "myImage", false, "My image", "myimage" );

    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        return new ImageDataEntry( config, new ContentKey( 123 ) );
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        return new Object[]{new ImageDataEntry( config, new ContentKey( 124 ) ),
            new ImageDataEntry( config, new ContentKey( 123 ), "imagetext" )};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        return new ImageDataEntry( config, new ContentKey( 123 ) );
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        return new ImageDataEntry( config, new ContentKey( 123 ) );
    }
}
