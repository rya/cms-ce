package com.enonic.cms.core.structure.menuitem;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

class MenuItemData
{
    private static final String ROOT_ELEMENT = "data";

    private static final String ATTRIBUTE_CACHE_DISABLED = "cachedisabled";

    private static final String ATTRIBUTE_CACHE_TYPE = "cachetype";

    private Document xmlDoc;

    public MenuItemData( Document xmlDoc )
    {
        this.xmlDoc = xmlDoc;
    }

    public MenuItemData()
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

    public void addRequestParameter( final String name, final String value, final String override )
    {
        Element rootEl = getAndEnsureRootElement();
        Element parametersEl = getAndEnsureElement( rootEl, "parameters" );
        Element parameterEl = new Element( "parameter" );

        parameterEl.setText( value );
        parameterEl.setAttribute( "name", name );
        if ( override != null )
        {
            parameterEl.setAttribute( "override", override );
        }

        parametersEl.addContent( parameterEl );
    }

    public Map<String, MenuItemRequestParameter> getRequestParameters()
    {
        Element rootEl = getAndEnsureRootElement();
        Element parametersEl = getAndEnsureElement( rootEl, "parameters" );
        @SuppressWarnings({"unchecked"}) List<Element> children = parametersEl.getChildren( "parameter" );

        Map<String, MenuItemRequestParameter> parametersByName = new LinkedHashMap<String, MenuItemRequestParameter>();

        for ( Element element : children )
        {
            String name = element.getAttributeValue( "name" );
            String value = element.getText();
            String override = element.getAttributeValue( "override" );

            if ( name != null )
            {
                MenuItemRequestParameter parameter = new MenuItemRequestParameter( name, value, override );
                parametersByName.put( name, parameter );
            }
        }

        return parametersByName;
    }

    public MenuItemRequestParameter getRequestParameter( final String name )
    {
        return getRequestParameters().get( name );
    }

    public void removeRequestParameters()
    {
        Element rootEl = getAndEnsureRootElement();
        Element parametersEl = getAndEnsureElement( rootEl, "parameters" );
        rootEl.removeContent( parametersEl );
    }

    public Set<String> getAllowedPageTypes()
    {
        Element rootEl = getAndEnsureRootElement();
        Element pageTypesEl = getAndEnsureElement( rootEl, "pagetypes" );

        Set<String> pageTypes = new HashSet<String>();

        @SuppressWarnings({"unchecked"}) List<Element> childrenEl = pageTypesEl.getChildren( "allow" );
        for ( Element allowEl : childrenEl )
        {
            pageTypes.add( allowEl.getAttributeValue( "type" ) );
        }

        return Collections.unmodifiableSet( pageTypes );
    }

    private Element getAndEnsureElement( Element parentEl, String childName )
    {
        Element child = parentEl.getChild( childName );
        if ( child == null )
        {
            child = new Element( childName );
            parentEl.addContent( child );
        }

        return child;
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

    public Document getJDOMDocument()
    {
        return xmlDoc;
    }

}
