/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql;

public class TableQualifierGenerator
{
    int id = 0;

    public String next()
    {
        String q = "t" + String.valueOf( id );

        id++;
        if ( id == Integer.MAX_VALUE )
        {
            id = 0;
        }

        return q;
    }
}
