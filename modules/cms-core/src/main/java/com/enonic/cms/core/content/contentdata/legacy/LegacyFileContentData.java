/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.legacy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.enonic.cms.core.content.binary.BinaryDataAndBinary;
import com.enonic.cms.core.content.binary.BinaryDataEntity;
import com.enonic.cms.core.content.binary.BinaryDataKey;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;


public class LegacyFileContentData
    extends AbstractBaseLegacyContentData
{
    private BinaryDataAndBinary binary;

    public LegacyFileContentData( Document contentDataXml, BinaryDataAndBinary binary )
    {
        super( contentDataXml );
        this.binary = binary;
    }

    protected String resolveTitle()
    {
        final Element nameEl = contentDataEl.getChild( "name" );
        return nameEl.getText();
    }

    protected List<BinaryDataAndBinary> resolveBinaryDataAndBinaryList()
    {
        List<BinaryDataAndBinary> list = new ArrayList<BinaryDataAndBinary>();
        list.add( binary );
        return list;
    }

    public void replaceBinaryKeyPlaceholders( List<BinaryDataKey> binaryDatas )
    {
        if ( binaryDatas == null || binaryDatas.size() == 0 )
        {
            return;
        }

        final Element binaryDataEl = contentDataEl.getChild( "binarydata" );
        Attribute attr = binaryDataEl.getAttribute( "key" );
        replaceBinaryKeyPlaceHolder( attr, binaryDatas );
    }

    public void turnBinaryKeysIntoPlaceHolders( Map<BinaryDataKey, Integer> indexByBinaryDataKey )
    {
        Iterator it = contentDataEl.getDescendants( new ElementFilter( "binarydata" ) );
        while ( it.hasNext() )
        {
            Element binaryDataEl = (Element) it.next();
            Attribute keyAttr = binaryDataEl.getAttribute( "key" );
            BinaryDataKey binaryDataKey = new BinaryDataKey( keyAttr.getValue() );
            Integer index = indexByBinaryDataKey.get( binaryDataKey );
            if ( index != null )
            {
                keyAttr.setValue( "%" + index );
            }
        }
    }

    public List<BinaryDataKey> getRemovedBinaries( LegacyFileContentData newContentData )
    {
        List<BinaryDataKey> list = new ArrayList<BinaryDataKey>();
        BinaryDataEntity newBinaryData = newContentData.binary.getBinaryData();
        BinaryDataKey currentBinaryDataKey = resolveBinaryDataKey();

        if ( newBinaryData.getBinaryDataKey() == null || newBinaryData.getBinaryDataKey().equals( currentBinaryDataKey ) )
        {
            list.add( currentBinaryDataKey );
        }
        return list;
    }

    public BinaryDataKey resolveBinaryDataKey()
    {
        if ( binary != null && binary.getBinaryData() != null && binary.getBinaryData().getBinaryDataKey() != null )
        {
            return binary.getBinaryData().getBinaryDataKey();
        }
        else
        {
            return resolveBinaryDataKeyFromContentDataXml();
        }
    }

    private BinaryDataKey resolveBinaryDataKeyFromContentDataXml()
    {
        final Element binaryDataEl = contentDataEl.getChild( "binarydata" );
        String keyStr = binaryDataEl.getAttributeValue( "key" );
        if ( keyStr == null || keyStr.startsWith( "%" ) )
        {
            return null;
        }

        return new BinaryDataKey( keyStr );
    }
}