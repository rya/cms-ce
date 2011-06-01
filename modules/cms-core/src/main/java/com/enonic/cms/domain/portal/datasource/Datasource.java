/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.portal.datasource;

import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;

public class Datasource
{
    private Element xmlElement;

    public Datasource( Element element )
    {
        xmlElement = element;
    }

    public Element getXmlElement()
    {
        return xmlElement;
    }

    public String getMethodName()
    {
        Element methodNameEl = xmlElement.getChild( "methodname" );
        if ( methodNameEl == null )
        {
            return null;
        }

        String methodName = methodNameEl.getText();
        if ( ( methodName == null ) || ( methodName.length() == 0 ) )
        {
            return null;
        }
        return methodName;
    }

    public List getParameterElements()
    {
        Element parametersElem = xmlElement.getChild( "parameters" );
        return parametersElem.getChildren();
    }

    public String getResultElementName()
    {
        String value = xmlElement.getAttributeValue( "result-element" );
        if ( value == null )
        {
            return null;
        }
        if ( value.trim().length() == 0 )
        {
            return null;
        }

        return value;
    }

    public String getCondition()
    {
        Attribute conditionAttr = xmlElement.getAttribute( "condition" );
        if ( conditionAttr == null )
        {
            return null;
        }
        return conditionAttr.getValue().trim();
    }

    public boolean isCacheable()
    {
        String methodname = getMethodName();

        // we cant cache getPreferences calls, cause they depend on objectKey, which change within a request
        if ( methodname!= null && methodname.startsWith( "getPreferences" ) )
        {
            return false;
        }

        Attribute cacheableAttr = xmlElement.getAttribute( "cache" );
        if ( cacheableAttr != null )
        {
            return Boolean.valueOf( cacheableAttr.getValue().trim() );
        }

        // default value except plugins
        return true;
    }
}
