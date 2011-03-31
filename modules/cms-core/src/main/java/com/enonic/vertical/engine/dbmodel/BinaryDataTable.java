/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.Table;

public final class BinaryDataTable
    extends Table
{
    private static final BinaryDataTable BinaryData = new BinaryDataTable( "tBinaryData", "binarydata", "binarydatas" );

    public Column bda_lKey = new Column( "bda_lKey", "@key", true, true, Constants.COLUMN_INTEGER, null, -1 );

    public Column bda_sFileName = new Column( "bda_sFileName", "@filename", false, false, Constants.COLUMN_VARCHAR, null, 256 );

    public Column bda_lFileSize = new Column( "bda_lFileSize", "@filesize", true, false, Constants.COLUMN_INTEGER, null, -1 );

    public Column bda_dteTimestamp =
        new Column( "bda_dteTimestamp", "@timestamp", true, false, Constants.COLUMN_CURRENT_TIMESTAMP, null, -1 );

    public Column bda_sBlobKey = new Column( "bda_sBlobKey", "null", false, false, Constants.COLUMN_VARCHAR, null, 40 );

    private BinaryDataTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( bda_lKey );
        addColumn( bda_sFileName );
        addColumn( bda_lFileSize );
        addColumn( bda_dteTimestamp );
        addColumn( bda_sBlobKey );
    }

    public static BinaryDataTable getInstance()
    {
        return BinaryData;
    }

}