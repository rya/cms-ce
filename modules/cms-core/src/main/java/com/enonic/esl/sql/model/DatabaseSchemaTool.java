/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql.model;

import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.framework.jdbc.dialect.Dialect;

/**
 * This class implements the database script generator. It does insert placeholders where database specific values should appear.
 */
public final class DatabaseSchemaTool
{
    /**
     * Generate create foreign keys.
     */
    public static List<String> generateCreateForeignKeys( Database db )
    {
        ArrayList<String> list = new ArrayList<String>();
        Table[] tables = db.getTables();

        for ( int i = 0; i < tables.length; i++ )
        {
            list.addAll( generateCreateForeignKeys( tables[i] ) );
        }

        return list;
    }

    /**
     * Generate create foreign keys.
     */
    public static List<String> generateCreateForeignKeys( Table table )
    {
        ArrayList<String> list = new ArrayList<String>();
        ForeignKey[] foreignKeys = table.getRealForeignKeys();

        for ( int i = 0; i < foreignKeys.length; i++ )
        {
            StringBuffer sql = new StringBuffer();
            sql.append( "ALTER TABLE " );
            sql.append( table );
            sql.append( "\n\tADD CONSTRAINT " );
            sql.append( foreignKeys[i].getName() );
            sql.append( " FOREIGN KEY (" );

            ForeignKey.Reference[] references = foreignKeys[i].getReferences();
            for ( int j = 0; j < references.length; j++ )
            {
                sql.append( references[j].localColumn );
                if ( j < references.length - 1 )
                {
                    sql.append( ", " );
                }
            }

            sql.append( ")\n\tREFERENCES " );
            sql.append( foreignKeys[i].getRemoteTable() );
            sql.append( "(" );

            for ( int j = 0; j < references.length; j++ )
            {
                sql.append( references[j].remoteColumn );
                if ( j < references.length - 1 )
                {
                    sql.append( ", " );
                }
            }

            sql.append( ") @updateRestrict@ @deleteRestrict@\n" );
            list.add( sql.toString() );
        }

        return list;
    }

    /**
     * Generate create indexes.
     */
    public static List<String> generateCreateIndexes( Database db )
    {
        ArrayList<String> list = new ArrayList<String>();
        Table[] tables = db.getTables();

        for ( int i = 0; i < tables.length; i++ )
        {
            list.addAll( generateCreateIndexes( tables[i] ) );
        }

        return list;
    }

    /**
     * Generate create indexes.
     */
    public static List<String> generateCreateIndexes( Table table )
    {
        ArrayList<String> list = new ArrayList<String>();
        Index[] indexes = table.getIndexes();

        for ( int i = 0; i < indexes.length; i++ )
        {
            StringBuffer sql = new StringBuffer();
            sql.append( "CREATE INDEX " );
            sql.append( indexes[i].getName() );
            sql.append( " ON " );
            sql.append( table );
            sql.append( " (" );

            ArrayList<Column> columns = indexes[i].getColumns();
            for ( int j = 0; j < columns.size(); j++ )
            {
                sql.append( columns.get( j ) );
                if ( j < columns.size() - 1 )
                {
                    sql.append( ", " );
                }
            }

            sql.append( ")" );
            list.add( sql.toString() );
        }

        return list;
    }

    /**
     * Generate create table.
     */
    public static String generateCreateTable( Table table )
    {
        StringBuffer sql = new StringBuffer();
        sql.append( "CREATE TABLE " );
        sql.append( table.getName() );
        sql.append( " (\n" );

        Column[] columns = table.getColumns();
        for ( int i = 0; i < columns.length; i++ )
        {
            sql.append( "\t" );
            sql.append( columns[i].getName() );
            sql.append( " " );
            sql.append( Dialect.getTypePlaceholder( columns[i].getType().getSQLType(), columns[i].getSize() ) );
            sql.append( " " );
            sql.append( columns[i].isRequired() ? "@notNullable@" : "@nullable@" );
            sql.append( ", \n" );
        }

        sql.append( "\tprimary key (" );
        Column[] pkColumns = table.getPrimaryKeys();
        for ( int i = 0; i < pkColumns.length; i++ )
        {
            sql.append( pkColumns[i].getName() );
            if ( i < pkColumns.length - 1 )
            {
                sql.append( ", " );
            }
        }

        sql.append( ")\n)" );
        return sql.toString();
    }

    /**
     * Generate create view.
     */
    public static String generateCreateView( View view )
    {
        StringBuffer sql = new StringBuffer();
        sql.append( "CREATE VIEW " );
        sql.append( view.getName() );
        sql.append( " (\n" );

        Column[] columns = view.getColumns();
        for ( int i = 0; i < columns.length; i++ )
        {
            if ( i > 0 )
            {
                sql.append( ", " );
            }

            sql.append( columns[i].getName() );
        }

        sql.append( ") as\n" );
        sql.append( view.getSelectSql() );
        return sql.toString();
    }

    /**
     * Generate create tables.
     */
    public static List<String> generateCreateTables( Database db )
    {
        ArrayList<String> list = new ArrayList<String>();
        Table[] tables = db.getTables();

        for ( int i = 0; i < tables.length; i++ )
        {
            list.add( generateCreateTable( tables[i] ) );
        }

        return list;
    }

    /**
     * Generate create views.
     */
    public static List<String> generateCreateViews( Database db )
    {
        ArrayList<String> list = new ArrayList<String>();
        View[] views = db.getViews();

        for ( int i = 0; i < views.length; i++ )
        {
            list.add( generateCreateView( views[i] ) );
        }

        return list;
    }

    /**
     * Generate create views.
     */
    public static List<String> generateDropViews( Database db )
    {
        ArrayList<String> list = new ArrayList<String>();
        View[] views = db.getViews();

        /* Reverse order - views might be dependent on each other */
        for ( int i = views.length; i != 0; i-- )
        {
            list.add( "DROP VIEW " + views[i - 1].getName() );
        }
        return list;
    }

    /**
     * Generate create database.
     */
    public static List<String> generateDatabaseSchema( Database db )
    {
        ArrayList<String> list = new ArrayList<String>();
        list.addAll( generateCreateTables( db ) );
        list.addAll( generateCreateForeignKeys( db ) );
        list.addAll( generateCreateIndexes( db ) );
        list.addAll( generateCreateViews( db ) );
        list.addAll( db.getStatements() );
        return list;
    }
}
