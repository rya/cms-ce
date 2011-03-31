/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class SelectString
    extends SqlString
{

    private final static TableQualifierGenerator tGen = new TableQualifierGenerator();

    private final List<CharSequence> selectList = new ArrayList<CharSequence>();

    private final List<String[]> fromList = new ArrayList<String[]>();

    private final List<StringBuffer> joinList = new ArrayList<StringBuffer>();

    private AndOrWhereClause whereClause;

    private AndOrWhereClause havingClause;

    private final List<String> orderByList = new ArrayList<String>();

    private final List<String> groupByList = new ArrayList<String>();

    public SelectString()
    {
    }

    /**
     * Insert the method's description here.
     *
     * @param column String
     */
    public void addColumnToGroupBy( String column )
    {

        addColumnToGroupBy( null, column );
    }

    /**
     * Insert the method's description here.
     *
     * @param column   String
     * @param asString String
     */
    public void addColumnToGroupBy( String tableAlias, String column )
    {

        String columnPart;
        if ( tableAlias != null )
        {
            columnPart = tableAlias + "." + column;
        }
        else
        {
            columnPart = column;
        }

        groupByList.add( columnPart );
    }

    /**
     * Insert the method's description here.
     *
     * @param column   String
     * @param asString String
     */
    public void addColumnToOrderBy( String tableAlias, String column, boolean ascending )
    {

        String columnPart;
        if ( tableAlias != null )
        {
            columnPart = tableAlias + "." + column;
        }
        else
        {
            columnPart = column;
        }
        if ( ascending )
        {
            columnPart += " ASC";
        }
        else
        {
            columnPart += " DESC";
        }

        orderByList.add( columnPart );
    }

    /**
     * Insert the method's description here.
     *
     * @param column   String
     * @param asString String
     */
    public void addColumnToOrderBy( String column, boolean ascending )
    {

        addColumnToOrderBy( null, column, ascending );
    }

    /**
     * Add a column to the list of columns that should be selected. (I.e. "SELECT foo FROM ...")
     *
     * @param colunm The column name.
     */
    public void addColumnToSelect( String column )
    {

        addColumnToSelect( column, null );
    }

    /**
     * Add a column to the list of columns that should be selected, with the specified alias.
     *
     * @param colunm   The column name.
     * @param asString The column alias.
     */
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

    public void addColumnToSelect( String tableAlias, String column, String asString )
    {

        StringBuffer selectPart = new StringBuffer( column );
        if ( tableAlias != null )
        {
            selectPart.insert( 0, tableAlias );
        }
        if ( asString != null )
        {
            selectPart.append( " AS " );
            selectPart.append( asString );
        }

        selectList.add( selectPart );
    }

    /**
     * Insert the method's description here.
     */
    public WhereClause addHavingClause( WhereClause newHavingClause )
    {

        if ( havingClause == null )
        {
            havingClause = new AndOrWhereClause();
        }
        havingClause.addWhereClause( newHavingClause );

        return havingClause;
    }

    /**
     * Insert the method's description here.
     *
     * @param column   String
     * @param asString String
     */
    public void addInnerJoinClause( String table, JoinCondition[] joinCondition )
    {

        addJoinClause( "INNER ", table, joinCondition );
    }

    public void addLeftOuterJoinClause( String table, JoinCondition[] joinCondition )
    {

        addJoinClause( "LEFT OUTER ", table, null, joinCondition );
    }

    /**
     * Insert the method's description here.
     *
     * @param column   String
     * @param asString String
     */
    public void addInnerJoinClause( String table, String tableAlias, JoinCondition[] joinCondition )
    {

        addJoinClause( "INNER ", table, tableAlias, joinCondition );
    }

    /**
     * Insert the method's description here.
     *
     * @param column          String
     * @param JoinCondition[] joinCondition
     */
    public void addJoinClause( String table, JoinCondition[] joinCondition )
    {

        addJoinClause( null, table, null, joinCondition );
    }

    /**
     * Insert the method's description here.
     *
     * @param column          String
     * @param JoinCondition[] joinCondition
     */
    public void addJoinClause( String table, String tableAlias, JoinCondition[] joinCondition )
    {

        addJoinClause( null, table, tableAlias, joinCondition );
    }

    /**
     * Insert the method's description here.
     *
     * @param column          String
     * @param JoinCondition[] joinCondition
     */
    private void addJoinClause( String pre, String table, String tableAlias, JoinCondition[] joinCondition )
    {

        String tablePart = table;
        if ( tableAlias != null )
        {
            tablePart = tablePart + " " + tableAlias;
        }

        // create JOIN clause
        StringBuffer joinClause = new StringBuffer( "JOIN " );
        if ( pre != null )
        {
            joinClause.insert( 0, pre );
        }
        joinClause.append( tablePart );
        joinClause.append( " ON " );
        for ( int i = 0; i < joinCondition.length; i++ )
        {
            if ( joinCondition[i].leftTableAlias != null && joinCondition[i].leftTableAlias.length() > 0 )
            {
                joinClause.append( joinCondition[i].leftTableAlias );
            }
            joinClause.append( joinCondition[i].leftColumn );
            joinClause.append( "=" );
            if ( joinCondition[i].rightTableAlias != null && joinCondition[i].rightTableAlias.length() > 0 )
            {
                joinClause.append( joinCondition[i].rightTableAlias );
            }
            joinClause.append( joinCondition[i].rightColumn );

            joinClause.append( " AND " );
        }
        joinClause.delete( joinClause.length() - 4, joinClause.length() );

        joinList.add( joinClause );
    }

    /**
     * Insert the method's description here.
     *
     * @param column   String
     * @param asString String
     */
    public void addLeftJoinClause( String table, JoinCondition[] joinCondition )
    {

        addJoinClause( "LEFT ", table, joinCondition );
    }

    /**
     * Insert the method's description here.
     *
     * @param column   String
     * @param asString String
     */
    public void addLeftJoinClause( String table, String tableAlias, JoinCondition[] joinCondition )
    {

        addJoinClause( "LEFT ", table, tableAlias, joinCondition );
    }

    /**
     * Insert the method's description here.
     *
     * @param column   String
     * @param asString String
     */
    public void addRightJoinClause( String table, JoinCondition[] joinCondition )
    {

        addJoinClause( "RIGHT ", table, null, joinCondition );
    }

    /**
     * Insert the method's description here.
     *
     * @param column   String
     * @param asString String
     */
    public void addRightJoinClause( String table, String tableAlias, JoinCondition[] joinCondition )
    {

        addJoinClause( "RIGHT ", table, tableAlias, joinCondition );
    }

    /**
     * Add a table to the FROM list.
     *
     * @param table The table name.
     */
    public void addTableToFrom( String table )
    {

        addTableToFrom( table, null );
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


    /**
     * Add the specified where clause to the select.
     */
    public WhereClause addWhereClause( WhereClause newWhereClause )
    {

        if ( whereClause == null )
        {
            whereClause = new AndOrWhereClause();
        }
        whereClause.addWhereClause( newWhereClause );

        return whereClause;
    }

    /**
     * Insert the method's description here.
     */
    public void clearFromList()
    {

        fromList.clear();
    }

    /**
     * Insert the method's description here.
     */
    public void clearJoinList()
    {

        joinList.clear();
    }

    /**
     * Insert the method's description here.
     */
    public void clearOrderByList()
    {

        orderByList.clear();
    }

    /**
     * Insert the method's description here.
     */
    public void clearSelectList()
    {

        selectList.clear();
    }

    /**
     * Insert the method's description here.
     */
    public void clearWhereClause()
    {

        whereClause = null;
    }

    /**
     * Insert the method's description here.
     *
     * @param orderBy String
     */
    public void generateOrderByClause( SelectString select, Map orderByMap, String orderBy, String defaultOrderBy )
    {

        select.clearOrderByList();
        StringTokenizer tokenizer;
        if ( orderBy != null )
        {
            tokenizer = new StringTokenizer( orderBy, "," );
        }
        else if ( defaultOrderBy != null )
        {
            tokenizer = new StringTokenizer( defaultOrderBy, "," );
        }
        else
        {
            return;
        }

        while ( tokenizer.hasMoreTokens() )
        {
            String token = tokenizer.nextToken().trim();
            int spaceIdx = token.indexOf( ' ' );
            String attribute;
            boolean ascending;

            if ( spaceIdx > 0 )
            {
                attribute = token.substring( 0, spaceIdx );
                String ordering = token.substring( spaceIdx + 1, token.length() ).toUpperCase();
                ascending = !"DESC".equals( ordering );
            }
            else
            {
                attribute = token;
                ascending = true;
            }

            String column = (String) orderByMap.get( attribute );
            if ( column != null )
            {
                select.addColumnToOrderBy( column, ascending );
            }
        }
    }

    /**
     * @see com.enonic.vertical.sql.SQLString
     */
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

        // JOIN clause
        if ( !joinList.isEmpty() )
        {
            Iterator<StringBuffer> iterator = joinList.iterator();
            while ( iterator.hasNext() )
            {
                sql.append( iterator.next() );
                if ( linefeed )
                {
                    sql.replace( sql.length() - 1, sql.length(), " \n" );
                }
                else
                {
                    sql.replace( sql.length() - 1, sql.length(), " " );
                }
            }
        }

        // WHERE clause
        if ( whereClause != null )
        {
            sql.append( "WHERE " );
            sql.append( whereClause.generateSql() );
            if ( linefeed )
            {
                sql.append( " \n" );
            }
            else
            {
                sql.append( ' ' );
            }
        }

        // GROUP BY clause
        if ( !groupByList.isEmpty() )
        {
            sql.append( "GROUP BY " );
            Iterator<String> iterator = groupByList.iterator();
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

        // HAVING clause
        if ( havingClause != null )
        {
            sql.append( "HAVING " );
            sql.append( havingClause.generateSql() );
            if ( linefeed )
            {
                sql.append( " \n" );
            }
            else
            {
                sql.append( ' ' );
            }
        }

        // ORDER BY clause
        if ( !orderByList.isEmpty() )
        {
            sql.append( "ORDER BY " );
            Iterator<String> iterator = orderByList.iterator();
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

        return sql.toString();
    }

    public String getTableAlias( String tableName )
    {
        String q = null;

        Iterator<String[]> iter = fromList.iterator();
        while ( iter.hasNext() )
        {
            String[] next = iter.next();

            if ( next[0].equals( tableName ) )
            {
                q = next[1];
                break;
            }
        }

        return q;
    }
}
