/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql.model;

import java.io.InputStream;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.enonic.esl.sql.model.datatypes.DataType;

/**
 * This class implements the loading of database model.
 */
public final class DatabaseXMLFactory
{
    /**
     * Generate table.
     */
    private static Table generateTable( Element current )
    {
        String tableName = current.getAttributeValue( "name" );
        String elementName = current.getAttributeValue( "elementname" );
        String parentName = current.getAttributeValue( "parentname" );
        Table table = new Table( tableName, elementName, parentName );

        List list = current.getChildren();
        for ( Object child : list )
        {
            Element childElement = (Element) child;
            if ( childElement.getName().equals( "column" ) )
            {
                Column column = generateColumn( childElement );
                if ( "true".equals( childElement.getAttributeValue( "title" ) ) )
                {
                    table.setTitleColumn( column );
                }
                table.addColumn( column );
            }
            else if ( childElement.getName().equals( "foreign-key" ) )
            {
                ForeignKeyColumn fkColumn = generateForeignKeyColumn( childElement );
                if ( "true".equals( childElement.getAttributeValue( "title" ) ) )
                {
                    table.setTitleColumn( fkColumn );
                }
                table.addColumn( fkColumn );
            }
        }

        return table;
    }

    /**
     * Generate column.
     */
    private static Column generateColumn( Element current )
    {
        String xpath = current.getAttributeValue( "xpath" );
        boolean required = Boolean.valueOf( current.getAttributeValue( "required" ) );
        boolean primaryKey = Boolean.valueOf( current.getAttributeValue( "primaryKey" ) );
        String typeStr = current.getAttributeValue( "type" );
        DataType type = Constants.getType( typeStr );
        int size = Integer.parseInt( current.getAttributeValue( "size", "-1" ) );
        String columnName = current.getAttributeValue( "name" );
        String defaultValue = current.getAttributeValue( "defaultValue" );
        return new Column( columnName, xpath, required, primaryKey, type, defaultValue, size );
    }

    /**
     * Generate foreign key.
     */
    private static ForeignKeyColumn generateForeignKeyColumn( Element current )
    {
        String xpath = current.getAttributeValue( "xpath" );
        boolean required = Boolean.valueOf( current.getAttributeValue( "required" ) );
        boolean primaryKey = Boolean.valueOf( current.getAttributeValue( "primaryKey" ) );
        String typeStr = current.getAttributeValue( "type" );
        DataType type = Constants.getType( typeStr );

        // <foreign-key> elements
        Element referenceElem = current.getChild( "reference" );
        String columnName = referenceElem.getAttributeValue( "local" );
        String defaultValue = current.getAttributeValue( "defaultValue" );
        String referencedTableName = current.getAttributeValue( "foreignTable" );
        String referencedColumnName = referenceElem.getAttributeValue( "foreign" );
        boolean isDelete = "true".equals( current.getAttributeValue( "delete" ) );
        return new ForeignKeyColumn( columnName, xpath, required, primaryKey, type, defaultValue, referencedTableName, referencedColumnName,
                                     isDelete, -1 );
    }

    /**
     * Generate real foreign key.
     */
    private static ForeignKey generateRealForeignKey( Element current, Database database )
    {
        String tableName = current.getParentElement().getAttributeValue( "name" );
        Table table = database.getTable( tableName );
        String foreignKeyName = current.getAttributeValue( "name" );
        String foreignTableName = current.getAttributeValue( "foreignTable" );
        Table foreignTable = database.getTable( foreignTableName );
        ForeignKey foreignKey = new ForeignKey( foreignKeyName, foreignTable );

        List columns = current.getChildren( "column" );
        for ( Object child : columns )
        {
            Element childElement = (Element) child;
            String columnName = childElement.getAttributeValue( "name" );
            String foreignColumnName = childElement.getAttributeValue( "foreignColumn" );
            Column column = table.getColumn( columnName );
            Column foreignColumn = foreignTable.getColumn( foreignColumnName );
            foreignKey.addReference( column, foreignColumn );
        }

        return foreignKey;
    }

    /**
     * Generate index.
     */
    private static Index generateIndex( Element current, Table table )
    {
        String indexName = current.getAttributeValue( "name" );
        Index index = new Index( indexName );

        List columns = current.getChildren( "column" );
        for ( Object child : columns )
        {
            Element childElement = (Element) child;
            String columnName = childElement.getAttributeValue( "name" );
            Column column = table.getColumn( columnName );
            index.addColumn( column );
        }

        return index;
    }

    /**
     * Generate view.
     */
    private static View generateView( Element current )
    {
        String viewName = current.getAttributeValue( "name" );
        String elementName = current.getAttributeValue( "elementname" );
        String parentName = current.getAttributeValue( "parentname" );
        String selectSql = current.getChildText( "selectsql" );
        View view = new View( viewName, elementName, parentName, selectSql );

        List columns = current.getChildren( "column" );
        for ( Object child : columns )
        {
            Element childElement = (Element) child;
            Column column = generateColumn( childElement );
            if ( "true".equals( childElement.getAttributeValue( "title" ) ) )
            {
                view.setTitleColumn( column );
            }

            view.addColumn( column );
        }

        return view;
    }

    /**
     * Generate database.
     */
    public static Database generateDatabase( Document doc )
    {
        Element docElem = doc.getRootElement();
        String name = docElem.getAttributeValue( "name" );
        int version = Integer.parseInt( docElem.getAttributeValue( "version" ) );
        Database database = new Database( name, version );

        // <table..>
        List children = docElem.getChildren( "table" );
        for ( Object child : children )
        {
            database.addTable( generateTable( (Element) child ) );
        }

        // <view..>
        children = docElem.getChildren( "view" );
        for ( Object child : children )
        {
            database.addView( generateView( (Element) child ) );
        }

        // <statement..>
        children = docElem.getChildren( "statement" );
        for ( Object child : children )
        {
            database.addStatement( ( (Element) child ).getText() );
        }

        // foreign keys and indexes
        children = docElem.getChildren( "table" );
        for ( Object child : children )
        {
            String tableName = ( (Element) child ).getAttributeValue( "name" );
            Table table = database.getTable( tableName );

            for ( Object item : ( (Element) child ).getChildren( "foreignkey" ) )
            {
                Element foreignKeyElem = (Element) item;
                ForeignKey foreignKey = generateRealForeignKey( foreignKeyElem, database );
                table.addRealForeignKey( foreignKey );
            }

            for ( Object item : ( (Element) child ).getChildren( "index" ) )
            {
                Element indexElem = (Element) item;
                Index index = generateIndex( indexElem, table );
                table.addIndex( index );
            }
        }

        database.setDatabaseMappings();
        return database;
    }

    /**
     * Generate database.
     */
    public static Database generateDatabase( InputStream doc )
        throws Exception
    {
        SAXBuilder builder = new SAXBuilder();
        return generateDatabase( builder.build( doc ) );
    }
}
