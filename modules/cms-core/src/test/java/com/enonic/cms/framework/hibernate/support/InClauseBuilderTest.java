/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.hibernate.support;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;


public class InClauseBuilderTest
    extends TestCase
{

    public void testAppendNoValues()
    {

        final List<Integer> values = new ArrayList<Integer>();

        final StringBuffer sql = new StringBuffer();

        InClauseBuilder builder = new InClauseBuilder( "col", values )
        {

            public void appendValue( StringBuffer sql, Object value )
            {
                sql.append( value );
            }
        };
        builder.setMaxValuesPrInClause( 3 );
        builder.appendTo( sql );

        StringBuffer expectedSql = new StringBuffer();
        assertEquals( expectedSql.toString(), sql.toString() );
    }

    public void testAppendOneValue()
    {

        final List<Integer> values = new ArrayList<Integer>();
        values.add( 1 );

        final StringBuffer sql = new StringBuffer();

        InClauseBuilder builder = new InClauseBuilder( "col", values )
        {

            public void appendValue( StringBuffer sql, Object value )
            {
                sql.append( value );
            }
        };
        builder.setMaxValuesPrInClause( 3 );
        builder.appendTo( sql );

        StringBuffer expectedSql = new StringBuffer();
        expectedSql.append( "(col IN (1))" );
        assertEquals( expectedSql.toString(), sql.toString() );
    }

    public void testAppendOneLessThanThreshold()
    {

        final List<Integer> values = new ArrayList<Integer>();
        values.add( 1 );
        values.add( 2 );

        final StringBuffer sql = new StringBuffer();

        InClauseBuilder builder = new InClauseBuilder( "col", values )
        {

            public void appendValue( StringBuffer sql, Object value )
            {
                sql.append( value );
            }
        };
        builder.setMaxValuesPrInClause( 3 );
        builder.appendTo( sql );

        StringBuffer expectedSql = new StringBuffer();
        expectedSql.append( "(col IN (1,2))" );
        assertEquals( expectedSql.toString(), sql.toString() );
    }

    public void testAppendSameAmountAsThreshold()
    {

        final List<Integer> values = new ArrayList<Integer>();
        values.add( 1 );
        values.add( 2 );
        values.add( 3 );

        final StringBuffer sql = new StringBuffer();

        InClauseBuilder builder = new InClauseBuilder( "col", values )
        {

            public void appendValue( StringBuffer sql, Object value )
            {
                sql.append( value );
            }
        };
        builder.setMaxValuesPrInClause( 3 );
        builder.appendTo( sql );

        StringBuffer expectedSql = new StringBuffer();
        expectedSql.append( "(col IN (1,2,3))" );
        assertEquals( expectedSql.toString(), sql.toString() );
    }

    public void testAppendOneMoreThanThreshold()
    {

        final List<Integer> values = new ArrayList<Integer>();
        values.add( 1 );
        values.add( 2 );
        values.add( 3 );
        values.add( 4 );

        final StringBuffer sql = new StringBuffer();

        InClauseBuilder builder = new InClauseBuilder( "col", values )
        {

            public void appendValue( StringBuffer sql, Object value )
            {
                sql.append( value );
            }
        };
        builder.setMaxValuesPrInClause( 3 );
        builder.appendTo( sql );

        StringBuffer expectedSql = new StringBuffer();
        expectedSql.append( "(col IN (1,2,3) OR col IN (4))" );
        assertEquals( expectedSql.toString(), sql.toString() );
    }

    public void testAppendMany()
    {

        final List<Integer> values = new ArrayList<Integer>();
        values.add( 1 );
        values.add( 2 );
        values.add( 3 );
        values.add( 4 );
        values.add( 5 );
        values.add( 6 );
        values.add( 7 );
        values.add( 8 );
        values.add( 9 );
        values.add( 10 );
        values.add( 11 );

        final StringBuffer sql = new StringBuffer();

        InClauseBuilder builder = new InClauseBuilder( "col", values )
        {

            public void appendValue( StringBuffer sql, Object value )
            {
                sql.append( value );
            }
        };
        builder.setMaxValuesPrInClause( 3 );
        builder.appendTo( sql );

        StringBuffer expectedSql = new StringBuffer();
        expectedSql.append( "(col IN (1,2,3) OR col IN (4,5,6) OR col IN (7,8,9) OR col IN (10,11))" );
        assertEquals( expectedSql.toString(), sql.toString() );
    }

}
