/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.Table;

public final class PreferencesTable
    extends Table
{
    private static final PreferencesTable Preferences = new PreferencesTable( "tPreferences", "null", "null" );

    public Column prf_sKey = new Column( "prf_sKey", "null", true, true, Constants.COLUMN_VARCHAR, null, 255 );

    public Column prf_sValue = new Column( "prf_sValue", "null", true, false, Constants.COLUMN_VARCHAR, null, 1024 );

    private PreferencesTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( prf_sKey );
        addColumn( prf_sValue );
    }

    public static PreferencesTable getInstance()
    {
        return Preferences;
    }

}