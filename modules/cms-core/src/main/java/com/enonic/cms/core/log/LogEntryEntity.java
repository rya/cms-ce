/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.log;

import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.framework.util.LazyInitializedJDOMDocument;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jdom.Document;

import java.io.Serializable;
import java.util.Date;

public class LogEntryEntity
    implements Serializable
{

    private LogEntryKey key;

    private Integer type;

    private Integer tableKey;

    private Integer count;

    private Integer keyValue;

    private String inetAddress;

    private String path;

    private String title;

    private LazyInitializedJDOMDocument xmlData;

    private Date timestamp = new Date();

    private UserEntity user;

    private SiteEntity site;


    public LogEntryKey getKey()
    {
        return key;
    }

    public Integer getType()
    {
        return type;
    }

    public Integer getTableKey()
    {
        return tableKey;
    }

    public Integer getCount()
    {
        return count;
    }

    public Integer getKeyValue()
    {
        return keyValue;
    }

    public String getInetAddress()
    {
        return inetAddress;
    }

    public String getPath()
    {
        return path;
    }

    public String getTitle()
    {
        return title;
    }

    public Document getXmlData()
    {
        return xmlData.getDocument();
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public UserEntity getUser()
    {
        return user;
    }

    public SiteEntity getSite()
    {
        return site;
    }

    public void setKey( LogEntryKey key )
    {
        this.key = key;
    }

    public void setType( Integer type )
    {
        this.type = type;
    }

    public void setTableKey( Integer tableKey )
    {
        this.tableKey = tableKey;
    }

    public void setCount( Integer count )
    {
        this.count = count;
    }

    public void setKeyValue( Integer keyValue )
    {
        this.keyValue = keyValue;
    }

    public void setInetAddress( String inetAddress )
    {
        this.inetAddress = inetAddress;
    }

    public void setPath( String path )
    {
        this.path = path;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public void setXmlData( Document value )
    {
        if ( value == null )
        {
            this.xmlData = null;
        }
        else
        {
            this.xmlData = LazyInitializedJDOMDocument.parse( value );
        }
    }

    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
    }

    public void setUser( UserEntity user )
    {
        this.user = user;
    }

    public void setSite( SiteEntity site )
    {
        this.site = site;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof LogEntryEntity) )
        {
            return false;
        }

        LogEntryEntity that = (LogEntryEntity) o;

        if ( !key.equals( that.getKey() ) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        final int initialNonZeroOddNumber = 645;
        final int multiplierNonZeroOddNumber = 387;
        return new HashCodeBuilder( initialNonZeroOddNumber, multiplierNonZeroOddNumber ).append( key ).toHashCode();
    }
}
