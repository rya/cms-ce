/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class CategoryTable
    extends Table
{
    private static final CategoryTable Category = new CategoryTable( "tCategory", "category", "categories" );

    public Column cat_lKey = new Column( "cat_lKey", "@key", true, true, Constants.COLUMN_INTEGER, -1 );

    public ForeignKeyColumn cat_uni_lKey =
        new ForeignKeyColumn( "cat_uni_lKey", "@unitkey", false, false, Constants.COLUMN_INTEGER, null, "tUnit", "uni_lKey", false, -1 );

    public ForeignKeyColumn cat_cty_lKey =
        new ForeignKeyColumn( "cat_cty_lKey", "@contenttypekey", false, false, Constants.COLUMN_INTEGER, null, "tContentType", "cty_lKey",
                              false, -1 );

    public ForeignKeyColumn cat_cat_lSuper =
        new ForeignKeyColumn( "cat_cat_lSuper", "@supercategorykey", false, false, Constants.COLUMN_INTEGER, null, "tCategory", "cat_lKey",
                              false, -1 );

    public ForeignKeyColumn cat_usr_hOwner =
        new ForeignKeyColumn( "cat_usr_hOwner", "owner/@key", true, false, Constants.COLUMN_CHAR, null, "tUser", "usr_hKey", false, -1 );

    public Column cat_dteCreated = new Column( "cat_dteCreated", "@created", true, false, Constants.COLUMN_CREATED_TIMESTAMP, -1 );

    public Column cat_bDeleted = new Column( "cat_bDeleted", "@deleted", true, false, Constants.COLUMN_BOOLEAN, -1 );

    public Column cat_sName = new Column( "cat_sName", "@name", true, false, Constants.COLUMN_VARCHAR, 256 );

    public Column cat_sDescription = new Column( "cat_sDescription", "description", false, false, Constants.COLUMN_VARCHAR, 1024 );

    public ForeignKeyColumn cat_usr_hModifier =
        new ForeignKeyColumn( "cat_usr_hModifier", "modifier/@key", true, false, Constants.COLUMN_CHAR, null, "tUser", "usr_hKey", false,
                              -1 );

    public Column cat_dteTimestamp =
        new Column( "cat_dteTimestamp", "@timestamp", true, false, Constants.COLUMN_CURRENT_TIMESTAMP, -1 );

    public Column cat_bAutoApprove = new Column( "cat_bAutoApprove", "@autoApprove", true, false, Constants.COLUMN_BOOLEAN, -1 );

    private CategoryTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( cat_lKey );
        addColumn( cat_uni_lKey );
        addColumn( cat_cty_lKey );
        addColumn( cat_cat_lSuper );
        addColumn( cat_usr_hOwner );
        addColumn( cat_dteCreated );
        addColumn( cat_bDeleted );
        addColumn( cat_sName );
        addColumn( cat_sDescription );
        addColumn( cat_usr_hModifier );
        addColumn( cat_dteTimestamp );
        addColumn( cat_bAutoApprove );
    }

    public static CategoryTable getInstance()
    {
        return Category;
    }

}