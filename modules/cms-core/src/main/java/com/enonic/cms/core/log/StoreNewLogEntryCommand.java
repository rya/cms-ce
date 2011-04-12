/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.log;

import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.security.user.UserKey;
import org.jdom.Document;

/**
 * Jul 9, 2009
 */
public class StoreNewLogEntryCommand
{
    private LogType type;

    private Table table;

    private Integer tableKeyValue;

    private Integer count;

    private String inetAddress;

    private String path;

    private String title;

    private Document xmlData;

    private UserKey user;

    private SiteEntity site;

    public LogType getType()
    {
        return type;
    }

    public void setType( LogType type )
    {
        this.type = type;
    }

    public Table getTable()
    {
        return table;
    }

    public void setTableKey( Table tableKey )
    {
        this.table = tableKey;
    }

    public Integer getCount()
    {
        return count;
    }

    public void setCount( Integer count )
    {
        this.count = count;
    }

    public Integer getTableKeyValue()
    {
        return tableKeyValue;
    }

    public void setTableKeyValue( Integer tableKeyValue )
    {
        this.tableKeyValue = tableKeyValue;
    }

    public String getInetAddress()
    {
        return inetAddress;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath( String path )
    {
        this.path = path;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public Document getXmlData()
    {
        return xmlData;
    }

    public void setXmlData( Document xmlData )
    {
        this.xmlData = xmlData;
    }

    public UserKey getUser()
    {
        return user;
    }

    public void setUser( UserKey user )
    {
        this.user = user;
    }

    public SiteEntity getSite()
    {
        return site;
    }

    public void setSite( SiteEntity site )
    {
        this.site = site;
    }
}
