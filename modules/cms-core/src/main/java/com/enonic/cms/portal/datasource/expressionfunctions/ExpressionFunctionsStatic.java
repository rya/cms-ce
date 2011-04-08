/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource.expressionfunctions;

/**
 * This class implements the expression functions.
 */
public final class ExpressionFunctionsStatic
{

    public static String isnotblank( String str )
    {
        return Boolean.toString( ExpressionFunctionsFactory.get().createExpressionFunctions().isnotblank( str ) );
    }

    public static String isblank( String str )
    {
        return Boolean.toString( ExpressionFunctionsFactory.get().createExpressionFunctions().isblank( str ) );
    }

    public static String isnotempty( String str )
    {
        return Boolean.toString( ExpressionFunctionsFactory.get().createExpressionFunctions().isnotempty( str ) );
    }

    public static String isempty( String str )
    {
        return Boolean.toString( ExpressionFunctionsFactory.get().createExpressionFunctions().isempty( str ) );
    }

    public static String select( String s1, String s2 )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().select( s1, s2 );
    }

    public static String concat( String a, String b )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().concat( a, b );
    }

    public static String replace( String source, String regex, String replacement )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().replace( source, regex, replacement );
    }

    public static String substring( String source, int beginIndex, int endIndex )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().substring( source, beginIndex, endIndex );
    }

    public static int stringlength( String source )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().stringlength( source );
    }

    public static String lower( String source )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().lower( source );
    }

    public static String upper( String source )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().upper( source );
    }

    public static String trim( String source )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().trim( source );
    }

    public static int min( int v1, int v2 )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().min( v1, v2 );
    }

    public static int max( int v1, int v2 )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().max( v1, v2 );
    }

    public static String currentDate( String format )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().currentDate( format );
    }

    public static String currentDatePlusOffset( String format, String period )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().currentDatePlusOffset( format, period );
    }

    public static String currentDateMinusOffset( String format, String period )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().currentDateMinusOffset( format, period );
    }

    public static String periodHoursMinutes( int hours, int minutes )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().periodHoursMinutes( hours, minutes );
    }

    public static String pref( String scope, String key )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().pref( scope, key );
    }

    /**
     * This method will take a freetext search string and create a valid query that can be used in the getContent* methods.  The search
     * string is spilt into tokens.  Using the operator, it may be specified whether the field must contain all or any of the words in the
     * search string.
     *
     * @param fieldName    The name of the field to search for the words in the search string.
     * @param searchString The words to search for.
     * @param operator     Must be either AND or OR.  Case doesn't matter.
     * @return A syntactically correct search that may be used as the query parameter in getContent* methods on the data source. With care,
     *         it may also be merged with other queries using AND or OR.
     * @throws IllegalArgumentException If any of the parameters are empty or the operator is not AND or OR.
     */
    public static String buildFreetextQuery( String fieldName, String searchString, String operator )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().buildFreetextQuery( fieldName, searchString, operator );
    }

    public static String getPageKey()
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().getPageKey();
    }

    public static String getPortletWindowKey()
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().getWindowKey();
    }

    public static String pageKey(String path)
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().pageKey(path);
    }
}
