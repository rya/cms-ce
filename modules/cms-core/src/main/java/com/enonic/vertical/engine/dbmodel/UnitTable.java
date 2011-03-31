/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class UnitTable
    extends Table
{
    private static final UnitTable Unit = new UnitTable( "tUnit", "unit", "units" );

    public Column uni_lKey = new Column( "uni_lKey", "@key", true, true, Constants.COLUMN_INTEGER, null, -1 );

    public ForeignKeyColumn uni_lan_lKey =
        new ForeignKeyColumn( "uni_lan_lKey", "@languagekey", false, false, Constants.COLUMN_INTEGER, null, "tLanguage", "lan_lKey", false,
                              -1 );

    public Column uni_sName = new Column( "uni_sName", "name", true, false, Constants.COLUMN_VARCHAR, null, 32 );

    public Column uni_sDescription = new Column( "uni_sDescription", "description", false, false, Constants.COLUMN_VARCHAR, null, 256 );

    public ForeignKeyColumn uni_lSuperKey =
        new ForeignKeyColumn( "uni_lSuperKey", "@superkey", false, false, Constants.COLUMN_INTEGER, null, "tUnit", "uni_lKey", false, -1 );

    public Column uni_bDeleted = new Column( "uni_bDeleted", "@deleted", true, false, Constants.COLUMN_BOOLEAN, Boolean.FALSE, -1 );

    public Column uni_dteTimestamp =
        new Column( "uni_dteTimestamp", "timestamp", true, false, Constants.COLUMN_CURRENT_TIMESTAMP, null, -1 );

    private UnitTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( uni_lKey );
        addColumn( uni_lan_lKey );
        addColumn( uni_sName );
        addColumn( uni_sDescription );
        addColumn( uni_lSuperKey );
        addColumn( uni_bDeleted );
        addColumn( uni_dteTimestamp );
    }

    public static UnitTable getInstance()
    {
        return Unit;
    }

}