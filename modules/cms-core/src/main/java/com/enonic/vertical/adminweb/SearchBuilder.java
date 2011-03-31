/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.util.HashSet;
import java.util.StringTokenizer;

public final class SearchBuilder
{
    public static SearchStringBuffer buildFromUserInput( String search, boolean includeTitle, boolean includeAllDataFields,
                                                         boolean includeAttchments )
    {
        SearchStringBuffer query = new SearchStringBuffer();
        HashSet<String> params = getParams( search );
        for ( String param : params )
        {
            SearchStringBuffer paramQuery = new SearchStringBuffer();

            if ( includeTitle )
            {
                paramQuery.appendTitle( param );
            }
            if ( includeAllDataFields )
            {
                paramQuery.appendData( SearchStringBuffer.Operator.OR, param );
            }
            if ( includeAttchments )
            {
                paramQuery.appendAttachments( SearchStringBuffer.Operator.OR, param );
            }
            query.appendRaw( paramQuery );
        }
        return query;
    }

    private static HashSet<String> getParams( String search )
    {
        HashSet<String> params = new HashSet<String>();
        StringTokenizer tok = new StringTokenizer( search, " " );

        while ( tok.hasMoreTokens() )
        {
            String param = tok.nextToken();
            if ( param.length() > 0 )
            {
                params.add( param );
            }
        }
        return params;
    }

}