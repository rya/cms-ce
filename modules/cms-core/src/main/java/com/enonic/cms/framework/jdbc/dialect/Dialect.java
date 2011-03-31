/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc.dialect;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.enonic.cms.framework.jdbc.lob.SimpleBlob;
import com.enonic.cms.framework.jdbc.lob.SimpleClob;

/**
 * This class defines the jdbc dialect.
 */
public abstract class Dialect
{
    /**
     * Placeholder constants.
     */
    private final static String P_SEPARATOR = "separator";

    private final static String P_NULLABLE = "nullable";

    private final static String P_NOT_NULLABLE = "notNullable";

    private final static String P_UPDATE_RESTRICT = "updateRestrict";

    private final static String P_DELETE_RESTRICT = "deleteRestrict";

    private final static String P_UPDATE_CASCADE = "updateCascade";

    private final static String P_DELETE_CASCADE = "deleteCascade";

    private final static String P_CURRENT_TIMESTAMP = "currentTimestamp";

    private final static String P_INTEGER_TYPE = "integer";

    private final static String P_FLOAT_TYPE = "float";

    private final static String P_BIGINT_TYPE = "bigint";

    private final static String P_VARCHAR_TYPE = "varchar";

    private final static String P_BLOB_TYPE = "blob";

    private final static String P_TIMESTAMP_TYPE = "timestamp";

    private final static String P_CHAR_TYPE = "char";

    private final static String P_MINTIMESTAMP_VALUE = "mintimestamp";

    private final static String P_LENGTH_FUNCTION = "length";

    /**
     * Placeholder pattern.
     */
    private final static Pattern PLACEHOLDER_PATTERN = Pattern.compile( "@([a-zA-Z_0-9]+)(\\(([0-9]+)\\))?@" );

    /**
     * Date time format.
     */
    private final static SimpleDateFormat TiMESTAMP_FORMAT = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

    /**
     * Dialect name.
     */
    private final String name;

    /**
     * List of vendor ids to match.
     */
    private final String[] vendorIds;

    /**
     * Placeholder map.
     */
    private final Map<String, String> placeholders;

    private boolean inlineTimestampForSpeed;

    /**
     * Use input stream for blob.
     */
    private boolean useInputStreamForBlob;

    public Dialect( String name, String[] vendorIds )
    {
        this.name = name;
        this.vendorIds = vendorIds;
        this.placeholders = new HashMap<String, String>();
        setUseInputStreamForBlob( true );
        setSeparatorValue( ";" );
        setNullableValue( "null" );
        setNotNullableValue( "not null" );
        setUpdateRestrictValue( "on update restrict" );
        setDeleteRestrictValue( "on delete restrict" );
        setUpdateCascadeValue( "on update cascade" );
        setDeleteCascadeValue( "on delete cascade" );
        setIntegerTypeValue( "integer" );
        setFloatTypeValue( "float" );
        setBigintTypeValue( "decimal(28,0)" );
        setCharTypeValue( "char(?)" );
        setVarcharTypeValue( "varchar(?)" );
        setBlobTypeValue( "blob" );
        setTimestampTypeValue( "timestamp" );
        setMinTimeStampValue( "0000-01-01 00:00:00" );
        setLengthFunctionName( "length" );
        setInlineTimestampForSpeed( false );
    }

    public boolean isInlineTimestampForSpeed()
    {
        return inlineTimestampForSpeed;
    }

    public void setInlineTimestampForSpeed( boolean inlineTimestampForSpeed )
    {
        this.inlineTimestampForSpeed = inlineTimestampForSpeed;
    }

    public String[] getVendorIds()
    {
        return this.vendorIds;
    }

    /**
     * Set separator placeholder.
     */
    public void setSeparatorValue( String value )
    {
        setPlaceholder( P_SEPARATOR, value );
    }

    /**
     * Set nullable placeholder.
     */
    public void setNullableValue( String value )
    {
        setPlaceholder( P_NULLABLE, value );
    }

