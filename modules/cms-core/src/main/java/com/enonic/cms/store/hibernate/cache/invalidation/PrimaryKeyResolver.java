/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.cache.invalidation;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PrimaryKeyResolver
{


    private static final String PATTERN = ".+\\s+columnName\\s*=\\s*['\"]??([\\d\\w]+)['\"]??.*";


    public Serializable resolveIntegerValue( String sql, final List paramList, final String columnName )
    {

        try
        {
            String patternStr = PATTERN.replace( "columnName", columnName );
            String strValue = doResolve( sql, paramList, patternStr );
            return new Integer( strValue );
        }
        catch ( Exception e )
        {
            // must not throw exception, since this methos is very critical and must work without failure
            return null;
        }
    }

    public Serializable resolveStringValue( String sql, final List paramList, final String columnName )
    {

        try
        {
            String patternStr = PATTERN.replace( "columnName", columnName );
            return doResolve( sql, paramList, patternStr );
        }
        catch ( Exception e )
        {
            // must not throw exception, since this methos is very critical and must work without failure
            return null;
        }
    }

    private String doResolve( String sql, final List paramList, final String patternStr )
    {

        sql = replaceValuePlaceHoldersWithValues( sql, paramList );
        Pattern pattern = Pattern.compile( patternStr );
        Matcher matcher = pattern.matcher( sql );
        if ( matcher.matches() )
        {
            return matcher.group( 1 ).trim();
        }

        return null;
    }

    private String replaceValuePlaceHoldersWithValues( final String sql, final List paramList )
    {

        int paramListIndex = 0;
        final StringBuffer replaced = new StringBuffer( sql.length() );
        for ( int i = 0; i < sql.length(); i++ )
        {
            final char c = sql.charAt( i );
            if ( c == '?' )
            {
                final int currentIndex = paramListIndex++;
                if ( currentIndex < paramList.size() )
                {
                    final Object value = paramList.get( currentIndex );
                    replaced.append( value != null ? value : "x" );
                }
            }
            else
            {
                replaced.append( c );
            }
        }
        return replaced.toString();
    }
}
