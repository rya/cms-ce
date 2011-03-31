/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.upgrade.service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionCallback;

import com.enonic.esl.sql.model.Database;
import com.enonic.esl.sql.model.DatabaseSchemaTool;
import com.enonic.esl.sql.model.Table;
import com.enonic.esl.sql.model.View;

import com.enonic.cms.framework.jdbc.DialectConnectionDecorator;
import com.enonic.cms.framework.jdbc.dialect.Db2Dialect;
import com.enonic.cms.framework.jdbc.dialect.Dialect;
import com.enonic.cms.framework.jdbc.dialect.H2Dialect;
import com.enonic.cms.framework.jdbc.dialect.OracleDialect;

import com.enonic.cms.store.DatabaseAccessor;
import com.enonic.cms.upgrade.UpgradeContext;
import com.enonic.cms.upgrade.log.UpgradeLog;

import com.enonic.cms.domain.SiteKey;

public final class UpgradeContextImpl
    implements UpgradeContext
{
    private final UpgradeLog log;

    private final SqlOperationHelper sqlHelper;

    private final int startModelNumber;

    private int currentModelNumber = -1;

    private final PropertyResolver propertyResolver;

    //default transaction timeout for upgrade is 24 hours
    //timeout in seconds: 24 hours (3600 * 24 = 86400)
    private int transactionTimeout = 86400;

    public UpgradeContextImpl( UpgradeLog log, PropertyResolver propertyResolver, SqlOperationHelper sqlHelper, int startModelNumber )
    {
        this.log = log;
        this.sqlHelper = sqlHelper;
        this.startModelNumber = startModelNumber;
        this.propertyResolver = propertyResolver;
    }

    public Dialect getDialect()
    {
        return this.sqlHelper.getDialect();
    }

    private Database getDatabase()
    {
        return DatabaseAccessor.getDatabase( currentModelNumber );
    }

    public int getCurrentModelNumber()
    {
        return this.currentModelNumber;
    }

    public int getStartModelNumber()
    {
        return this.startModelNumber;
    }

    public void setCurrentModelNumber( int currentModelNumber )
    {
        this.currentModelNumber = currentModelNumber;
    }

    public void updateModelNumber( int modelNumber )
        throws Exception
    {
        this.sqlHelper.updateModelNumber( modelNumber );
    }

    public void logInfo( String message )
    {
        this.log.logInfo( this.currentModelNumber, message );
    }

    public void logWarning( String message )
    {
        this.log.logWarning( this.currentModelNumber, message );
    }

    public void logWarning( String message, Throwable cause )
    {
        this.log.logWarning( this.currentModelNumber, message, cause );
    }

    public void logError( String message )
    {
        this.log.logError( this.currentModelNumber, message );
    }

    public void logError( String message, Throwable cause )
    {
        this.log.logError( this.currentModelNumber, message, cause );
    }

    public void createTable( String tableName )
        throws Exception
    {
        final Table table = this.getDatabase().getTable( tableName );
        executeStatement( DatabaseSchemaTool.generateCreateTable( table ), false );

        // creates both foreign keys and indexes
        createTableConstraints( tableName, true );
    }

    public void createViews( String... viewNames )
        throws Exception
    {
        for ( String viewName : viewNames )
        {
            final View view = this.getDatabase().getView( viewName );
            executeStatement( DatabaseSchemaTool.generateCreateView( view ), false );
        }
    }

    private void createTableIndexes( String tableName, boolean logSql )
        throws Exception
    {
        final List<String> statements = DatabaseSchemaTool.generateCreateIndexes( this.getDatabase().getTable( tableName ) );
        executeStatements( statements, logSql );
    }

    private void dropTableIndexes( String tableName, boolean logSql )
        throws Exception
    {

        Connection conn = null;

        try
        {
            conn = getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            Set<String> primaryKeys = getPrimaryKeys( metaData, tableName );
            Collection<String> indexNames = getIndexNames( metaData, tableName, primaryKeys );
            for ( String indexName : indexNames )
            {
                String sql = getDialect().translateDropIndex( tableName, indexName );
                executeStatement( sql, logSql );
            }
        }
        finally
        {
            close( conn );
        }

    }

    private void createTableForeignKeys( String tableName, boolean logSql )
        throws Exception
    {
        executeStatements( DatabaseSchemaTool.generateCreateForeignKeys( this.getDatabase().getTable( tableName ) ), logSql );
    }

    public void createTableConstraints( String tableName, boolean logSql )
        throws Exception
    {
        createTableIndexes( tableName, logSql );
        createTableForeignKeys( tableName, logSql );
    }

    public void dropTableConstraints( String tableName, boolean logSql )
        throws Exception
    {
        /* Dropping foreign keys before indexes. Some databases allways create a matching index for each foreign key */
        dropTableForeignKeys( tableName, logSql );
        dropTableIndexes( tableName, logSql );
    }

    private void dropTableForeignKeys( String tableName, boolean logSql )
        throws Exception
    {

        Connection conn = null;

        try
        {
            conn = getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            Collection<String> keys = getForeignKeys( metaData, tableName );
            for ( String key : keys )
            {
                String sql = getDialect().translateDropForeignKey( tableName, key );
                executeStatement( sql, logSql );
            }
        }
        finally
        {
            close( conn );
        }

    }

    public void dropTable( String tableName )
        throws Exception
    {
        executeStatement( "DROP TABLE " + tableName, false );
    }

    public void dropColumn( String tableName, String columnName )
        throws Exception
    {
        executeStatement( "ALTER TABLE " + tableName + " DROP COLUMN " + columnName, false );
    }

    public void dropView( String viewName )
        throws Exception
    {
        executeStatement( "DROP VIEW " + viewName, false );
    }

    public void dropViews( String... views )
        throws Exception
    {
        for ( String view : views )
        {
            dropView( view );
        }
    }

    public void createNewLastKey( String tableName, int lastKey )
        throws SQLException
    {
        getJdbcTemplate().execute(
            "INSERT INTO tKey (key_sTableName, key_lLastKey) VALUES ('" + tableName.toLowerCase() + "', " + lastKey + ")" );
    }

    public int executeUpdate( String statement, Object[] args )
        throws Exception
    {
        return getJdbcTemplate().update( statement, args );
    }

    public int executeUpdate( String statement, Object[] args, int[] jdbcTypes )
        throws Exception
    {
        return getJdbcTemplate().update( statement, args, jdbcTypes );
    }

    private void executeStatement( String sql, boolean logSql )
        throws Exception
    {
        Connection conn = null;
        Statement stmt = null;

        try
        {
            if ( logSql )
            {
                logInfo( sql );
            }
            conn = getConnection();
            stmt = conn.createStatement();
            stmt.execute( sql );
        }
        finally
        {
            close( stmt );
            close( conn );
        }
    }

    private void executeStatements( List list, boolean logSql )
        throws Exception
    {
        for ( Object value : list )
        {
            String sql = (String) value;
            executeStatement( sql, logSql );
        }
    }

    public Connection getConnection()
        throws SQLException
    {
        return this.sqlHelper.getConnection();
    }

    public JdbcTemplate getJdbcTemplate()
        throws SQLException
    {
        // return new JdbcTemplate( new SingleConnectionDataSource( getConnection(), true ) );
        return new UpdateJdbcTemplate( new DialectConnectionDecorator( sqlHelper.getDialect() ), sqlHelper.getUndecoratedDataSource() );
    }

    public void close( Connection conn )
    {
        this.sqlHelper.close( conn );
    }

    public void close( Statement stmt )
    {
        this.sqlHelper.close( stmt );
    }

    public void close( ResultSet result )
    {
        this.sqlHelper.close( result );
    }

    private Set<String> getPrimaryKeys( DatabaseMetaData metaData, String tableName )
        throws SQLException
    {
        Set<String> pks = new HashSet<String>();
        ResultSet resultSet = null;

        try
        {

            resultSet = metaData.getPrimaryKeys( null, getCurrentSchema(), tableName );

            while ( resultSet.next() )
            {
                String name = resultSet.getString( "PK_NAME" );

                if ( name != null )
                {
                    pks.add( name );
                }
            }
        }
        finally
        {
            if ( resultSet != null )
            {
                resultSet.close();
            }
        }

        return pks;
    }

    private Collection<String> getIndexNames( DatabaseMetaData metaData, String tableName, Set<String> primaryKeys )
        throws SQLException
    {

        Set<String> indexNames = new HashSet<String>();

        ResultSet resultSet = null;

        try
        {
            final String currentSchema = getCurrentSchema();
            final String table = getTableName( metaData, tableName );
            resultSet = metaData.getIndexInfo( null, null, table, false, false );
            while ( resultSet.next() )
            {
                String name = resultSet.getString( "INDEX_NAME" );
                boolean nonUnique = resultSet.getBoolean( "NON_UNIQUE" );
                String qualifier = resultSet.getString( "INDEX_QUALIFIER" );
                if ( isOracleDatabase() && !currentSchema.equalsIgnoreCase( resultSet.getString( "TABLE_SCHEM" ) ) )
                {
                    continue;
                }

                if ( isH2Database() )
                {
                    // Ignore qualifier on H2 database (should be placed inside dialect)
                    qualifier = null;
                }

                if ( qualifier != null && qualifier.trim().length() > 0 )
                {
                    /* some databases give empty ("") qualifier */
                    name = qualifier + "." + name;
                }

                if ( ( name != null ) && !primaryKeys.contains( name ) && nonUnique )
                {

                    indexNames.add( name );
                }
            }
        }
        finally
        {
            if ( resultSet != null )
            {
                resultSet.close();
            }
        }

        return indexNames;
    }

    private Set<String> getForeignKeys( DatabaseMetaData metaData, String tableName )
        throws SQLException
    {
        Set<String> keys = new HashSet<String>();

        ResultSet resultSet = null;
        try
        {
            resultSet = metaData.getImportedKeys( null, getCurrentSchema(), getTableName( metaData, tableName ) );
            while ( resultSet.next() )
            {
                String name = resultSet.getString( "FK_NAME" );
                if ( name != null )
                {
                    keys.add( name );
                }
            }
        }
        finally
        {
            if ( resultSet != null )
            {
                resultSet.close();
            }
        }
        return keys;
    }

    private String getTableName( DatabaseMetaData metaData, String tableName )
        throws SQLException
    {
        ResultSet resultSet = null;
        try
        {
            resultSet = metaData.getTables( null, getCurrentSchema(), "%", null );
            while ( resultSet.next() )
            {
                String name = resultSet.getString( "TABLE_NAME" );
                if ( name != null && name.equalsIgnoreCase( tableName ) )
                {
                    return name;
                }
            }
        }
        finally
        {
            if ( resultSet != null )
            {
                resultSet.close();
            }
        }
        return tableName;
    }

    public void reorganizeTablesForDb2( String... tables )
        throws Exception
    {
        if ( !isDb2Database() )
        {
            return;
        }
        logWarning( "Db2Dialect: Reorganizing tables" );

        // Below code illustrates that it is possible to automate this (find tables that needs reorg) :
        // Create generalt "cleanup" prcedure by selecting tables that requires reorg
        // select TABSCHEMA, TABNAME from SYSIBMADM.ADMINTABINFO where REORG_PENDING = 'Y'
        // CALL SYSPROC.ADMIN_CMD('reorg table <TABSCHEMA>.<TABNAME>')

        for ( String table : tables )
        {
            getJdbcTemplate().execute( "CALL SYSPROC.ADMIN_CMD('reorg table " + table + "')" );
        }
    }

    private String getCurrentSchema()
        throws SQLException
    {
        if ( isOracleDatabase() )
        {
            // Only Oracle (as far as we know) needs schema to differ from other schemas when the user as rights across
            // schemas.
            List rows = getJdbcTemplate().queryForList( "select sys_context( 'userenv', 'current_schema' ) as s from dual", String.class );
            if ( rows.size() > 0 )
            {
                return (String) rows.get( 0 );
            }
        }

        return null;
    }

    private boolean isDb2Database()
    {
        return ( getDialect() instanceof Db2Dialect );
    }

    private boolean isOracleDatabase()
    {
        return ( getDialect() instanceof OracleDialect );
    }

    private boolean isH2Database()
    {
        return ( getDialect() instanceof H2Dialect );
    }

    public void dropViews( boolean logSql )
        throws Exception
    {
        executeStatements( DatabaseSchemaTool.generateDropViews( getDatabase() ), logSql );
    }

    public void createViews( boolean logSql )
        throws Exception
    {
        executeStatements( DatabaseSchemaTool.generateCreateViews( getDatabase() ), logSql );
    }

    public String getProperty( String name )
    {
        return this.propertyResolver.getProperty( name );
    }

    public String getConfigDirPath()
    {
        return this.propertyResolver.getConfigDirPath();
    }


    public String getProperty( SiteKey siteKey, String name )
    {
        return this.propertyResolver.getProperty( siteKey, name );
    }

    public int generateNextKey( String tableName )
        throws Exception
    {
        return this.sqlHelper.generateNextKey( tableName );
    }

//    public Object execute( TransactionCallback callback )
//    {
//        return this.sqlHelper.execute( callback );
//    }

    public Object execute( TransactionCallback callback )
    {
        final int defaultTimeout = sqlHelper.getTransactionTimeout();
        sqlHelper.setTransactionTimeout( transactionTimeout );
        Object object = this.sqlHelper.execute( callback );
        sqlHelper.setTransactionTimeout( defaultTimeout );
        return object;
    }

    public int getTransactionTimeout()
    {
        return transactionTimeout;
    }

}
