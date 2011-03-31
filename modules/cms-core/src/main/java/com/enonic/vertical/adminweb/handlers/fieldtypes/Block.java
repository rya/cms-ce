/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.fieldtypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.xml.XMLTool;


public final class Block
{
    private String xPath;

    private int index;

    private ArrayList<Field> inputFields = new ArrayList<Field>();

    public Block( Element groupElem, HashMap fieldMapping )
    {
        if ( fieldMapping == null )
        {
            // regular content create, no import
            xPath = groupElem.getAttribute( "group" );
            if ( xPath.startsWith( "contentdata/" ) )
            {
                xPath = xPath.substring( "contentdata/".length() );
            }
        }
        else if ( fieldMapping.containsKey( "_block_" + groupElem.getAttribute( "name" ) ) )
        {
            xPath = (String) fieldMapping.get( "_block_" + groupElem.getAttribute( "name" ) );
        }
        else
        {
            xPath = "";
        }
        index = XMLTool.getElementIndex( groupElem ) + 1;

        Element[] inputElems = XMLTool.getElements( groupElem, "input" );
        for ( int i = 0; i < inputElems.length; i++ )
        {
            Field field = FieldFactory.getField( inputElems[i] );
            String fieldName = field.getName();
            if ( fieldMapping != null && fieldMapping.containsKey( fieldName ) )
            {
                String fieldXPath = (String) fieldMapping.get( fieldName );
                field.setXPath( fieldXPath );
            }
            else if ( fieldMapping != null )
            {
                field.setXPath( null );
            }

            if ( fieldMapping != null && fieldMapping.containsKey( fieldName + "_relationMap" ) )
            {
                Map relationMap = (Map) fieldMapping.get( fieldName + "_relationMap" );
                field.setRelationMap( relationMap );
            }

            inputFields.add( field );
        }
    }

    public final void XMLToMultiValueMap( Element contentDataElem, MultiValueMap fields )
    {
        if ( xPath.length() > 0 )
        {
            Element[] groupElems = XMLTool.selectElements( contentDataElem, xPath );
            for ( int i = 0; i < groupElems.length; i++ )
            {
                fields.put( "group" + index + "_counter", "" );
                for ( int j = 0; j < inputFields.size(); j++ )
                {
                    Field inputField = inputFields.get( j );

                    String xPath = inputField.getXPath();
                    if ( xPath != null )
                    {
                        NodeList dataNodes = XMLTool.selectNodes( groupElems[i], xPath );
                        if ( dataNodes != null && dataNodes.getLength() > 0 )
                        {
                            for ( int k = 0; k < dataNodes.getLength(); k++ )
                            {
                                Node dataNode = dataNodes.item( k );
                                inputField.XMLToMultiValueMap( inputField.getName(), dataNode, fields, i );
                            }
                        }
                        else
                        {
                            inputField.XMLToMultiValueMap( inputField.getName(), fields );
                        }
                    }
                }
            }
        }
        else
        {
            for ( int i = 0; i < inputFields.size(); i++ )
            {
                Field inputField = inputFields.get( i );
                String xPath = inputField.getXPath();
                if ( xPath != null )
                {
                    NodeList dataNodes = XMLTool.selectNodes( contentDataElem, xPath );
                    if ( dataNodes != null && dataNodes.getLength() > 0 )
                    {
                        for ( int k = 0; k < dataNodes.getLength(); k++ )
                        {
                            Node dataNode = dataNodes.item( k );
                            inputField.XMLToMultiValueMap( inputField.getName(), dataNode, fields, 1 );
                        }
                    }
                    else
                    {
                        inputField.XMLToMultiValueMap( inputField.getName(), fields );
                    }
                }
            }
        }
    }

}
