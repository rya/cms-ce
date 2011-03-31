/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.id;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.engine.TransactionHelper;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerationException;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.type.Type;
import org.hibernate.util.PropertiesHelper;
import org.hibernate.util.ReflectHelper;


/**
 * Class for generating keys for our "user typed" identifiers. Specify table and idClassName, where idClassName is the actual domain class
 * to instantiate passing an Integer to the constructor.
 */
public class IntegerBasedCustomIdentifierGenerator
    extends TransactionHelper
    implements PersistentIdentifierGenerator, Configurable
{
    private static final String SELECT_LASTKEY = "SELECT key_llastkey FROM tkey WHERE key_sTableName = ?";

    private static final String UPDATE_LASTKEY = "UPDATE tKey SET key_lLastKey = ( key_lLastKey + ? ) WHERE key_sTableName = ?";

    private static final String INSERT_NEWKEY = "INSERT INTO tKey ( key_sTableName, key_lLastKey ) VALUES ( ?, ? )";

    private static final String TABLE = "table";

    private static final String ID_CLASS_NAME = "idClassName";

    private String tableName;

    private Class idClass;

    public String[] sqlCreateStrings( Dialect dialect )
        throws HibernateException
    {
        StringBuffer sql = new StringBuffer();
        sql.append( "CREATE TABLE tKey (" );
        sql.append( " key_sTableName varchar(18) not null" );
        sql.append( ",key_lLastKey integer not null" );
        sql.append( ",primary key (key_sTableName)" );
        sql.append( " )" );

        return new String[]{sql.toString()};
    }

    public String[] sqlDropStrings( Dialect dialect )
        throws HibernateException
    {

        StringBuffer sql = new StringBuffer();
        sql.append( "DROP TABLE tKey" );

        return new String[]{sql.toString()};
    }

    public Object generatorKey()
    {
        return this.getClass().getName();
    }


    public void configure( Type type, Properties params, Dialect d )
        throws MappingException
    {
        idClass = parseClass( PropertiesHelper.getString( ID_CLASS_NAME, params, null ) );
        tableName = PropertiesHelper.getString( TABLE, params, null );
        if ( tableName == null )
        {
            throw new IllegalArgumentException( "Property '" + TABLE + "' is not set" );
        }

        tableName = tableName.toLowerCase();
    }


    private Class parseClass( String className )
    {
        try
        {
            return ReflectHelper.classForName( className );
        }
        catch ( ClassNotFoundException e )
        {
            throw new MappingException( "Failed to parse class: " + className, e );
        }
    }

    public Serializable generate( SessionImplementor session, Object object )
        throws HibernateException
    {
        return doWorkInNewTransaction( session );
    }

    protected Serializable doWorkInCurrentTransaction( Connection conn, String sql )
        throws SQLException
    {
        try
        {
            updateNextKey( conn, tableName, 1 );
        }
        catch ( KeyRowNotFoundException e )
        {
            // insert row for tableName and try one more time
            insertNewKey( conn, tableName );
            updateNextKey( conn, tableName, 1 );
        }

        Integer nextKey = selectLastKey( conn, tableName );

        return convertToUserType( nextKey );
    }

    private Serializable convertToUserType( Integer value )
    {
        try
        {
            Constructor constructor = idClass.getConstructor( new Class[]{Integer.class} );
            return (Serializable) constructor.newInstance( value );
        }
        catch ( Exception e )
        {
            throw new RuntimeException(
                "Failed to instantiate (" + value + "). " + idClass + " probably do not have a constructor that takes only one Integer.",
                e );
        }
    }

    private void updateNextKey( Connection conn, String tableName, int steps )
        throws SQLException, KeyRowNotFoundException
    {

        PreparedStatement ps = conn.prepareStatement( UPDATE_LASTKEY );
        try
        {
            ps.setInt( 1, steps );
            ps.setString( 2, tableName );
            int result = ps.executeUpdate();

            if ( result < 1 )
            {
                throw new KeyRowNotFoundException();
            }
        }
        finally
        {
            closeStatement( ps );
        }
    }

    private void insertNewKey( Connection conn, String tableName )
        throws SQLException
    {

        PreparedStatement ps = conn.prepareStatement( INSERT_NEWKEY );
        try
        {
            ps.setString( 1, tableName );
            ps.setInt( 2, 0 );
            ps.executeUpdate();
        }
        finally
        {
            closeStatement( ps );
        }
    }

    private Integer selectLastKey( Connection conn, String tableName )
        throws SQLException
    {

        PreparedStatement ps = conn.prepareStatement( SELECT_LASTKEY );
        try
        {
            ps.setString( 1, tableName );
            ResultSet rs = ps.executeQuery();
            if ( !rs.next() )
            {
                String msg = "Failed to read next key value for table '" + tableName + "', no row.";
                throw new IdentifierGenerationException( msg );
            }

            Integer lastKey = rs.getInt( 1 );

            if ( rs.wasNull() )
            {
                String msg = "Failed to read next key value for table '" + tableName + "', key_llastkey was null.";
                throw new IdentifierGenerationException( msg );
            }

            closeResultSet( rs );

            return lastKey;
        }
        finally
        {
            closeStatement( ps );
        }
    }

    private void closeResultSet( ResultSet rs )
        throws SQLException
    {
        if ( rs != null )
        {
            rs.close();
        }
    }

    private void closeStatement( PreparedStatement ps )
        throws SQLException
    {
        if ( ps != null )
        {
            ps.close();
        }
    }

    private class KeyRowNotFoundException
        extends RuntimeException
    {

    }

}
