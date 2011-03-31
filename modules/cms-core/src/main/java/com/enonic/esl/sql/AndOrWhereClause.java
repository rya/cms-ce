/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AndOrWhereClause
    extends WhereClause
{
    private final List whereClauseList = new ArrayList();

    private boolean and;

    /**
     * Insert the method's description here.
     *
     * @param and boolean
     */
    public AndOrWhereClause()
    {
        this.and = true;
    }

    /**
     * Insert the method's description here.
     *
     * @param and boolean
     */
    public AndOrWhereClause( boolean and )
    {
        this.and = and;
    }

    public void addWhereClause( WhereClause whereClause )
    {
        whereClauseList.add( whereClause );
    }

    /**
     * generateSql method comment.
     */
    public String generateSql()
    {
        StringBuffer sql = new StringBuffer();
        Iterator iterator = whereClauseList.iterator();
        while ( iterator.hasNext() )
        {
            WhereClause whereClause = (WhereClause) iterator.next();
            if ( whereClause instanceof AndOrWhereClause )
            {
                sql.append( '(' );
                sql.append( whereClause.generateSql() );
                sql.append( ')' );
            }
            else
            {
                sql.append( whereClause.generateSql() );
            }
            if ( iterator.hasNext() )
            {
                if ( and )
                {
                    sql.append( " AND " );
                }
                else
                {
                    sql.append( " OR " );
                }
            }
        }

        return sql.toString();
    }

    public List getWhereClauseList()
    {
        return whereClauseList;
    }

    public boolean isAnd()
    {
        return and;
    }

    public void setAnd( boolean and )
    {
        this.and = and;
    }
}
