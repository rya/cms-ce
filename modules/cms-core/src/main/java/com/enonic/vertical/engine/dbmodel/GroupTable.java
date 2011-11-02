/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class GroupTable
    extends Table
{
    private static final GroupTable Group = new GroupTable( "tGroup", "group", "groups" );

    public Column grp_hKey = new Column( "grp_hKey", "@key", true, true, Constants.COLUMN_CHAR, 40 );

    public Column grp_sName = new Column( "grp_sName", "name", true, false, Constants.COLUMN_VARCHAR, 256 );

    public ForeignKeyColumn grp_usr_hKey =
        new ForeignKeyColumn( "grp_usr_hKey", "@userkey", false, false, Constants.COLUMN_CHAR, null, "tUser", "usr_hKey", false, -1 );

    public ForeignKeyColumn grp_dom_lKey =
        new ForeignKeyColumn( "grp_dom_lKey", "@domainkey", false, false, Constants.COLUMN_INTEGER, null, "tDomain", "dom_lKey", false,
                              -1 );

    public Column grp_sDescription = new Column( "grp_sDescription", "description", false, false, Constants.COLUMN_VARCHAR, 1024 );

    public Column grp_bIsDeleted = new Column( "grp_bIsDeleted", "@deleted", true, false, Constants.COLUMN_BOOLEAN, -1 );

    public Column grp_bRestricted = new Column( "grp_bRestricted", "@restricted", true, false, Constants.COLUMN_BOOLEAN, -1 );

    public Column grp_sSyncValue = new Column( "grp_sSyncValue", "@syncvalue", true, false, Constants.COLUMN_VARCHAR, 2048 );

    public Column grp_lType = new Column( "grp_lType", "@type", true, false, Constants.COLUMN_INTEGER, -1 );

    private GroupTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( grp_hKey );
        addColumn( grp_sName );
        addColumn( grp_usr_hKey );
        addColumn( grp_dom_lKey );
        addColumn( grp_sDescription );
        addColumn( grp_bIsDeleted );
        addColumn( grp_bRestricted );
        addColumn( grp_sSyncValue );
        addColumn( grp_lType );
    }

    public static GroupTable getInstance()
    {
        return Group;
    }

}