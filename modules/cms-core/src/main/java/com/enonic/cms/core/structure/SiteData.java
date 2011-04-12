/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.enonic.cms.core.resource.ResourceKey;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

/**
 * May 29, 2009
 */
public class SiteData
{
    private static final String LOCALE_RESOLVER_ELEMENT_NAME = "locale-resolver";

    private static final String DEVICE_CLASS_RESOLVER_ELEMENT_NAME = "device-class-resolver";

    private static final String PATH_TO_PUBLIC_HOME_RESOURCES_ELEMENT_NAME = "path-to-public-home";

    private static final String PATH_TO_HOME_RESOURCES_ELEMENT_NAME = "path-to-home";

    private static final String DEFAULT_LOCALIZATION_RESOURCE_ELMENT_NAME = "default-localization-resource";

    private static final String DEFAULT_CSS_ELEMENT_NAME = "defaultcss";

    private Document xmlDoc;

    public SiteData( Document xmlDoc )
    {
        this.xmlDoc = xmlDoc;
    }

    public SiteData()
    {
        xmlDoc = new Document();
        xmlDoc.addContent( new Element( "menudata" ) );
    }

    public ResourceKey getPathToPublicResources()
    {
        Element element = getAndEnsureElement( getAndEnsureRootElement(), PATH_TO_PUBLIC_HOME_RESOURCES_ELEMENT_NAME );
        if ( element.getText() == null || element.getText().length() == 0 )
        {
            return null;
        }
        return new ResourceKey( element.getText() );
    }

    public ResourceKey getPathToResources()
    {
        Element element = getAndEnsureElement( getAndEnsureRootElement(), PATH_TO_HOME_RESOURCES_ELEMENT_NAME );
        if ( element.getText() == null || element.getText().length() == 0 )
        {
            return null;
        }
        return new ResourceKey( element.getText() );
    }

    public ResourceKey getDeviceClassResolver()
    {
        Element element = getAndEnsureElement( getAndEnsureRootElement(), DEVICE_CLASS_RESOLVER_ELEMENT_NAME );
        if ( element.getText() == null || element.getText().length() == 0 )
        {
            return null;
        }
        return new ResourceKey( element.getText() );
    }

    public ResourceKey getLocaleResolver()
    {
        Element element = getAndEnsureElement( getAndEnsureRootElement(), LOCALE_RESOLVER_ELEMENT_NAME );
        if ( element.getText() == null || element.getText().length() == 0 )
        {
            return null;
        }
        return new ResourceKey( element.getText() );
    }

    public ResourceKey getDefaultLocalizationResource()
    {
        Element element = getAndEnsureElement( getAndEnsureRootElement(), DEFAULT_LOCALIZATION_RESOURCE_ELMENT_NAME );
        if ( element.getText() == null || element.getText().length() == 0 )
        {
            return null;
        }
        return new ResourceKey( element.getText() );
    }

    public void addPageType( String value )
    {
        Element rootEl = getAndEnsureRootElement();
        Element pageTypesEl = getAndEnsureElement( rootEl, "pagetypes" );
        Element allowEl = new Element( "allow" );
        pageTypesEl.addContent( allowEl );
        allowEl.setAttribute( "type", value );
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

    public ResourceKey getDefaultCssKey()
    {
        if ( xmlDoc != null )
        {
            Element elem = xmlDoc.getRootElement().getChild( DEFAULT_CSS_ELEMENT_NAME );

            if ( elem != null && StringUtils.isNotEmpty( elem.getAttributeValue( "key" ) ) )
            {
                return new ResourceKey( elem.getAttributeValue( "key" ) );
            }
        }

        return null;
    }

    public void setDefaultCssKey( ResourceKey resourceKey )
    {
        Element element = getAndEnsureElement( getAndEnsureRootElement(), DEFAULT_CSS_ELEMENT_NAME );
        element.setAttribute( "key", resourceKey.toString() );

    }

    public void setPathToPublicResources( ResourceKey value )
    {
        Element element = getAndEnsureElement( getAndEnsureRootElement(), PATH_TO_PUBLIC_HOME_RESOURCES_ELEMENT_NAME );
        element.setText( value.toString() );
    }

    public void setPathToResources( ResourceKey value )
    {
        Element element = getAndEnsureElement( getAndEnsureRootElement(), PATH_TO_HOME_RESOURCES_ELEMENT_NAME );
        element.setText( value.toString() );
    }

    public void setDeviceClassResolver( ResourceKey value )
    {
        Element element = getAndEnsureElement( getAndEnsureRootElement(), DEVICE_CLASS_RESOLVER_ELEMENT_NAME );
        element.setText( value.toString() );
    }

    public void setLocaleResolver( ResourceKey value )
    {
        Element element = getAndEnsureElement( getAndEnsureRootElement(), LOCALE_RESOLVER_ELEMENT_NAME );
        element.setText( value.toString() );
    }

    public void setDefaultLocalizationResource( ResourceKey value )
    {
        Element element = getAndEnsureElement( getAndEnsureRootElement(), DEFAULT_LOCALIZATION_RESOURCE_ELMENT_NAME );
        element.setText( value.toString() );
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
            rootEl = new Element( "sitedata" );
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
