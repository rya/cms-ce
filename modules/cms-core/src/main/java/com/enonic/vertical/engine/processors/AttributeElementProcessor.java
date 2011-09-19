/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.processors;

import org.w3c.dom.Element;

public class AttributeElementProcessor
    implements ElementProcessor
{
    private String attributeName;

    private String attributeValue;

    public AttributeElementProcessor( String attributeName, String attributeValue )
    {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }

    public void process( Element elem )
    {
        elem.setAttribute( attributeName, attributeValue );
    }
}
