/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.View;

public final class ContentPublishedView
    extends View
{
    public Column con_lKey = new Column( "con_lKey", "@key", Constants.COLUMN_INTEGER );

    public Column cov_lKey = new Column( "cov_lKey", "@versionkey", Constants.COLUMN_INTEGER );

    public Column cat_lKey = new Column( "cat_lKey", "categoryname/@key", Constants.COLUMN_INTEGER );

    public Column cat_uni_lKey = new Column( "cat_uni_lKey", "@unitkey", Constants.COLUMN_INTEGER );

    public Column cat_cty_lKey = new Column( "cat_cty_lKey", "@contenttypekey", Constants.COLUMN_INTEGER );

    public Column cat_sName = new Column( "cat_sName", "categoryname", Constants.COLUMN_VARCHAR );

    public Column con_lan_lKey = new Column( "con_lan_lKey", "@languagekey", Constants.COLUMN_INTEGER );

    public Column usr_hOwner = new Column( "usr_hOwner", "owner/@key", Constants.COLUMN_CHAR );

    public Column usr_sOwnerUID = new Column( "usr_sOwnerUID", "owner/@uid", Constants.COLUMN_VARCHAR );

    public Column usr_sOwnerName = new Column( "usr_sOwnerName", "owner", Constants.COLUMN_VARCHAR );

    public Column usr_bOwnerDeleted = new Column( "usr_bOwnerDeleted", "owner/@deleted", Constants.COLUMN_BOOLEAN );

    public Column con_dteCreated = new Column( "con_dteCreated", "@created", Constants.COLUMN_CREATED_TIMESTAMP );

    public Column cov_lStatus = new Column( "cov_lStatus", "@status", Constants.COLUMN_INTEGER );

    public Column cov_lState = new Column( "cov_lState", "@state", Constants.COLUMN_INTEGER );

    public Column con_dtePublishFrom = new Column( "con_dtePublishFrom", "@publishfrom", Constants.COLUMN_TIMESTAMP );

    public Column con_dtePublishTo = new Column( "con_dtePublishTo", "@publishto", Constants.COLUMN_TIMESTAMP );

    public Column cov_sTitle = new Column( "cov_sTitle", "title", Constants.COLUMN_VARCHAR );

    public Column cov_xmlContentData = new Column( "cov_xmlContentData", ".", Constants.COLUMN_XML );

    public Column con_lPriority = new Column( "con_lPriority", "@priority", Constants.COLUMN_INTEGER );

    public Column usr_hModifier = new Column( "usr_hModifier", "modifier/@key", Constants.COLUMN_CHAR );

    public Column usr_sModifierUID = new Column( "usr_sModifierUID", "modifier/@uid", Constants.COLUMN_VARCHAR );

    public Column usr_sModifierName = new Column( "usr_sModifierName", "modifier", Constants.COLUMN_VARCHAR );

    public Column usr_bModDeleted = new Column( "usr_bModDeleted", "modifier/@deleted", Constants.COLUMN_BOOLEAN );

    public Column cov_dteTimestamp = new Column( "cov_dteTimestamp", "@timestamp", Constants.COLUMN_CURRENT_TIMESTAMP );

    private final static String SQL = "select con_lKey, cov_lKey, cat_lKey, cat_uni_lKey, cat_cty_lKey, " +
        "cat_sName, con_lan_lKey, con_usr_hOwner as usr_hOwner, o.usr_sUID as usr_sOwnerUID, " +
        "o.usr_sFullName as usr_sOwnerName, o.usr_bIsDeleted as usr_bOwnerDeleted, " +
        "con_dteCreated, 2 as cov_lStatus, 5 as cov_lState, " +
        "con_dtePublishFrom, con_dtePublishTo, cov_sTitle, cov_xmlContentData, con_lPriority, cov_usr_hModifier as usr_hModifier, " +
        "m.usr_sUID as usr_sModifierUID, m.usr_sFullName as usr_sModifierName, m.usr_bIsDeleted as usr_bModDeleted, cov_dteTimestamp " +
        "from tContent left join tContentVersion on con_cov_lKey = cov_lKey " +
        "left join tCategory on con_cat_lKey = cat_lKey left join tUser o on con_usr_hOwner = o.usr_hKey " +
        "left join tUser m on cov_usr_hModifier = m.usr_hKey where con_bDeleted = 0 and cat_bDeleted = 0 " + "and cov_lStatus = 2 " +
        "and con_dtePublishFrom <= @currentTimestamp@ and ( con_dtePublishTo is null OR con_dtePublishTo > @currentTimestamp@)";

    private static final ContentPublishedView ContentPublished = new ContentPublishedView( "vContentPublished" );

    private ContentPublishedView( String tableName )
    {
        super( tableName, "content", "contents", SQL, 7 );
        addColumn( con_lKey );
        addColumn( cov_lKey );
        addColumn( cat_lKey );
        addColumn( cat_uni_lKey );
        addColumn( cat_cty_lKey );
        addColumn( cat_sName );
        addColumn( con_lan_lKey );
        addColumn( usr_hOwner );
        addColumn( usr_sOwnerUID );
        addColumn( usr_sOwnerName );
        addColumn( usr_bOwnerDeleted );
        addColumn( con_dteCreated );
        addColumn( cov_lStatus );
        addColumn( cov_lState );
        addColumn( con_dtePublishFrom );
        addColumn( con_dtePublishTo );
        addColumn( cov_sTitle );
        addColumn( cov_xmlContentData );
        addColumn( con_lPriority );
        addColumn( usr_hModifier );
        addColumn( usr_sModifierUID );
        addColumn( usr_sModifierName );
        addColumn( usr_bModDeleted );
        addColumn( cov_dteTimestamp );
    }

    public static ContentPublishedView getInstance()
    {
        return ContentPublished;
    }

}