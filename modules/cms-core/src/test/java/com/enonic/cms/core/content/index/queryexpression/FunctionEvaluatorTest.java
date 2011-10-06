/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.queryexpression;

import org.junit.Test;

import static org.junit.Assert.*;

public class FunctionEvaluatorTest
{
    @Test
    public void testFunctionInCompareExpr()
    {
        QueryExpr expr = QueryParser.newInstance().parse( "X = today()" );
        expr = (QueryExpr) expr.evaluate( new FunctionEvaluator() );

        assertTrue( expr.getExpr() instanceof CompareExpr );
        CompareExpr compare = (CompareExpr) expr.getExpr();

        assertTrue( compare.getRight() instanceof ValueExpr );
        ValueExpr value = (ValueExpr) compare.getRight();

        assertTrue( value.isDate() );
    }

    @Test
    public void testFunctionInLogicalExpr()
    {
        QueryExpr expr = QueryParser.newInstance().parse( "y = 1 AND x = today()" );
        expr = (QueryExpr) expr.evaluate( new FunctionEvaluator() );

        assertTrue( expr.getExpr() instanceof LogicalExpr );
        LogicalExpr logicalExpr = (LogicalExpr) expr.getExpr();

        assertTrue( logicalExpr.getRight() instanceof CompareExpr );
        CompareExpr compare = (CompareExpr) logicalExpr.getRight();

        assertTrue( compare.getRight() instanceof ValueExpr );
        ValueExpr value = (ValueExpr) compare.getRight();

        assertTrue( value.isDate() );
    }
}
