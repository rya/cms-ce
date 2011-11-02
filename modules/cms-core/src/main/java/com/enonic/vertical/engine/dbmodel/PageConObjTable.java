/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class PageConObjTable
    extends Table
{
    private static final PageConObjTable PageConObj = new PageConObjTable( "tPageConObj", "null", "null" );

    public ForeignKeyColumn pco_pag_lKey =
        new ForeignKeyColumn( "pco_pag_lKey", "null", true, true, Constants.COLUMN_INTEGER, null, "tPage", "pag_lKey", false, -1 );

    public ForeignKeyColumn pco_cob_lKey =
        new ForeignKeyColumn( "pco_cob_lKey", "null", true, true, Constants.COLUMN_INTEGER, null, "tContentObject", "cob_lKey", false, -1 );

    public Column pco_lOrder = new Column( "pco_lOrder", "null", true, false, Constants.COLUMN_INTEGER, -1 );

    public ForeignKeyColumn pco_ptp_lKey =
        new ForeignKeyColumn( "pco_ptp_lKey", "null", true, false, Constants.COLUMN_INTEGER, null, "tPageTemplParam", "ptp_lKey", false,
                              -1 );

    public Column pco_dteTimestamp = new Column( "pco_dteTimestamp", "null", true, false, Constants.COLUMN_CURRENT_TIMESTAMP, -1 );

    private PageConObjTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( pco_pag_lKey );
        addColumn( pco_cob_lKey );
        addColumn( pco_lOrder );
        addColumn( pco_ptp_lKey );
        addColumn( pco_dteTimestamp );
    }

    public static PageConObjTable getInstance()
    {
        return PageConObj;
    }

}