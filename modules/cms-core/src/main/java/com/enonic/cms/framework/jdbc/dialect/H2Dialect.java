/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc.dialect;

/**
 * This class implements the Derby dialect.
 */
public final class H2Dialect
    extends Dialect
{
    /**
     * Vendor ids.
     */
    private final static String[] VENDOR_IDS = {"h2"};

    /**
     * Construct the dialect.
     */
    public H2Dialect()
    {
        super( "h2", VENDOR_IDS );
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
        setBigintTypeValue( "bigint" );
        setCharTypeValue( "char(?)" );
        setVarcharTypeValue( "varchar(?)" );
        setBlobTypeValue( "longvarbinary" );
        setTimestampTypeValue( "timestamp" );
    }
}