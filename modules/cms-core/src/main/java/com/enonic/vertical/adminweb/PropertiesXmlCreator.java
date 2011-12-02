package com.enonic.vertical.adminweb;

import java.util.Map;
import java.util.Properties;

import org.jdom.Element;

public class PropertiesXmlCreator
{

    public Element createElement( String elementName, String childName, Properties properties )
    {
        Element el = new Element( elementName );

        for ( Object key : properties.keySet() )
        {
            el.addContent( createElement( childName, (String) key, (String) properties.get( key ) ) );
        }

        return el;
    }


    public Element createElement( String elementName, String childName, Map<Object, Object> properties )
    {
        Element el = new Element( elementName );

        for ( Object key : properties.keySet() )
        {
            el.addContent( createElement( childName, (String) key, (String) properties.get( key ) ) );
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
