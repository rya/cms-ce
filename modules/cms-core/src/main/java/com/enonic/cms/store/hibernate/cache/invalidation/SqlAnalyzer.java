/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.cache.invalidation;

public class SqlAnalyzer
{
    private final String sql;

    private String tableName = null;

    private boolean isInsertType = false;

    public String getSql()
    {
        return sql;
    }

    public String resolveTableName()
    {
        return tableName;
    }

    public boolean isInsertType()
    {
        return isInsertType;
    }

    public SqlAnalyzer( String sql )
    {
        this.sql = sql;

        String[] parts = sql.trim().split( "[\\s()]+" );

        if ( ( parts.length >= 2 ) && parts[0].toLowerCase().equals( "update" ) )
        {
            tableName = parts[1];
        }
        else if ( ( parts.length >= 3 ) && parts[0].toLowerCase().equals( "insert" ) && parts[1].toLowerCase().equals( "into" ) )
        {
            tableName = parts[2];
            isInsertType = true;
        }
        else if ( ( parts.length >= 3 ) && parts[0].toLowerCase().equals( "delete" ) && parts[1].toLowerCase().equals( "from" ) )
        {
            tableName = parts[2];
        }
    }
}

