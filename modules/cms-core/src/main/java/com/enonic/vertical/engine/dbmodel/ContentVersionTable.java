/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class ContentVersionTable
    extends Table
{
    private static final ContentVersionTable ContentVersion = new ContentVersionTable( "tContentVersion", "content", "contents" );

    public Column cov_lKey = new Column( "cov_lKey", "@versionkey", true, true, Constants.COLUMN_INTEGER, null, -1 );

    public ForeignKeyColumn cov_con_lKey =
        new ForeignKeyColumn( "cov_con_lKey", "@key", true, false, Constants.COLUMN_INTEGER, null, "tContent", "con_lKey", false, -1 );

    public Column cov_lStatus = new Column( "cov_lStatus", "@status", true, false, Constants.COLUMN_INTEGER, null, -1 );

    public Column cov_sTitle = new Column( "cov_sTitle", "title", true, false, Constants.COLUMN_VARCHAR, null, 256 );

    public Column cov_sDescription = new Column( "cov_sDescription", "description", false, false, Constants.COLUMN_VARCHAR, null, 1024 );

    public Column cov_xmlContentData = new Column( "cov_xmlContentData", "contentdata", true, false, Constants.COLUMN_XML, null, 10 );

    public ForeignKeyColumn cov_usr_hModifier =
        new ForeignKeyColumn( "cov_usr_hModifier", "modifier/@key", true, false, Constants.COLUMN_CHAR, null, "tUser", "usr_hKey", false,
                              -1 );

    public ForeignKeyColumn cov_cov_lSnapshotSource =
        new ForeignKeyColumn( "cov_cov_lSnapshotSource", "@snapshotsource", false, false, Constants.COLUMN_INTEGER, null, "tContentVersion",
                              "cov_lKey", false, -1 );

    public Column cov_dteCreated =
        new Column( "cov_dteCreated", "@versioncreated", true, false, Constants.COLUMN_CREATED_TIMESTAMP, null, -1 );

    public Column cov_dteTimestamp =
        new Column( "cov_dteTimestamp", "@timestamp", true, false, Constants.COLUMN_CURRENT_TIMESTAMP, null, -1 );

    private ContentVersionTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( cov_lKey );
        addColumn( cov_con_lKey );
        addColumn( cov_lStatus );
        addColumn( cov_sTitle );
        addColumn( cov_sDescription );
        addColumn( cov_xmlContentData );
        addColumn( cov_usr_hModifier );
        addColumn( cov_cov_lSnapshotSource );
        addColumn( cov_dteCreated );
        addColumn( cov_dteTimestamp );
    }

    public static ContentVersionTable getInstance()
    {
        return ContentVersion;
    }

}