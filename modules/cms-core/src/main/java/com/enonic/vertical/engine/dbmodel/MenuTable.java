/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class MenuTable
    extends Table
{
    public static final MenuTable INSTANCE = new MenuTable( "tMenu", "menu", "menus" );

    public Column men_lKey = new Column( "men_lKey", "@key", true, true, Constants.COLUMN_INTEGER, -1 );

    public Column men_dteTimestamp =
        new Column( "men_dteTimestamp", "timestamp", true, false, Constants.COLUMN_CURRENT_TIMESTAMP, -1 );

    public Column men_mei_firstPage = new Column( "men_mei_firstPage", "firstpage", false, false, Constants.COLUMN_INTEGER, -1 );

    public Column men_mei_loginPage = new Column( "men_mei_loginPage", "loginpage", false, false, Constants.COLUMN_INTEGER, -1 );

    public Column men_mei_errorPage = new Column( "men_mei_errorPage", "errorpage", false, false, Constants.COLUMN_INTEGER, -1 );

    public ForeignKeyColumn men_pat_lKey =
        new ForeignKeyColumn( "men_pat_lKey", "pagetemplate", false, false, Constants.COLUMN_INTEGER, null, "tPageTemplate", "pat_lKey",
                              false, -1 );

    public Column men_sName = new Column( "men_sName", "name", false, false, Constants.COLUMN_VARCHAR, 64 );

    public Column men_xmlData = new Column( "men_xmlData", "xmldata", false, false, Constants.COLUMN_XML, 1 );

    public ForeignKeyColumn men_lan_lKey =
        new ForeignKeyColumn( "men_lan_lKey", "@language", true, false, Constants.COLUMN_INTEGER, null, "tLanguage", "lan_lKey", false,
                              -1 );

    public Column men_sStatisticsURL = new Column( "men_sStatisticsURL", "statistics", false, false, Constants.COLUMN_VARCHAR, 256 );

    public ForeignKeyColumn men_usr_hRunAs =
        new ForeignKeyColumn( "men_usr_hRunAs", "null", false, false, Constants.COLUMN_CHAR, null, "tUser", "usr_hKey", false, -1 );

    private MenuTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( men_lKey );
        addColumn( men_dteTimestamp );
        addColumn( men_mei_firstPage );
        addColumn( men_mei_loginPage );
        addColumn( men_mei_errorPage );
        addColumn( men_pat_lKey );
        addColumn( men_sName );
        addColumn( men_xmlData );
        addColumn( men_lan_lKey );
        addColumn( men_sStatisticsURL );
        addColumn( men_usr_hRunAs );
    }
}