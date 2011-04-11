/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.log;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.enonic.cms.framework.time.TimeService;

import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.store.dao.LogEntryDao;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.domain.log.LogEntryEntity;
import com.enonic.cms.domain.log.LogEntryKey;
import com.enonic.cms.domain.log.StoreNewLogEntryCommand;
import com.enonic.cms.domain.security.user.UserEntity;

public class LogServiceImpl
    implements LogService
{
    @Autowired
    private LogEntryDao logEntryDao;

    @Autowired
    private TimeService timeService;

    @Autowired
    private UserDao userDao;

    private static final int PATH_FIELD_MAX_LENGTH = 256;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public LogEntryKey storeNew( LogEntryEntity logEntry )
    {

        if ( logEntry.getType() == null )
        {
            throw new IllegalArgumentException( "Type must be set (nullable = no )" );
        }
        if ( logEntry.getUser() == null )
        {
            throw new IllegalArgumentException( "User must be set (nullable = no )" );
        }
        if ( logEntry.getTitle() == null )
        {
            throw new IllegalArgumentException( "Title must be set (nullable = no )" );
        }
        if ( logEntry.getXmlData() == null )
        {
            throw new IllegalArgumentException( "XML data must be set (nullable = no )" );
        }
        if ( logEntry.getTimestamp() == null )
        {
            throw new IllegalArgumentException( "Timestamp must be set (nullable = no )" );
        }

        logEntryDao.storeNew( logEntry );
        return logEntry.getKey();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public LogEntryKey storeNew( StoreNewLogEntryCommand command )
    {
        Assert.notNull( command.getType(), "type cannot be nul" );
        Assert.notNull( command.getUser(), "user cannot be nul" );
        Assert.notNull( command.getTitle(), "title cannot be nul" );

        HttpServletRequest httpRequest = ServletRequestAccessor.getRequest();
        String clientInetAddress = null;
        if ( httpRequest != null )
        {
            clientInetAddress = httpRequest.getRemoteAddr();
        }

        UserEntity user = userDao.findByKey( command.getUser() );

        LogEntryEntity logEntry = new LogEntryEntity();
        logEntry.setType( command.getType().asInteger() );
        logEntry.setTimestamp( timeService.getNowAsDateTime().toDate() );
        logEntry.setInetAddress( clientInetAddress );
        logEntry.setUser( user );
        logEntry.setTableKey( command.getTable().asInteger() );
        logEntry.setKeyValue( command.getTableKeyValue() );
        logEntry.setTitle( command.getTitle() );

        // logEntry.setSite( .. );

        logEntry.setPath( enshurePathWithinBoundary( command.getPath() ) );

        if ( command.getXmlData() != null )
        {
            logEntry.setXmlData( command.getXmlData() );
        }

        else

        {
            logEntry.setXmlData( createEmptyXmlData() );
        }

        logEntryDao.storeNew( logEntry );
        return logEntry.getKey();
    }

    private String enshurePathWithinBoundary( String suggestedPath )
    {
        String path = suggestedPath;

        if ( StringUtils.isNotEmpty( path ) && path.length() > PATH_FIELD_MAX_LENGTH )
        {
            String pathTooLongEnding = " ...";

            path = path.substring( 0, PATH_FIELD_MAX_LENGTH - pathTooLongEnding.length() );
            path = path + pathTooLongEnding;
        }
        return path;
    }

    private Document createEmptyXmlData()
    {

        Element root = new Element( "data" );
        return new Document( root );
    }


}
