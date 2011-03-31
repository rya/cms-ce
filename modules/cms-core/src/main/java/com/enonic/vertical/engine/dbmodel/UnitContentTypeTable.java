/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class UnitContentTypeTable
    extends Table
{
    private static final UnitContentTypeTable UnitContentType = new UnitContentTypeTable( "tUnitContentType", "null", "null" );

    public ForeignKeyColumn uct_uni_lKey =
        new ForeignKeyColumn( "uct_uni_lKey", "null", true, true, Constants.COLUMN_INTEGER, null, "tUnit", "uni_lKey", true, -1 );

    public ForeignKeyColumn uct_cty_lKey =
        new ForeignKeyColumn( "uct_cty_lKey", "null", true, true, Constants.COLUMN_INTEGER, null, "tContentType", "cty_lKey", true, -1 );

    private UnitContentTypeTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( uct_uni_lKey );
        addColumn( uct_cty_lKey );
    }

    public static UnitContentTypeTable getInstance()
    {
        return UnitContentType;
    }

}