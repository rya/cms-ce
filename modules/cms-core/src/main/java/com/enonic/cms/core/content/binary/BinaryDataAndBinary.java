/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.binary;

import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.framework.blob.BlobStoreObject;
import com.enonic.cms.framework.xml.IllegalCharacterCleaner;

import com.enonic.cms.api.client.model.content.BinaryInput;

import com.enonic.cms.core.content.contentdata.custom.BinaryDataEntry;


public class BinaryDataAndBinary
{

    private static IllegalCharacterCleaner xmlCleaner = new IllegalCharacterCleaner();

    private BinaryDataEntity binaryData;

    private BlobStoreObject binary;

    private String label;

    public BinaryDataAndBinary( BinaryDataEntity binaryData )
    {
        this.binaryData = binaryData;
    }

    public BinaryDataAndBinary( BinaryDataEntity binaryData, byte[] binary )
    {
        this( binaryData, new BlobStoreObject( binary ) );
    }

    public BinaryDataAndBinary( BinaryDataEntity binaryData, BlobStoreObject binary )
    {
        this.binaryData = binaryData;
        this.binary = binary;
    }


    public void setBinaryData( BinaryDataEntity value )
    {
        this.binaryData = value;
    }

    public BinaryDataEntity getBinaryData()
    {
        return binaryData;
    }

    public BlobStoreObject getBinary()
    {
        return binary;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel( String label )
    {
        this.label = label;
    }

    public static List<BinaryDataAndBinary> createNewFrom( BinaryData[] binaryDatas )
    {
        if ( binaryDatas == null || binaryDatas.length == 0 )
        {
            return new ArrayList<BinaryDataAndBinary>();
        }

        List<BinaryDataAndBinary> list = new ArrayList<BinaryDataAndBinary>( binaryDatas.length );
        for ( BinaryData binaryData : binaryDatas )
        {
            list.add( create( binaryData ) );
        }
        return list;
    }

    private static BinaryDataAndBinary create( BinaryData binaryData )
    {
        BinaryDataEntity binaryDataEntity = new BinaryDataEntity();
        binaryDataEntity.setName( xmlCleaner.cleanXml( binaryData.fileName ) );
        if ( binaryData.data != null )
        {
            binaryDataEntity.setSize( binaryData.data.length );
        }

        BlobStoreObject blob = new BlobStoreObject( binaryData.data );
        binaryDataEntity.setBlobKey( blob.getId() );

        BinaryDataAndBinary binaryDataAndBinary = new BinaryDataAndBinary( binaryDataEntity, blob );
        binaryDataAndBinary.setLabel( binaryData.label );
        return binaryDataAndBinary;
    }

    public BinaryDataEntity createBinaryDataForSave()
    {
        BinaryDataEntity binaryDataForSave = new BinaryDataEntity();
        binaryDataForSave.setBinaryDataKey( binaryData.getBinaryDataKey() );
        binaryDataForSave.setCreatedAt( binaryData.getCreatedAt() );
        binaryDataForSave.setName( xmlCleaner.cleanXml( binaryData.getName() ) );
        binaryDataForSave.setSize( binaryData.getSize() );
        binaryDataForSave.setBlobKey( binary.getId() );
        return binaryDataForSave;
    }

    public static List<BinaryDataAndBinary> convert( List<BinaryDataEntry> binaryEntries )
    {
        List<BinaryDataAndBinary> list = new ArrayList<BinaryDataAndBinary>();
        for ( BinaryDataEntry binaryDataEntry : binaryEntries )
        {
            BinaryDataEntity binaryData = new BinaryDataEntity();
            binaryData.setName( binaryDataEntry.getBinaryName() );
            byte[] data = binaryDataEntry.getBinary();
            binaryData.setSize( data.length );

            BlobStoreObject blob = new BlobStoreObject( data );
            binaryData.setBlobKey( blob.getId() );

            list.add( new BinaryDataAndBinary( binaryData, blob ) );
        }
        return list;
    }

    public static List<BinaryDataAndBinary> convertFromBinaryInputs( List<BinaryInput> binaryInputs )
    {
        List<BinaryDataAndBinary> list = new ArrayList<BinaryDataAndBinary>();
        for ( BinaryInput binaryInput : binaryInputs )
        {
            BinaryDataAndBinary binaryDataAndBinary = convertFromBinaryInput( binaryInput );
            list.add( binaryDataAndBinary );
        }
        return list;
    }

    public static BinaryDataAndBinary convertFromBinaryInput( final BinaryInput binaryInput )
    {
        return convertFromNameAndData( binaryInput.getBinaryName(), binaryInput.getBinary() );
    }

    public static BinaryDataAndBinary convertFromBinaryEntry( final BinaryDataEntry binaryEntry )
    {
        return convertFromNameAndData( binaryEntry.getBinaryName(), binaryEntry.getBinary() );
    }

    private static BinaryDataAndBinary convertFromNameAndData( final String name, final byte[] data )
    {
        final BinaryDataEntity binaryData = new BinaryDataEntity();
        binaryData.setName( xmlCleaner.cleanXml( name ) );
        binaryData.setSize( data.length );

        BlobStoreObject blob = new BlobStoreObject( data );
        binaryData.setBlobKey( blob.getId() );

        return new BinaryDataAndBinary( binaryData, blob );
    }
}
