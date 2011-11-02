/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Database
{
    private static final Logger LOG = LoggerFactory.getLogger( Database.class.getName() );

    private final List<Table> tables = new LinkedList<Table>();

    private final List<View> views = new LinkedList<View>();

    private final String name;

    private final HashMap<String, Table> columnTableMap = new HashMap<String, Table>();

    private final int version;

    private final List<String> statements = new LinkedList<String>();

    public Database( String databaseName, int version )
    {
        this.name = databaseName;
        this.version = version;
    }

    public List<String> getStatements()
    {
        return Collections.unmodifiableList( this.statements );
    }

    public void addTable( Table table )
    {
        tables.add( table );
        Column[] columns = table.getColumns();
        for ( int i = 0; i < columns.length; i++ )
        {
            columnTableMap.put( columns[i].getName().toLowerCase(), table );
        }
    }

    public void addView( View view )
    {
        views.add( view );
        Column[] columns = view.getColumns();
        for ( int i = 0; i < columns.length; i++ )
        {
            columnTableMap.put( columns[i].getName().toLowerCase(), view );
        }
    }

    public void addStatement( String statement )
    {
        if ( ( statement != null ) && !statement.trim().equals( "" ) )
        {
            this.statements.add( statement );
        }
    }

    /*
      * This method MUST be called after columns have been added
      */

    public void setDatabaseMappings()
    {
        // Set all foreign key mappings
        for ( int i = 0; i < tables.size(); i++ )
        {
            Table table = tables.get( i );

            // Traverse all foreign keys
            Column[] columns = table.getColumns();
            for ( int j = 0; j < columns.length; j++ )
            {
                Column column = columns[j];

                // set foreign key mappings
                if ( column.isForeignKey() )
                {
                    ForeignKeyColumn fk = (ForeignKeyColumn) column;
                    Table referencedTable = getTable( fk.getReferencedTableName() );
                    if ( referencedTable != null )
                    {
                        Column referencedColumn = referencedTable.getColumn( fk.getReferencedColumnName() );
                        fk.setReferencedTable( referencedTable );
                        fk.setReferencedColumn( referencedColumn );
                        referencedTable.setReference( fk );
                    }
                    else
                    {
                        LOG.info( "skipping FK: {}.{}, couldn't find referenced table {}",
                                  new Object[]{table, fk, fk.getReferencedTableName()} );
                    }
                }
            }
        }
    }

    public Table getTable( String tableName )
    {
        Table table = null;
        for ( int i = 0; i < tables.size(); i++ )
        {
            if ( tableName.equalsIgnoreCase( tables.get( i ).getName() ) )
            {
                table = tables.get( i );
            }
        }

        if ( table == null )
        {
            LOG.info( "Database.getTable(String): table {} does not exist", tableName );
        }

        return table;
    }

    public View getView( String viewName )
    {
        View view = null;
        for ( int i = 0; i < views.size(); i++ )
        {
            if ( viewName.equalsIgnoreCase( views.get( i ).getName() ) )
            {
                view = views.get( i );
            }
        }

        if ( view == null )
        {
            LOG.info( "Database.getView(String): view {} does not exist ", viewName );
        }

        return view;
    }

    public Table getTableByColumnName( String columnName )
    {
        return columnTableMap.get( columnName.toLowerCase() );
    }

    public String getName()
    {
        return name;
    }

    public Table[] getTables()
    {
        Table[] tableArray = new Table[tables.size()];
        int i = 0;
        Iterator<Table> iter = tables.iterator();
        while ( iter.hasNext() )
        {
            tableArray[i++] = iter.next();
        }

        return tableArray;
    }

    public View[] getViews()
    {
        View[] viewArray = new View[views.size()];
        for ( int i = 0; i < views.size(); i++ )
        {
            viewArray[i] = views.get( i );
        }

        return viewArray;
    }

    /**
     * Return the model version.
     */
    public int getVersion()
    {
        return this.version;
    }
}
