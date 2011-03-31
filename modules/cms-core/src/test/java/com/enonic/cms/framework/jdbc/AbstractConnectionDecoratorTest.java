/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.mockito.Mockito;

import com.enonic.cms.framework.jdbc.delegate.DelegatingConnection;
import com.enonic.cms.framework.jdbc.delegate.DelegatingResultSet;
import com.enonic.cms.framework.jdbc.delegate.DelegatingStatement;

import static org.junit.Assert.*;

/**
 * Jan 19, 2010
 */
public abstract class AbstractConnectionDecoratorTest
{
    protected ResultSet realResultsetForStatement;

    protected ResultSet realResultsetForPreparedStatement;

    protected Statement realStatement;

    protected PreparedStatement realPreparedStatement;

    protected Connection realConnection;


    public void setupRealConnectionStatementAndResultSet()
        throws SQLException
    {
        realConnection = Mockito.mock( Connection.class );
        realStatement = Mockito.mock( Statement.class );
        realPreparedStatement = Mockito.mock( PreparedStatement.class );

        Mockito.when( realConnection.createStatement() ).thenReturn( realStatement );
        Mockito.when( realConnection.prepareStatement( Mockito.anyString() ) ).thenReturn( realPreparedStatement );

        realResultsetForStatement = Mockito.mock( ResultSet.class );
        realResultsetForPreparedStatement = Mockito.mock( ResultSet.class );

        Mockito.when( realStatement.executeQuery( Mockito.anyString() ) ).thenReturn( realResultsetForStatement );
        Mockito.when( realPreparedStatement.executeQuery() ).thenReturn( realResultsetForPreparedStatement );

        Mockito.when( realResultsetForStatement.getString( 1 ) ).thenReturn( "dummy" );
        Mockito.when( realResultsetForPreparedStatement.getString( 1 ) ).thenReturn( "dummy" );
    }

    public void testDecoratingLevel( ConnectionDecorator connectionDecorator, int levels )
        throws SQLException
    {
        testDecoratingLevelWithStatement( connectionDecorator, levels );
        testDecoratingLevelWithPreparedStatement( connectionDecorator, levels );
    }

    public void testDecoratingLevelWithStatement( ConnectionDecorator connectionDecorator, int levels )
        throws SQLException
    {
        Connection decoratedConnnection = decorate( connectionDecorator, realConnection, levels );

        assertDecoratedConnection( realConnection, decoratedConnnection, levels );

        // verify statement
        Statement decoratedStatement = decoratedConnnection.createStatement();
        assertDecoratedStatement( realStatement, decoratedStatement, levels );

        // verify resultset
        ResultSet decoratedResultSet = decoratedStatement.executeQuery( "dummy" );
        assertDelegatingResultSet( realResultsetForStatement, decoratedResultSet, levels );

        // verify get column
        String actualColumnValue = decoratedResultSet.getString( 1 );
        assertNotNull( actualColumnValue );
        assertEquals( "dummy", actualColumnValue );
    }

    public void testDecoratingLevelWithPreparedStatement( ConnectionDecorator connectionDecorator, int levels )
        throws SQLException
    {
        Connection decoratedConnnection = decorate( connectionDecorator, realConnection, levels );

        assertDecoratedConnection( realConnection, decoratedConnnection, levels );

        // verify statement
        PreparedStatement decoratedPreparedStatement = decoratedConnnection.prepareStatement( "dummySQL" );
        assertDecoratedStatement( realPreparedStatement, decoratedPreparedStatement, levels );

        // verify resultset
        ResultSet decoratedResultSet = decoratedPreparedStatement.executeQuery();
        assertDelegatingResultSet( realResultsetForPreparedStatement, decoratedResultSet, levels );

        // verify get column
        String actualColumnValue = decoratedResultSet.getString( 1 );
        assertNotNull( actualColumnValue );
        assertEquals( "dummy", actualColumnValue );
    }

    Connection decorate( ConnectionDecorator connectionDecorator, Connection connectionToBeDecorated, int times )
        throws SQLException
    {
        if ( times <= 0 )
        {
            return connectionToBeDecorated;
        }

        Connection decorated = connectionDecorator.decorate( connectionToBeDecorated );
        return decorate( connectionDecorator, decorated, times - 1 );
    }

    void assertDecoratedConnection( Connection real, Connection decorated, int numberOfLevelsToReal )
    {
        assertNotSame( real, decorated );
        assertTrue( "decorated Connection not DelegatingConnection at level " + numberOfLevelsToReal,
                    decorated instanceof DelegatingConnection );

        DelegatingConnection connectionAsDelegatingConnection = (DelegatingConnection) decorated;
        if ( numberOfLevelsToReal == 1 )
        {
            assertSame( real, connectionAsDelegatingConnection.getDelegate() );
        }
        else
        {
            assertNotSame( real, connectionAsDelegatingConnection.getDelegate() );
            assertDecoratedConnection( real, (Connection) connectionAsDelegatingConnection.getDelegate(), numberOfLevelsToReal - 1 );
        }
    }

    void assertDecoratedStatement( Statement real, Statement decorated, int numberOfLevelsToReal )
    {
        assertNotSame( real, decorated );
        assertTrue( "decorated Statement not DelegatingStatement at level " + numberOfLevelsToReal,
                    decorated instanceof DelegatingStatement );

        DelegatingStatement statementAsDelegatingStatement = (DelegatingStatement) decorated;
        if ( numberOfLevelsToReal == 1 )
        {
            assertSame( real, statementAsDelegatingStatement.getDelegate() );
        }
        else
        {
            assertNotSame( real, statementAsDelegatingStatement.getDelegate() );
            assertDecoratedStatement( real, (Statement) statementAsDelegatingStatement.getDelegate(), numberOfLevelsToReal - 1 );
        }
    }

    void assertDelegatingResultSet( ResultSet real, ResultSet decorated, int numberOfLevelsToReal )
    {
        assertNotSame( real, decorated );
        assertTrue( "decorated ResultSet not DelegatingResultSet at level " + numberOfLevelsToReal,
                    decorated instanceof DelegatingResultSet );

        DelegatingResultSet resultSetAsDelegatingResultSet = (DelegatingResultSet) decorated;
        if ( numberOfLevelsToReal == 1 )
        {
            assertSame( real, resultSetAsDelegatingResultSet.getDelegate() );
        }
        else
        {
            assertNotSame( real, resultSetAsDelegatingResultSet.getDelegate() );
            assertDelegatingResultSet( real, (ResultSet) resultSetAsDelegatingResultSet.getDelegate(), numberOfLevelsToReal - 1 );
        }
    }
}
