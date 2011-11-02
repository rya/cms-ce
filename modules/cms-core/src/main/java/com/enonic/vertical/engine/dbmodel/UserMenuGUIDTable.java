/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class UserMenuGUIDTable
    extends Table
{
    private static final UserMenuGUIDTable UserMenuGUID = new UserMenuGUIDTable( "tUserMenuGUID", "null", "null" );

    public ForeignKeyColumn umg_usr_hKey =
        new ForeignKeyColumn( "umg_usr_hKey", "null", true, true, Constants.COLUMN_CHAR, null, "tUser", "usr_hKey", true, -1 );

    public ForeignKeyColumn umg_men_lKey =
        new ForeignKeyColumn( "umg_men_lKey", "null", true, true, Constants.COLUMN_INTEGER, null, "tMenu", "men_lKey", true, -1 );

    public Column umg_sGUID = new Column( "umg_sGUID", "null", true, false, Constants.COLUMN_VARCHAR, 256 );

    public Column umg_dteCreated = new Column( "umg_dteCreated", "timestamp", true, false, Constants.COLUMN_CREATED_TIMESTAMP, -1 );

    private UserMenuGUIDTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( umg_usr_hKey );
        addColumn( umg_men_lKey );
        addColumn( umg_sGUID );
        addColumn( umg_dteCreated );
    }

    public static UserMenuGUIDTable getInstance()
    {
        return UserMenuGUID;
    }

}