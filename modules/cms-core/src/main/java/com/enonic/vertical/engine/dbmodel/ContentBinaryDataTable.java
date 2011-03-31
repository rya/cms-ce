/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class ContentBinaryDataTable
    extends Table
{
    private static final ContentBinaryDataTable ContentBinaryData = new ContentBinaryDataTable( "tContentBinaryData", "null", "null" );

    public Column cbd_lKey = new Column( "cbd_lKey", "null", true, true, Constants.COLUMN_INTEGER, null, -1 );

    public ForeignKeyColumn cbd_cov_lKey =
        new ForeignKeyColumn( "cbd_cov_lKey", "null", true, false, Constants.COLUMN_INTEGER, null, "tContentVersion", "cov_lKey", false,
                              -1 );

    public ForeignKeyColumn cbd_bda_lKey =
        new ForeignKeyColumn( "cbd_bda_lKey", "null", true, false, Constants.COLUMN_INTEGER, null, "tBinaryData", "bda_lKey", true, -1 );

    public Column cbd_sLabel = new Column( "cbd_sLabel", "null", false, false, Constants.COLUMN_VARCHAR, null, 32 );

    private ContentBinaryDataTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( cbd_lKey );
        addColumn( cbd_cov_lKey );
        addColumn( cbd_bda_lKey );
        addColumn( cbd_sLabel );
    }

    public static ContentBinaryDataTable getInstance()
    {
        return ContentBinaryData;
    }

}