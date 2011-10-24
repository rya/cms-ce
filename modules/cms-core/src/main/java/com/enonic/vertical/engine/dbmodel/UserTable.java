/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class UserTable
    extends Table
{
    public static final UserTable INSTANCE = new UserTable( "tUser", "user", "users" );

    public Column usr_hKey = new Column( "usr_hKey", "@key", true, true, Constants.COLUMN_CHAR, null, 40 );

    public Column usr_sUID = new Column( "usr_sUID", "@uid", true, false, Constants.COLUMN_VARCHAR, null, 256 );

    public Column usr_sFullName = new Column( "usr_sFullName", "@fullname", true, false, Constants.COLUMN_VARCHAR, null, 256 );

    public Column usr_dteTimestamp =
        new Column( "usr_dteTimestamp", "timestamp", true, false, Constants.COLUMN_CURRENT_TIMESTAMP, null, -1 );

    public Column usr_bIsDeleted = new Column( "usr_bIsDeleted", "@deleted", true, false, Constants.COLUMN_BOOLEAN, Boolean.FALSE, -1 );

    public Column usr_ut_lKey = new Column( "usr_ut_lKey", "@usertype", true, false, Constants.COLUMN_INTEGER, null, -1 );

    public ForeignKeyColumn usr_dom_lKey =
        new ForeignKeyColumn( "usr_dom_lKey", "@domainkey", false, false, Constants.COLUMN_INTEGER, null, "tDomain", "dom_lKey", false,
                              -1 );

    public Column usr_sSyncValue = new Column( "usr_sSyncValue", "@syncvalue", true, false, Constants.COLUMN_VARCHAR, null, 2048 );

    public Column usr_sEmail = new Column( "usr_sEmail", "null", false, false, Constants.COLUMN_VARCHAR, null, 256 );

    public Column usr_sPassword = new Column( "usr_sPassword", "null", false, false, Constants.COLUMN_VARCHAR, null, 64 );

    public Column usr_grp_hKey = new Column( "usr_grp_hKey", "null", false, false, Constants.COLUMN_CHAR, null, 40 );

    public Column usr_photo = new Column( "usr_photo", "null", false, false, Constants.COLUMN_BINARY, null, 1 );

    private UserTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( usr_hKey );
        addColumn( usr_sUID );
        addColumn( usr_sFullName );
        addColumn( usr_dteTimestamp );
        addColumn( usr_bIsDeleted );
        addColumn( usr_ut_lKey );
        addColumn( usr_dom_lKey );
        addColumn( usr_sSyncValue );
        addColumn( usr_sEmail );
        addColumn( usr_sPassword );
        addColumn( usr_grp_hKey );
        addColumn( usr_photo );
    }
}