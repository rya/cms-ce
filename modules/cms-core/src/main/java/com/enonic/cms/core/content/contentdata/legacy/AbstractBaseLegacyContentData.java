/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.legacy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.binary.BinaryDataAndBinary;
import com.enonic.cms.core.content.binary.BinaryDataKey;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.transform.JDOMSource;

import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.sxpath.XPathEvaluator;
import net.sf.saxon.sxpath.XPathExpression;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.content.contentdata.ContentData;


public abstract class AbstractBaseLegacyContentData
    implements ContentData
{
    private String contentDataXmlString;

    protected Document contentDataXml;

    protected Element contentDataEl;

    private String title;

    private List<BinaryDataAndBinary> binaryDataAndBinaries;

    public AbstractBaseLegacyContentData( Document contentDataXml )
    {
        this.contentDataXml = contentDataXml;
        contentDataEl = contentDataXml.getRootElement();

        if ( !"contentdata".equals( contentDataEl.getName() ) )
        {
            throw new IllegalArgumentException( "Expected contentdata as root element" );
        }
    }

    public Document getContentDataXml()
    {
        return contentDataXml;
    }

    private String getContentDataXmlString()
    {
        if ( contentDataXmlString == null )
        {
            contentDataXmlString = JDOMUtil.printDocument( contentDataXml );
        }
        return contentDataXmlString;
    }

    public String getTitle()
    {
        if ( title == null )
        {
            title = resolveTitle();
        }
        return title;
    }

    public Set<ContentKey> resolveRelatedContentKeys()
    {
        return new HashSet<ContentKey>();
    }

    public boolean hasRelatedChild( ContentKey contentKey )
    {
        return resolveRelatedContentKeys().contains( contentKey );
    }

    public List<BinaryDataAndBinary> getBinaryDataAndBinaryList()
    {
        if ( binaryDataAndBinaries == null )
        {
            binaryDataAndBinaries = resolveBinaryDataAndBinaryList();
        }
        return binaryDataAndBinaries;
    }

    protected abstract String resolveTitle();

    protected abstract List<BinaryDataAndBinary> resolveBinaryDataAndBinaryList();


    protected void replaceBinaryKeyPlaceHolder( Attribute attribute, List<BinaryDataKey> binaryDatas )
    {
        String keyPlaceHolder = attribute.getValue();
        Integer keyPlaceHolderIndex = resolvePlaceHolderIndex( keyPlaceHolder );
        if ( keyPlaceHolderIndex == null )
        {
            // key already set, lets return
            return;
        }

        BinaryDataKey key = findBinaryDataKeyByIndex( keyPlaceHolderIndex, binaryDatas );
        if ( key == null )
        {
            attribute.setValue( "#ERROR: Binary key not found" );
        }
        else
        {
            attribute.setValue( key.toString() );
        }
    }

    private BinaryDataKey findBinaryDataKeyByIndex( Integer keyPlaceHolderIndex, List<BinaryDataKey> binaryDatas )
    {
        if ( keyPlaceHolderIndex < 0 || keyPlaceHolderIndex > binaryDatas.size() - 1 )
        {
            return null;
        }

        return binaryDatas.get( keyPlaceHolderIndex );
    }

    private Integer resolvePlaceHolderIndex( String keyPlaceHolder )
    {
        if ( keyPlaceHolder == null )
        {
            return null;
        }

        if ( !keyPlaceHolder.startsWith( "%" ) )
        {
            return null;
        }

        return Integer.valueOf( keyPlaceHolder.substring( 1 ) );
    }

    protected List<ContentKey> resolveContentKeysByXPath( String xPath )
    {
        try
        {
            XPathEvaluator xpathEvaluator = new XPathEvaluator();
            XPathExpression expr = xpathEvaluator.createExpression( xPath );

            final JDOMSource docAsDomSource = new JDOMSource( contentDataXml );

            List<ContentKey> contentKeys = new ArrayList<ContentKey>();

            List nodes = expr.evaluate( docAsDomSource );

            for ( Object node : nodes )
            {
                if ( node instanceof NodeInfo )
                {
                    NodeInfo nodeInfo = (NodeInfo) node;
                    contentKeys.add( new ContentKey( nodeInfo.getStringValue() ) );
                }
                else
                {
                    contentKeys.add( new ContentKey( String.valueOf( node ) ) );
                }
            }

            return contentKeys;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof AbstractBaseLegacyContentData ) )
        {
            return false;
        }

        AbstractBaseLegacyContentData that = (AbstractBaseLegacyContentData) o;

        if ( !getContentDataXmlString().equals( that.getContentDataXmlString() ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder( 317, 381 ).
            append( getContentDataXmlString() ).
            toHashCode();
    }
}
