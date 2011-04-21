/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jdom.Document;

import java.io.Serializable;
import java.util.Date;

public class ContentHandlerEntity
    implements Serializable
{
    private ContentHandlerKey key;

    private String name;

    private String className;

    private String description;

    private Document xmlConfig;

    private Date timestamp;

    public ContentHandlerKey getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public String getClassName()
    {
        return className;
    }

    public String getDescription()
    {
        return description;
    }

    public Document getXmlConfig()
    {
        return xmlConfig;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setKey( ContentHandlerKey key )
    {
        this.key = key;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setClassName( String className )
    {
        this.className = className;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public void setXmlConfig( Document xmlConfig )
    {
        this.xmlConfig = xmlConfig;
    }

    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ContentHandlerEntity ) )
        {
            return false;
        }

        ContentHandlerEntity that = (ContentHandlerEntity) o;

        if ( !getKey().equals( that.getKey() ) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 633, 531 ).append( key ).toHashCode();
    }
}
