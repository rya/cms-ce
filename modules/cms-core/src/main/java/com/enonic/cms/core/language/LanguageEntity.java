/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.language;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class LanguageEntity
    implements Serializable
{
    private LanguageKey key;

    private String code;

    private String description;

    private Date timestamp;

    public LanguageEntity()
    {
        // Default constructor used by Hibernate.
    }

    public LanguageEntity( LanguageEntity source )
    {
        this();

        this.key = source.getKey();
        this.code = source.getCode();
        this.description = source.getDescription();
        this.timestamp = source.getTimestamp();
    }

    public LanguageKey getKey()
    {
        return key;
    }

    public String getCode()
    {
        return code;
    }

    public String getDescription()
    {
        return description;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setKey( LanguageKey key )
    {
        this.key = key;
    }

    public void setCode( String code )
    {
        this.code = code;
    }

    public void setDescription( String description )
    {
        this.description = description;
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
        if ( !( o instanceof LanguageEntity ) )
        {
            return false;
        }

        LanguageEntity that = (LanguageEntity) o;

        if ( !key.equals( that.getKey() ) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 857, 945 ).append( key ).toHashCode();
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s.append( "key = " ).append( getKey() );
        s.append( ", code = " ).append( getCode() );
        return s.toString();
    }
}
