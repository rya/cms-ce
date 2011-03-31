/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.support;

import java.io.PrintWriter;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

public final class TraceableDataSourceImpl
    implements TraceableDataSource
{
    private final HashSet<String> ignoreClasses;

    private final HashSet<String> acceptFilter;

    private final DataSource source;

    private final AtomicInteger connKey;

    private final Map<String, Entry> openMap;

    public TraceableDataSourceImpl( DataSource source )
    {
        this.connKey = new AtomicInteger( 0 );
        this.ignoreClasses = new HashSet<String>();
        this.acceptFilter = new HashSet<String>();
        this.source = source;
        this.openMap = new ConcurrentHashMap<String, Entry>();

        addIgnoreClass( TraceableDataSourceImpl.class );
        addIgnoreClass( Entry.class );
        addAcceptFilter( "com.enonic." );
        addAcceptFilter( "org.springframework." );
    }

    private void addIgnoreClass( Class clz )
    {
        this.ignoreClasses.add( clz.getName() );
    }

    private void addAcceptFilter( String filter )
    {
        this.acceptFilter.add( filter );
    }

    public Connection getConnection()
        throws SQLException
    {
        return connectionOpened( this.source.getConnection() );
    }

    public Connection getConnection( String user, String password )
        throws SQLException
    {
        return connectionOpened( this.source.getConnection( user, password ) );
    }

    public PrintWriter getLogWriter()
        throws SQLException
    {
        return this.source.getLogWriter();
    }

    public void setLogWriter( PrintWriter printWriter )
        throws SQLException
    {
        this.source.setLogWriter( printWriter );
    }

    public void setLoginTimeout( int timeout )
        throws SQLException
    {
        this.source.setLoginTimeout( timeout );
    }

    public int getLoginTimeout()
        throws SQLException
    {
        return this.source.getLoginTimeout();
    }

    public Collection<ConnectionTraceInfo> getTraceInfo()
    {
        return new ArrayList<ConnectionTraceInfo>( this.openMap.values() );
    }

    private Connection connectionOpened( Connection conn )
    {
        if ( conn != null )
        {
            String id = conn.toString() + ":" + this.connKey.incrementAndGet();
            this.openMap.put( id, new Entry( id ) );
            return new TraceConnection( id, conn );
        }
        else
        {
            return null;
        }
    }

    private void connectionClosed( String id )
    {
        this.openMap.remove( id );
    }

    private boolean acceptClass( String clzName )
    {
        if ( this.ignoreClasses.contains( clzName ) )
        {
            return false;
        }

        for ( String pattern : this.acceptFilter )
        {
            if ( clzName.startsWith( pattern ) )
            {
                return true;
            }
        }

        return false;
    }

    private List<String> getMethodTrace()
    {
        ArrayList<String> list = new ArrayList<String>();
        for ( StackTraceElement e : new Throwable().getStackTrace() )
        {
            if ( acceptClass( e.getClassName() ) )
            {
                list.add( e.getClassName() + "." + e.getMethodName() );
            }
        }

        return list;
    }

    private final class Entry
        implements ConnectionTraceInfo
    {
        private final String id;

        private final List<String> trace;

        private final long opened;

        public Entry( String id )
        {
            this.id = id;
            this.trace = getMethodTrace();
            this.opened = System.currentTimeMillis();
        }

        public long getAge()
        {
            return System.currentTimeMillis() - this.opened;
        }

        private String getAgeString()
        {
            return MessageFormat.format( "{0,number,#.##} sec", (float) getAge() / 1000f );
        }

        public String getConnectionId()
        {
            return this.id;
        }

        public Collection<String> getStackTrace()
        {
            return this.trace;
        }

        public String toString()
        {
            StringBuffer str = new StringBuffer();
            str.append( "Connection [" ).append( this.id ).append( "] opened " );
            str.append( getAgeString() ).append( " ago from..." );

            for ( String e : this.trace )
            {
                str.append( "\n  " ).append( e );
            }

            return str.toString();
        }
    }

    private final class TraceConnection
        implements Connection
    {
        private final String id;

        private final Connection conn;

        public TraceConnection( String id, Connection conn )
        {
            this.id = id;
            this.conn = conn;
        }

        public Statement createStatement()
            throws SQLException
        {
            return conn.createStatement();
        }

        public PreparedStatement prepareStatement( String s )
            throws SQLException
        {
            return conn.prepareStatement( s );
        }

        public CallableStatement prepareCall( String s )
            throws SQLException
        {
            return conn.prepareCall( s );
        }

        public String nativeSQL( String s )
            throws SQLException
        {
            return conn.nativeSQL( s );
        }

        public void setAutoCommit( boolean b )
            throws SQLException
        {
            conn.setAutoCommit( b );
        }

        public boolean getAutoCommit()
            throws SQLException
        {
            return conn.getAutoCommit();
        }

        public void commit()
            throws SQLException
        {
            conn.commit();
        }

        public void rollback()
            throws SQLException
        {
            conn.rollback();
        }

        public void close()
            throws SQLException
        {
            connectionClosed( this.id );
            conn.close();
        }

        public boolean isClosed()
            throws SQLException
        {
            return conn.isClosed();
        }

        public DatabaseMetaData getMetaData()
            throws SQLException
        {
            return conn.getMetaData();
        }

        public void setReadOnly( boolean b )
            throws SQLException
        {
            conn.setReadOnly( b );
        }

        public boolean isReadOnly()
            throws SQLException
        {
            return conn.isReadOnly();
        }

        public void setCatalog( String s )
            throws SQLException
        {
            conn.setCatalog( s );
        }

        public String getCatalog()
            throws SQLException
        {
            return conn.getCatalog();
        }

        public void setTransactionIsolation( int i )
            throws SQLException
        {
            conn.setTransactionIsolation( i );
        }

        public int getTransactionIsolation()
            throws SQLException
        {
            return conn.getTransactionIsolation();
        }

        public SQLWarning getWarnings()
            throws SQLException
        {
            return conn.getWarnings();
        }

        public void clearWarnings()
            throws SQLException
        {
            conn.clearWarnings();
        }

        public Statement createStatement( int i, int i1 )
            throws SQLException
        {
            return conn.createStatement( i, i1 );
        }

        public PreparedStatement prepareStatement( String s, int i, int i1 )
            throws SQLException
        {
            return conn.prepareStatement( s, i, i1 );
        }

        public CallableStatement prepareCall( String s, int i, int i1 )
            throws SQLException
        {
            return conn.prepareCall( s, i, i1 );
        }

        public Map<String, Class<?>> getTypeMap()
            throws SQLException
        {
            return conn.getTypeMap();
        }

        public void setTypeMap( Map<String, Class<?>> stringClassMap )
            throws SQLException
        {
            conn.setTypeMap( stringClassMap );
        }

        public void setHoldability( int i )
            throws SQLException
        {
            conn.setHoldability( i );
        }

        public int getHoldability()
            throws SQLException
        {
            return conn.getHoldability();
        }

        public Savepoint setSavepoint()
            throws SQLException
        {
            return conn.setSavepoint();
        }

        public Savepoint setSavepoint( String s )
            throws SQLException
        {
            return conn.setSavepoint( s );
        }

        public void rollback( Savepoint savepoint )
            throws SQLException
        {
            conn.rollback( savepoint );
        }

        public void releaseSavepoint( Savepoint savepoint )
            throws SQLException
        {
            conn.releaseSavepoint( savepoint );
        }

        public Statement createStatement( int i, int i1, int i2 )
            throws SQLException
        {
            return conn.createStatement( i, i1, i2 );
        }

        public PreparedStatement prepareStatement( String s, int i, int i1, int i2 )
            throws SQLException
        {
            return conn.prepareStatement( s, i, i1, i2 );
        }

        public CallableStatement prepareCall( String s, int i, int i1, int i2 )
            throws SQLException
        {
            return conn.prepareCall( s, i, i1, i2 );
        }

        public PreparedStatement prepareStatement( String s, int i )
            throws SQLException
        {
            return conn.prepareStatement( s, i );
        }

        public PreparedStatement prepareStatement( String s, int[] ints )
            throws SQLException
        {
            return conn.prepareStatement( s, ints );
        }

        public PreparedStatement prepareStatement( String s, String[] strings )
            throws SQLException
        {
            return conn.prepareStatement( s, strings );
        }

        public Clob createClob()
            throws SQLException
        {
            return this.conn.createClob();
        }

        public Blob createBlob()
            throws SQLException
        {
            return this.conn.createBlob();
        }

        public NClob createNClob()
            throws SQLException
        {
            return this.conn.createNClob();
        }

        public SQLXML createSQLXML()
            throws SQLException
        {
            return this.conn.createSQLXML();
        }

        public boolean isValid( int timeout )
            throws SQLException
        {
            return this.conn.isValid( timeout );
        }

        public void setClientInfo( String name, String value )
            throws SQLClientInfoException
        {
            this.conn.setClientInfo( name, value );
        }

        public void setClientInfo( Properties properties )
            throws SQLClientInfoException
        {
            this.conn.setClientInfo( properties );
        }

        public String getClientInfo( String name )
            throws SQLException
        {
            return this.conn.getClientInfo( name );
        }

        public Properties getClientInfo()
            throws SQLException
        {
            return this.conn.getClientInfo();
        }

        public Array createArrayOf( String typeName, Object[] elements )
            throws SQLException
        {
            return this.conn.createArrayOf( typeName, elements );
        }

        public Struct createStruct( String typeName, Object[] attributes )
            throws SQLException
        {
            return this.conn.createStruct( typeName, attributes );
        }

        public <T> T unwrap( Class<T> iface )
            throws SQLException
        {
            return this.conn.unwrap( iface );
        }

        public boolean isWrapperFor( Class<?> iface )
            throws SQLException
        {
            return this.conn.isWrapperFor( iface );
        }
    }

    public <T> T unwrap( Class<T> iface )
        throws SQLException
    {
        return this.source.unwrap( iface );
    }

    public boolean isWrapperFor( Class<?> iface )
        throws SQLException
    {
        return this.source.isWrapperFor( iface );
    }
}
