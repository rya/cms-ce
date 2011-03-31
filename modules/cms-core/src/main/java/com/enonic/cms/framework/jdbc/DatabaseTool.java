/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc;

public class DatabaseTool
{

    public static String generateDropTable( String tableName )
    {

        return "DROP TABLE " + tableName;
    }

}
