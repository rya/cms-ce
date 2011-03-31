/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.Table;

public final class BlobStoreTable
    extends Table
{
    private static final BlobStoreTable BlobStore = new BlobStoreTable( "tBlobStore", "null", "null" );

    public Column bst_key = new Column( "bst_key", "null", true, true, Constants.COLUMN_VARCHAR, null, 40 );

    public Column bst_size = new Column( "bst_size", "null", true, false, Constants.COLUMN_INTEGER, null, -1 );

    public Column bst_data = new Column( "bst_data", "null", true, false, Constants.COLUMN_BINARY, null, 1000 );

    private BlobStoreTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( bst_key );
        addColumn( bst_size );
        addColumn( bst_data );
    }

    public static BlobStoreTable getInstance()
    {
        return BlobStore;
    }

}