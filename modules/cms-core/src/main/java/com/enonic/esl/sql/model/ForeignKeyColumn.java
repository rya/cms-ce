/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

/*
 * Created on 13.apr.2004
 *
 */
package com.enonic.esl.sql.model;

import com.enonic.esl.sql.model.datatypes.DataType;

public class ForeignKeyColumn
    extends Column
{
    private String referencedTableName, referencedColumnName;

    private Table referencedTable;

    private Column referencedColumn;

    private boolean isDelete;

    public ForeignKeyColumn( String name, String xpath, boolean required, boolean primaryKey, DataType type, Object defaultValue,
                             String referencedTableName, String referencedColumnName, boolean isDelete, int size )
    {
        super( name, xpath, required, primaryKey, type, defaultValue, size );

        this.referencedTableName = referencedTableName;
        this.referencedColumnName = referencedColumnName;
        this.isDelete = isDelete;
    }

    protected String getReferencedTableName()
    {
        return referencedTableName;
    }

    protected String getReferencedColumnName()
    {
        return referencedColumnName;
    }

    public Table getReferencedTable()
    {
        return referencedTable;
    }

    public Column getReferencedColumn()
    {
        return referencedColumn;
    }

    public void setReferencedTable( Table referencedTable )
    {
        this.referencedTable = referencedTable;
    }

    public void setReferencedColumn( Column referencedColumn )
    {
        this.referencedColumn = referencedColumn;
    }

    public boolean isForeignKey()
    {
        return true;
    }

    public boolean isDelete()
    {
        return isDelete;
    }

    public int getSize()
    {
        if ( this.referencedColumn != null )
        {
            return this.referencedColumn.getSize();
        }
        else
        {
            return -1;
        }
    }
}
