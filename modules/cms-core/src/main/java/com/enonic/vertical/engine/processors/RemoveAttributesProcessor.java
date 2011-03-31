/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.processors;

import org.w3c.dom.Element;

public class RemoveAttributesProcessor
    implements ElementProcessor
{
    private String[] attributeNames;

    public RemoveAttributesProcessor( String[] attributeNames )
    {
        this.attributeNames = attributeNames;
    }

    /**
     * @see com.enonic.vertical.engine.base.ElementProcessor#process(org.w3c.dom.Element)
     */
    public void process( Element elem )
    {
        for ( int i = 0; i < attributeNames.length; i++ )
        {
            elem.removeAttribute( attributeNames[i] );
        }
    }

}
