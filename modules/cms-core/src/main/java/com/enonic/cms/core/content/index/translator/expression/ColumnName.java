/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.translator.expression;


public class ColumnName
{
    private String alias;

    private String columnName;

    public ColumnName()
    {
        alias = null;
        columnName = null;
    }

    public ColumnName( String columnName )
    {
        this.columnName = columnName;
        this.alias = null;
    }

    public ColumnName( String alias, String columnName )
    {
        this.alias = alias;
        this.columnName = columnName;
    }

    public String toString()
    {
        if ( alias == null )
        {
            return columnName;
        }
        else
        {
            return alias + "." + columnName;
        }
    }

    public String getAlias()
    {
        return alias;
    }

    public void setAlias( String alias )
    {
        this.alias = alias;
    }

    public String getColumnName()
    {
        return columnName;
    }

    public void setColumnName( String columnName )
    {
        this.columnName = columnName;
    }

    public boolean isOnRootTable()
    {
        return alias != null && alias.equals( "x" );
    }
}
