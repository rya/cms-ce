/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class LogEntryTable
    extends Table
{
    public static final LogEntryTable INSTANCE = new LogEntryTable( "tLogEntry", "logentry", "logentries" );

    public Column len_sKey = new Column( "len_sKey", "@key", true, true, Constants.COLUMN_CHAR, 28 );

    public Column len_lTypeKey = new Column( "len_lTypeKey", "@typekey", true, false, Constants.COLUMN_INTEGER, -1 );

    public ForeignKeyColumn len_usr_hKey =
        new ForeignKeyColumn( "len_usr_hKey", "@userkey", true, false, Constants.COLUMN_CHAR, null, "tUser", "usr_hKey", false, -1 );

    public ForeignKeyColumn len_men_lKey =
        new ForeignKeyColumn( "len_men_lKey", "@menukey", false, false, Constants.COLUMN_INTEGER, null, "tMenu", "men_lKey", true, -1 );

    public Column len_lTableKey = new Column( "len_lTableKey", "@tablekey", false, false, Constants.COLUMN_INTEGER, -1 );

    public Column len_lKeyValue = new Column( "len_lKeyValue", "@tablekeyvalue", false, false, Constants.COLUMN_INTEGER, -1 );

    public Column len_lCount = new Column( "len_lCount", "@count", false, false, Constants.COLUMN_INTEGER, -1 );

    public Column len_sInetAddress = new Column( "len_sInetAddress", "@inetaddress", false, false, Constants.COLUMN_VARCHAR, 256 );

    public Column len_sPath = new Column( "len_sPath", "@path", false, false, Constants.COLUMN_VARCHAR, 256 );

    public Column len_sTitle = new Column( "len_sTitle", "title", true, false, Constants.COLUMN_VARCHAR, 256 );

    public Column len_xmlData = new Column( "len_xmlData", "data", true, false, Constants.COLUMN_XML, 1 );

    public Column len_dteTimestamp =
        new Column( "len_dteTimestamp", "@timestamp", true, false, Constants.COLUMN_CURRENT_TIMESTAMP, -1 );

    private LogEntryTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( len_sKey );
        addColumn( len_lTypeKey );
        addColumn( len_usr_hKey );
        addColumn( len_men_lKey );
        addColumn( len_lTableKey );
        addColumn( len_lKeyValue );
        addColumn( len_lCount );
        addColumn( len_sInetAddress );
        addColumn( len_sPath );
        addColumn( len_sTitle );
        addColumn( len_xmlData );
        addColumn( len_dteTimestamp );
    }
}