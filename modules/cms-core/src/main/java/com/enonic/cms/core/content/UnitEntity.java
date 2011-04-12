/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;

public class UnitEntity
    implements Serializable
{
    private int key;

    private String name;

    private String description;

    private Date timestamp;

    private int deleted;

    private UnitEntity parent;

    private LanguageEntity language;

    private Set<ContentTypeEntity> contentTypes;

    public int getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public boolean isDeleted()
    {
        return deleted != 0;
    }

    public UnitEntity getParent()
    {
        return parent;
    }

    public LanguageEntity getLanguage()
    {
        return language;
    }

    public Set<ContentTypeEntity> getContentTypes()
    {
        return contentTypes;
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

    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
    }

    public void setDeleted( boolean deleted )
    {
        this.deleted = deleted ? 1 : 0;
    }

    public void setParent( UnitEntity parent )
    {
        this.parent = parent;
    }

    public void setLanguage( LanguageEntity language )
    {
        this.language = language;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof UnitEntity ) )
        {
            return false;
        }

        UnitEntity that = (UnitEntity) o;

        return key == that.getKey();

    }

    public int hashCode()
    {
        return new HashCodeBuilder( 549, 363 ).append( key ).toHashCode();
    }
}
