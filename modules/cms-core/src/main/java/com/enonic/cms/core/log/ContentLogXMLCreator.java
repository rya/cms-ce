/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.log;

import java.util.Date;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.CmsDateAndTimeFormats;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentXMLCreator;
import com.enonic.cms.store.dao.ContentDao;

public class ContentLogXMLCreator
{
    private ContentXMLCreator contentXMLCreator = new ContentXMLCreator();

    private boolean includeContentData = false;

    private ContentDao contentDao;

    public ContentLogXMLCreator()
    {
        contentXMLCreator.setIncludeRelatedContentsInfo( false );
        contentXMLCreator.setIncludeRepositoryPathInfo( true );
    }

    public void setIncludeContentData( boolean includeContentData )
    {
        this.includeContentData = includeContentData;
    }

    public void setContentDao( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    private Element doCreateLogElement( LogEntryEntity logEntry )
    {
        Element logEl = new Element( "log" );

        logEl.setAttribute( "key", logEntry.getKey().toString() );
        logEl.setAttribute( "type", Integer.toString( logEntry.getType() ) );
        logEl.setAttribute( "user", logEntry.getUser().getName() );
        logEl.setAttribute( "username", logEntry.getUser().getDisplayName() );
        setDateAttributeConditional( logEl, "timestamp", logEntry.getTimestamp() );
        logEl.addContent( new Element( "title" ).setText( logEntry.getTitle() ) );

        if ( logEntry.getTableKey() != null )
        {
            logEl.setAttribute( "tablekey", Integer.toString( logEntry.getTableKey() ) );
        }

        if ( logEntry.getKeyValue() != null )
        {
            logEl.setAttribute( "contentkey", Integer.toString( logEntry.getKeyValue() ) );
        }

        if ( logEntry.getCount() != null )
        {
            logEl.setAttribute( "count", Integer.toString( logEntry.getCount() ) );
        }

        if ( logEntry.getInetAddress() != null )
        {
            logEl.setAttribute( "inetaddress", logEntry.getInetAddress() );
        }

        if ( logEntry.getPath() != null )
        {
            logEl.setAttribute( "path", logEntry.getPath() );
        }
        if ( logEntry.getSite() != null )
        {
            logEl.setAttribute( "site", logEntry.getSite().getName() );
            logEl.setAttribute( "sitekey", logEntry.getSite().getKey().toString() );
        }

        if ( includeContentData && logEntry.getKeyValue() != null && logEntry.getTableKey().equals( Table.CONTENT.asInteger() ) )
        {
            ContentEntity content = contentDao.findByKey( new ContentKey( logEntry.getKeyValue() ) );
            logEl.addContent( contentXMLCreator.createSingleContentVersionElement( null, content.getMainVersion() ) );
        }

        return logEl;
    }

    public XMLDocument createLogsDocument( LogEntryResultSet logResult )
    {
        Element logsElements = createLogsElement();

        for ( LogEntryEntity logEntry : logResult.getLogEntries() )
        {
            logsElements.addContent( doCreateLogElement( logEntry ) );
        }

        logsElements.setAttribute( "totalcount", Integer.toString( logResult.getTotalCount() ) );
        logsElements.setAttribute( "count", Integer.toString( logResult.getLength() ) );
        logsElements.setAttribute( "index", Integer.toString( logResult.getFromIndex() ) );

        return XMLDocumentFactory.create( new Document( logsElements ) );
    }

    private Element createLogsElement()
    {
        return new Element( "logs" );
    }

    private void setDateAttributeConditional( Element log, String attribute, Date d )
    {
        if ( d != null )
        {
            log.setAttribute( attribute, CmsDateAndTimeFormats.printAs_XML_DATE( d ) );
        }
    }
}
