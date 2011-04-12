/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.google.common.base.Preconditions;

import com.enonic.cms.framework.util.JDOMUtil;

public class Datasources
{
    private Element datasourcesEl;

    private DatasourcesType type;

    private String resultRootName;

    public Datasources( DatasourcesType type )
    {
        this.type = type;
        Element rootElement = new Element( "pagetemplatedata" );
        new Document( rootElement );
        datasourcesEl = new Element( "datasources" );
        rootElement.addContent( datasourcesEl );
        resultRootName = datasourcesEl.getAttributeValue( "result-element" );
    }

    public Datasources( DatasourcesType type, Element datasourcesEl )
    {
        Preconditions.checkNotNull( type );
        Preconditions.checkNotNull( datasourcesEl );
        Preconditions.checkArgument( "datasources".equals( datasourcesEl.getName() ),
                                     "Expected datasources to be the root element, but was: " + datasourcesEl.getName() );

        this.type = type;
        this.datasourcesEl = datasourcesEl;
        resultRootName = datasourcesEl.getAttributeValue( "result-element" );
    }

    public boolean isOfType( DatasourcesType x )
    {
        return this.type.equals( x );
    }

    public boolean hasSessionContext()
    {
        return hasAttributeValue( "true", "sessioncontext", datasourcesEl );
    }

    public boolean hasHttpContext()
    {
        return hasAttributeValue( "true", "httpcontext", datasourcesEl );
    }

    public boolean hasCookieContext()
    {
        return hasAttributeValue( "true", "cookiecontext", datasourcesEl );
    }

    public boolean hasStyleContext()
    {
        return hasAttributeValue( "true", "stylecontext", datasourcesEl );
    }

    public boolean isDocumentTrue()
    {
        return hasAttributeValue( "true", "document", datasourcesEl );
    }

    public boolean hasShoppingCart()
    {
        return isShoppingCartSetToFull() || isShoppingCartSetToSummary();
    }

    public boolean isShoppingCartSetToFull()
    {
        return "full".equals( datasourcesEl.getAttributeValue( "shoppingcart" ) );
    }

    public boolean isShoppingCartSetToSummary()
    {
        return "summary".equals( datasourcesEl.getAttributeValue( "shoppingcart" ) );
    }

    public List<Datasource> getDatasourceElements()
    {
        ArrayList<Datasource> datasources = new ArrayList<Datasource>();
        for ( Element datasourceElement : JDOMUtil.getElements( datasourcesEl ) )
        {
            datasources.add( new Datasource( datasourceElement ) );
        }
        return datasources;
    }

    public boolean isCacheable()
    {
        boolean cacheable = !hasSessionContext();
        cacheable &= !hasHttpContext();
        cacheable &= !hasCookieContext();
        cacheable &= !hasShoppingCart();
        return cacheable;
    }

    private boolean hasAttributeValue( String expectedValue, String attrName, Element el )
    {
        return expectedValue.equals( el.getAttributeValue( attrName ) );
    }

    public String getResultRootName()
    {
        return resultRootName;
    }
}
