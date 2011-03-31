/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.upgrade.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterDisposer;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;

import com.enonic.cms.framework.jdbc.ConnectionDecorator;

public class UpdateJdbcTemplate
    extends JdbcTemplate
{

    private ConnectionDecorator connectionDecorator;

    public UpdateJdbcTemplate( final ConnectionDecorator decorator, final DataSource dataSource )
    {
        super( dataSource );
        this.connectionDecorator = decorator;
    }

    private Connection decorateConnection( Connection con )
    {
        try
        {
            return connectionDecorator.decorate( con );
        }
        catch ( SQLException e )
        {
            DataSourceUtils.releaseConnection( con, getDataSource() );
            con = null;
            throw getExceptionTranslator().translate( "decorate connection", null, e );
        }

    }

    public Object execute( StatementCallback action )
        throws DataAccessException
    {
        Assert.notNull( action, "Callback object must not be null" );

        Connection con = DataSourceUtils.getConnection( getDataSource() );
        //Dette er trikset som gjør at underliggende connection fremstår som den samme for Spring
        //uavhengig av decoration eller ei
        Connection decoratedCon = decorateConnection( con );
        Statement stmt = null;
        try
        {
            Connection conToUse = con;
            if ( getNativeJdbcExtractor() != null && getNativeJdbcExtractor().isNativeConnectionNecessaryForNativeStatements() )
            {
                conToUse = getNativeJdbcExtractor().getNativeConnection( con );
            }
            stmt = decoratedCon.createStatement();
            applyStatementSettings( stmt );
            Statement stmtToUse = stmt;
            if ( getNativeJdbcExtractor() != null )
            {
                stmtToUse = getNativeJdbcExtractor().getNativeStatement( stmt );
            }
            Object result = action.doInStatement( stmtToUse );
            handleWarnings( stmt );
            return result;
        }
        catch ( SQLException ex )
        {
            // Release Connection early, to avoid potential connection pool deadlock
            // in the case when the exception translator hasn't been initialized yet.
            JdbcUtils.closeStatement( stmt );
            stmt = null;
            DataSourceUtils.releaseConnection( con, getDataSource() );
            con = null;
            throw getExceptionTranslator().translate( "StatementCallback", getSql( action ), ex );
        }
        finally
        {
            JdbcUtils.closeStatement( stmt );
            DataSourceUtils.releaseConnection( con, getDataSource() );
        }
    }

    public Object execute( PreparedStatementCreator psc, PreparedStatementCallback action )
        throws DataAccessException
    {

        Assert.notNull( psc, "PreparedStatementCreator must not be null" );
        Assert.notNull( action, "Callback object must not be null" );

        Connection con = DataSourceUtils.getConnection( getDataSource() );
        //Dette er trikset som gjør at underliggende connection fremstår som den samme for Spring
        //uavhengig av decoration eller ei
        Connection decoratedCon = decorateConnection( con );
        PreparedStatement ps = null;
        try
        {
            Connection conToUse = con;
            if ( getNativeJdbcExtractor() != null && getNativeJdbcExtractor().isNativeConnectionNecessaryForNativePreparedStatements() )
            {
                conToUse = getNativeJdbcExtractor().getNativeConnection( con );
            }
            ps = psc.createPreparedStatement( decoratedCon );
            applyStatementSettings( ps );
            PreparedStatement psToUse = ps;
            if ( getNativeJdbcExtractor() != null )
            {
                psToUse = getNativeJdbcExtractor().getNativePreparedStatement( ps );
            }
            Object result = action.doInPreparedStatement( psToUse );
            handleWarnings( ps );
            return result;
        }
        catch ( SQLException ex )
        {
            // Release Connection early, to avoid potential connection pool deadlock
            // in the case when the exception translator hasn't been initialized yet.
            if ( psc instanceof ParameterDisposer )
            {
                ( (ParameterDisposer) psc ).cleanupParameters();
            }
            String sql = getSql( psc );
            psc = null;
            JdbcUtils.closeStatement( ps );
            ps = null;
            DataSourceUtils.releaseConnection( con, getDataSource() );
            con = null;
            throw getExceptionTranslator().translate( "PreparedStatementCallback", sql, ex );
        }
        finally
        {
            if ( psc instanceof ParameterDisposer )
            {
                ( (ParameterDisposer) psc ).cleanupParameters();
            }
            JdbcUtils.closeStatement( ps );
            DataSourceUtils.releaseConnection( con, getDataSource() );
        }
    }

    public Object execute( CallableStatementCreator csc, CallableStatementCallback action )
        throws DataAccessException
    {

        Assert.notNull( csc, "CallableStatementCreator must not be null" );
        Assert.notNull( action, "Callback object must not be null" );

        Connection con = DataSourceUtils.getConnection( getDataSource() );
        //Dette er trikset som gjør at underliggende connection fremstår som den samme for Spring
        //uavhengig av decoration eller ei
        Connection decoratedCon = decorateConnection( con );
        CallableStatement cs = null;
        try
        {
            Connection conToUse = con;
            if ( getNativeJdbcExtractor() != null )
            {
                conToUse = getNativeJdbcExtractor().getNativeConnection( con );
            }
            cs = csc.createCallableStatement( decoratedCon );
            applyStatementSettings( cs );
            CallableStatement csToUse = cs;
            if ( getNativeJdbcExtractor() != null )
            {
                csToUse = getNativeJdbcExtractor().getNativeCallableStatement( cs );
            }
            Object result = action.doInCallableStatement( csToUse );
            handleWarnings( cs );
            return result;
        }
        catch ( SQLException ex )
        {
            // Release Connection early, to avoid potential connection pool deadlock
            // in the case when the exception translator hasn't been initialized yet.
            if ( csc instanceof ParameterDisposer )
            {
                ( (ParameterDisposer) csc ).cleanupParameters();
            }
            String sql = getSql( csc );
            csc = null;
            JdbcUtils.closeStatement( cs );
            cs = null;
            DataSourceUtils.releaseConnection( con, getDataSource() );
            con = null;
            throw getExceptionTranslator().translate( "CallableStatementCallback", sql, ex );
        }
        finally
        {
            if ( csc instanceof ParameterDisposer )
            {
                ( (ParameterDisposer) csc ).cleanupParameters();
            }
            JdbcUtils.closeStatement( cs );
            DataSourceUtils.releaseConnection( con, getDataSource() );
        }
    }

    // copied from JdbcTemplate

    private static String getSql( Object sqlProvider )
    {
        if ( sqlProvider instanceof SqlProvider )
        {
            return ( (SqlProvider) sqlProvider ).getSql();
        }
        else
        {
            return null;
        }
    }


}
