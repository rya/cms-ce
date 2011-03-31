/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.View;

public final class ConAccessRightView
    extends View
{
    public Column coa_con_lKey = new Column( "coa_con_lKey", "null", Constants.COLUMN_INTEGER );

    public Column grp_hKey = new Column( "grp_hKey", "null", Constants.COLUMN_CHAR );

    public Column grp_sName = new Column( "grp_sName", "null", Constants.COLUMN_VARCHAR );

    public Column grp_lType = new Column( "grp_lType", "null", Constants.COLUMN_INTEGER );

    public Column usr_hKey = new Column( "usr_hKey", "null", Constants.COLUMN_CHAR );

    public Column usr_sUID = new Column( "usr_sUID", "null", Constants.COLUMN_VARCHAR );

    public Column usr_sFullName = new Column( "usr_sFullName", "null", Constants.COLUMN_VARCHAR );

    public Column coa_bRead = new Column( "coa_bRead", "null", Constants.COLUMN_BOOLEAN );

    public Column coa_bUpdate = new Column( "coa_bUpdate", "null", Constants.COLUMN_BOOLEAN );

    public Column coa_bDelete = new Column( "coa_bDelete", "null", Constants.COLUMN_BOOLEAN );

    private final static String SQL = "select coa_con_lKey, coa_grp_hKey as grp_hKey, grp_sName, grp_lType, usr_hKey as usr_hKey, " +
        "usr_sUID, usr_sFullName, coa_bRead, coa_bUpdate, coa_bDelete from tConAccessRight2 c1 " +
        "join tContent on coa_con_lKey = con_lKey join tGroup on coa_grp_hKey = grp_hKey " + "left join tUser on usr_grp_hKey = grp_hKey " +
        "where con_bDeleted = 0 and grp_bIsDeleted = 0 and (usr_bIsDeleted = 0 or usr_bIsDeleted IS NULL)";

    private static final ConAccessRightView ConAccessRight = new ConAccessRightView( "vConAccessRight" );

    private ConAccessRightView( String tableName )
    {
        super( tableName, "null", "null", SQL, 3 );
        addColumn( coa_con_lKey );
        addColumn( grp_hKey );
        addColumn( grp_sName );
        addColumn( grp_lType );
        addColumn( usr_hKey );
        addColumn( usr_sUID );
        addColumn( usr_sFullName );
        addColumn( coa_bRead );
        addColumn( coa_bUpdate );
        addColumn( coa_bDelete );
    }

    public static ConAccessRightView getInstance()
    {
        return ConAccessRight;
    }

}