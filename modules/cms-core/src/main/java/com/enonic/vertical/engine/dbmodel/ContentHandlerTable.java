/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.Table;

public final class ContentHandlerTable
    extends Table
{
    private static final ContentHandlerTable ContentHandler =
        new ContentHandlerTable( "tContentHandler", "contenthandler", "contenthandlers" );

    public Column han_lKey = new Column( "han_lKey", "@key", true, true, Constants.COLUMN_INTEGER, -1 );

    public Column han_sName = new Column( "han_sName", "name", true, false, Constants.COLUMN_VARCHAR, 32 );

    public Column han_sClass = new Column( "han_sClass", "class", true, false, Constants.COLUMN_VARCHAR, 256 );

    public Column han_sDescription = new Column( "han_sDescription", "description", false, false, Constants.COLUMN_VARCHAR, 256 );

    public Column han_XMLConfig = new Column( "han_XMLConfig", "xmlconfig", false, false, Constants.COLUMN_XML, 1 );

    public Column han_dteTimestamp =
        new Column( "han_dteTimestamp", "timestamp", true, false, Constants.COLUMN_CURRENT_TIMESTAMP, -1 );

    private ContentHandlerTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( han_lKey );
        addColumn( han_sName );
        addColumn( han_sClass );
        addColumn( han_sDescription );
        addColumn( han_XMLConfig );
        addColumn( han_dteTimestamp );
    }

    public static ContentHandlerTable getInstance()
    {
        return ContentHandler;
    }

}