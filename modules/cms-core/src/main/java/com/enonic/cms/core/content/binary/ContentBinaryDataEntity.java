/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.binary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.enonic.cms.core.content.ContentVersionEntity;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class ContentBinaryDataEntity
    implements Serializable
{

    @SuppressWarnings({"UnusedDeclaration"})
    private ContentBinaryDataKey key;

    private String label;

    private ContentVersionEntity contentVersion;

    private BinaryDataEntity binaryData;

    public String getLabel()
    {
        return label;
    }

    public ContentVersionEntity getContentVersion()
    {
        return contentVersion;
    }

    public BinaryDataEntity getBinaryData()
    {
        return binaryData;
    }

    public void setLabel( String label )
    {
        this.label = label;
    }

    public void setContentVersion( ContentVersionEntity contentVersion )
    {
        this.contentVersion = contentVersion;
    }

    public void setBinaryData( BinaryDataEntity binaryData )
    {
        this.binaryData = binaryData;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ContentBinaryDataEntity ) )
        {
            return false;
        }

        ContentBinaryDataEntity that = (ContentBinaryDataEntity) o;

        if ( binaryData != null ? !binaryData.equals( that.getBinaryData() ) : that.getBinaryData() != null )
        {
            return false;
        }
        if ( contentVersion != null ? !contentVersion.equals( that.getContentVersion() ) : that.getContentVersion() != null )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 679, 347 ).append( key ).toHashCode();
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s.append( "key = " ).append( key != null ? key : "null" );
        s.append( ", contentVersion = " ).append( contentVersion != null ? contentVersion.getKey() : "null" );
        s.append( ", binaryData = " ).append( binaryData != null ? binaryData.getKey() : "null" );
        s.append( ", label = " ).append( label != null ? label : "null" );

        return s.toString();
    }

    public static ContentBinaryDataEntity createNewFrom( BinaryDataAndBinary obj )
    {
        ContentBinaryDataEntity newCBD = new ContentBinaryDataEntity();
        newCBD.setLabel( obj.getLabel() );
        newCBD.setBinaryData( obj.getBinaryData() );
        return newCBD;
    }

    public static List<ContentBinaryDataEntity> createNewFrom( Collection<BinaryDataAndBinary> collection )
    {
        List<ContentBinaryDataEntity> list = new ArrayList<ContentBinaryDataEntity>();
        if ( collection == null || collection.isEmpty() )
        {
            return list;
        }

        for ( BinaryDataAndBinary binaryDataAndBinary : collection )
        {
            list.add( createNewFrom( binaryDataAndBinary ) );
        }

        return list;
    }

}
