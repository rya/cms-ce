/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content.binary;

import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.ContentVersionKey;
import com.enonic.cms.core.content.binary.BinaryDataEntity;
import com.enonic.cms.core.content.binary.ContentBinaryDataEntity;
import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;


public class ContentBinaryDataEntityEqualsTest
    extends AbstractEqualsTest
{
    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {

        ContentVersionEntity cv1 = new ContentVersionEntity();
        cv1.setKey( new ContentVersionKey( 1 ) );
        BinaryDataEntity bd1 = new BinaryDataEntity();
        bd1.setKey( 1 );
        ContentBinaryDataEntity i1 = new ContentBinaryDataEntity();
        i1.setContentVersion( cv1 );
        i1.setBinaryData( bd1 );
        return i1;
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        ContentVersionEntity cv1 = new ContentVersionEntity();
        cv1.setKey( new ContentVersionKey( 1 ) );
        BinaryDataEntity bd1 = new BinaryDataEntity();
        bd1.setKey( 2 );
        ContentBinaryDataEntity i1 = new ContentBinaryDataEntity();
        i1.setContentVersion( cv1 );
        i1.setBinaryData( bd1 );

        ContentVersionEntity cv2 = new ContentVersionEntity();
        cv2.setKey( new ContentVersionKey( 2 ) );
        BinaryDataEntity bd2 = new BinaryDataEntity();
        bd2.setKey( 1 );
        ContentBinaryDataEntity i2 = new ContentBinaryDataEntity();
        i2.setContentVersion( cv2 );
        i2.setBinaryData( bd2 );

        return new Object[]{i1, i2};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        ContentBinaryDataEntity i1 = new ContentBinaryDataEntity();

        ContentVersionEntity cv1 = new ContentVersionEntity();
        cv1.setKey( new ContentVersionKey( 1 ) );

        BinaryDataEntity bd1 = new BinaryDataEntity();
        bd1.setKey( 1 );

        i1.setContentVersion( cv1 );
        i1.setBinaryData( bd1 );
        return i1;
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        ContentBinaryDataEntity i1 = new ContentBinaryDataEntity();

        ContentVersionEntity cv1 = new ContentVersionEntity();
        cv1.setKey( new ContentVersionKey( 1 ) );

        BinaryDataEntity bd1 = new BinaryDataEntity();
        bd1.setKey( 1 );

        i1.setContentVersion( cv1 );
        i1.setBinaryData( bd1 );
        return i1;
    }
}
