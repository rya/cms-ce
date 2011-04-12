/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.legacy;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.enonic.cms.core.content.binary.BinaryDataAndBinary;
import com.enonic.cms.core.content.binary.BinaryDataKey;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;

/**
 *
 */
public class LegacyFormContentData
    extends AbstractBaseLegacyContentData
{
    public LegacyFormContentData( Document contentDataXml )
    {
        super( contentDataXml );
    }

    protected String resolveTitle()
    {
        List nodes = contentDataEl.getChild( "form" ).getChildren( "item" );
        Element titleNode = null;
        for ( Object node : nodes )
        {
            Element formItemElement = (Element) node;
            Attribute titleAttr = formItemElement.getAttribute( "title" );
            if ( ( titleAttr != null ) && ( titleAttr.getValue().equals( "true" ) ) )
            {
                List dataNodes = formItemElement.getChildren();
                if ( ( dataNodes != null ) && ( dataNodes.size() > 0 ) )
                {
                    titleNode = (Element) dataNodes.get( 0 );
                }
            }
        }

        final Element generalFormTitleElement = contentDataEl.getChild( "form" ).getChild( "title" );
        return generalFormTitleElement.getText() + ": " + ( titleNode != null ? titleNode.getText() : "" );
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

        List nodes = contentDataEl.getChild( "form" ).getChildren( "item" );
        for ( Object node : nodes )
        {
            Element itemElement = (Element) node;
            for ( Object itemChild : itemElement.getChildren() )
            {
                Element itemChildElement = (Element) itemChild;
                if ( itemChildElement.getName().equals( "binarydata" ) )
                {
                    Attribute attr = ( itemChildElement ).getAttribute( "key" );
                    replaceBinaryKeyPlaceHolder( attr, binaryDatas );
                }
            }
        }
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
}
