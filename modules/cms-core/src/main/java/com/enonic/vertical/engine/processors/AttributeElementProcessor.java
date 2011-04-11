/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.processors;

import java.util.Map;

import org.w3c.dom.Element;

public class AttributeElementProcessor
    implements ElementProcessor
{
    private String attributeName;

    private String attributeValue;

    private Map attributeValues;

    public AttributeElementProcessor( String attributeName, Map attributeValues )
    {
        this.attributeName = attributeName;
        this.attributeValues = attributeValues;
    }

    public AttributeElementProcessor( String attributeName, String attributeValue )
    {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }

    /**
     * @see com.enonic.vertical.engine.processors.ElementProcessor#process(org.w3c.dom.Element)
     */
    public void process( Element elem )
    {
        if ( attributeValues != null )
        {
            String key = elem.getAttribute( "key" );
            if ( key != null && key.length() > 0 )
            {
                if ( attributeValues.containsKey( key ) )
                {
                    elem.setAttribute( attributeName, attributeValues.get( key ).toString() );
                }
                else if ( attributeValues.containsKey( new Integer( Integer.parseInt( key ) ) ) )
                {
                    elem.setAttribute( attributeName, attributeValues.get( new Integer( Integer.parseInt( key ) ) ).toString() );
                }
            }
        }
        else
        {
            elem.setAttribute( attributeName, attributeValue );
        }
    }

}
