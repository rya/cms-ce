/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class DefaultMenuARTable
    extends Table
{
    private static final DefaultMenuARTable DefaultMenuAR = new DefaultMenuARTable( "tDefaultMenuAR", "null", "null" );

    public ForeignKeyColumn dma_men_lKey =
        new ForeignKeyColumn( "dma_men_lKey", "null", true, true, Constants.COLUMN_INTEGER, null, "tMenu", "men_lKey", false, -1 );

    public ForeignKeyColumn dma_grp_hKey =
        new ForeignKeyColumn( "dma_grp_hKey", "null", true, true, Constants.COLUMN_CHAR, null, "tGroup", "grp_hKey", false, -1 );

    public Column dma_bRead = new Column( "dma_bRead", "null", true, false, Constants.COLUMN_BOOLEAN, null, -1 );

    public Column dma_bCreate = new Column( "dma_bCreate", "null", true, false, Constants.COLUMN_BOOLEAN, null, -1 );

    public Column dma_bDelete = new Column( "dma_bDelete", "null", true, false, Constants.COLUMN_BOOLEAN, null, -1 );

    public Column dma_bPublish = new Column( "dma_bPublish", "null", true, false, Constants.COLUMN_BOOLEAN, null, -1 );

    public Column dma_bAdministrate = new Column( "dma_bAdministrate", "null", true, false, Constants.COLUMN_BOOLEAN, null, -1 );

    public Column dma_bUpdate = new Column( "dma_bUpdate", "null", true, false, Constants.COLUMN_BOOLEAN, null, -1 );

    public Column dma_bAdd = new Column( "dma_bAdd", "null", true, false, Constants.COLUMN_BOOLEAN, null, -1 );

    private DefaultMenuARTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( dma_men_lKey );
        addColumn( dma_grp_hKey );
        addColumn( dma_bRead );
        addColumn( dma_bCreate );
        addColumn( dma_bDelete );
        addColumn( dma_bPublish );
        addColumn( dma_bAdministrate );
        addColumn( dma_bUpdate );
        addColumn( dma_bAdd );
    }

    public static DefaultMenuARTable getInstance()
    {
        return DefaultMenuAR;
    }

}