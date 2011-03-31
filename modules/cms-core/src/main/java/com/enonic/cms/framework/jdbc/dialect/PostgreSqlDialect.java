/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc.dialect;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class implements the PostgreSQL dialect.
 */
public final class PostgreSqlDialect
    extends Dialect
{

    /**
     * Vendor ids.
     */
    private final static String[] VENDOR_IDS = {"postgresql"};

    /**
     * Construct the dialect.
     */
    public PostgreSqlDialect()
    {
        super( "postgresql", VENDOR_IDS );
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
        setBigintTypeValue( "decimal(28)" );
        setCharTypeValue( "char(?)" );
        setVarcharTypeValue( "varchar(?)" );
        setBlobTypeValue( "bytea" );
        setTimestampTypeValue( "timestamp" );
    }

    /**
     * Return boolean value.
     */
    public boolean getBoolean( ResultSet result, int columnIndex )
        throws SQLException
    {
        return getInt( result, columnIndex ) > 0;
    }

    /**
     * Set boolean value.
     */
    public void setBoolean( PreparedStatement stmt, int parameterIndex, boolean x )
        throws SQLException
    {
        setInt( stmt, parameterIndex, x ? 1 : 0 );
    }

    /**
     * Set null value.
     */
    public void setNull( PreparedStatement stmt, int parameterIndex, int sqlType )
        throws SQLException
    {
        if ( isBlobType( sqlType ) )
        {
            stmt.setBytes( parameterIndex, null );
        }
        else
        {
            super.setNull( stmt, parameterIndex, sqlType );
        }
    }
}
