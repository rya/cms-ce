/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.Table;

public final class UserFieldTable
    extends Table
{
    private static final UserFieldTable UserField = new UserFieldTable( "tUserField", "null", "null" );

    public Column usf_usr_hKey = new Column( "usf_usr_hKey", "null", true, true, Constants.COLUMN_CHAR, 40 );

    public Column usf_name = new Column( "usf_name", "null", true, true, Constants.COLUMN_VARCHAR, 100 );

    public Column usf_value = new Column( "usf_value", "null", false, false, Constants.COLUMN_VARCHAR, 512 );

    private UserFieldTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( usf_usr_hKey );
        addColumn( usf_name );
        addColumn( usf_value );
    }

    public static UserFieldTable getInstance()
    {
        return UserField;
    }

}