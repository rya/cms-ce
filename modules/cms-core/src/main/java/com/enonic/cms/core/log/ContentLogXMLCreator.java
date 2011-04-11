/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.log;

import org.jdom.Element;

import com.enonic.cms.core.content.ContentXMLCreator;
import com.enonic.cms.store.dao.ContentDao;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.log.LogEntryEntity;
import com.enonic.cms.domain.log.Table;

public class ContentLogXMLCreator
    extends LogXMLCreator
{

    private ContentXMLCreator contentXMLCreator = new ContentXMLCreator();

    private boolean includeContentData = false;

    private ContentDao contentDao;

    public ContentLogXMLCreator()
    {
        contentXMLCreator.setIncludeRelatedContentsInfo( false );
        contentXMLCreator.setIncludeRepositoryPathInfo( true );

    }

    public ContentXMLCreator getContentXMLCreator()
    {
        return contentXMLCreator;
    }

    public void setContentXMLCreator( ContentXMLCreator contentXMLCreator )
    {
        this.contentXMLCreator = contentXMLCreator;
    }

    public boolean isIncludeContentData()
    {
        return includeContentData;
    }

    public void setIncludeContentData( boolean includeContentData )
    {
        this.includeContentData = includeContentData;
    }

    public ContentDao getContentDao()
    {
        return contentDao;
    }

    public void setContentDao( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    protected Element doCreateLogElement( LogEntryEntity logEntry )
    {

        Element logEl = super.doCreateLogElement( logEntry );
        if ( includeContentData && logEntry.getKeyValue() != null && logEntry.getTableKey().equals( Table.CONTENT.asInteger() ) )
        {
            ContentEntity content = contentDao.findByKey( new ContentKey( logEntry.getKeyValue() ) );
            logEl.addContent( contentXMLCreator.createSingleContentVersionElement( null, content.getMainVersion() ) );
        }

        return logEl;
    }


}
