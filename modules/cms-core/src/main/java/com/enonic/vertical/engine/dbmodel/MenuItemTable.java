/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class MenuItemTable
    extends Table
{
    private static final MenuItemTable MenuItem = new MenuItemTable( "tMenuItem", "null", "null" );

    public Column mei_lKey = new Column( "mei_lKey", "null", true, true, Constants.COLUMN_INTEGER, -1 );

    public Column mei_sName = new Column( "mei_sName", "null", true, false, Constants.COLUMN_VARCHAR, 256 );

    public ForeignKeyColumn mei_men_lKey =
        new ForeignKeyColumn( "mei_men_lKey", "null", true, false, Constants.COLUMN_INTEGER, null, "tMenu", "men_lKey", false, -1 );

    public Column mei_mid_lkey = new Column( "mei_mid_lkey", "null", true, false, Constants.COLUMN_INTEGER, -1 );

    public ForeignKeyColumn mei_lParent =
        new ForeignKeyColumn( "mei_lParent", "null", false, false, Constants.COLUMN_INTEGER, null, "tMenuItem", "mei_lKey", false, -1 );

    public Column mei_lOrder = new Column( "mei_lOrder", "null", true, false, Constants.COLUMN_INTEGER, -1 );

    public ForeignKeyColumn mei_pag_lKey =
        new ForeignKeyColumn( "mei_pag_lKey", "null", false, false, Constants.COLUMN_INTEGER, null, "tPage", "pag_lKey", false, -1 );

    public Column mei_dteTimestamp = new Column( "mei_dteTimestamp", "null", true, false, Constants.COLUMN_CURRENT_TIMESTAMP, -1 );

    public Column mei_sSubTitle = new Column( "mei_sSubTitle", "null", false, false, Constants.COLUMN_VARCHAR, 256 );

    public Column mei_bHidden = new Column( "mei_bHidden", "null", false, false, Constants.COLUMN_BOOLEAN, -1 );

    public Column mei_sDescription = new Column( "mei_sDescription", "null", false, false, Constants.COLUMN_VARCHAR, 1024 );

    public Column mei_bNoAuth = new Column( "mei_bNoAuth", "null", false, false, Constants.COLUMN_BOOLEAN, -1 );

    public ForeignKeyColumn mei_usr_hOwner =
        new ForeignKeyColumn( "mei_usr_hOwner", "null", false, false, Constants.COLUMN_CHAR, null, "tUser", "usr_hKey", false, -1 );

    public ForeignKeyColumn mei_usr_hModifier =
        new ForeignKeyColumn( "mei_usr_hModifier", "null", false, false, Constants.COLUMN_CHAR, null, "tUser", "usr_hKey", false, -1 );

    public Column mei_xmlData = new Column( "mei_xmlData", "null", false, false, Constants.COLUMN_XML, 1 );

    public Column mei_sKeywords = new Column( "mei_sKeywords", "null", false, false, Constants.COLUMN_VARCHAR, 1024 );

    public ForeignKeyColumn mei_lan_lKey =
        new ForeignKeyColumn( "mei_lan_lKey", "null", false, false, Constants.COLUMN_INTEGER, null, "tLanguage", "lan_lKey", false, -1 );

    public Column mei_sURL = new Column( "mei_sURL", "null", false, false, Constants.COLUMN_VARCHAR, 256 );

    public Column mei_bURLOpenNewWin = new Column( "mei_bURLOpenNewWin", "null", false, false, Constants.COLUMN_BOOLEAN, -1 );

    public ForeignKeyColumn mei_mei_lShortcut =
        new ForeignKeyColumn( "mei_mei_lShortcut", "null", false, false, Constants.COLUMN_INTEGER, null, "tMenuItem", "mei_lKey", true,
                              -1 );

    public Column mei_bShortcutForward = new Column( "mei_bShortcutForward", "null", false, false, Constants.COLUMN_BOOLEAN, -1 );

    public Column mei_bSection = new Column( "mei_bSection", "null", false, false, Constants.COLUMN_BOOLEAN, -1 );

    public Column mei_bOrderedSection = new Column( "mei_bOrderedSection", "null", false, false, Constants.COLUMN_BOOLEAN, -1 );

    public Column mei_lRunAs = new Column( "mei_lRunAs", "null", false, false, Constants.COLUMN_INTEGER, -1 );

    public Column mei_sDisplayName = new Column( "mei_sDisplayName", "null", false, false, Constants.COLUMN_VARCHAR, 256 );

    private MenuItemTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( mei_lKey );
        addColumn( mei_sName );
        addColumn( mei_men_lKey );
        addColumn( mei_mid_lkey );
        addColumn( mei_lParent );
        addColumn( mei_lOrder );
        addColumn( mei_pag_lKey );
        addColumn( mei_dteTimestamp );
        addColumn( mei_sSubTitle );
        addColumn( mei_bHidden );
        addColumn( mei_sDescription );
        addColumn( mei_bNoAuth );
        addColumn( mei_usr_hOwner );
        addColumn( mei_usr_hModifier );
        addColumn( mei_xmlData );
        addColumn( mei_sKeywords );
        addColumn( mei_lan_lKey );
        addColumn( mei_sURL );
        addColumn( mei_bURLOpenNewWin );
        addColumn( mei_mei_lShortcut );
        addColumn( mei_bShortcutForward );
        addColumn( mei_bSection );
        addColumn( mei_bOrderedSection );
        addColumn( mei_lRunAs );
        addColumn( mei_sDisplayName );
    }

    public static MenuItemTable getInstance()
    {
        return MenuItem;
    }

}