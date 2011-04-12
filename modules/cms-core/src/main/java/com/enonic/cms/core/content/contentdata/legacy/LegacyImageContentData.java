/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.legacy;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.binary.BinaryDataKey;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;

import com.enonic.cms.core.content.binary.BinaryDataAndBinary;


public class LegacyImageContentData
    extends AbstractBaseLegacyContentData
{
    public LegacyImageContentData( Document contentDataXml )
    {
        super( contentDataXml );
    }

    protected String resolveTitle()
    {
        final Element nameEl = contentDataEl.getChild( "name" );
        return nameEl.getText();
    }

    protected List<BinaryDataAndBinary> resolveBinaryDataAndBinaryList()
    {
        return null;
    }

    public void replaceBinaryKeyPlaceholders( List<BinaryDataKey> binaryDatas )
    {
        if ( binaryDatas == null || binaryDatas.size() == 0 )
        {
            return;
        }

        replaceBinaryKeyPlaceHoldersInImages( binaryDatas );
        replaceBinaryKeyPlaceHoldersInSourceImage( binaryDatas );
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

    @Override
    public Set<ContentKey> resolveRelatedContentKeys()
    {
        final Set<ContentKey> contentKeys = new HashSet<ContentKey>();

        contentKeys.addAll( resolveContentKeysByXPath( "/contentdata/file/@key" ) );

        return contentKeys;
    }

    private void replaceBinaryKeyPlaceHoldersInSourceImage( List<BinaryDataKey> binaryDatas )
    {
        Element sourceimageEl = contentDataEl.getChild( "sourceimage" );
        if ( sourceimageEl == null )
        {
            return;
        }

        Element binaryDataEl = sourceimageEl.getChild( "binarydata" );

        Attribute keyAttr = binaryDataEl.getAttribute( "key" );
        replaceBinaryKeyPlaceHolder( keyAttr, binaryDatas );
    }

    private void replaceBinaryKeyPlaceHoldersInImages( List<BinaryDataKey> binaryDatas )
    {
        Element imagesEl = contentDataEl.getChild( "images" );
        if ( imagesEl == null )
        {
            return;
        }

        List<Element> imageElList = imagesEl.getChildren( "image" );
        for ( Element imageEl : imageElList )
        {
            Element binaryDataEl = imageEl.getChild( "binarydata" );
            if ( binaryDataEl != null )
            {
                Attribute keyAttr = binaryDataEl.getAttribute( "key" );
                replaceBinaryKeyPlaceHolder( keyAttr, binaryDatas );
            }
        }
    }


}
