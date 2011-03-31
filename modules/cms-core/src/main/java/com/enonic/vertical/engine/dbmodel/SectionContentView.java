/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.View;

public final class SectionContentView
    extends View
{
    public Column con_lKey = new Column( "con_lKey", "@key", Constants.COLUMN_INTEGER );

    public Column cat_lKey = new Column( "cat_lKey", "@categorykey", Constants.COLUMN_INTEGER );

    public Column cat_uni_lKey = new Column( "cat_uni_lKey", "@unitkey", Constants.COLUMN_INTEGER );

    public Column cat_cty_lKey = new Column( "cat_cty_lKey", "@contenttypekey", Constants.COLUMN_INTEGER );

    public Column cat_sName = new Column( "cat_sName", "@categoryname", Constants.COLUMN_VARCHAR );

    public Column con_lan_lKey = new Column( "con_lan_lKey", "@languagekey", Constants.COLUMN_INTEGER );

    public Column usr_hOwner = new Column( "usr_hOwner", "owner/@key", Constants.COLUMN_CHAR );

    public Column usr_sOwnerUID = new Column( "usr_sOwnerUID", "owner/@uid", Constants.COLUMN_VARCHAR );

    public Column usr_sOwnerName = new Column( "usr_sOwnerName", "owner", Constants.COLUMN_VARCHAR );

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

    public Column cov_dteTimestamp = new Column( "cov_dteTimestamp", "@timestamp", Constants.COLUMN_CURRENT_TIMESTAMP );

    public Column mei_lKey = new Column( "mei_lKey", "@sectionkey", Constants.COLUMN_INTEGER );

    public Column mei_men_lKey = new Column( "mei_men_lKey", "@menukey", Constants.COLUMN_INTEGER );

    public Column mei_sName = new Column( "mei_sName", "@sectionname", Constants.COLUMN_VARCHAR );

    public Column sco_lOrder = new Column( "sco_lOrder", "@order", Constants.COLUMN_INTEGER );

    public Column sco_bApproved = new Column( "sco_bApproved", "@approved", Constants.COLUMN_BOOLEAN );

    public Column sco_dteTimestamp = new Column( "sco_dteTimestamp", "@sectiontimestamp", Constants.COLUMN_CURRENT_TIMESTAMP );

    private final static String SQL =
        "select con_lKey, cat_lKey, cat_uni_lKey, cat_cty_lKey, " + "cat_sName, con_lan_lKey, usr_hOwner, usr_sOwnerUID, usr_sOwnerName, " +
            "con_dteCreated, cov_lStatus, cov_lState, con_dtePublishFrom, con_dtePublishTo, cov_sTitle, cov_xmlContentData, " +
            "con_lPriority, usr_hModifier, usr_sModifierUID, usr_sModifierName, cov_dteTimestamp, " +
            "mei_lKey, mei_men_lKey, mei_sName, sco_lOrder, sco_bApproved, sco_dteTimestamp from tSectionContent2 " + "join " +
            ContentView.getInstance().getReplacementSql() + " on sco_con_lKey = con_lKey " + "join tMenuItem on sco_mei_lKey = mei_lKey";

    private static final SectionContentView SectionContent = new SectionContentView( "vSectionContent" );

    private SectionContentView( String tableName )
    {
        super( tableName, "null", "null", SQL, 12 );
        addColumn( con_lKey );
        addColumn( cat_lKey );
        addColumn( cat_uni_lKey );
        addColumn( cat_cty_lKey );
        addColumn( cat_sName );
        addColumn( con_lan_lKey );
        addColumn( usr_hOwner );
        addColumn( usr_sOwnerUID );
        addColumn( usr_sOwnerName );
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
        addColumn( cov_dteTimestamp );
        addColumn( mei_lKey );
        addColumn( mei_men_lKey );
        addColumn( mei_sName );
        addColumn( sco_lOrder );
        addColumn( sco_bApproved );
        addColumn( sco_dteTimestamp );
    }

    public static SectionContentView getInstance()
    {
        return SectionContent;
    }
}