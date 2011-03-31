/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.View;

public final class ContentMinimalView
    extends View
{
    public Column con_lKey = new Column( "con_lKey", "@key", Constants.COLUMN_INTEGER );

    public Column cov_lKey = new Column( "cov_lKey", "@versionkey", Constants.COLUMN_INTEGER );

    public Column cat_lKey = new Column( "cat_lKey", "categoryname/@key", Constants.COLUMN_INTEGER );

    public Column cat_uni_lKey = new Column( "cat_uni_lKey", "@unitkey", Constants.COLUMN_INTEGER );

    public Column cat_cty_lKey = new Column( "cat_cty_lKey", "@contenttypekey", Constants.COLUMN_INTEGER );

    public Column cat_sName = new Column( "cat_sName", "categoryname", Constants.COLUMN_VARCHAR );

    public Column con_lan_lKey = new Column( "con_lan_lKey", "@languagekey", Constants.COLUMN_INTEGER );

    public Column con_usr_hOwner = new Column( "con_usr_hOwner", "owner/@key", Constants.COLUMN_CHAR );

    public Column con_dteCreated = new Column( "con_dteCreated", "@created", Constants.COLUMN_CREATED_TIMESTAMP );

    public Column cov_lStatus = new Column( "cov_lStatus", "@status", Constants.COLUMN_INTEGER );

    public Column cov_lState = new Column( "cov_lState", "@state", Constants.COLUMN_INTEGER );

    public Column con_dtePublishFrom = new Column( "con_dtePublishFrom", "@publishfrom", Constants.COLUMN_TIMESTAMP );

    public Column con_dtePublishTo = new Column( "con_dtePublishTo", "@publishto", Constants.COLUMN_TIMESTAMP );

    public Column cov_sTitle = new Column( "cov_sTitle", "title", Constants.COLUMN_VARCHAR );

    public Column cov_xmlContentData = new Column( "cov_xmlContentData", ".", Constants.COLUMN_XML );

    public Column con_lPriority = new Column( "con_lPriority", "@priority", Constants.COLUMN_INTEGER );

    public Column cov_usr_hModifier = new Column( "cov_usr_hModifier", "modifier/@key", Constants.COLUMN_CHAR );

    public Column cov_dteTimestamp = new Column( "cov_dteTimestamp", "@timestamp", Constants.COLUMN_CURRENT_TIMESTAMP );

    private final static String SQL = "select con_lKey, cov_lKey, cat_lKey, cat_uni_lKey, cat_cty_lKey, " +
        "cat_sName, con_lan_lKey, con_usr_hOwner, con_dteCreated, cov_lStatus, " + ContentView.STATE_SQL_CASE + " as cov_lState, " +
        "con_dtePublishFrom, con_dtePublishTo, cov_sTitle, cov_xmlContentData, con_lPriority, cov_usr_hModifier, cov_dteTimestamp " +
        "from tContent left join tContentVersion on con_cov_lKey = cov_lKey " +
        "left join tCategory on con_cat_lKey = cat_lKey where con_bDeleted = 0";

    private static final ContentMinimalView ContentMinimal = new ContentMinimalView( "vContentMinimal" );

    private ContentMinimalView( String tableName )
    {
        super( tableName, "content", "contents", SQL, 4 );
        addColumn( con_lKey );
        addColumn( cov_lKey );
        addColumn( cat_lKey );
        addColumn( cat_uni_lKey );
        addColumn( cat_cty_lKey );
        addColumn( cat_sName );
        addColumn( con_lan_lKey );
        addColumn( con_usr_hOwner );
        addColumn( con_dteCreated );
        addColumn( cov_lStatus );
        addColumn( cov_lState );
        addColumn( con_dtePublishFrom );
        addColumn( con_dtePublishTo );
        addColumn( cov_sTitle );
        addColumn( cov_xmlContentData );
        addColumn( con_lPriority );
        addColumn( cov_usr_hModifier );
        addColumn( cov_dteTimestamp );
    }

    public static ContentMinimalView getInstance()
    {
        return ContentMinimal;
    }

}