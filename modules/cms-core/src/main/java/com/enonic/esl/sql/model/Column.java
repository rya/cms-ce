/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

/*
 * Created on 06.apr.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.enonic.esl.sql.model;

import java.io.Serializable;

import com.enonic.esl.sql.model.datatypes.DataType;
import com.enonic.esl.util.StringUtil;

public class Column
    implements Serializable
{
    private String name;

    private String xpath;

    private boolean required;

    private boolean primaryKey;

    private boolean nullColumn;

    private boolean notColumn;

    private DataType type;

    private Table table;

    private int size = -1;

    public Column( String name, String xpath )
    {
        this.name = name;
        this.xpath = xpath;
    }

    // A little hack.. We need a way to represent columns in WHERE clauses, WHERE col IS NULL

    private Column( String name, boolean nullColumn, boolean notColumn )
    {
        this.name = name;
        this.nullColumn = nullColumn;
        this.notColumn = notColumn;
    }

    public Column( String name, String xpath, DataType type )
    {
        this.name = name;
        this.xpath = xpath;
        this.type = type;
    }

    public Column(String name, String xpath, boolean required, boolean primaryKey, DataType type, int size)
    {
        this.name = name;
        this.xpath = xpath;
        this.required = required;
        this.primaryKey = primaryKey;
        this.type = type;
        this.size = size;
    }

    public String getName()
    {
        return name;
    }

    public DataType getType()
    {
        return type;
    }

    public String getColumnValue( Object xpathValue )
    {
        return getType().getSQLValue( xpathValue );
    }

    public Column getCountColumn()
    {
        return new Column( "count(" + name + ")", null );
    }

    public Column getNullColumn()
    {
        return new Column( name, true, false );
    }

    public String getXPath()
    {
        return xpath;
    }

    public String toString()
    {
        return name;
    }

    public boolean isPrimaryKey()
    {
        return primaryKey;
    }

    public boolean isForeignKey()
    {
        return false;
    }

    public boolean isRequired()
    {
        return required;
    }

    public boolean isNullColumn()
    {
        return nullColumn;
    }

    public boolean isNotColumn()
    {
        return notColumn;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if ( !( obj instanceof Column ) )
        {
            return false;
        }

        Column other = (Column) obj;

        if ( !this.getName().equalsIgnoreCase( other.getName() ) )
        {
            return false;
        }

        if ( this.getType() != other.getType() )
        {
            return false;
        }

        if ( this.isPrimaryKey() != other.isPrimaryKey() )
        {
            return false;
        }

        if ( this.isForeignKey() != other.isForeignKey() )
        {
            return false;
        }

        if ( this.isRequired() != other.isRequired() )
        {
            return false;
        }

        return true;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        int hashCode = name.toLowerCase().hashCode();
        hashCode ^= type.hashCode();
        if ( primaryKey )
        {
            hashCode ^= 0x80000000;
        }
        if ( isForeignKey() )
        {
            hashCode ^= 0x40000000;
        }
        if ( required )
        {
            hashCode ^= 0x20000000;
        }
        return hashCode;
    }

    public Table getTable()
    {
        return table;
    }

    public void setTable( Table table )
    {
        this.table = table;
    }

    public int getSize()
    {
        return size;
    }
}
