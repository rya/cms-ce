/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class ContentTable
    extends Table
{
    private static final ContentTable Content = new ContentTable( "tContent", "content", "contents" );

    public Column con_lKey = new Column( "con_lKey", "@key", true, true, Constants.COLUMN_INTEGER, null, -1 );

    public Column con_lSourceKey = new Column( "con_lSourceKey", "@sourcekey", false, false, Constants.COLUMN_INTEGER, null, -1 );

    public ForeignKeyColumn con_cat_lKey =
        new ForeignKeyColumn( "con_cat_lKey", "categoryname/@key", true, false, Constants.COLUMN_INTEGER, null, "tCategory", "cat_lKey",
                              false, -1 );

    public ForeignKeyColumn con_lan_lKey =
        new ForeignKeyColumn( "con_lan_lKey", "@languagekey", true, false, Constants.COLUMN_INTEGER, null, "tLanguage", "lan_lKey", false,
                              -1 );

    public Column con_dteCreated = new Column( "con_dteCreated", "@created", true, false, Constants.COLUMN_CREATED_TIMESTAMP, null, -1 );

    public Column con_bDeleted = new Column( "con_bDeleted", "@deleted", true, false, Constants.COLUMN_BOOLEAN, Boolean.FALSE, -1 );

    public Column con_dtePublishFrom =
        new Column( "con_dtePublishFrom", "@publishfrom", false, false, Constants.COLUMN_TIMESTAMP, null, -1 );

    public Column con_dtePublishTo = new Column( "con_dtePublishTo", "@publishto", false, false, Constants.COLUMN_TIMESTAMP, null, -1 );

    public Column con_lPriority = new Column( "con_lPriority", "@priority", true, false, Constants.COLUMN_INTEGER, null, -1 );

    public Column con_cov_lKey = new Column( "con_cov_lKey", "@versionkey", false, false, Constants.COLUMN_INTEGER, null, -1 );

    public ForeignKeyColumn con_usr_hOwner =
        new ForeignKeyColumn( "con_usr_hOwner", "owner/@key", true, false, Constants.COLUMN_CHAR, null, "tUser", "usr_hKey", false, -1 );

    public ForeignKeyColumn con_usr_hAssignee =
        new ForeignKeyColumn( "con_usr_hAssignee", "assignee/@key", false, false, Constants.COLUMN_CHAR, null, "tUser", "usr_hKey", false,
                              -1 );

    public ForeignKeyColumn con_usr_hAssigner =
        new ForeignKeyColumn( "con_usr_hAssigner", "assigner/@key", false, false, Constants.COLUMN_CHAR, null, "tUser", "usr_hKey", false,
                              -1 );

    public Column con_dteDueDate = new Column( "con_dteDueDate", "@duedate", false, false, Constants.COLUMN_TIMESTAMP, null, -1 );

    public Column con_sAssignmentDescription =
        new Column( "con_sAssignmentDescription", "@assignmentdescription", false, false, Constants.COLUMN_VARCHAR, null, 2048 );

    public Column con_dteTimestamp = new Column( "con_dteTimestamp", "@duedate", false, false, Constants.COLUMN_TIMESTAMP, null, -1 );

    public Column con_cov_lDraft = new Column( "con_cov_lDraft", "@draft", false, false, Constants.COLUMN_INTEGER, null, -1 );

    public Column con_sName = new Column( "con_sName", "null", false, false, Constants.COLUMN_VARCHAR, null, 256 );

    private ContentTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( con_lKey );
        addColumn( con_lSourceKey );
        addColumn( con_cat_lKey );
        addColumn( con_lan_lKey );
        addColumn( con_dteCreated );
        addColumn( con_bDeleted );
        addColumn( con_dtePublishFrom );
        addColumn( con_dtePublishTo );
        addColumn( con_lPriority );
        addColumn( con_cov_lKey );
        addColumn( con_usr_hOwner );
        addColumn( con_usr_hAssignee );
        addColumn( con_usr_hAssigner );
        addColumn( con_dteDueDate );
        addColumn( con_sAssignmentDescription );
        addColumn( con_dteTimestamp );
        addColumn( con_cov_lDraft );
        addColumn( con_sName );
    }

    public static ContentTable getInstance()
    {
        return Content;
    }

}