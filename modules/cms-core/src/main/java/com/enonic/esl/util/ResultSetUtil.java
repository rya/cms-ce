/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.util;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetUtil
{

    public static void printItems( ResultSet rs )
    {
        try
        {
            int columns = rs.getMetaData().getColumnCount();
            for ( int i = 0; i < columns; i++ )
            {
                System.out.println( i + ": " + rs.getObject( i + 1 ) );
            }
        }
        catch ( SQLException sqle )
        {
            sqle.printStackTrace();
        }
    }

}