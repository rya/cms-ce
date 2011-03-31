/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc.dialect;

/**
 * This class implements the MySQL dialect.
 */
public final class MySqlDialect
    extends Dialect
{
    /**
     * Vendor ids.
     */
    private static final String[] VENDOR_IDS = {"mysql"};

    /**
     * Construct the dialect.
     */
    public MySqlDialect()
    {
        super( "mysql", VENDOR_IDS );
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
        setBigintTypeValue( "bigint" );
        setCharTypeValue( "char(?)" );
        setVarcharTypeValue( "varchar(?)" );
        setBlobTypeValue( "longblob" );
        setTimestampTypeValue( "timestamp" );
    }

    public String translateDropForeignKey( String tableName, String foreignKeyName )
    {
        StringBuffer sql = new StringBuffer();
        sql.append( "ALTER TABLE " ).append( tableName ).append( " DROP FOREIGN KEY " ).append( foreignKeyName );
        return sql.toString();
    }

    public String translateDropIndex( String tableName, String indexName )
    {
        StringBuffer sql = new StringBuffer();
        sql.append( "ALTER TABLE " ).append( tableName ).append( " DROP INDEX " ).append( indexName );
        return sql.toString();
    }

    public String translateGenerateStatistics( String tableName )
    {
        StringBuffer sql = new StringBuffer();
        sql.append( "ANALYZE TABLE " ).append( tableName );
        return sql.toString();
    }
}
