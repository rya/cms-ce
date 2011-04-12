/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.queryexpression;

import org.joda.time.ReadableDateTime;
import org.junit.Test;

import static org.junit.Assert.*;

public class DateCompareEvaluatorTest
{
    private QueryExpr parseQuery( String query )
    {
        QueryExpr expr = QueryParser.newInstance().parse( query );
        expr = (QueryExpr) expr.evaluate( new FunctionEvaluator() );
        expr = (QueryExpr) expr.evaluate( new DateCompareEvaluator() );
        return expr;
    }

    @Test
    public void testDateCompareEqual()
    {
        QueryExpr expr = parseQuery( "x = date('2008-12-01')" );

        assertTrue( expr.getExpr() instanceof LogicalExpr );
        LogicalExpr logical = (LogicalExpr) expr.getExpr();

        assertTrue( logical.getLeft() instanceof CompareExpr );
        CompareExpr leftCompare = (CompareExpr) logical.getLeft();

        assertTrue( logical.getRight() instanceof CompareExpr );
        CompareExpr rightCompare = (CompareExpr) logical.getRight();

        assertEquals( CompareExpr.GTE, leftCompare.getOperator() );
        assertTrue( leftCompare.getRight() instanceof ValueExpr );
        ValueExpr lowerValue = (ValueExpr) leftCompare.getRight();

        assertEquals( CompareExpr.LTE, rightCompare.getOperator() );
        assertTrue( rightCompare.getRight() instanceof ValueExpr );
        ValueExpr upperValue = (ValueExpr) rightCompare.getRight();

        assertTrue( lowerValue.isDate() );

        ReadableDateTime lowerDate = (ReadableDateTime) lowerValue.getValue();
        assertEquals( 0, lowerDate.getHourOfDay() );
        assertEquals( 0, lowerDate.getMinuteOfHour() );
        assertEquals( 0, lowerDate.getSecondOfMinute() );
        assertEquals( 0, lowerDate.getMillisOfSecond() );

        assertTrue( upperValue.isDate() );

        ReadableDateTime upperDate = (ReadableDateTime) upperValue.getValue();
        assertEquals( 23, upperDate.getHourOfDay() );
        assertEquals( 59, upperDate.getMinuteOfHour() );
        assertEquals( 59, upperDate.getSecondOfMinute() );
        assertEquals( 999, upperDate.getMillisOfSecond() );
    }

    @Test
    public void testDateCompareLessThanEqual()
    {
        QueryExpr expr = parseQuery( "x <= date('2008-12-01')" );

        assertTrue( expr.getExpr() instanceof CompareExpr );
        CompareExpr compare = (CompareExpr) expr.getExpr();

        assertTrue( compare.getRight() instanceof ValueExpr );
        ValueExpr value = (ValueExpr) compare.getRight();

        assertTrue( value.isDate() );

        ReadableDateTime date = (ReadableDateTime) value.getValue();
        assertEquals( 23, date.getHourOfDay() );
        assertEquals( 59, date.getMinuteOfHour() );
        assertEquals( 59, date.getSecondOfMinute() );
        assertEquals( 999, date.getMillisOfSecond() );
    }
}
