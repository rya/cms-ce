/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.upgrade;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionCallback;

import com.enonic.cms.framework.jdbc.dialect.Dialect;

import com.enonic.cms.core.SiteKey;

public interface UpgradeContext
{
    public int getTransactionTimeout();

    public Dialect getDialect();

    public int getCurrentModelNumber();

    public int getStartModelNumber();

    public void setCurrentModelNumber( int currentModelNumber );

    public void updateModelNumber( int modelNumber )
        throws Exception;

    public void logInfo( String message );

    public void logWarning( String message );

    public void logWarning( String s, Throwable cause );

    public void logError( String message );

    public void logError( String message, Throwable cause );

    public void createTable( String tableName )
        throws Exception;

    public void createViews( String... viewNames )
        throws Exception;

    public void createTableConstraints( String tableName, boolean logSql )
        throws Exception;

    public void dropTableConstraints( String tableName, boolean logSql )
        throws Exception;

    public void dropTable( String tableName )
        throws Exception;

    public void dropColumn( String tableName, String columnName )
        throws Exception;

    public void dropView( String viewName )
        throws Exception;

    public void dropViews( String... views )
        throws Exception;

    public void createNewLastKey( String tableName, int lastKey )
        throws SQLException;

    public int executeUpdate( String statement, Object[] args )
        throws Exception;

    public int executeUpdate( String statement, Object[] args, int[] jdbcTypes )
        throws Exception;

    public Connection getConnection()
        throws SQLException;

    public JdbcTemplate getJdbcTemplate()
        throws Exception;

    public void close( Connection conn );

    public void close( Statement stmt );

    public void close( ResultSet result );

    public void reorganizeTablesForDb2( String... tables )
        throws Exception;

    public void dropViews( boolean logSql )
        throws Exception;

    public void createViews( boolean logSql )
        throws Exception;

    public String getProperty( String propName );

    public String getProperty( SiteKey siteKey, String propName );

    public int generateNextKey( String tableName )
        throws Exception;

    public Object execute( TransactionCallback callback );

    public String getConfigDirPath();

}
