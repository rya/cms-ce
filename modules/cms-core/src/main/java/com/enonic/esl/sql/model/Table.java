/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.esl.containers.StringMap;

public class Table
{
    private static final Logger LOG = LoggerFactory.getLogger( Table.class.getName() );

    private String tableName, elementName, parentName;

    private ArrayList<Column> columnList = new ArrayList<Column>();

    private Map<String, Column> columnNameMap = new StringMap( false );

    private Map<String, Column> columnXPathMap = new StringMap( false );

    private ArrayList<Column> foreignKeys = new ArrayList<Column>();

    private ArrayList<Column> primaryKeys = new ArrayList<Column>();

    private ArrayList<ForeignKeyColumn> referencedKeys = new ArrayList<ForeignKeyColumn>();

    private Column titleColumn = null;

    private ArrayList<ForeignKey> realForeignKeys = new ArrayList<ForeignKey>();

    private ArrayList<Index> indexes = new ArrayList<Index>();

    public Table( String tableName, String elementName, String parentName )
    {
        this.tableName = tableName;
        this.elementName = elementName;
        this.parentName = parentName;
    }

    public void addColumn( Column column )
    {
        if ( column instanceof ForeignKeyColumn )
        {
            foreignKeys.add( column );
        }
        if ( column.isPrimaryKey() )
        {
            primaryKeys.add( column );
        }
        columnList.add( column );

        columnNameMap.put( column.getName(), column );
        columnXPathMap.put( column.getXPath(), column );

        column.setTable( this );
    }

    public String getName()
    {
        return tableName;
    }

    public ForeignKeyColumn[] getForeignKeys()
    {
        ForeignKeyColumn[] foreignKeysArray = new ForeignKeyColumn[foreignKeys.size()];
        for ( int i = 0; i < foreignKeys.size(); i++ )
        {
            foreignKeysArray[i] = (ForeignKeyColumn) foreignKeys.get( i );
        }

        return foreignKeysArray;
    }

    public ForeignKeyColumn[] getReferencedKeys( boolean deleteOnly )
    {
        List<ForeignKeyColumn> selectedKeys = new ArrayList<ForeignKeyColumn>();

        for ( int i = 0; i < referencedKeys.size(); i++ )
        {
            ForeignKeyColumn fkColumn = referencedKeys.get( i );

            if ( !deleteOnly || ( deleteOnly && fkColumn.isDelete() ) )
            {
                selectedKeys.add( fkColumn );
            }
        }

        ForeignKeyColumn[] foreignKeysArray = new ForeignKeyColumn[selectedKeys.size()];
        for ( int i = 0; i < selectedKeys.size(); i++ )
        {
            foreignKeysArray[i] = selectedKeys.get( i );
        }

        return foreignKeysArray;
    }

    public ForeignKeyColumn getForeignKey( Table table )
    {
        for ( int i = 0; i < foreignKeys.size(); i++ )
        {
            if ( ( (ForeignKeyColumn) foreignKeys.get( i ) ).getReferencedTable() == table )
            {
                return (ForeignKeyColumn) foreignKeys.get( i );
            }
        }

        return null;
    }

    public Column[] getColumns()
    {
        return columnList.toArray( new Column[columnList.size()] );
    }

    public Column[] getPrimaryKeys()
    {
        return primaryKeys.toArray( new Column[primaryKeys.size()] );
    }

    public Column getColumn( String columnName )
    {
        if ( columnName.startsWith( "count(" ) )
        {
            LOG.info( "COUNT: {}", columnName );
            return new Column( columnName, null );
        }
        else
        {
            return columnNameMap.get( columnName );
        }
    }

    public Column getColumnByXPath( String xpath )
    {
        return columnXPathMap.get( xpath );
    }

    public String toString()
    {
        return tableName;
    }

    public String getParentName()
    {
        return parentName;
    }

    public String getElementName()
    {
        return elementName;
    }

    public int getIndex( Column column )
    {
        return columnList.indexOf( column ) + 1;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if ( ( obj instanceof Table ) == false )
        {
            return false;
        }

        Table other = (Table) obj;

        if ( tableName.equals( other.tableName ) == false )
        {
            return false;
        }

        Column[] thisColumns = this.getColumns();
        Column[] otherColumns = other.getColumns();

        if ( thisColumns.length != otherColumns.length )
        {
            return false;
        }

        // Check all columns
        for ( int i = 0; i < thisColumns.length; i++ )
        {
            if ( !thisColumns[i].equals( otherColumns[i] ) )
            {
                return false;
            }
        }

        return true;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        int hashCode = tableName.hashCode();
        Column[] thisColumns = this.getColumns();
        for ( int i = 0; i < thisColumns.length; i++ )
        {
            hashCode ^= thisColumns[i].hashCode();
        }
        return hashCode;
    }

    public Column getBlobColumn()
    {
        Column[] columns = this.getColumns();
        for ( int i = 0; i < columns.length; i++ )
        {
            if ( columns[i].getType().isBlobType() )
            {
                return columns[i];
            }
        }
        return null;
    }
    /*
     public Column getNameColumn() {
         Column[] columns = this.getColumns();
         for (int i=0; i<columns.length; i++) {
             if (columns[i].getName().endsWith("Name"))
                 return columns[i];
         }
         return null;
     }
     */

    public Column getParentColumn()
    {
        Column[] columns = this.getColumns();
        for ( int i = 0; i < columns.length; i++ )
        {
            if ( columns[i].getName().endsWith( "Parent" ) || columns[i].getName().endsWith( "Super" ) )
            {
                return columns[i];
            }
        }
        return null;
    }

    public Column getDeletedColumn()
    {
        Column[] columns = this.getColumns();
        for ( int i = 0; i < columns.length; i++ )
        {
            if ( columns[i].getName().endsWith( "bDeleted" ) || columns[i].getName().endsWith( "IsDeleted" ) )
            {
                return columns[i];
            }
        }
        return null;
    }

    public void setTitleColumn( Column column )
    {
        titleColumn = column;
    }

    public Column getTitleColumn()
    {
        return titleColumn;
    }

    public Column getXMLColumn()
    {
        Column[] columns = this.getColumns();
        for ( int i = 0; i < columns.length; i++ )
        {
            if ( columns[i].getType() == Constants.COLUMN_XML || columns[i].getType() == Constants.COLUMN_SHORTXML )
            {
                return columns[i];
            }
        }
        return null;
    }

    public void setReference( ForeignKeyColumn column )
    {
        referencedKeys.add( column );
    }

    public void addRealForeignKey( ForeignKey foreignKey )
    {
        realForeignKeys.add( foreignKey );
    }

    public ForeignKey[] getRealForeignKeys()
    {
        ForeignKey[] foreignKeys = new ForeignKey[realForeignKeys.size()];

        for ( int i = 0; i < foreignKeys.length; i++ )
        {
            foreignKeys[i] = realForeignKeys.get( i );
        }
        return foreignKeys;
    }

    public void addIndex( Index index )
    {
        indexes.add( index );
    }

    public Index[] getIndexes()
    {
        Index[] indexesArray = new Index[indexes.size()];

        for ( int i = 0; i < indexes.size(); i++ )
        {
            indexesArray[i] = indexes.get( i );
        }
        return indexesArray;
    }

}