    /**
     * Set not nullable placeholder.
     */
    public void setNotNullableValue( String value )
    {
        setPlaceholder( P_NOT_NULLABLE, value );
    }

    /**
     * Set update restrict placeholder.
     */
    public void setUpdateRestrictValue( String value )
    {
        setPlaceholder( P_UPDATE_RESTRICT, value );
    }

    /**
     * Set delete restrict placeholder.
     */
    public void setDeleteRestrictValue( String value )
    {
        setPlaceholder( P_DELETE_RESTRICT, value );
    }

    /**
     * Set update cascade placeholder.
     */
    public void setUpdateCascadeValue( String value )
    {
        setPlaceholder( P_UPDATE_CASCADE, value );
    }

    /**
     * Set delete cascade placeholder.
     */
    public void setDeleteCascadeValue( String value )
    {
        setPlaceholder( P_DELETE_CASCADE, value );
    }

    /**
     * Set integer type placeholder.
     */
    public void setIntegerTypeValue( String value )
    {
        setPlaceholder( P_INTEGER_TYPE, value );
    }

    /**
     * Set float type placeholder.
     */
    public void setFloatTypeValue( String value )
    {
        setPlaceholder( P_FLOAT_TYPE, value );
    }

    /**
     * Set bigint type placeholder.
     */
    public void setBigintTypeValue( String value )
    {
        setPlaceholder( P_BIGINT_TYPE, value );
    }

    /**
     * Set char type placeholder.
     */
    public void setCharTypeValue( String value )
    {
        setPlaceholder( P_CHAR_TYPE, value );
    }

    /**
     * Set varchar type placeholder.
     */
    public void setVarcharTypeValue( String value )
    {
        setPlaceholder( P_VARCHAR_TYPE, value );
    }

    /**
     * Set timestamp type placeholder.
     */
    public void setTimestampTypeValue( String value )
    {
        setPlaceholder( P_TIMESTAMP_TYPE, value );
    }

    /**
     * Set minimum timestamp value placeholder.
     */
    public void setMinTimeStampValue( String value )
    {
        setPlaceholder( P_MINTIMESTAMP_VALUE, value );
    }

    /**
     * Set the name fo the length function placeholder.
     */
    public void setLengthFunctionName( String value )
    {
        setPlaceholder( P_LENGTH_FUNCTION, value );
    }

    /**
     * Set blob type placeholder.
     */
    public void setBlobTypeValue( String value )
    {
        setPlaceholder( P_BLOB_TYPE, value );
    }

    public String getName()
    {
        return this.name;
    }

    public String getPlaceholder( String key )
    {
        return this.placeholders.get( key );
    }

    private void setPlaceholder( String key, String value )
    {
        this.placeholders.put( key, value );
    }

    public boolean getUseInputStreamForBlob()
    {
        return this.useInputStreamForBlob;
    }

    public void setUseInputStreamForBlob( boolean flag )
    {
        this.useInputStreamForBlob = flag;
    }

    /**
     * Translate the sql statement by replacing placeholders.
     */
    public String translateStatement( String sql )
    {
        updateCurrentTimestamp();
        StringBuffer result = new StringBuffer();
        Matcher m = PLACEHOLDER_PATTERN.matcher( sql );

        while ( m.find() )
        {
            String placeholderKey = m.group( 1 );
            String replacement = getReplacementText( placeholderKey );
            if ( m.groupCount() > 2 )
            {
                String text = m.group( 3 );
                if ( text != null )
                {
                    replacement = replacement.replaceAll( "\\?", text );
                }
            }

            m.appendReplacement( result, replacement );
        }

        m.appendTail( result );
        return result.toString();
    }

    private String getReplacementText( String placeholder )
    {
        String replacement = getPlaceholder( placeholder );
        if ( replacement != null )
        {
            return replacement;
        }
        else
        {
            throw new IllegalArgumentException( "Unknown placeholder '" + placeholder + "'" );
        }
    }

