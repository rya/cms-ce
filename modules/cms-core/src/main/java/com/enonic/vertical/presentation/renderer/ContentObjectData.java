/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.presentation.renderer;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.resource.ResourceKey;

/**
 * Container for contentobject data.
 */
public final class ContentObjectData
{
    private ResourceKey styleSheetKey;

    private XMLDocument stylesheet;

    private Element methodCalls;

    private Element objectParameters;

    private Element borderParameters;

    private ResourceKey borderStyleSheetKey;

    private XMLDocument borderStyleSheet;

    private String name;

    private Document documentDoc;

    private Document shoppingCartDoc;

    private SiteKey ownerSiteKey;

    private int key;

    private Integer cacheTime; // how long the object should be cached

    private String cacheType;

    private int cKeyIndex = 0;

    private boolean cacheDisabled;

    public int getKey()
    {
        return key;
    }

    public void setKey( int key )
    {
        this.key = key;
    }

    public Document getDocumentDoc()
    {
        return documentDoc;
    }

    public Document getShoppingCartDoc()
    {
        return shoppingCartDoc;
    }

    public XMLDocument getStyleSheet()
    {
        return stylesheet;
    }

    public Element getMethodCalls()
    {
        return methodCalls;
    }

    public Element getObjectParameters()
    {
        return objectParameters;
    }

    public Element getBorderParameters()
    {
        return borderParameters;
    }

    public XMLDocument getBorderStyleSheet()
    {
        return borderStyleSheet;
    }

    public SiteKey getOwnerSiteKey()
    {
        return ownerSiteKey;
    }

    public void setStylesheet( XMLDocument stylesheet )
    {
        this.stylesheet = stylesheet;
    }

    public void setMethodCalls( Element methodCalls )
    {
        this.methodCalls = methodCalls;
    }

    public void setObjectParameters( Element objectParameters )
    {
        this.objectParameters = objectParameters;
    }

    public void setBorderParameters( Element borderParameters )
    {
        this.borderParameters = borderParameters;
    }

    public void setBorderStyleSheet( XMLDocument borderStyleSheet )
    {
        this.borderStyleSheet = borderStyleSheet;
    }

    public void setDocumentDoc( Document documentDoc )
    {
        this.documentDoc = documentDoc;
    }

    public void setShoppingCartDoc( Document cartDoc )
    {
        shoppingCartDoc = cartDoc;
    }

    public void setOwnerSiteKey( SiteKey key )
    {
        this.ownerSiteKey = key;
    }

    /**
     * Validate that the required data has been retrieved.
     */
    public boolean isValid()
    {
        return ( ownerSiteKey == null || stylesheet == null || methodCalls == null ) ? false : true;
    }

    /**
     * @param cacheType
     */
    public void setCacheType( String cacheType )
    {
        this.cacheType = cacheType;
    }

    /**
     * @return
     */
    public boolean isCacheDisabled()
    {
        return cacheDisabled;
    }

    /**
     * @return
     */
    public Integer getCacheTime()
    {
        return cacheTime;
    }

    /**
     * @return
     */
    public String getCacheType()
    {
        return cacheType;
    }

    /**
     * @return
     */
    public String getName()
    {
        return name;
    }

    public void setCacheDisabled( boolean b )
    {
        cacheDisabled = b;
    }


    public void setCacheTime( Integer value )
    {
        cacheTime = value;
    }


    public void setName( String string )
    {
        name = string;
    }

    /**
     * @return Returns the borderStyleSheetKey.
     */
    public ResourceKey getBorderStyleSheetKey()
    {
        return borderStyleSheetKey;
    }

    /**
     * @param borderStyleSheetKey The borderStyleSheetKey to set.
     */
    public void setBorderStyleSheetKey( ResourceKey borderStyleSheetKey )
    {
        this.borderStyleSheetKey = borderStyleSheetKey;
    }

    /**
     * @return Returns the styleSheetKey.
     */
    public ResourceKey getStyleSheetKey()
    {
        return styleSheetKey;
    }

    /**
     * @param styleSheetKey The styleSheetKey to set.
     */
    public void setStyleSheetKey( ResourceKey styleSheetKey )
    {
        this.styleSheetKey = styleSheetKey;
    }
}
