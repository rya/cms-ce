/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class SecConTypeFilter2Table
    extends Table
{
    private static final SecConTypeFilter2Table SecConTypeFilter2 = new SecConTypeFilter2Table( "tSecConTypeFilter2", "null", "null" );

    public Column sctf_lkey = new Column( "sctf_lkey", "null", true, true, Constants.COLUMN_INTEGER, null, -1 );

    public ForeignKeyColumn sctf_cty_lKey =
        new ForeignKeyColumn( "sctf_cty_lKey", "null", true, false, Constants.COLUMN_INTEGER, null, "tContentType", "cty_lKey", true, -1 );

    public ForeignKeyColumn sctf_mei_lKey =
        new ForeignKeyColumn( "sctf_mei_lKey", "null", true, false, Constants.COLUMN_INTEGER, null, "tMenuItem", "mei_lKey", true, -1 );

    private SecConTypeFilter2Table( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( sctf_lkey );
        addColumn( sctf_cty_lKey );
        addColumn( sctf_mei_lKey );
    }

    public static SecConTypeFilter2Table getInstance()
    {
        return SecConTypeFilter2;
    }

}