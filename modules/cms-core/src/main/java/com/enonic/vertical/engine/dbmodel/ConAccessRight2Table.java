/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class ConAccessRight2Table
    extends Table
{
    private static final ConAccessRight2Table ConAccessRight2 = new ConAccessRight2Table( "tConAccessRight2", "null", "null" );

    public Column coa_sKey = new Column( "coa_sKey", "null", true, true, Constants.COLUMN_CHAR, null, 32 );

    public ForeignKeyColumn coa_con_lKey =
        new ForeignKeyColumn( "coa_con_lKey", "null", true, false, Constants.COLUMN_INTEGER, null, "tContent", "con_lKey", false, -1 );

    public ForeignKeyColumn coa_grp_hKey =
        new ForeignKeyColumn( "coa_grp_hKey", "null", true, false, Constants.COLUMN_CHAR, null, "tGroup", "grp_hKey", false, -1 );

    public Column coa_bRead = new Column( "coa_bRead", "null", true, false, Constants.COLUMN_BOOLEAN, null, -1 );

    public Column coa_bUpdate = new Column( "coa_bUpdate", "null", true, false, Constants.COLUMN_BOOLEAN, null, -1 );

    public Column coa_bDelete = new Column( "coa_bDelete", "null", true, false, Constants.COLUMN_BOOLEAN, null, -1 );

    private ConAccessRight2Table( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( coa_sKey );
        addColumn( coa_con_lKey );
        addColumn( coa_grp_hKey );
        addColumn( coa_bRead );
        addColumn( coa_bUpdate );
        addColumn( coa_bDelete );
    }

    public static ConAccessRight2Table getInstance()
    {
        return ConAccessRight2;
    }

}