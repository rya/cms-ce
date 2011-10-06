/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.queryexpression;

import java.util.List;

import org.codehaus.jparsec.Tokens;
import org.codehaus.jparsec.functors.Binary;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.functors.Map2;
import org.codehaus.jparsec.functors.Map3;
import org.codehaus.jparsec.functors.Unary;

final class QueryMapper
{
    public static Map<String, ValueExpr> stringToNumberExpr()
    {
        return new Map<String, ValueExpr>()
        {
            public ValueExpr map( final String from )
            {
                return new ValueExpr( new Double( from.trim() ) );
            }
        };
    }

    public static Map<String, ValueExpr> stringToStringExpr()
    {
        return new Map<String, ValueExpr>()
        {
            public ValueExpr map( final String from )
            {
                return new ValueExpr( from.trim() );
            }
        };
    }

    public static Map<String, FieldExpr> stringToFieldExpr()
    {
        return new Map<String, FieldExpr>()
        {
            public FieldExpr map( final String from )
            {
                return new FieldExpr( from.trim() );
            }
        };
    }

    public static Map<String, Tokens.Fragment> stringToFragment( final String tag )
    {
        return new Map<String, Tokens.Fragment>()
        {
            public Tokens.Fragment map( final String from )
            {
                return Tokens.fragment( from, tag );
            }
        };
    }

    public static Map3<FieldExpr, Integer, Expression, CompareExpr> compareExprMapper()
    {
        return new Map3<FieldExpr, Integer, Expression, CompareExpr>()
        {
            public CompareExpr map( final FieldExpr a, final Integer b, final Expression c )
            {
                return new CompareExpr( b, a, c );
            }
        };
    }

    public static Map<List<ValueExpr>, ArrayExpr> valuesToArrayExpr()
    {
        return new Map<List<ValueExpr>, ArrayExpr>()
        {
            public ArrayExpr map( final List<ValueExpr> from )
            {
                return new ArrayExpr( from.toArray( new ValueExpr[from.size()] ) );
            }
        };
    }

    public static Map2<String, ArrayExpr, FunctionExpr> functionExprMapper()
    {
        return new Map2<String, ArrayExpr, FunctionExpr>()
        {
            public FunctionExpr map( final String a, final ArrayExpr b )
            {
                return new FunctionExpr( a.trim(), b );
            }
        };
    }

    public static Map<ValueExpr, ValueExpr> prefixSuffixMapper( final String prefix, final String suffix )
    {
        return new Map<ValueExpr, ValueExpr>()
        {
            public ValueExpr map( final ValueExpr from )
            {
                String str = (String) from.getValue();

                if ( prefix != null )
                {
                    str = prefix + str;
                }

                if ( suffix != null )
                {
                    str = str + suffix;
                }

                return new ValueExpr( str );
            }
        };
    }

    public static Binary<Expression> logicalExprMapper( final int op )
    {
        return new Binary<Expression>()
        {
            public Expression map( final Expression left, final Expression right )
            {
                return new LogicalExpr( op, left, right );
            }
        };
    }

    public static Unary<Expression> notExprMapper()
    {
        return new Unary<Expression>()
        {
            public Expression map( final Expression from )
            {
                return new NotExpr( from );
            }
        };
    }

    public static Map<List<OrderFieldExpr>, OrderByExpr> orderByExprMapper()
    {
        return new Map<List<OrderFieldExpr>, OrderByExpr>()
        {
            public OrderByExpr map( final List<OrderFieldExpr> from )
            {
                return new OrderByExpr( from.toArray( new OrderFieldExpr[from.size()] ) );
            }
        };
    }

    public static Map2<FieldExpr, Boolean, OrderFieldExpr> orderFieldExprMapper()
    {
        return new Map2<FieldExpr, Boolean, OrderFieldExpr>()
        {
            public OrderFieldExpr map( final FieldExpr a, final Boolean b )
            {
                return new OrderFieldExpr( a, b );
            }
        };
    }

    public static Map2<Expression, OrderByExpr, QueryExpr> queryExprMapper()
    {
        return new Map2<Expression, OrderByExpr, QueryExpr>()
        {
            public QueryExpr map( final Expression a, final OrderByExpr b )
            {
                return new QueryExpr( a, b );
            }
        };
    }
}
