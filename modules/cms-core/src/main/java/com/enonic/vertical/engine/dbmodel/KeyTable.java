/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.Table;

public final class KeyTable
    extends Table
{
    private static final KeyTable Key = new KeyTable( "tKey", "null", "null" );

    public Column key_sTableName = new Column( "key_sTableName", "null", true, true, Constants.COLUMN_VARCHAR, null, 18 );

    public Column key_lLastKey = new Column( "key_lLastKey", "null", true, false, Constants.COLUMN_INTEGER, null, -1 );

    private KeyTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( key_sTableName );
        addColumn( key_lLastKey );
    }

    public static KeyTable getInstance()
    {
        return Key;
    }

}