    public void setByte( PreparedStatement stmt, int parameterIndex, byte x )
        throws SQLException
    {
        stmt.setByte( parameterIndex, x );
    }

    public void setDouble( PreparedStatement stmt, int parameterIndex, double x )
        throws SQLException
    {
        stmt.setDouble( parameterIndex, x );
    }

    public void setFloat( PreparedStatement stmt, int parameterIndex, float x )
        throws SQLException
    {
        stmt.setFloat( parameterIndex, x );
    }

    public void setInt( PreparedStatement stmt, int parameterIndex, int x )
        throws SQLException
    {
        stmt.setInt( parameterIndex, x );
    }

    public void setNull( PreparedStatement stmt, int parameterIndex, int sqlType )
        throws SQLException
    {
        stmt.setNull( parameterIndex, convertType( sqlType ) );
    }

    public void setLong( PreparedStatement stmt, int parameterIndex, long x )
        throws SQLException
    {
        stmt.setLong( parameterIndex, x );
    }

    public void setShort( PreparedStatement stmt, int parameterIndex, short x )
        throws SQLException
    {
        stmt.setShort( parameterIndex, x );
    }

    public void setBoolean( PreparedStatement stmt, int parameterIndex, boolean x )
        throws SQLException
    {
        stmt.setBoolean( parameterIndex, x );
    }

    public void setBytes( PreparedStatement stmt, int parameterIndex, byte[] x )
        throws SQLException
    {
        setBlob( stmt, parameterIndex, new SimpleBlob( x ) );
    }

    public void setBinaryStream( PreparedStatement stmt, int parameterIndex, InputStream x, int length )
        throws SQLException
    {
        setBlob( stmt, parameterIndex, new SimpleBlob( x, length ) );
    }

    public void setString( PreparedStatement stmt, int parameterIndex, String x )
        throws SQLException
    {
        stmt.setString( parameterIndex, x );
    }

    public void setBigDecimal( PreparedStatement stmt, int parameterIndex, BigDecimal x )
        throws SQLException
    {
        stmt.setBigDecimal( parameterIndex, x );
    }

    public void setBlob( PreparedStatement stmt, int parameterIndex, Blob x )
        throws SQLException
    {
        if ( x == null )
        {
            stmt.setNull( parameterIndex, Types.BLOB );
        }
        else
        {
            boolean useStream = getUseInputStreamForBlob();
            if ( useStream )
            {
                stmt.setBinaryStream( parameterIndex, x.getBinaryStream(), (int) x.length() );
            }
            else
            {
                stmt.setBlob( parameterIndex, x );
            }
        }
    }

    public void setClob( PreparedStatement stmt, int parameterIndex, Clob x )
        throws SQLException
    {
        if ( x == null )
        {
            stmt.setNull( parameterIndex, Types.CLOB );
        }
        else
        {
            boolean useStream = getUseInputStreamForBlob();
            if ( useStream )
            {
                stmt.setCharacterStream( parameterIndex, x.getCharacterStream(), (int) x.length() );
            }
            else
            {
                stmt.setClob( parameterIndex, x );
            }
        }
    }

    public void setDate( PreparedStatement stmt, int parameterIndex, Date x )
        throws SQLException
    {
        stmt.setDate( parameterIndex, x );
    }

    public void setTime( PreparedStatement stmt, int parameterIndex, Time x )
        throws SQLException
    {
        stmt.setTime( parameterIndex, x );
    }

    public void setTimestamp( PreparedStatement stmt, int parameterIndex, Timestamp x )
        throws SQLException
    {
        stmt.setTimestamp( parameterIndex, x );
    }

    public void setAsciiStream( PreparedStatement stmt, int parameterIndex, InputStream x, int length )
        throws SQLException
    {
        setClob( stmt, parameterIndex, new SimpleClob( x, length ) );
    }

    public void setCharacterStream( PreparedStatement stmt, int parameterIndex, Reader reader, int length )
        throws SQLException
    {
        setClob( stmt, parameterIndex, new SimpleClob( reader, length ) );
    }

