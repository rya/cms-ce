/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.hibernate.support;

import java.util.Collection;

public abstract class InClauseBuilder<T>
{

    private int maxValuesPrInClause = 500;

    private String columnName;

    private Collection<T> values;

    public InClauseBuilder( String columnName, Collection<T> values )
    {
        this.columnName = columnName;
        this.values = values;
    }

    public void setMaxValuesPrInClause( int value )
    {
        this.maxValuesPrInClause = value;
    }

    public void appendTo( final StringBuffer sql )
    {

        if ( values == null || values.size() == 0 )
        {
            return;
        }

        sql.append( "(" );
        int i = 0;
        int size = values.size();
        boolean firstInClause = true;
        for ( T value : values )
        {

            boolean newInClause = i == 0 || ( i % maxValuesPrInClause ) == 0;
            if ( newInClause )
            {
                if ( !firstInClause )
                {
                    sql.append( ") OR " );
                }
                sql.append( columnName ).append( " IN (" );
                firstInClause = false;
            }

            if ( !newInClause )
            {
                sql.append( "," );
            }
            appendValue( sql, value );

            if ( i == size - 1 )
            {
                sql.append( ")" );
            }

            i++;
        }
        sql.append( ")" );
    }

    public abstract void appendValue( final StringBuffer sql, final T value );

    public String toString()
    {
        StringBuffer str = new StringBuffer();
        appendTo( str );
        return str.toString();
    }
}
