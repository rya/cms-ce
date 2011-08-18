package com.enonic.vertical.adminweb;

import java.util.Map;
import java.util.Properties;

import org.jdom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 8/17/11
 * Time: 8:58 AM
 */
public class PropertiesXmlCreator
{

    public Element createPropertiesElement( String elementName, String childName, Properties properties )
    {
        Element el = new Element( elementName );

        for ( Object key : properties.keySet() )
        {
            el.addContent( createElement( childName, (String) key, (String) properties.get( key ) ) );
        }

        return el;
    }


    public Element createPropertiesElement( String elementName, String childName, Map<String, String> properties )
    {
        Element el = new Element( elementName );

        for ( String key : properties.keySet() )
        {
            el.addContent( createElement( childName, key, properties.get( key ) ) );
        }

        return el;
    }

    private Element createElement( String name, String key, String value )
    {
        Element el = new Element( name );
        el.setAttribute( "name", key );
        el.setAttribute( "value", value );

        return el;
    }

}
