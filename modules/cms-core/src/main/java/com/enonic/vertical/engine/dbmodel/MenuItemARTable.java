/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class MenuItemARTable
    extends Table
{
    private static final MenuItemARTable MenuItemAR = new MenuItemARTable( "tMenuItemAR", "null", "null" );

    public ForeignKeyColumn mia_mei_lKey =
        new ForeignKeyColumn( "mia_mei_lKey", "null", true, true, Constants.COLUMN_INTEGER, null, "tMenuItem", "mei_lKey", false, -1 );

    public ForeignKeyColumn mia_grp_hKey =
        new ForeignKeyColumn( "mia_grp_hKey", "null", true, true, Constants.COLUMN_CHAR, null, "tGroup", "grp_hKey", false, -1 );

    public Column mia_bRead = new Column( "mia_bRead", "null", true, false, Constants.COLUMN_BOOLEAN, null, -1 );

    public Column mia_bCreate = new Column( "mia_bCreate", "null", true, false, Constants.COLUMN_BOOLEAN, null, -1 );

    public Column mia_bPublish = new Column( "mia_bPublish", "null", true, false, Constants.COLUMN_BOOLEAN, null, -1 );

    public Column mia_bAdministrate = new Column( "mia_bAdministrate", "null", true, false, Constants.COLUMN_BOOLEAN, null, -1 );

    public Column mia_bUpdate = new Column( "mia_bUpdate", "null", true, false, Constants.COLUMN_BOOLEAN, null, -1 );

    public Column mia_bDelete = new Column( "mia_bDelete", "null", true, false, Constants.COLUMN_BOOLEAN, null, -1 );

    public Column mia_bAdd = new Column( "mia_bAdd", "null", false, false, Constants.COLUMN_BOOLEAN, null, -1 );

    private MenuItemARTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( mia_mei_lKey );
        addColumn( mia_grp_hKey );
        addColumn( mia_bRead );
        addColumn( mia_bCreate );
        addColumn( mia_bPublish );
        addColumn( mia_bAdministrate );
        addColumn( mia_bUpdate );
        addColumn( mia_bDelete );
        addColumn( mia_bAdd );
    }

    public static MenuItemARTable getInstance()
    {
        return MenuItemAR;
    }

}