    public void setObject( PreparedStatement stmt, int parameterIndex, Object x )
        throws SQLException
    {
        if ( x instanceof Boolean )
        {
            setObject( stmt, parameterIndex, x, Types.BOOLEAN );
        }
        else if ( x instanceof Byte )
        {
            setObject( stmt, parameterIndex, x, Types.TINYINT );
        }
        else if ( x instanceof Short )
        {
            setObject( stmt, parameterIndex, x, Types.SMALLINT );
        }
        else if ( x instanceof Integer )
        {
            setObject( stmt, parameterIndex, x, Types.INTEGER );
        }
        else if ( x instanceof Long )
        {
            setObject( stmt, parameterIndex, x, Types.BIGINT );
        }
        else if ( x instanceof Float )
        {
            setObject( stmt, parameterIndex, x, Types.FLOAT );
        }
        else if ( x instanceof Double )
        {
            setObject( stmt, parameterIndex, x, Types.DOUBLE );
        }
        else if ( x instanceof String )
        {
            setObject( stmt, parameterIndex, x, Types.VARCHAR );
        }
        else if ( x instanceof byte[] )
        {
            setObject( stmt, parameterIndex, x, Types.BLOB );
        }
        else if ( x instanceof Date )
        {
            setObject( stmt, parameterIndex, x, Types.DATE );
        }
        else if ( x instanceof Time )
        {
            setObject( stmt, parameterIndex, x, Types.TIME );
        }
        else if ( x instanceof Timestamp )
        {
            setObject( stmt, parameterIndex, x, Types.TIMESTAMP );
        }
        else if ( x == null )
        {
            stmt.setNull( parameterIndex, Types.VARCHAR );
        }
        else
        {
            stmt.setObject( parameterIndex, x );
        }
    }

    public void setObject( PreparedStatement stmt, int parameterIndex, Object x, int sqlType )
        throws SQLException
    {
        if ( x == null )
        {
            setNull( stmt, parameterIndex, sqlType );
        }
        else if ( sqlType == Types.BOOLEAN )
        {
            setBoolean( stmt, parameterIndex, (Boolean) x );
        }
        else if ( sqlType == Types.TINYINT )
        {
            setByte( stmt, parameterIndex, ( (Number) x ).byteValue() );
        }
        else if ( sqlType == Types.SMALLINT )
        {
            setShort( stmt, parameterIndex, ( (Number) x ).shortValue() );
        }
        else if ( sqlType == Types.INTEGER )
        {
            setInt( stmt, parameterIndex, ( (Number) x ).intValue() );
        }
        else if ( sqlType == Types.BIGINT )
        {
            setLong( stmt, parameterIndex, ( (Number) x ).longValue() );
        }
        else if ( sqlType == Types.FLOAT )
        {
            setFloat( stmt, parameterIndex, ( (Number) x ).floatValue() );
        }
        else if ( sqlType == Types.DOUBLE )
        {
            setDouble( stmt, parameterIndex, ( (Number) x ).doubleValue() );
        }
        else if ( sqlType == Types.VARCHAR )
        {
            setString( stmt, parameterIndex, (String) x );
        }
        else if ( sqlType == Types.DATE )
        {
            setTimestamp( stmt, parameterIndex, new Timestamp( ( (java.util.Date) x ).getTime() ) );
        }
        else if ( sqlType == Types.TIME )
        {
            setTimestamp( stmt, parameterIndex, new Timestamp( ( (java.util.Date) x ).getTime() ) );
        }
        else if ( sqlType == Types.TIMESTAMP )
        {
            setTimestamp( stmt, parameterIndex, (Timestamp) x );
        }
        else if ( isBlobType( sqlType ) && ( x instanceof Blob ) )
        {
            setBlob( stmt, parameterIndex, (Blob) x );
        }
        else if ( isBlobType( sqlType ) && ( x instanceof byte[] ) )
        {
            setBytes( stmt, parameterIndex, (byte[]) x );
        }
        else
        {
            stmt.setObject( parameterIndex, x );
        }
    }

