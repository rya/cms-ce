/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.config;

public final class IndexPathHelper
{
    public static String transformName( String value )
    {
        return transformOldPath( value ).substring( "content".length() );
    }

    /**
     * Transform the path in form of contentdata/xxx.
     */
    public static String transformOldPath( String value )
    {
        value = resolveDataAlias( value );

        if ( value.startsWith( "contentdata/" ) )
        {
            return value;
        }

        return "contentdata/" + value;
    }

    public static String transformNewPath( String value )
    {
        return resolveDataAlias( value );
    }

    private static String resolveDataAlias( String value )
    {
        value = value.trim();

        if ( value.startsWith( "/" ) )
        {
            value = value.substring( 1 );
        }
        if ( value.startsWith( "contentdata/" ) )
        {
            return value;
        }
        if ( value.startsWith( "data/" ) )
        {
            return "content" + value;
        }
        return value;
    }
}
