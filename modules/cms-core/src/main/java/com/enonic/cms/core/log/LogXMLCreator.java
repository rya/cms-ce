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

import com.enonic.cms.domain.CmsDateAndTimeFormats;
import com.enonic.cms.domain.log.LogEntryEntity;
import com.enonic.cms.domain.log.LogEntryResultSet;

public class LogXMLCreator
{
    public static final String DEFUALT_RESULTROOTNAME = "logs";

    private String resultRootName;

    public LogXMLCreator()
    {
    }

    public String getResultRootName()
    {
        return resultRootName;
    }

    public void setResultRootName( final String value )
    {
        resultRootName = value;
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

    protected Element doCreateLogElement( LogEntryEntity logEntry )
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

        return logEl;
    }

    private Element createLogsElement()
    {
        if ( resultRootName != null && resultRootName.length() > 0 )
        {
            return new Element( resultRootName );
        }
        return new Element( DEFUALT_RESULTROOTNAME );
    }

    /**
     * If the given date is a valid date, it is set as an attribute in the content, overriding any previous values if they exist. If the
     * given date is not valid, no changes are performed.
     *
     * @param content   The Element of which to add this attribute.
     * @param attribute The name of the attribute.
     * @param d         The date containing the value to set.
     */
    private static void setDateAttributeConditional( Element log, String attribute, Date d )
    {
        if ( d != null )
        {
            log.setAttribute( attribute, CmsDateAndTimeFormats.printAs_XML_DATE( d ) );
        }
    }


}
