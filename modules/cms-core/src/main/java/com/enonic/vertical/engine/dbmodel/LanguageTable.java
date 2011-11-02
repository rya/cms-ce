/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.Table;

public final class LanguageTable
    extends Table
{
    private static final LanguageTable Language = new LanguageTable( "tLanguage", "language", "languages" );

    public Column lan_lKey = new Column( "lan_lKey", "@key", true, true, Constants.COLUMN_INTEGER, -1 );

    public Column lan_sCode = new Column( "lan_sCode", "@languagecode", true, false, Constants.COLUMN_VARCHAR, 32 );

    public Column lan_sDescription = new Column( "lan_sDescription", ".", false, false, Constants.COLUMN_VARCHAR, 256 );

    public Column lan_dteTimestamp =
        new Column( "lan_dteTimestamp", "timestamp", true, false, Constants.COLUMN_CURRENT_TIMESTAMP, -1 );

    private LanguageTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( lan_lKey );
        addColumn( lan_sCode );
        addColumn( lan_sDescription );
        addColumn( lan_dteTimestamp );
    }

    public static LanguageTable getInstance()
    {
        return Language;
    }

}