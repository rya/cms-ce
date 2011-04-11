/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql.model;

import java.util.Collections;
import java.util.HashMap;
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
        for ( Column column : columns )
        {
            columnTableMap.put( column.getName().toLowerCase(), table );
        }
    }

    public void addView( View view )
    {
        views.add( view );
        Column[] columns = view.getColumns();
        for ( Column column : columns )
        {
            columnTableMap.put( column.getName().toLowerCase(), view );
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
        for ( Table table : tables )
        {
            // Traverse all foreign keys
            Column[] columns = table.getColumns();
            for ( Column column : columns )
            {
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

    public Table getTableByParentName( String parentName )
    {
        Table table = null;
        for ( Table table1 : tables )
        {
            if ( parentName.equalsIgnoreCase( table1.getParentName() ) )
            {
                table = table1;
            }
        }

        return table;
    }

    public Table getTableByElementName( String elementName )
    {
        Table table = null;
        for ( Table table1 : tables )
        {
            if ( elementName.equalsIgnoreCase( table1.getElementName() ) )
            {
                table = table1;
            }
        }

        return table;
    }

    public Table getTable( String tableName )
    {
        Table table = null;
        for ( Table table1 : tables )
        {
            if ( tableName.equalsIgnoreCase( table1.getName() ) )
            {
                table = table1;
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
        for ( View view1 : views )
        {
            if ( viewName.equalsIgnoreCase( view1.getName() ) )
            {
                view = view1;
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
        for ( Table table : tables )
        {
            tableArray[i++] = table;
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

    public Table[] getTablesAndViews()
    {
        Table[] tableArray = new Table[tables.size() + views.size()];
        for ( int i = 0; i < tables.size(); i++ )
        {
            tableArray[i] = tables.get( i );
        }

        for ( int i = 0; i < views.size(); i++ )
        {
            tableArray[i + tables.size()] = views.get( i );
        }

        return tableArray;
    }

    /**
     * Return the model version.
     */
    public int getVersion()
    {
        return this.version;
    }
}
