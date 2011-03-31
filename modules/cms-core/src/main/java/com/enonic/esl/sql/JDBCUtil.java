/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: jvs Date: 16.mai.2003 Time: 09:28:14 To change this template use Options | File Templates.
 */
public class JDBCUtil
{


    public static void addValuesToPreparedStatement( PreparedStatement preparedStatement, List values )
        throws SQLException
    {
        int i = 1;
        for ( Iterator iter = values.iterator(); iter.hasNext(); i++ )
        {
            Object paramValue = iter.next();
            preparedStatement.setObject( i, paramValue );
        }
    }
}
