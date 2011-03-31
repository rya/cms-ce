/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.View;

public final class MenuARView
    extends View
{
    public Column dma_men_lKey = new Column( "dma_men_lKey", "null", Constants.COLUMN_INTEGER );

    public Column grp_hKey = new Column( "grp_hKey", "null", Constants.COLUMN_CHAR );

    public Column grp_sName = new Column( "grp_sName", "null", Constants.COLUMN_VARCHAR );

    public Column grp_lType = new Column( "grp_lType", "null", Constants.COLUMN_INTEGER );

    public Column usr_hKey = new Column( "usr_hKey", "null", Constants.COLUMN_CHAR );

    public Column usr_sUID = new Column( "usr_sUID", "null", Constants.COLUMN_VARCHAR );

    public Column usr_sFullName = new Column( "usr_sFullName", "null", Constants.COLUMN_VARCHAR );

    public Column dma_bRead = new Column( "dma_bRead", "null", Constants.COLUMN_BOOLEAN );

    public Column dma_bCreate = new Column( "dma_bCreate", "null", Constants.COLUMN_BOOLEAN );

    public Column dma_bPublish = new Column( "dma_bPublish", "null", Constants.COLUMN_BOOLEAN );

    public Column dma_bAdministrate = new Column( "dma_bAdministrate", "null", Constants.COLUMN_BOOLEAN );

    public Column dma_bUpdate = new Column( "dma_bUpdate", "null", Constants.COLUMN_BOOLEAN );

    public Column dma_bDelete = new Column( "dma_bDelete", "null", Constants.COLUMN_BOOLEAN );

    public Column dma_bAdd = new Column( "dma_bAdd", "null", Constants.COLUMN_BOOLEAN );

    private final static String SQL =
        "select DMA_MEN_LKEY, DMA_grp_hKey as grp_hKey, GRP_SNAME, GRP_LTYPE, usr_hKey as usr_hKey, USR_SUID, " +
            "USR_SFULLNAME, DMA_BREAD, " + "DMA_BCREATE, DMA_BPUBLISH, DMA_BADMINISTRATE, DMA_BUPDATE, DMA_BDELETE, DMA_BADD from " +
            "TDEFAULTMENUAR join TGROUP on DMA_grp_hKey = grp_hKey join TMENU on DMA_MEN_LKEY = MEN_LKEY " +
            "left join TUSER on usr_grp_hKey = grp_hKey " + "where GRP_BISDELETED = 0 and (USR_BISDELETED = 0 or USR_BISDELETED IS NULL)";

    private static final MenuARView MenuAR = new MenuARView( "vMenuAR" );

    private MenuARView( String tableName )
    {
        super( tableName, "null", "null", SQL, 10 );
        addColumn( dma_men_lKey );
        addColumn( grp_hKey );
        addColumn( grp_sName );
        addColumn( grp_lType );
        addColumn( usr_hKey );
        addColumn( usr_sUID );
        addColumn( usr_sFullName );
        addColumn( dma_bRead );
        addColumn( dma_bCreate );
        addColumn( dma_bPublish );
        addColumn( dma_bAdministrate );
        addColumn( dma_bUpdate );
        addColumn( dma_bDelete );
        addColumn( dma_bAdd );
    }

    public static MenuARView getInstance()
    {
        return MenuAR;
    }
}
