/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.View;

public final class MenuItemARView
    extends View
{
    public Column mia_mei_lKey = new Column( "mia_mei_lKey", "null", Constants.COLUMN_INTEGER );

    public Column grp_hKey = new Column( "grp_hKey", "null", Constants.COLUMN_CHAR );

    public Column grp_sName = new Column( "grp_sName", "null", Constants.COLUMN_VARCHAR );

    public Column grp_lType = new Column( "grp_lType", "null", Constants.COLUMN_INTEGER );

    public Column usr_hKey = new Column( "usr_hKey", "null", Constants.COLUMN_CHAR );

    public Column usr_sUID = new Column( "usr_sUID", "null", Constants.COLUMN_VARCHAR );

    public Column usr_sFullName = new Column( "usr_sFullName", "null", Constants.COLUMN_VARCHAR );

    public Column mia_bRead = new Column( "mia_bRead", "null", Constants.COLUMN_BOOLEAN );

    public Column mia_bCreate = new Column( "mia_bCreate", "null", Constants.COLUMN_BOOLEAN );

    public Column mia_bPublish = new Column( "mia_bPublish", "null", Constants.COLUMN_BOOLEAN );

    public Column mia_bAdministrate = new Column( "mia_bAdministrate", "null", Constants.COLUMN_BOOLEAN );

    public Column mia_bUpdate = new Column( "mia_bUpdate", "null", Constants.COLUMN_BOOLEAN );

    public Column mia_bDelete = new Column( "mia_bDelete", "null", Constants.COLUMN_BOOLEAN );

    public Column mia_bAdd = new Column( "mia_bAdd", "null", Constants.COLUMN_BOOLEAN );

    private final static String SQL = "select MIA_MEI_LKEY, MIA_grp_hKey as grp_hKey, GRP_SNAME, GRP_LTYPE, " +
        "usr_hKey as usr_hKey, USR_SUID, USR_SFULLNAME, MIA_BREAD, MIA_BCREATE, MIA_BPUBLISH, " +
        "MIA_BADMINISTRATE, MIA_BUPDATE, MIA_BDELETE, MIA_BADD from TMENUITEMAR " +
        "join TGROUP on MIA_grp_hKey = grp_hKey join TMENUITEM on MIA_MEI_LKEY = MEI_LKEY " +
        "left join TUSER on usr_grp_hKey = grp_hKey where GRP_BISDELETED = 0 and (USR_BISDELETED = 0 or " + "USR_BISDELETED IS NULL)";

    private static final MenuItemARView MenuItemAR = new MenuItemARView( "vMenuItemAR" );

    private MenuItemARView( String tableName )
    {
        super( tableName, "null", "null", SQL, 11 );
        addColumn( mia_mei_lKey );
        addColumn( grp_hKey );
        addColumn( grp_sName );
        addColumn( grp_lType );
        addColumn( usr_hKey );
        addColumn( usr_sUID );
        addColumn( usr_sFullName );
        addColumn( mia_bRead );
        addColumn( mia_bCreate );
        addColumn( mia_bPublish );
        addColumn( mia_bAdministrate );
        addColumn( mia_bUpdate );
        addColumn( mia_bDelete );
        addColumn( mia_bAdd );
    }

    public static MenuItemARView getInstance()
    {
        return MenuItemAR;
    }
}
