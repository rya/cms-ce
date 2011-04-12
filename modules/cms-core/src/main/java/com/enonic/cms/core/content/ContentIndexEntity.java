/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.io.Serializable;
import java.util.Date;

import com.enonic.cms.core.content.category.CategoryKey;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class ContentIndexEntity
    implements Serializable
{

    private String key;

    private ContentKey contentKey;

    private int contentStatus;

    private Date contentPublishFrom;

    private Date contentPublishTo;

    private CategoryKey categoryKey;

    private int contentTypeKey;

    private String path;

    private String value;

    private String orderValue;

    private Float numValue;

    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }

    public ContentKey getContentKey()
    {
        return contentKey;
    }

    public void setContentKey( ContentKey contentKey )
    {
        this.contentKey = contentKey;
    }

    public CategoryKey getCategoryKey()
    {
        return categoryKey;
    }

    public void setCategoryKey( CategoryKey categoryKey )
    {
        this.categoryKey = categoryKey;
    }

    public int getContentTypeKey()
    {
        return contentTypeKey;
    }

    public void setContentTypeKey( int contentTypeKey )
    {
        this.contentTypeKey = contentTypeKey;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath( String path )
    {
        this.path = path;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public String getOrderValue()
    {
        return orderValue;
    }

    public void setOrderValue( String orderValue )
    {

        if ( orderValue == null || orderValue.length() == 0 )
        {
            orderValue = "#";
        }
        this.orderValue = orderValue;
    }

    public Float getNumValue()
    {
        return numValue;
    }

    public void setNumValue( Float value )
    {
        numValue = value;
    }

    public void setContentStatus( int value )
    {
        contentStatus = value;
    }

    public void setPublishFrom( Date value )
    {
        contentPublishFrom = value;
    }

    public void setPublishTo( Date value )
    {
        contentPublishTo = value;
    }

    public int getContentStatus()
    {
        return contentStatus;
    }

    public Date getContentPublishFrom()
    {
        return contentPublishFrom;
    }

    public Date getContentPublishTo()
    {
        return contentPublishTo;
    }

    public boolean valueEquals( ContentIndexEntity other )
    {
        if ( this == other )
        {
            return true;
        }
        if ( other == null )
        {
            return false;
        }

        if ( categoryKey == null )
        {
            if ( other.categoryKey != null )
            {
                return false;
            }
        }
        else if ( !categoryKey.equals( other.categoryKey ) )
        {
            return false;
        }
        if ( contentKey == null )
        {
            if ( other.contentKey != null )
            {
                return false;
            }
        }
        else if ( !contentKey.equals( other.contentKey ) )
        {
            return false;
        }
        if ( contentPublishFrom == null )
        {
            if ( other.contentPublishFrom != null )
            {
                return false;
            }
        }
        else if ( !contentPublishFrom.equals( other.contentPublishFrom ) )
        {
            return false;
        }
        if ( contentPublishTo == null )
        {
            if ( other.contentPublishTo != null )
            {
                return false;
            }
        }
        else if ( !contentPublishTo.equals( other.contentPublishTo ) )
        {
            return false;
        }
        if ( contentStatus != other.contentStatus )
        {
            return false;
        }
        if ( contentTypeKey != other.contentTypeKey )
        {
            return false;
        }
        if ( numValue == null )
        {
            if ( other.numValue != null )
            {
                return false;
            }
        }
        else if ( !numValue.equals( other.numValue ) )
        {
            return false;
        }
        if ( orderValue == null )
        {
            if ( other.orderValue != null )
            {
                return false;
            }
        }
        else if ( !orderValue.equals( other.orderValue ) )
        {
            return false;
        }
        if ( path == null )
        {
            if ( other.path != null )
            {
                return false;
            }
        }
        else if ( !path.equals( other.path ) )
        {
            return false;
        }
        if ( value == null )
        {
            if ( other.value != null )
            {
                return false;
            }
        }
        else if ( !value.equals( other.value ) )
        {
            return false;
        }
        return true;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ContentIndexEntity ) )
        {
            return false;
        }

        ContentIndexEntity that = (ContentIndexEntity) o;

        return key.equals( that.getKey() );
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 923, 479 ).append( key ).toHashCode();
    }

}
