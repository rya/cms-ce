package com.enonic.cms.framework.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;

public class DriverFixConnectionDecoratorTest
{
    private Statement mockStatement;
    private PreparedStatement mockPreparedStatement;
    private Connection decoratedConnection;

    @Before
    public void setUp()
        throws Exception
    {
        final Connection mockConnection = Mockito.mock( Connection.class );
        this.mockStatement = Mockito.mock( Statement.class );
        this.mockPreparedStatement = Mockito.mock( PreparedStatement.class );

        Mockito.when( mockConnection.createStatement() ).thenReturn( this.mockStatement );
        Mockito.when( mockConnection.prepareStatement(Mockito.anyString()) ).thenReturn( this.mockPreparedStatement );

        final DriverFixConnectionDecorator decorator = new DriverFixConnectionDecorator();
        this.decoratedConnection = decorator.decorate( mockConnection );
    }

    @Test
    public void testSetQueryTimeoutStatement()
        throws Exception
    {
        final Statement stmt = this.decoratedConnection.createStatement();
        assertNotNull(stmt);

        assertSetQueryTimeout( stmt, this.mockStatement, false );
        assertSetQueryTimeout( stmt, this.mockStatement, true );
    }

    @Test
    public void testSetQueryTimeoutPreparedStatement()
        throws Exception
    {
        final PreparedStatement stmt = this.decoratedConnection.prepareStatement("SELECT * FROM test");
        assertNotNull(stmt);

        assertSetQueryTimeout( stmt, this.mockPreparedStatement, false );
        assertSetQueryTimeout( stmt, this.mockPreparedStatement, true );
    }

    private void assertSetQueryTimeout(final Statement decorated, final Statement mock, final boolean fail)
        throws Exception
    {
        if (fail) {
            Mockito.doThrow( new SQLException()).when( mock ).setQueryTimeout( Mockito.anyInt() );
        }

        decorated.setQueryTimeout( 100 );

        if (!fail) {
            Mockito.verify( mock, Mockito.times( 1 ) ).setQueryTimeout( 100 );
        }
    }
}
