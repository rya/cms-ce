/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.tools;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.esl.sql.model.Database;
import com.enonic.esl.sql.model.DatabaseSchemaTool;
import com.enonic.esl.sql.model.Table;

import com.enonic.cms.framework.jdbc.DatabaseTool;
import com.enonic.cms.framework.jdbc.dialect.Dialect;

import com.enonic.cms.store.DatabaseAccessor;
import com.enonic.cms.store.support.ConnectionFactory;

import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.IndexService;
import com.enonic.cms.core.content.RegenerateIndexBatcher;

import com.enonic.cms.domain.content.contenttype.ContentTypeEntity;


public class ReindexContentToolServiceImpl
    implements ReindexContentToolService
{

    private final static String TCONTENTINDEX = "tContentIndex";

    private IndexService indexService;

    private ContentService contentService;

    private ConnectionFactory connectionFactory;

    private Database database;

    private Dialect dialect;

    /* timeout: 24 timer (3600 * 24 = 86400) */

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, timeout = 86400)
    public void reindexAllContent( List<String> logEntries )
    {

        Connection conn = getConnection();

        try
        {
            database = DatabaseAccessor.getLatestDatabase();
            doReindexAllContent( conn, logEntries );
        }
        catch ( Exception e )
        {

            StringBuffer message = new StringBuffer();
            message.append( e.getMessage() );

            Throwable cause = e.getCause();
            while ( cause != null )
            {
                message.append( "Caused by:" ).append( cause.getMessage() );
                cause = cause.getCause();
            }

            logEntries.add( message.toString() );
        }
        finally
        {
            close( conn );
        }
    }

    public void doReindexAllContent( Connection conn, List<String> logEntries )
        throws Exception
    {

        logEntries.clear();

        long globalStart = System.currentTimeMillis();

        //recreateTable( conn, logEntries );

        Collection<ContentTypeEntity> contentTypes = contentService.getAllContentTypes();

        logEntries.add( "Generating indexes for " + contentTypes.size() + " content types..." );

        int count = 1;
        for ( ContentTypeEntity contentType : contentTypes )
        {

            StringBuffer message = new StringBuffer();
            message.append( "Generating indexes for '" ).append( contentType.getName() ).append( "'" );
            message.append( " (#" ).append( count++ ).append( " of " ).append( contentTypes.size() ).append( ")..." );

            logEntries.add( message.toString() );

            long start = System.currentTimeMillis();

            RegenerateIndexBatcher batcher = new RegenerateIndexBatcher( indexService, contentService );
            final int batchSize = 10;
            //final int batchSize = 100;

            batcher.regenerateIndex( contentType, batchSize, logEntries );

            long end = System.currentTimeMillis();

            logEntries.add( "... index values generated in " + ( end - start ) + " ms" );
        }

        generateStatistics( conn, logEntries );

        long globalTimeUsed = ( System.currentTimeMillis() - globalStart ) / 1000;
        String timeUsed = globalTimeUsed > 240 ? globalTimeUsed / 60 + " min" : globalTimeUsed + " sec";

        logEntries.add( "Reindexing of all content types was successful!" );
        logEntries.add( "Total time used: " + timeUsed );

    }

    private void generateStatistics( Connection conn, List<String> logEntries )
        throws Exception
    {

        logEntries.add( "Generating/analyzing statistics for table '" + TCONTENTINDEX + "' ..." );
        executeStatement( conn, dialect.translateGenerateStatistics( TCONTENTINDEX ) );
        logEntries.add( "... generating/analyzing statistics was successful!" );
        conn.commit();

    }

    private void recreateTable( Connection conn, List<String> logEntries )
        throws Exception
    {

        logEntries.add( "Removing table '" + TCONTENTINDEX + "' ..." );
        executeStatement( conn, DatabaseTool.generateDropTable( TCONTENTINDEX ) );
        conn.commit();

        Table table = this.database.getTable( TCONTENTINDEX );
        executeStatement( conn, DatabaseSchemaTool.generateCreateTable( table ) );
        conn.commit();

        // add indexes
        executeStatements( conn, DatabaseSchemaTool.generateCreateIndexes( this.database.getTable( TCONTENTINDEX ) ) );
        conn.commit();

        // add foreign keys
        executeStatements( conn, DatabaseSchemaTool.generateCreateForeignKeys( this.database.getTable( TCONTENTINDEX ) ) );
        conn.commit();

        logEntries.add( "... table successfully created." );
    }

    /**
     * Execute statements.
     */
    private void executeStatements( Connection conn, List list )
        throws Exception
    {
        for ( Object value : list )
        {
            String sql = (String) value;
            executeStatement( conn, sql );
        }
    }

    /**
     * Execute statement.
     */
    private void executeStatement( Connection conn, String sql )
        throws Exception
    {
        Statement stmt = null;

        try
        {
            conn = getConnection();
            stmt = conn.createStatement();
            stmt.execute( sql );
        }
        finally
        {
            close( stmt );
        }
    }

    /**
     * Return the connection.
     */
    public Connection getConnection()
    {
        try
        {
            return this.connectionFactory.getConnection( true );
        }
        catch ( SQLException e )
        {
            throw new RuntimeException( "Failed to get connection", e );
        }
    }

    /**
     * Close statement.
     */
    public void close( Statement stmt )
    {
        JdbcUtils.closeStatement( stmt );
    }

    /**
     * Close connection.
     */
    public void close( Connection conn )
    {
        this.connectionFactory.releaseConnection( conn );
    }

    @Autowired
    public void setIndexService( @Qualifier("indexService") IndexService value )
    {
        this.indexService = value;
    }

    @Autowired
    public void setContentService( @Qualifier("contentService") ContentService value )
    {
        this.contentService = value;
    }

    @Autowired
    public void setConnectionFactory( @Qualifier("connectionFactory") ConnectionFactory value )
    {
        this.connectionFactory = value;
    }

    @Autowired
    public void setDialect( @Qualifier("dialectFactory") Dialect value )
    {
        this.dialect = value;
    }
}
