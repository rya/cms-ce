/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc.dialect;

import java.sql.Types;

/**
 * This class implements the DB2 dialect.
 */
public final class Db2Dialect
    extends Dialect
{
    /**
     * Vendor ids.
     */
    private final static String[] VENDOR_IDS = {"db2"};

    /**
     * Construct the dialect.
     */
    public Db2Dialect()
    {
        super( "db2", VENDOR_IDS );
        setUseInputStreamForBlob( true );
        setSeparatorValue( ";" );
        setNullableValue( "" );
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
        setBlobTypeValue( "blob(?M)" );
        setTimestampTypeValue( "timestamp" );
        setInlineTimestampForSpeed( true );
    }

    @Override
    protected int convertType( int sqlType )
    {
        if ( isBlobType( sqlType ) )
        {
            return Types.BLOB;
        }
        else
        {
            return sqlType;
        }
    }
}
