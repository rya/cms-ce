/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class ContentTypeTable
    extends Table
{
    private static final ContentTypeTable ContentType = new ContentTypeTable( "tContentType", "contenttype", "contenttypes" );

    public Column cty_lKey = new Column( "cty_lKey", "@key", true, true, Constants.COLUMN_INTEGER, null, -1 );

    public Column cty_sName = new Column( "cty_sName", "name", true, false, Constants.COLUMN_VARCHAR, null, 32 );

    public Column cty_sDescription = new Column( "cty_sDescription", "description", false, false, Constants.COLUMN_VARCHAR, null, 256 );

    public Column cty_mbData = new Column( "cty_mbData", "moduledata", true, false, Constants.COLUMN_XML, null, 1 );

    public Column cty_dteTimestamp =
        new Column( "cty_dteTimestamp", "timestamp", true, false, Constants.COLUMN_CURRENT_TIMESTAMP, null, -1 );

    public ForeignKeyColumn cty_han_lKey =
        new ForeignKeyColumn( "cty_han_lKey", "@contenthandlerkey", true, false, Constants.COLUMN_INTEGER, null, "tContentHandler",
                              "han_lKey", false, -1 );

    public Column cty_bLocal = new Column( "cty_bLocal", "@local", true, false, Constants.COLUMN_BOOLEAN, Boolean.FALSE, -1 );

    public Column cty_sCSS = new Column( "cty_sCSS", "@csskey", false, false, Constants.COLUMN_VARCHAR, null, 1024 );

    private ContentTypeTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( cty_lKey );
        addColumn( cty_sName );
        addColumn( cty_sDescription );
        addColumn( cty_mbData );
        addColumn( cty_dteTimestamp );
        addColumn( cty_han_lKey );
        addColumn( cty_bLocal );
        addColumn( cty_sCSS );
    }

    public static ContentTypeTable getInstance()
    {
        return ContentType;
    }

}