/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class ContentObjectTable
    extends Table
{
    private static final ContentObjectTable ContentObject = new ContentObjectTable( "tContentObject", "null", "null" );

    public Column cob_lKey = new Column( "cob_lKey", "null", true, true, Constants.COLUMN_INTEGER, null, -1 );

    public ForeignKeyColumn cob_men_lKey =
        new ForeignKeyColumn( "cob_men_lKey", "null", false, false, Constants.COLUMN_INTEGER, null, "tMenu", "men_lKey", false, -1 );

    public Column cob_sName = new Column( "cob_sName", "null", true, false, Constants.COLUMN_VARCHAR, null, 256 );

    public Column cob_xmlData = new Column( "cob_xmlData", "null", false, false, Constants.COLUMN_XML, null, 10 );

    public Column cob_dteTimestamp = new Column( "cob_dteTimestamp", "null", true, false, Constants.COLUMN_CURRENT_TIMESTAMP, null, -1 );

    public Column cob_sStyle = new Column( "cob_sStyle", "null", true, false, Constants.COLUMN_VARCHAR, null, 1024 );

    public Column cob_sBorder = new Column( "cob_sBorder", "null", false, false, Constants.COLUMN_VARCHAR, null, 1024 );

    public Column cob_lRunAs = new Column( "cob_lRunAs", "null", false, false, Constants.COLUMN_INTEGER, null, -1 );

    private ContentObjectTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( cob_lKey );
        addColumn( cob_men_lKey );
        addColumn( cob_sName );
        addColumn( cob_xmlData );
        addColumn( cob_dteTimestamp );
        addColumn( cob_sStyle );
        addColumn( cob_sBorder );
        addColumn( cob_lRunAs );
    }

    public static ContentObjectTable getInstance()
    {
        return ContentObject;
    }

}