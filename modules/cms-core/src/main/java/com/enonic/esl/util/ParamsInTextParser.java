/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.util;

import java.util.StringTokenizer;

import com.enonic.esl.containers.ExtendedMap;

public class ParamsInTextParser
{

    public static ExtendedMap parseParamsInText( String text, String paramsStartBlock, String paramsEndBlock, String paramsSeperator )
    {

        int paramBlockStart = text.indexOf( paramsStartBlock );
        if ( paramBlockStart == -1 )
        {
            return null;
        }

        int paramBlockEnd = text.indexOf( paramsEndBlock, paramBlockStart );
        if ( paramBlockEnd - 1 <= paramBlockStart )
        {
            return null;
        }

        String params = text.substring( paramBlockStart + 1, paramBlockEnd );
        StringTokenizer st = new StringTokenizer( params, paramsSeperator );
        ExtendedMap parameters = new ExtendedMap();
        while ( st.hasMoreTokens() )
        {
            String param = st.nextToken();
            StringTokenizer st2 = new StringTokenizer( param, "=" );
            String paramName = null;
            String paramValue = null;
            if ( st2.hasMoreTokens() )
            {
                paramName = st2.nextToken();
            }
            if ( st2.hasMoreTokens() )
            {
                paramValue = st2.nextToken();
            }

            if ( paramName != null && paramValue != null )
            {
                parameters.putString( paramName, paramValue );
            }
        }

        return parameters;
    }
}