    protected boolean isBlobType( int sqlType )
    {
        return ( sqlType == Types.BLOB ) || ( sqlType == Types.BINARY ) || ( sqlType == Types.LONGVARBINARY ) ||
            ( sqlType == Types.VARBINARY ) || ( sqlType == Types.JAVA_OBJECT );
    }

    public byte getByte( ResultSet result, int columnIndex )
        throws SQLException
    {
        return result.getByte( columnIndex );
    }

    public double getDouble( ResultSet result, int columnIndex )
        throws SQLException
    {
        return result.getDouble( columnIndex );
    }

    public float getFloat( ResultSet result, int columnIndex )
        throws SQLException
    {
        return result.getFloat( columnIndex );
    }

    public int getInt( ResultSet result, int columnIndex )
        throws SQLException
    {
        return result.getInt( columnIndex );
    }

    public long getLong( ResultSet result, int columnIndex )
        throws SQLException
    {
        return result.getLong( columnIndex );
    }

    public short getShort( ResultSet result, int columnIndex )
        throws SQLException
    {
        return result.getShort( columnIndex );
    }

    public boolean getBoolean( ResultSet result, int columnIndex )
        throws SQLException
    {
        return result.getBoolean( columnIndex );
    }

    public byte[] getBytes( ResultSet result, int columnIndex )
        throws SQLException
    {
        if ( this.useInputStreamForBlob )
        {
            return getBytesFromStream( result, columnIndex );
        }
        else
        {
            byte[] value = result.getBytes( columnIndex );
            return result.wasNull() ? null : value;
        }
    }

