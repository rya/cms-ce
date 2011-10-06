/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.queryexpression;

import org.junit.Test;

import com.enonic.cms.core.content.index.optimizer.LogicalOrOptimizer;

import static org.junit.Assert.*;

public class QueryParserTest
{
    private QueryExpr parseQuery( String query )
    {
        return new QueryParser().parse( query );
    }

    private String parseQueryToString( String query )
    {
        return parseQuery( query ).toString();
    }

    @Test
    public void testCharsInField()
    {
        QueryExpr test = parseQuery( "@a.b.c/d.e.f/g-h-i/j_k_l/* = -2" );
        assertEquals( "@a.b.c/d.e.f/g-h-i/j_k_l/* = -2.0", test.toString() );

        test = parseQuery( "*/b/c/d/* = -2" );
        assertEquals( "*/b/c/d/* = -2.0", test.toString() );

        test = parseQuery( "* = -2" );
        assertEquals( "* = -2.0", test.toString() );
    }

    @Test
    public void testCombinedLogicalOr()
    {
        QueryExpr test = parseQuery( "a = 1 or b = 2 or c = 3" );

        LogicalOrOptimizer logicalOrOptimizer = new LogicalOrOptimizer();
        Expression optExpr = logicalOrOptimizer.optimize( test.getExpr() );

        assertEquals( "or[a = 1.0, b = 2.0, c = 3.0]", optExpr.toString() );

    }

    @Test
    public void testCombinedLogicalOrWithMetaFields()
    {
        QueryExpr test = parseQuery( "status = 2 or publishfrom > 'mydate' or key = 3398" );

        LogicalOrOptimizer logicalOrOptimizer = new LogicalOrOptimizer();
        Expression optExpr = logicalOrOptimizer.optimize( test.getExpr() );

        assertEquals( "or[status = 2.0, publishfrom > 'mydate', key = 3398.0]", optExpr.toString() );

    }


    @Test
    public void testCombinedLogicalOrWithAndClause()
    {
        QueryExpr test = parseQuery( "a = 1 and (b = 2 or c = 3)" );

        LogicalOrOptimizer logicalOrOptimizer = new LogicalOrOptimizer();
        Expression optExpr = logicalOrOptimizer.optimize( test.getExpr() );

        assertEquals( "(a = 1.0 AND or[b = 2.0, c = 3.0])", optExpr.toString() );

    }

    @Test
    public void testAtAfterSlash()
    {
        QueryExpr test = parseQuery( "data/topkey/@key = 1 or @key = 1" );
    }

    @Test
    public void testFields()
    {
        assertEquals( "a = 1.0", parseQueryToString( "a = 1" ) );
        assertEquals( "a/b = 1.0", parseQueryToString( "a/b = 1" ) );
        assertEquals( "a.b = 1.0", parseQueryToString( "a.b = 1" ) );
    }

    @Test
    public void testNumberValue()
    {
        assertEquals( "a = 4.0", parseQueryToString( "a = 4" ) );
        assertEquals( "a = 8.0", parseQueryToString( "a = 8.0" ) );
    }

    @Test
    public void testStringValue()
    {
        assertEquals( "a = ''", parseQueryToString( "a = ''" ) );
        assertEquals( "a = 'value'", parseQueryToString( "a = 'value'" ) );
    }
}
