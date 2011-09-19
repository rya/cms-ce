/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SelectString
    extends SqlString
{
    private final static TableQualifierGenerator tGen = new TableQualifierGenerator();

    private final List<CharSequence> selectList = new ArrayList<CharSequence>();

    private final List<String[]> fromList = new ArrayList<String[]>();

    public SelectString()
    {
    }

    public void addColumnToSelect( String column )
    {

        addColumnToSelect( column, null );
    }

    public void addColumnToSelect( String column, String asString )
    {

        String selectPart;
        if ( asString != null )
        {
            selectPart = column + " AS " + asString;
        }
        else
        {
            selectPart = column;
        }

        selectList.add( selectPart );
    }

    /**
     * Add a table to the FROM list. This method can also generate a table alias.
     *
     * @param table         The table name.
     * @param generateAlias Set to <code>true</code> if you want to generate a table alias/qualifier.
     * @return The generated table alias/qualifier.
     */
    public String addTableToFrom( String table, boolean generateAlias )
    {
        String alias = null;

        if ( generateAlias )
        {
            synchronized ( tGen )
            {
                alias = tGen.next();
            }
        }

        addTableToFrom( table, alias );

        return alias;
    }

    /**
     * Add a table to the FROM list.
     *
     * @param table      The table name.
     * @param tableAlias The table alias or designator.
     */
    public void addTableToFrom( String table, String tableAlias )
    {

        String[] tablePart = new String[]{table, tableAlias};
        fromList.add( tablePart );
    }

    protected String generateSQL( boolean linefeed )
    {
        StringBuffer sql = new StringBuffer();

        // SELECT clause
        if ( !selectList.isEmpty() )
        {
            sql.append( "SELECT " );
            Iterator<CharSequence> iterator = selectList.iterator();
            while ( iterator.hasNext() )
            {
                sql.append( iterator.next() );
                sql.append( ',' );
            }
            if ( linefeed )
            {
                sql.replace( sql.length() - 1, sql.length(), " \n" );
            }
            else
            {
                sql.replace( sql.length() - 1, sql.length(), " " );
            }
        }

        // FROM clause
        if ( !fromList.isEmpty() )
        {
            sql.append( "FROM " );
            Iterator<String[]> iterator = fromList.iterator();
            while ( iterator.hasNext() )
            {
                String[] next = iterator.next();
                sql.append( next[0] );

                // qualifier?
                String q = next[1];
                if ( q != null )
                {
                    sql.append( " " );
                    sql.append( q );
                }

                sql.append( ',' );
            }

            if ( linefeed )
            {
                sql.replace( sql.length() - 1, sql.length(), " \n" );
            }
            else
            {
                sql.replace( sql.length() - 1, sql.length(), " " );
            }
        }

        return sql.toString();
    }
}
