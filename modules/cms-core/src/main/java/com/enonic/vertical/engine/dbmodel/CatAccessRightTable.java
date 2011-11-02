/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class CatAccessRightTable
    extends Table
{
    private static final CatAccessRightTable CatAccessRight = new CatAccessRightTable( "tCatAccessRight", "null", "null" );

    public ForeignKeyColumn car_cat_lKey =
        new ForeignKeyColumn( "car_cat_lKey", "null", true, true, Constants.COLUMN_INTEGER, null, "tCategory", "cat_lKey", false, -1 );

    public ForeignKeyColumn car_grp_hKey =
        new ForeignKeyColumn( "car_grp_hKey", "null", true, true, Constants.COLUMN_CHAR, null, "tGroup", "grp_hKey", false, -1 );

    public Column car_bRead = new Column( "car_bRead", "null", true, false, Constants.COLUMN_BOOLEAN, -1 );

    public Column car_bCreate = new Column( "car_bCreate", "null", true, false, Constants.COLUMN_BOOLEAN, -1 );

    public Column car_bPublish = new Column( "car_bPublish", "null", true, false, Constants.COLUMN_BOOLEAN, -1 );

    public Column car_bAdministrate = new Column( "car_bAdministrate", "null", true, false, Constants.COLUMN_BOOLEAN, -1 );

    public Column car_bAdminRead = new Column( "car_bAdminRead", "null", true, false, Constants.COLUMN_BOOLEAN, -1 );

    private CatAccessRightTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( car_cat_lKey );
        addColumn( car_grp_hKey );
        addColumn( car_bRead );
        addColumn( car_bCreate );
        addColumn( car_bPublish );
        addColumn( car_bAdministrate );
        addColumn( car_bAdminRead );
    }

    public static CatAccessRightTable getInstance()
    {
        return CatAccessRight;
    }

}