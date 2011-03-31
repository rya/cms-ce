/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class MenuItemContentTable
    extends Table
{
    private static final MenuItemContentTable MenuItemContent = new MenuItemContentTable( "tMenuItemContent", "null", "null" );

    public ForeignKeyColumn mic_mei_lKey =
        new ForeignKeyColumn( "mic_mei_lKey", "null", true, true, Constants.COLUMN_INTEGER, null, "tMenuItem", "mei_lKey", true, -1 );

    public ForeignKeyColumn mic_con_lKey =
        new ForeignKeyColumn( "mic_con_lKey", "null", true, true, Constants.COLUMN_INTEGER, null, "tContent", "con_lKey", false, -1 );

    private MenuItemContentTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( mic_mei_lKey );
        addColumn( mic_con_lKey );
    }

    public static MenuItemContentTable getInstance()
    {
        return MenuItemContent;
    }

}