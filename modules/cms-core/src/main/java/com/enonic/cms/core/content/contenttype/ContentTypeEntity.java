/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.resource.ResourceKey;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.xml.XMLBytes;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentHandlerEntity;
import com.enonic.cms.core.content.ContentHandlerName;

public class ContentTypeEntity
    implements Serializable
{

    private int key;

    private String name;

    private String description;

    private XMLBytes data;

    private transient ContentTypeConfig contentTypeConfig;

    private Date timestamp;

    private ContentHandlerEntity handler;

    private ResourceKey defaultCssKey;

    private Set<CategoryEntity> categories;

    public ContentTypeEntity()
    {
    }

    public ContentTypeEntity( int key, String name )
    {
        this.key = key;
        this.name = name;
    }

    public int getKey()
    {
        return key;
    }

    public ContentTypeKey getContentTypeKey()
    {
        return new ContentTypeKey( key );
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public XMLBytes getData()
    {
        return data;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public ContentHandlerEntity getHandler()
    {
        return handler;
    }

    public ResourceKey getDefaultCssKey()
    {
        return defaultCssKey;
    }

    public Set<CategoryEntity> getCategories()
    {
        return categories;
    }

    public Collection<CategoryEntity> getCategories( boolean includeDeleted )
    {
        if ( includeDeleted )
        {
            return getCategories();
        }

        List<CategoryEntity> nonDeletedCategories = new ArrayList<CategoryEntity>();
        for ( CategoryEntity category : getCategories() )
        {
            if ( !category.isDeleted() )
            {
                nonDeletedCategories.add( category );
            }
        }
        return nonDeletedCategories;
    }

    public void setKey( int key )
    {
        this.key = key;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public void setData( XMLBytes data )
    {
        this.data = data;
        this.contentTypeConfig = null;
    }

    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
    }

    public void setHandler( ContentHandlerEntity handler )
    {
        this.handler = handler;
    }

    public void setDefaultCssKey( ResourceKey defaultCssKey )
    {
        this.defaultCssKey = defaultCssKey;
    }

    public void setCategories( Set<CategoryEntity> categories )
    {
        this.categories = categories;
    }

    public ContentTypeConfig getContentTypeConfig()
    {
        if ( this.contentTypeConfig == null )
        {
            this.contentTypeConfig = parseContentTypeConfig( getData() );
        }
        return this.contentTypeConfig;
    }

    private ContentTypeConfig parseContentTypeConfig( XMLBytes configData )
    {
        ContentHandlerName contentHandlerName = getContentHandlerName();

        if ( !ContentHandlerName.CUSTOM.equals( contentHandlerName ) )
        {
            throw new IllegalStateException( "This method is only supported when the content type based on the custom handler" );
        }

        if ( configData == null )
        {
            return null;
        }
        Document contentTypeDoc = configData.getAsJDOMDocument();
        if ( contentTypeDoc == null )
        {
            return null;
        }

        Element contentTypeRootEl = contentTypeDoc.getRootElement();
        if ( "config".equals( contentTypeRootEl.getName() ) )
        {
            return ContentTypeConfigParser.parse( contentHandlerName, contentTypeRootEl );
        }

        Element contentTypeConfigEl = contentTypeRootEl.getChild( "config" );
        if ( contentTypeConfigEl == null )
        {
            return null;
        }

        return ContentTypeConfigParser.parse( contentHandlerName, contentTypeConfigEl );
    }

    public ContentHandlerName getContentHandlerName()
    {
        return ContentHandlerName.parse( handler.getClassName() );
    }

    public List<ContentEntity> getAllContent( boolean includeDeleted )
    {
        ArrayList<ContentEntity> contents = new ArrayList<ContentEntity>();
        for ( CategoryEntity cat : getCategories() )
        {
            for ( ContentEntity content : cat.getContents() )
            {
                if ( includeDeleted || !content.isDeleted() )
                {
                    contents.add( content );
                }
            }
        }
        return contents;
    }

    public Element getIndexingParametersXML()
    {

        if ( getData() == null )
        {
            return new Element( "indexparameters" );
        }

        Document doc = getData().getAsJDOMDocument();

        if ( doc == null )
        {
            return new Element( "indexparameters" );
        }

        Element indexParamsEl = doc.getRootElement().getChild( "indexparameters" );

        if ( indexParamsEl == null )
        {
            indexParamsEl = new Element( "indexparameters" );
        }

        return indexParamsEl;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ContentTypeEntity ) )
        {
            return false;
        }

        ContentTypeEntity that = (ContentTypeEntity) o;

        return key == that.getKey();

    }

    public int hashCode()
    {
        return new HashCodeBuilder( 235, 923 ).append( key ).toHashCode();
    }
}
