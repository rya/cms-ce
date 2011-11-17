package com.enonic.cms.core.structure.portlet;

import java.io.UnsupportedEncodingException;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

class PortletData
{
    private static final String ROOT_ELEMENT = "data";

    private static final String ATTRIBUTE_CACHE_DISABLED = "cachedisabled";

    private static final String ATTRIBUTE_CACHE_TYPE = "cachetype";

    private Document xmlDoc;

    public PortletData( Document xmlDoc )
    {
        this.xmlDoc = xmlDoc;
    }

    public PortletData()
    {
        xmlDoc = new Document();
        xmlDoc.addContent( new Element( ROOT_ELEMENT ) );
    }

    public Boolean getCacheDisabled()
    {
        Element element = getAndEnsureRootElement();

        String cacheDisabled = element.getAttributeValue( ATTRIBUTE_CACHE_DISABLED );

        if ( cacheDisabled == null || cacheDisabled.length() == 0 )
        {
            return null;
        }
        return Boolean.valueOf( cacheDisabled );
    }

    public void setCacheDisabled( boolean disabled )
    {
        Element element = getAndEnsureRootElement();
        Attribute cacheDisabled = element.getAttribute( ATTRIBUTE_CACHE_DISABLED );
        if ( cacheDisabled != null )
        {
            cacheDisabled.setValue( Boolean.toString( disabled ) );
        }
        else
        {
            element.setAttribute( ATTRIBUTE_CACHE_DISABLED, Boolean.toString( disabled ) );
        }
    }

    public String getCacheType()
    {
        Element element = getAndEnsureRootElement();

        String cacheType = element.getAttributeValue( ATTRIBUTE_CACHE_TYPE );

        if ( cacheType == null || cacheType.length() == 0 )
        {
            return null;
        }
        return cacheType;
    }

    public void setCacheType( String type )
    {
        Element element = getAndEnsureRootElement();
        Attribute cacheType = element.getAttribute( ATTRIBUTE_CACHE_TYPE );
        if ( cacheType != null )
        {
            cacheType.setValue( type );
        }
        else
        {
            element.setAttribute( ATTRIBUTE_CACHE_TYPE, type );
        }
    }

    private Element getAndEnsureRootElement()
    {
        Element rootEl;
        if ( xmlDoc == null )
        {
            xmlDoc = new Document();
            rootEl = new Element( ROOT_ELEMENT );
            xmlDoc.setRootElement( rootEl );
        }
        rootEl = xmlDoc.getRootElement();
        return rootEl;
    }

    public byte[] getAsBytes()
    {

        XMLDocument xmlDocument = XMLDocumentFactory.create( xmlDoc );
        try
        {
            return xmlDocument.getAsString().getBytes( "UTF-8" );
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new RuntimeException( "Failed to get as bytes: ", e );
        }
    }

}