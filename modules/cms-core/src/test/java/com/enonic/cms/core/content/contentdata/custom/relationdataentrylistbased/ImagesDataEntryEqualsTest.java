/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.relationdataentrylistbased;

import com.enonic.cms.core.content.ContentKey;
import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.ImageDataEntry;
import com.enonic.cms.core.content.contenttype.dataentryconfig.ImageDataEntryConfig;

/**
 * Oct 9, 2009
 */
public class ImagesDataEntryEqualsTest
    extends AbstractEqualsTest
{
    private ImageDataEntryConfig config = new ImageDataEntryConfig( "myImages", false, "My images", "myimages" );

    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        return new ImagesDataEntry( config ).add( new ImageDataEntry( config, new ContentKey( 123 ) ) );
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        return new Object[]{new ImagesDataEntry( config ).add( new ImageDataEntry( config, new ContentKey( 123 ), "imagetext" ) ),
            new ImagesDataEntry( config ).add( new ImageDataEntry( config, new ContentKey( 124 ) ) )};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        return new ImagesDataEntry( config ).add( new ImageDataEntry( config, new ContentKey( 123 ) ) );
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        return new ImagesDataEntry( config ).add( new ImageDataEntry( config, new ContentKey( 123 ) ) );
    }
}
