/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.binary;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Date;

import com.enonic.esl.util.StringUtil;

import com.enonic.cms.api.client.model.content.image.ImageContentDataInput;
import com.enonic.cms.core.content.ContentVersionEntity;

/**
 * This class is deprecated
 *
 * @Deprecated
 */
public class BinaryData
    implements Serializable
{

    public static final String LABEL_FILE = "file";

    private static final long serialVersionUID = -2286685939812284906L;

    public int key = -1;

    public int contentKey = -1;

    public byte[] data;

    public Date timestamp;

    public String fileName;

    public String label;

    public boolean anonymousAccess = false;

    public String getSafeFileName()
    {
        if ( fileName == null )
        {
            return null;
        }
        else
        {
            return StringUtil.stripControlChars( fileName );
        }
    }

    public void setSafeFileName( String fileName )
    {
        if ( ( fileName != null ) && fileName.length() > 0 )
        {
            this.fileName = StringUtil.stripControlChars( fileName );
        }
        else
        {
            this.fileName = "noname.bin";
        }
    }

    public ContentBinaryDataEntity createContentBinaryDataEntity( ContentVersionEntity contentVersion, BinaryDataEntity binaryData )
    {
        ContentBinaryDataEntity entity = new ContentBinaryDataEntity();
        entity.setBinaryData( binaryData );
        entity.setContentVersion( contentVersion );
        entity.setLabel( label );
        return entity;
    }

    public BinaryDataEntity createBinaryDataEntity( Date creationDateTime )
    {
        BinaryDataEntity entity = new BinaryDataEntity();
        if ( key > -1 )
        {
            entity.setKey( key );
        }
        entity.setName( getSafeFileName() );
        if ( data != null )
        {
            entity.setSize( data.length );
        }
        if ( timestamp == null )
        {
            entity.setCreatedAt( creationDateTime );
        }
        else
        {
            entity.setCreatedAt( timestamp );
        }

        return entity;
    }

    public static BinaryData createBinaryDataFromStream( final ByteArrayOutputStream stream, final String fileName )
    {
        return createBinaryDataFromStream( stream, fileName, null, null );
    }

    public static BinaryData createBinaryDataFromStream( final ByteArrayOutputStream stream, final String fileName, final String label,
                                                         final ImageContentDataInput contentData )
    {
        final BinaryData binaryData = new BinaryData();
        binaryData.fileName = contentData == null ? fileName : contentData.binary.getBinaryName();
        binaryData.data = contentData == null ? stream.toByteArray() : contentData.binary.getBinary();
        binaryData.label = label;
        return binaryData;
    }
}
