/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.hibernate.support;

/**
 * Builds Select statements using HQL syntax.   This class can not be used for regular SQL.
 */
public class SelectBuilder
{
    public static final int NO_JOIN = 0;

    public static final int INNER_JOIN = 1;

    public static final int LEFT_JOIN = 2;

    public static final int LEFT_JOIN_FETCH = 3;

    private boolean leftJoinIncluded = false;

    private StringBuffer hql;

    private int tabs = 0;

    private int filtersAdded = 0;

    private int fromTablesAdded = 0;

    private boolean filterGroup = false;

    private String filterGroupType;

    private boolean filterGroupStarted = false;

    public SelectBuilder( int tabs )
    {
        this.hql = new StringBuffer();
        this.tabs = tabs;
    }

    public SelectBuilder( StringBuffer hql, int tabs )
    {
        this.hql = hql;
        this.tabs = tabs;
    }

    public void addSelect( String columns )
    {
        appendTabs();
        hql.append( "SELECT " ).append( columns );
    }

    public void addSelectColumn( String column )
    {
        hql.append( ", " ).append( column );
    }

    public void addFromTable( String tableName, String alias, int joinMethod, String joinCondition )
    {

        hql.append( "\n" );
        appendTabs();
        if ( fromTablesAdded == 0 )
        {
            hql.append( "FROM " ).append( tableName ).append( " AS " ).append( alias );
        }
        else
        {
            if ( joinMethod == INNER_JOIN )
            {
                if ( leftJoinIncluded )
                {
                    throw new IllegalStateException( "Can not add regular joins after adding the first left (outer) join." );
                }
                // INNER JOIN tContentIndex AS t4 ON t4.cix_lContentKey = x.cix_lContentKey AND t4.cix_sPath = 'publishfrom'
                hql.append( "INNER JOIN " ).append( tableName ).append( " AS " ).append( alias ).append( " ON " ).append( joinCondition );
            }
            else if ( joinMethod == LEFT_JOIN )
            {
                leftJoinIncluded = true;
                // left join c.currentVersion
                hql.append( "LEFT JOIN " ).append( tableName );
            }
            else if ( joinMethod == LEFT_JOIN_FETCH )
            {
                leftJoinIncluded = true;
                // left join fetch c.currentVersion
                hql.append( "LEFT JOIN FETCH " ).append( tableName );
            }
            else
            {
                if ( leftJoinIncluded )
                {
                    throw new IllegalStateException( "Can not add regular joins after adding the first left (outer) join." );
                }
                hql.append( ", " ).append( tableName ).append( " AS " ).append( alias );
            }
        }

        fromTablesAdded++;
    }


    private void appendTabs()
    {
        for ( int i = 0; i < tabs; i++ )
        {
            hql.append( "\t" );
        }
    }

    public void addFilter( String type, String test )
    {

        hql.append( "\n" );
        appendTabs();

        final boolean startFilterGroup = filterGroup && !filterGroupStarted;

        if ( startFilterGroup )
        {
            hql.append( filtersAdded == 0 ? "WHERE" : filterGroupType ).append( " " );
        }
        else
        {
            hql.append( filtersAdded == 0 ? "WHERE" : type ).append( " " );
        }

        if ( startFilterGroup )
        {
            hql.append( "(" );
            hql.append( "\n" );
            appendTabs();
            filterGroupStarted = true;
        }

        hql.append( test );

        filtersAdded++;
    }


    public void startFilterGroup( String type )
    {

        filterGroup = true;
        filterGroupType = type;
        filterGroupStarted = false;
    }

    public void endFilterGroup()
    {

        if ( filterGroup && filterGroupStarted )
        {
            hql.append( "\n" );
            appendTabs();
            hql.append( ")" );
        }

        filterGroup = false;
    }

    public void addOrderBy( String columns )
    {

        hql.append( "\n" );
        appendTabs();
        hql.append( "ORDER BY " ).append( columns );
    }

    public void append( String s )
    {
        hql.append( " " ).append( s );
    }

    public String toString()
    {
        return hql.toString();
    }
}
