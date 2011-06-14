/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content.index.queryexpression;

import org.joda.time.ReadableDateTime;
import org.junit.Test;

import com.enonic.cms.domain.content.index.ContentIndexConstants;

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

    @Test
    public void testParseLessThanExpressionWithDateStringValue()
    {
        QueryExpr expr1 = parseQuery( "publishFrom <= date('2008-12-01')" );
        QueryExpr expr2 = parseQuery( "publishFrom <= '2008-12-01'" );

        assertEquals( expr1.toString(), expr2.toString() );
    }

    @Test
    public void testParseLessThanExpressionWithDateTimeStringValue()
    {
        QueryExpr expr1 = parseQuery( "publishFrom <= date('2008-12-01 23:50')" );
        QueryExpr expr2 = parseQuery( "publishFrom <= '2008-12-01 23:50'" );

        assertEquals( expr1.toString(), expr2.toString() );
    }

    @Test
    public void testParseGreaterThanExpressionWithDateStringValue()
    {
        QueryExpr expr1 = parseQuery( "publishFrom > date('2008-12-01')" );
        QueryExpr expr2 = parseQuery( "publishFrom > '2008-12-01'" );

        assertEquals( expr1.toString(), expr2.toString() );
    }

    @Test
    public void testParseGreaterThanExpressionWithDateTimeStringValue()
    {
        QueryExpr expr1 = parseQuery( "publishFrom > date('2008-12-01 23:50')" );
        QueryExpr expr2 = parseQuery( "publishFrom > '2008-12-01 23:50'" );

        assertEquals( expr1.toString(), expr2.toString() );
    }

    @Test
    public void testParseEqualExpressionWithDateStringValue()
    {
        QueryExpr expr1 = parseQuery( "publishFrom = date('2008-12-01')" );
        QueryExpr expr2 = parseQuery( "publishFrom = '2008-12-01'" );

        assertEquals( expr1.toString(), expr2.toString() );
    }

    @Test
    public void testParseEqualExpressionWithDateTimeStringValue()
    {
        QueryExpr expr1 = parseQuery( "publishFrom = date('2008-12-01 23:50')" );
        QueryExpr expr2 = parseQuery( "publishFrom = '2008-12-01 23:50'" );

        assertEquals( expr1.toString(), expr2.toString() );
    }

    @Test
    public void testValidDateFormatWithoutFunction()
    {
        QueryExpr expr = parseQuery( "publishfrom <= '2008-12-01'" );
        Expression expression = expr.getExpr();
        assertTrue( expression instanceof CompareExpr );

        CompareExpr compExpr = (CompareExpr) expression;
        Expression rightExpr = compExpr.getRight();
        assertTrue( rightExpr instanceof ValueExpr );

        ValueExpr rightValExpr = (ValueExpr) rightExpr;
        assertTrue( "Expected right expression value of type date", rightValExpr.isDate() && ( !rightValExpr.isString() ) );
    }

    @Test
    public void testInvalidDateFormatWithoutFunction()
    {
        QueryExpr expr = parseQuery( "publishfrom <= '2008/12/01'" );
        Expression expression = expr.getExpr();
        assertTrue( expression instanceof CompareExpr );

        CompareExpr compExpr = (CompareExpr) expression;
        Expression rightExpr = compExpr.getRight();
        assertTrue( rightExpr instanceof ValueExpr );

        ValueExpr rightValExpr = (ValueExpr) rightExpr;
        assertTrue( "Expected right expression value of type string", rightValExpr.isString() && ( !rightValExpr.isDate() ) );
    }

    @Test
    public void testDateLikeOperation()
    {
        QueryExpr expr = parseQuery( "publishFrom LIKE '2008-10-10'" );

        assertTrue(expr.getExpr() instanceof CompareExpr);
        CompareExpr cexpr = (CompareExpr)expr.getExpr();

        assertTrue(cexpr.getLeft() instanceof FieldExpr);
        assertTrue(cexpr.getRight() instanceof ValueExpr);

        assertEquals(CompareExpr.LIKE, cexpr.getOperator());

        ValueExpr rightValExpr = (ValueExpr) cexpr.getRight();
        assertTrue( "Expected right expression value of type string", rightValExpr.isString() && (!rightValExpr.isDate()) );
    }

    @Test
    public void testDateLikeOperationWithWildcard()
    {
        QueryExpr expr = parseQuery( "publishFrom LIKE '2008-%'" );

        assertTrue(expr.getExpr() instanceof CompareExpr);
        CompareExpr cexpr = (CompareExpr)expr.getExpr();

        assertTrue(cexpr.getLeft() instanceof FieldExpr);
        assertTrue(cexpr.getRight() instanceof ValueExpr);

        assertEquals(CompareExpr.LIKE, cexpr.getOperator());

        ValueExpr rightValExpr = (ValueExpr) cexpr.getRight();
        assertTrue( "Expected right expression value of type string", rightValExpr.isString() && (!rightValExpr.isDate()) );
    }

    @Test
    public void testDateNotLikeOperation()
    {
        QueryExpr expr = parseQuery( "publishFrom NOT LIKE '2008-10-10'" );

        assertTrue(expr.getExpr() instanceof CompareExpr);
        CompareExpr cexpr = (CompareExpr)expr.getExpr();

        assertTrue(cexpr.getLeft() instanceof FieldExpr);
        assertTrue(cexpr.getRight() instanceof ValueExpr);

        assertEquals( CompareExpr.NOT_LIKE, cexpr.getOperator() );

        ValueExpr rightValExpr = (ValueExpr) cexpr.getRight();
        assertTrue( "Expected right expression value of type string", rightValExpr.isString() && (!rightValExpr.isDate()) );
    }

    @Test
    public void testNotDateFieldOperation()
    {
        QueryExpr expr = parseQuery( ContentIndexConstants.F_FULLTEXT + " = '2008-10-10'" );

        assertTrue( expr.getExpr() instanceof CompareExpr );
        CompareExpr cexpr = (CompareExpr) expr.getExpr();

        assertTrue( cexpr.getLeft() instanceof FieldExpr );
        assertTrue( cexpr.getRight() instanceof ValueExpr );

        assertEquals( CompareExpr.EQ, cexpr.getOperator() );

        ValueExpr rightValExpr = (ValueExpr) cexpr.getRight();
        assertTrue( "Expected right expression value of type string", rightValExpr.isString() && (!rightValExpr.isDate()) );
    }

    @Test
    public void testValidValueExprDate()
    {
        ValueExpr expr = new ValueExpr( "2010-12-12" );
        assertTrue( expr.isValidDateString() );

        expr = new ValueExpr( "2010-12-12 23:59:59" );
        assertTrue( expr.isValidDateString() );

        expr = new ValueExpr( "2010-12-12 23:59" );
        assertTrue( expr.isValidDateString() );

        expr = new ValueExpr( "2010-12-12T23:59:59" );
        assertTrue( expr.isValidDateString() );
}

    @Test
    public void testInvalidValueExprDate()
    {
        ValueExpr expr = new ValueExpr( "2010/12/12" );
        assertFalse( expr.isValidDateString() );

        expr = new ValueExpr( "2010/12/12 23:59:59" );
        assertFalse( expr.isValidDateString() );

        expr = new ValueExpr( "2010/12/12 23:59" );
        assertFalse( expr.isValidDateString() );

        expr = new ValueExpr( "2010/12/12T23:59:59" );
        assertFalse( expr.isValidDateString() );

        expr = new ValueExpr( "2010/12/12" );
        assertFalse( expr.isValidDateString() );
    }

}
