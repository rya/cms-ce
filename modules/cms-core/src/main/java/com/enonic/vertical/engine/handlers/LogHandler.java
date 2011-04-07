/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.VerticalCreateException;
import com.enonic.vertical.engine.VerticalEngineLogger;
import com.enonic.vertical.engine.VerticalRemoveException;
import com.enonic.vertical.engine.VerticalUpdateException;
import com.enonic.vertical.engine.XDG;
import com.enonic.vertical.engine.processors.ProcessElementException;
import com.enonic.vertical.event.ContentHandlerListener;
import com.enonic.vertical.event.MenuHandlerEvent;
import com.enonic.vertical.event.MenuHandlerListener;

import com.enonic.cms.domain.log.LogType;
import com.enonic.cms.domain.log.StoreNewLogEntryCommand;
import com.enonic.cms.domain.log.Table;
import com.enonic.cms.domain.security.user.User;
import com.enonic.cms.domain.security.userstore.UserStoreEntity;
import com.enonic.cms.domain.security.userstore.UserStoreKey;


public final class LogHandler
    extends BaseHandler
    implements ContentHandlerListener, MenuHandlerListener
{

    /**
     * Create new entries in the log. Root element in the XML is assumed to be &lt;logentry&gt; or &lt;logentries&gt;.
     *
     * @parameter xmlData The log entry XML.
     */
    public String[] createLogEntries( User user, Document logentriesDoc )
        throws VerticalCreateException
    {

        Element rootElem = logentriesDoc.getDocumentElement();
        Element[] logentryElems;
        if ( rootElem.getTagName().equals( "logentries" ) )
        {
            logentryElems = XMLTool.getElements( rootElem, "logentry" );
        }
        else
        {
            logentryElems = new Element[]{rootElem};
        }

        List<Element> readLogentryElems = new LinkedList<Element>();
        List<Element> otherLogentryElems = new LinkedList<Element>();

        for ( int i = 0; i < logentryElems.length; i++ )
        {
            int typeKey = Integer.parseInt( logentryElems[i].getAttribute( "typekey" ) );
            final LogType logType = LogType.parse( typeKey );
            if ( logType == LogType.LOGIN || logType == LogType.LOGOUT )
            {
                // set userkey
                logentryElems[i].setAttribute( "userkey", String.valueOf( user.getKey() ) );

                // set title: user name
                StringBuffer userName = new StringBuffer( user.getDisplayName() );
                userName.append( " (" );
                userName.append( user.getName() );
                userName.append( ')' );
                Element titleElem = XMLTool.getElement( logentryElems[i], "title" );
                if ( titleElem != null )
                {
                    XMLTool.removeChildNodes( titleElem );
                    XMLTool.createTextNode( logentriesDoc, logentryElems[i], userName.toString() );
                }
                else
                {
                    XMLTool.createElement( logentriesDoc, logentryElems[i], "title", userName.toString() );
                }

                otherLogentryElems.add( logentryElems[i] );
                break;
            }
            else if ( logType == LogType.LOGIN_USERSTORE )
            {
                // set userkey
                logentryElems[i].setAttribute( "userkey", String.valueOf( user.getKey() ) );

                UserStoreKey userStoreKey = new UserStoreKey( logentryElems[i].getAttribute( "userstorekey" ) );
                UserStoreEntity userStore = userStoreDao.findByKey( userStoreKey );
                // set title: site name
                //int siteKey = Integer.parseInt(logentryElems[i].getAttribute("sitekey"));

                //SiteHandler siteHandler = getSiteHandler();
                //String siteName = siteHandler.getSiteName(siteKey);
                Element titleElem = XMLTool.getElement( logentryElems[i], "title" );
                if ( titleElem != null )
                {
                    XMLTool.removeChildNodes( titleElem );
                    XMLTool.createTextNode( logentriesDoc, logentryElems[i], userStore.getName() );
                }
                else
                {
                    XMLTool.createElement( logentriesDoc, logentryElems[i], "title", userStore.getName() );
                }

                otherLogentryElems.add( logentryElems[i] );
                break;
            }
            //getDomainHandler().hvaSkjerHer();
            else if ( logType == LogType.LOGIN_FAILED )
            {
                User anonymousUser = getUserHandler().getAnonymousUser();
                logentryElems[i].setAttribute( "userkey", String.valueOf( anonymousUser.getKey() ) );

                otherLogentryElems.add( logentryElems[i] );
                break;
            }
            else if ( logType == LogType.ENTITY_CREATED )
            {

            }
            else if ( logType == LogType.ENTITY_UPDATED )
            {

            }
            else if ( logType == LogType.ENTITY_UPDATED )
            {

            }
            else if ( logType == LogType.ENTITY_OPENED )
            {

                int tableKey = Integer.parseInt( logentryElems[i].getAttribute( "tablekey" ) );
                int tableKeyValue = Integer.parseInt( logentryElems[i].getAttribute( "tablekeyvalue" ) );

                if ( Table.CONTENT.asInteger() == tableKey )
                {
                    ContentHandler contentHandler = getContentHandler();
                    if ( typeKey != LogType.ENTITY_REMOVED.asInteger() )
                    {
                        //int siteKey = contentHandler.getSiteKey(tableKeyValue);
                        //logentryElems[i].setAttribute("sitekey", String.valueOf(siteKey));
                    }
                    String path = contentHandler.getCategoryPathString( tableKeyValue ).toString();
                    logentryElems[i].setAttribute( "path", path );
                    break;
                }
                else if ( Table.MENUITEM.asInteger() == tableKey )
                {
                    break;
                }

                if ( typeKey != LogType.ENTITY_OPENED.asInteger() )
                {
                    // set user key
                    logentryElems[i].setAttribute( "userkey", String.valueOf( user.getKey() ) );
                    otherLogentryElems.add( logentryElems[i] );
                }
                else
                {
                    // set read count
                    logentryElems[i].setAttribute( "count", "1" );
                    readLogentryElems.add( logentryElems[i] );
                }
            }
        }

        CommonHandler commonHandler = getCommonHandler();

        String[] keys = null;

        try

        {
            if ( readLogentryElems.size() > 0 && otherLogentryElems.size() > 0 )
            {
                // NOTE! Either we will get read logs from the presentation layer or we would get
                //       other log entries from the admin-console (like create,update,remove and login)
                String msg = "Cannot create both read log entries and other log entries!";
                VerticalEngineLogger.fatalEngine( this.getClass(), 0, msg, null );
            }
            else
            {
                Object[] array = commonHandler.createEntities( null, logentriesDoc, null );
                keys = new String[array.length];
                System.arraycopy( array, 0, keys, 0, array.length );
            }
        }

        catch (

            ProcessElementException pee

            )

        {
            // should not be thrown when no processors asigned
            VerticalEngineLogger.fatalEngine( this.getClass(), 0, "Ignored exception!", pee );
        }

        return keys;
    }

    /**
     * @see com.enonic.vertical.event.MenuHandlerListener#createdMenuItem(com.enonic.vertical.event.MenuHandlerEvent)
     */
    public void createdMenuItem( MenuHandlerEvent e )
        throws VerticalCreateException
    {

        StoreNewLogEntryCommand command = new StoreNewLogEntryCommand();
        command.setTableKey( Table.MENUITEM );
        command.setTableKeyValue( e.getMenuItemKey() );
        command.setType( LogType.ENTITY_CREATED );
        command.setUser( e.getUser().getKey() );
        command.setTitle( e.getTitle() );

        logService.storeNew( command );
    }

    public void removedMenuItem( MenuHandlerEvent e )
        throws VerticalRemoveException
    {
        StoreNewLogEntryCommand command = new StoreNewLogEntryCommand();
        command.setTableKey( Table.MENUITEM );
        command.setTableKeyValue( e.getMenuItemKey() );
        command.setType( LogType.ENTITY_REMOVED );
        command.setUser( e.getUser().getKey() );
        command.setTitle( e.getTitle() );

        logService.storeNew( command );
    }

    public void updatedMenuItem( MenuHandlerEvent e )
        throws VerticalUpdateException
    {
        StoreNewLogEntryCommand command = new StoreNewLogEntryCommand();
        command.setTableKey( Table.MENUITEM );
        command.setTableKeyValue( e.getMenuItemKey() );
        command.setType( LogType.ENTITY_UPDATED );
        command.setUser( e.getUser().getKey() );
        command.setTitle( e.getTitle() );

        logService.storeNew( command );
    }

    public int getReadCount( int tableKey, int tableKeyValue )
    {
        Column[] whereColumns = {db.tLogEntry.len_lTableKey, db.tLogEntry.len_lKeyValue};
        int[] paramValues = {tableKey, tableKeyValue};
        StringBuffer sql = XDG.generateSelectSQL( db.tLogEntry, db.tLogEntry.len_lCount.getCustomColumn( "sum" ), false, whereColumns );
        XDG.appendWhereSQL( sql, db.tLogEntry.len_lTypeKey, XDG.OPERATOR_EQUAL, LogType.ENTITY_OPENED.asInteger() );
        CommonHandler commonHandler = getCommonHandler();
        int readCount = commonHandler.getInt( sql.toString(), paramValues );
        if ( readCount > 0 )
        {
            return readCount;
        }
        else
        {
            return 0;
        }
    }
}
