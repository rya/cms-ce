/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.queryexpression;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.ReadableDateTime;

public class DateCompareEvaluator
    extends QueryEvaluatorAdapter
{
    public Object evaluate( CompareExpr expr )
    {
        Expression left = (Expression) expr.getLeft().evaluate( this );
        Expression right = (Expression) expr.getRight().evaluate( this );

        if ( ( right instanceof ValueExpr ) && ( (ValueExpr) right ).isDate() )
        {
            return createFixedDateCompare( expr.getOperator(), left, (ValueExpr) right );
        }
        else
        {
            return new CompareExpr( expr.getOperator(), left, right );
        }
    }

    private Expression createFixedDateCompare( int op, Expression left, ValueExpr right )
    {
        if ( op == CompareExpr.EQ )
        {
            return createFixedDateEq( left, right );
        }
        else if ( op == CompareExpr.LTE )
        {
            return createFixedDateLte( left, right );
        }
        else
        {
            return new CompareExpr( op, left, right );
        }
    }

    private Expression createFixedDateEq( Expression left, ValueExpr right )
    {
        ReadableDateTime date = (ReadableDateTime) right.getValue();
        if ( date instanceof DateMidnight )
        {
            CompareExpr lowerBound = new CompareExpr( CompareExpr.GTE, left, new ValueExpr( createLowerBoundDate( (DateMidnight) date ) ) );
            CompareExpr upperBound = new CompareExpr( CompareExpr.LTE, left, new ValueExpr( createUpperBoundDate( (DateMidnight) date ) ) );
            return new LogicalExpr( LogicalExpr.AND, lowerBound, upperBound );
        }

        return new CompareExpr( CompareExpr.EQ, left, right );
    }

    private Expression createFixedDateLte( Expression left, ValueExpr right )
    {
        ReadableDateTime date = (ReadableDateTime) right.getValue();
        if ( date instanceof DateMidnight )
        {
            right = new ValueExpr( createUpperBoundDate( (DateMidnight) date ) );
        }

        return new CompareExpr( CompareExpr.LTE, left, right );
    }

    private ReadableDateTime createLowerBoundDate( DateMidnight date )
    {
        return date.toDateTime();
    }

    private ReadableDateTime createUpperBoundDate( DateMidnight date )
    {
        DateTime dateMidnight = date.toDateTime();
        return dateMidnight.plus( new Period( 23, 59, 59, 999 ) );
    }
}
