/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.fieldtypes;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.xml.XMLTool;


public class RelatedContent
    extends Field
{
    private boolean multiple;

    public RelatedContent( Element inputElem )
    {
        super( inputElem );
        this.multiple = !"false".equals( inputElem.getAttribute( "multiple" ) );
    }

    public void XMLToMultiValueMap( String name, Node dataNode, MultiValueMap fields, int groupCounter )
    {
        if ( getRelationMap() != null )
        {
            Map relationMap = getRelationMap();
            String key = XMLTool.getNodeText( dataNode );
            if ( key != null )
            {
                key = key.toLowerCase();
            }
            if ( relationMap.containsKey( key ) )
            {
                fields.put( name, relationMap.get( key ) );
                if ( fields.containsKey( name + "_counter" ) )
                {
                    List valueList = fields.getValueList( name + "_counter" );
                    if ( valueList.size() > groupCounter )
                    {
                        int counter = ( (Integer) valueList.get( groupCounter ) ).intValue() + 1;
                        valueList.set( groupCounter, new Integer( counter ) );
                        fields.remove( name + "_counter" );
                        fields.put( name + "_counter", valueList );
                    }
                    else
                    {
                        fields.put( name + "_counter", 1 );
                    }
                }
                else
                {
                    fields.put( name + "_counter", 1 );
                }
            }
            else
            {
                fields.put( name + "_counter", 0 );
            }
        }
        else if ( dataNode instanceof Element )
        {
            Element dataElem = (Element) dataNode;
            if ( multiple )
            {
                Element[] contentElems = XMLTool.getElements( dataElem, "content" );
                if ( contentElems.length == 0 )
                {
                    fields.put( name, null );
                }
                else
                {
                    for ( int i = 0; i < contentElems.length; i++ )
                    {
                        String key = contentElems[i].getAttribute( "key" );
                        fields.put( name, key );
                    }
                }
                fields.put( name + "_counter", contentElems.length );
            }
            else
            {
                String value = dataElem.getAttribute( "key" );
                if ( value != null && value.length() > 0 )
                {
                    fields.put( name, value );
                }
                else
                {
                    fields.put( name, null );
                }
            }
        }
    }

    public void setData( Element elem, String data )
    {
        if ( getRelationMap() != null )
        {
            XMLTool.createTextNode( elem.getOwnerDocument(), elem, data );
        }
        else if ( multiple )
        {
            XMLTool.createElement( elem.getOwnerDocument(), elem, "content" ).setAttribute( "key", data );
        }
        else
        {
            elem.setAttribute( "key", data );
        }
    }

}
