/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.View;

public final class CatAccessRightView
    extends View
{
    public final Column car_cat_lKey = new Column( "car_cat_lKey", "null", Constants.COLUMN_INTEGER );

    public final Column grp_hKey = new Column( "grp_hKey", "null", Constants.COLUMN_CHAR );

    public final Column grp_sName = new Column( "grp_sName", "null", Constants.COLUMN_VARCHAR );

    public final Column grp_lType = new Column( "grp_lType", "null", Constants.COLUMN_INTEGER );

    public final Column usr_hKey = new Column( "usr_hKey", "null", Constants.COLUMN_CHAR );

    public final Column usr_sUID = new Column( "usr_sUID", "null", Constants.COLUMN_VARCHAR );

    public final Column usr_sFullName = new Column( "usr_sFullName", "null", Constants.COLUMN_VARCHAR );

    public final Column car_bRead = new Column( "car_bRead", "null", Constants.COLUMN_BOOLEAN );

    public final Column car_bCreate = new Column( "car_bCreate", "null", Constants.COLUMN_BOOLEAN );

    public final Column car_bPublish = new Column( "car_bPublish", "null", Constants.COLUMN_BOOLEAN );

    public final Column car_bAdministrate = new Column( "car_bAdministrate", "null", Constants.COLUMN_BOOLEAN );

    public final Column car_bAdminread = new Column( "car_bAdminread", "null", Constants.COLUMN_BOOLEAN );

    private final static String SQL = "select CAR_CAT_LKEY, CAR_grp_hKey as grp_hKey, GRP_SNAME, GRP_LTYPE, " +
        "usr_hKey as usr_hKey, USR_SUID, USR_SFULLNAME, CAR_BREAD, CAR_BCREATE, CAR_BPUBLISH, " +
        "CAR_BADMINISTRATE, CAR_BADMINREAD from TCATACCESSRIGHT " +
        "join TGROUP on CAR_grp_hKey = grp_hKey join TCATEGORY on CAR_CAT_LKEY = CAT_LKEY " +
        "left join TUSER on usr_grp_hKey = grp_hKey " +
        "where CAT_BDELETED = 0 and GRP_BISDELETED = 0 and (USR_BISDELETED = 0 or USR_BISDELETED IS NULL)";

    private static final CatAccessRightView CatAccessRight = new CatAccessRightView( "vCatAccessRight" );

    private CatAccessRightView( String tableName )
    {
        super( tableName, "null", "null", SQL, 1 );
        addColumn( car_cat_lKey );
        addColumn( grp_hKey );
        addColumn( grp_sName );
        addColumn( grp_lType );
        addColumn( usr_hKey );
        addColumn( usr_sUID );
        addColumn( usr_sFullName );
        addColumn( car_bRead );
        addColumn( car_bCreate );
        addColumn( car_bPublish );
        addColumn( car_bAdministrate );
        addColumn( car_bAdminread );
    }

    public static CatAccessRightView getInstance()
    {
        return CatAccessRight;
    }
}
