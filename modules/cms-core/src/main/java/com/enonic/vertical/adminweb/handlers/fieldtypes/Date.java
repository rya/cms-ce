/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.fieldtypes;

import java.text.ParseException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.util.DateUtil;
import com.enonic.esl.xml.XMLTool;


public class Date
    extends Field
{

    public Date( Element inputElem )
    {
        super( inputElem );
    }

    public void XMLToMultiValueMap( String name, Node dataNode, MultiValueMap fields, int groupCounter )
    {
        String value;
        if ( dataNode instanceof Element )
        {
            value = XMLTool.getElementText( (Element) dataNode );
        }
        else
        {
            value = XMLTool.getNodeText( dataNode );
        }

        try
        {
            if ( value != null && value.length() > 0 )
            {
                fields.put( "date" + name, DateUtil.formatDateTime( DateUtil.parseISODate( value ) ) );
            }
            else
            {
                fields.put( "date" + name, null );
            }
        }
        catch ( ParseException pe )
        {
            throw new RuntimeException( "Unable to parse date: " + value );
        }
    }

}