    private byte[] getBytesFromStream( ResultSet result, int columnIndex )
        throws SQLException
    {
        InputStream inputStream = result.getBinaryStream( columnIndex );
        if ( inputStream == null )
        {
            return null;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( 2048 );
        byte[] buffer = new byte[2048];

        try
        {
            while ( true )
            {
                int amountRead = inputStream.read( buffer );
                if ( amountRead == -1 )
                {
                    break;
                }
                outputStream.write( buffer, 0, amountRead );
            }

            inputStream.close();
            outputStream.close();
        }
        catch ( IOException ioe )
        {
            throw new SQLException( "IOException occurred reading byte stream" );
        }

        return outputStream.toByteArray();
    }

    public InputStream getAsciiStream( ResultSet result, int columnIndex )
        throws SQLException
    {
        InputStream value = result.getAsciiStream( columnIndex );
        return result.wasNull() ? null : value;
    }

    public InputStream getBinaryStream( ResultSet result, int columnIndex )
        throws SQLException
    {
        InputStream value = result.getBinaryStream( columnIndex );
        return result.wasNull() ? null : value;
    }

    public Reader getCharacterStream( ResultSet result, int columnIndex )
        throws SQLException
    {
        Reader value = result.getCharacterStream( columnIndex );
        return result.wasNull() ? null : value;
    }

    public Object getObject( ResultSet result, int columnIndex )
        throws SQLException
    {
        Object value = result.getObject( columnIndex );
        return repairNumericValue( result, columnIndex, value );
    }

    private Object repairNumericValue( ResultSet result, int columnIndex, Object value )
        throws SQLException
    {
        if ( value == null )
        {
            return null;
        }

        if ( !( value instanceof Number ) )
        {
            return value;
        }

        int jdbcType = result.getMetaData().getColumnType( columnIndex );

        if ( value instanceof BigInteger || value instanceof BigDecimal )
        {
            return repairBigNumericValue( jdbcType, (Number) value );
        }
        else
        {
            return value;
        }
    }

    private Object repairBigNumericValue( int jdbcType, Number object )
    {
        switch ( jdbcType )
        {
            case Types.DECIMAL:
            case Types.FLOAT:
                return object.doubleValue();
            case Types.BIGINT:
                return object.longValue();
            default:
                return object.intValue();
        }
    }

    public String getString( ResultSet result, int columnIndex )
        throws SQLException
    {
        String value = result.getString( columnIndex );
        return result.wasNull() ? null : value;
    }

    public BigDecimal getBigDecimal( ResultSet result, int columnIndex )
        throws SQLException
    {
        BigDecimal value = result.getBigDecimal( columnIndex );
        return result.wasNull() ? null : value;
    }

    public Blob getBlob( ResultSet result, int columnIndex )
        throws SQLException
    {
        byte[] value = getBytes( result, columnIndex );
        return value != null ? new SimpleBlob( value ) : null;
    }

    public Clob getClob( ResultSet result, int columnIndex )
        throws SQLException
    {
        Clob value = result.getClob( columnIndex );
        return result.wasNull() ? null : value;
    }

    public Date getDate( ResultSet result, int columnIndex )
        throws SQLException
    {
        Date value = result.getDate( columnIndex );
        return result.wasNull() ? null : value;
    }

    public Time getTime( ResultSet result, int columnIndex )
        throws SQLException
    {
        Time value = result.getTime( columnIndex );
        return result.wasNull() ? null : value;
    }

    public Timestamp getTimestamp( ResultSet result, int columnIndex )
        throws SQLException
    {
        Timestamp value = result.getTimestamp( columnIndex );
        return result.wasNull() ? null : value;
    }

    private static String getTypeName( int sqlType )
    {
        switch ( sqlType )
        {
            case Types.CHAR:
                return "char";
            case Types.VARCHAR:
                return "varchar";
            case Types.BLOB:
            case Types.BINARY:
            case Types.LONGVARBINARY:
                return "blob";
            case Types.INTEGER:
                return "integer";
            case Types.TIMESTAMP:
                return "timestamp";
            case Types.FLOAT:
                return "float";
            case Types.BIGINT:
                return "bigint";
            default:
                return "unknown";
        }
    }

    public static String getTypePlaceholder( int sqlType )
    {
        return getTypePlaceholder( sqlType, -1 );
    }

    public static String getTypePlaceholder( int sqlType, int size )
    {
        String type = "@" + getTypeName( sqlType );
        if ( size > 0 )
        {
            return type + "(" + size + ")@";
        }
        else
        {
            return type + "@";
        }
    }

    public boolean matchesVendorId( String productName )
    {
        for ( String vendorId : this.vendorIds )
        {
            if ( productName.toLowerCase().indexOf( vendorId.toLowerCase() ) >= 0 )
            {
                return true;
            }
        }

        return false;
    }

    public void initConnection( Connection conn )
        throws SQLException
    {
        // Do nothing
    }

    public String translateDropForeignKey( String tableName, String foreignKeyName )
    {
        StringBuffer sql = new StringBuffer();
        sql.append( "ALTER TABLE " ).append( tableName ).append( " DROP CONSTRAINT " ).append( foreignKeyName );
        return sql.toString();
    }

    public String translateDropIndex( String tableName, String indexName )
    {
        StringBuffer sql = new StringBuffer();
        sql.append( " DROP INDEX " ).append( indexName );
        return sql.toString();
    }

    public String translateGenerateStatistics( String tableName )
    {
        StringBuffer sql = new StringBuffer();
        sql.append( "ANALYZE " ).append( tableName );
        return sql.toString();
    }

    public Timestamp getMinDate()
    {
        return Timestamp.valueOf( getPlaceholder( P_MINTIMESTAMP_VALUE ) );
    }

    private synchronized void updateCurrentTimestamp()
    {
        setPlaceholder( P_CURRENT_TIMESTAMP, formatTimestamp( System.currentTimeMillis() ) );
    }

    public String formatTimestamp( long time )
    {
        return "'" + TiMESTAMP_FORMAT.format( new Date( time ) ) + "'";
    }

    protected int convertType( int sqlType )
    {
        return sqlType;
    }
}
