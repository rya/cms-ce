/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.springframework.util.Assert;

import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.domain.LanguageKey;
import com.enonic.cms.domain.content.ContentAccessEntity;
import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.binary.BinaryDataAndBinary;
import com.enonic.cms.domain.content.contentdata.ContentData;

/**
 * Created by rmy - Date: Jun 3, 2009
 */
public abstract class BaseContentCommand
{
    private DateTime availableFrom;

    private DateTime availableTo;

    private String changeComment;

    private Integer priority;

    private LanguageKey language;

    private ContentData contentData;

    private final SortedMap<String, ContentAccessEntity> contentAccessRights = new TreeMap<String, ContentAccessEntity>();

    protected List<BinaryDataAndBinary> binaryDatas = new ArrayList<BinaryDataAndBinary>();

    private boolean useCommandsBinaryDataToAdd = false;

    public Collection<ContentAccessEntity> getContentAccessRights()
    {
        return contentAccessRights.values();
    }

    public boolean hasContentAccessRight( ContentAccessEntity contentAccess )
    {
        return contentAccessRights.containsKey( contentAccess.getGroup().getGroupKey().toString() );
    }

    public void addContentAccessRights( Collection<ContentAccessEntity> values, ContentEntity content )
    {
        for ( ContentAccessEntity contentAccess : values )
        {
            contentAccess.setContent( content );
            contentAccessRights.put( contentAccess.getGroup().getGroupKey().toString(), contentAccess );
        }
    }

    public DateTime getAvailableFrom()
    {
        return availableFrom;
    }

    public Date getAvailableFromAsDate()
    {
        return availableFrom != null ? availableFrom.toDate() : null;
    }

    public void setAvailableFrom( Date availableFrom )
    {
        if ( availableFrom != null )
        {
            this.availableFrom = new DateTime( availableFrom ).minuteOfHour().roundFloorCopy();
        }
        else
        {
            this.availableFrom = null;
        }
    }

    public DateTime getAvailableTo()
    {
        return availableTo;
    }

    public Date getAvailableToAsDate()
    {
        return availableTo != null ? availableTo.toDate() : null;
    }

    public void setAvailableTo( Date availableTo )
    {
        if ( availableTo != null )
        {
            this.availableTo = new DateTime( availableTo ).minuteOfHour().roundFloorCopy();
        }
        else
        {
            this.availableTo = null;
        }
    }

    public Integer getPriority()
    {
        return priority;
    }

    public void setPriority( Integer priority )
    {
        this.priority = priority;
    }

    public LanguageKey getLanguage()
    {
        return language;
    }

    public void setLanguage( LanguageEntity value )
    {
        Assert.notNull( value );
        this.language = value.getKey();
    }

    public void setLanguage( LanguageKey value )
    {
        Assert.notNull( value );
        this.language = value;
    }

    public ContentData getContentData()
    {
        return contentData;
    }

    public void setContentData( ContentData contentData )
    {
        this.contentData = contentData;
    }

    public List<BinaryDataAndBinary> getBinaryDatas()
    {
        return binaryDatas;
    }

    public void setBinaryDatas( List<BinaryDataAndBinary> value )
    {
        this.binaryDatas = value;
    }

    public boolean useCommandsBinaryDataToAdd()
    {
        return useCommandsBinaryDataToAdd;
    }

    public void setUseCommandsBinaryDataToAdd( boolean useCommandsBinaryDataToAdd )
    {
        this.useCommandsBinaryDataToAdd = useCommandsBinaryDataToAdd;
    }

    public String getChangeComment()
    {
        return changeComment;
    }

    public void setChangeComment( String changeComment )
    {
        this.changeComment = changeComment;
    }
}
