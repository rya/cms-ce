/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

import org.joda.time.ReadableDateTime;
import org.junit.Test;

import com.enonic.cms.domain.content.index.ContentIndexQuery;
import com.enonic.cms.domain.content.index.ContentIndexQueryExprParser;
import com.enonic.cms.domain.content.index.queryexpression.CompareExpr;
import com.enonic.cms.domain.content.index.queryexpression.LogicalExpr;
import com.enonic.cms.domain.content.index.queryexpression.QueryExpr;
import com.enonic.cms.domain.content.index.queryexpression.ValueExpr;

import static org.junit.Assert.*;


public class ContentQueryExprParserTest
{

    @Test
    public void testBothFunctionAndDateCompareEvaluatorsAreUsed()
    {
        //We use a date compare expression to check that both function and dateCompare evaluators are called.
        ContentIndexQuery contentQuery = new ContentIndexQuery( "x = date('2008-12-01')" );

        QueryExpr queryExpr = ContentIndexQueryExprParser.parse( contentQuery );

        assertTrue( queryExpr.getExpr() instanceof LogicalExpr );
        LogicalExpr logical = (LogicalExpr) queryExpr.getExpr();

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
        assertEquals( 2008, lowerDate.getYear() );
        assertEquals( 12, lowerDate.getMonthOfYear() );
        assertEquals( 1, lowerDate.getDayOfMonth() );
        assertEquals( 0, lowerDate.getHourOfDay() );
        assertEquals( 0, lowerDate.getMinuteOfHour() );
        assertEquals( 0, lowerDate.getSecondOfMinute() );
        assertEquals( 0, lowerDate.getMillisOfSecond() );

        assertTrue( upperValue.isDate() );

        ReadableDateTime upperDate = (ReadableDateTime) upperValue.getValue();
        assertEquals( 2008, upperDate.getYear() );
        assertEquals( 12, upperDate.getMonthOfYear() );
        assertEquals( 1, upperDate.getDayOfMonth() );
        assertEquals( 23, upperDate.getHourOfDay() );
        assertEquals( 59, upperDate.getMinuteOfHour() );
        assertEquals( 59, upperDate.getSecondOfMinute() );
        assertEquals( 999, upperDate.getMillisOfSecond() );
    }

    @Test
    public void testOrderBy()
    {
        ContentIndexQuery contentQuery;
        QueryExpr queryExpr;

        contentQuery = new ContentIndexQuery( "ORDER BY contentdata/id DESC" );
        queryExpr = ContentIndexQueryExprParser.parse( contentQuery );
        assertNull( queryExpr.getExpr() );
        assertEquals( contentQuery.getQuery(), queryExpr.getOrderBy().toString() );
        assertEquals( 1, queryExpr.getOrderBy().getFields().length );
        assertTrue( queryExpr.getOrderBy().getFields()[0].isDescending() );

        contentQuery = new ContentIndexQuery( "ORDER BY contentdata/sap-id DESC" );
        assertNull( queryExpr.getExpr() );
        queryExpr = ContentIndexQueryExprParser.parse( contentQuery );
        assertEquals( contentQuery.getQuery(), queryExpr.getOrderBy().toString() );
        assertEquals( 1, queryExpr.getOrderBy().getFields().length );
        assertTrue( queryExpr.getOrderBy().getFields()[0].isDescending() );

        contentQuery = new ContentIndexQuery( "ORDER BY contentdata.other ASC" );
        assertNull( queryExpr.getExpr() );
        queryExpr = ContentIndexQueryExprParser.parse( contentQuery );
        assertEquals( contentQuery.getQuery(), queryExpr.getOrderBy().toString() );
        assertEquals( 1, queryExpr.getOrderBy().getFields().length );
        assertTrue( queryExpr.getOrderBy().getFields()[0].isAscending() );
    }
}
