/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.View;

public final class CategoryView
    extends View
{
    public Column cat_lKey = new Column( "cat_lKey", "null", Constants.COLUMN_INTEGER );

    public Column cat_uni_lKey = new Column( "cat_uni_lKey", "null", Constants.COLUMN_INTEGER );

    public Column cat_cty_lKey = new Column( "cat_cty_lKey", "null", Constants.COLUMN_INTEGER );

    public Column cat_cat_lSuper = new Column( "cat_cat_lSuper", "null", Constants.COLUMN_INTEGER );

    public Column usr_hOwner = new Column( "usr_hOwner", "null", Constants.COLUMN_CHAR );

    public Column usr_sOwnerUID = new Column( "usr_sOwnerUID", "null", Constants.COLUMN_VARCHAR );

    public Column usr_sOwnerName = new Column( "usr_sOwnerName", "null", Constants.COLUMN_VARCHAR );

    public Column cat_dteCreated = new Column( "cat_dteCreated", "null", Constants.COLUMN_CREATED_TIMESTAMP );

    public Column cat_sName = new Column( "cat_sName", "null", Constants.COLUMN_VARCHAR );

    public Column cat_sDescription = new Column( "cat_sDescription", "null", Constants.COLUMN_VARCHAR );

    public Column usr_hModifier = new Column( "usr_hModifier", "null", Constants.COLUMN_CHAR );

    public Column usr_sModifierUID = new Column( "usr_sModifierUID", "null", Constants.COLUMN_VARCHAR );

    public Column usr_sModifierName = new Column( "usr_sModifierName", "null", Constants.COLUMN_VARCHAR );

    public Column cat_dteTimestamp = new Column( "cat_dteTimestamp", "null", Constants.COLUMN_CURRENT_TIMESTAMP );

    private final static String SQL = "select CAT_LKEY, CAT_UNI_LKEY, CAT_CTY_LKEY, CAT_CAT_LSUPER, " +
        "CAT_USR_hOwner as usr_hOwner, o.USR_SUID as usr_sOwnerUID, o.USR_SFULLNAME as usr_sOwnerName, CAT_DTECREATED, CAT_SNAME, " +
        "CAT_SDESCRIPTION, CAT_USR_hModifier as usr_hModifier, m.USR_SUID as usr_sModifierUID, " +
        "m.USR_SFULLNAME as usr_sModifierName, CAT_DTETIMESTAMP from TCATEGORY " +
        "join TUSER o on CAT_USR_hOwner = o.usr_hKey join TUSER m on CAT_USR_hModifier = m.usr_hKey " + "where CAT_BDELETED = 0 " +
        "and ( CAT_UNI_LKEY is null or CAT_UNI_LKEY not in (select UNI_LKEY from TUNIT where UNI_BDELETED <> 0) )";

    private static final CategoryView Category = new CategoryView( "vCategory" );

    private CategoryView( String tableName )
    {
        super( tableName, "null", "null", SQL, 2 );
        addColumn( cat_lKey );
        addColumn( cat_uni_lKey );
        addColumn( cat_cty_lKey );
        addColumn( cat_cat_lSuper );
        addColumn( usr_hOwner );
        addColumn( usr_sOwnerUID );
        addColumn( usr_sOwnerName );
        addColumn( cat_dteCreated );
        addColumn( cat_sName );
        addColumn( cat_sDescription );
        addColumn( usr_hModifier );
        addColumn( usr_sModifierUID );
        addColumn( usr_sModifierName );
        addColumn( cat_dteTimestamp );
    }

    public static CategoryView getInstance()
    {
        return Category;
    }

}
