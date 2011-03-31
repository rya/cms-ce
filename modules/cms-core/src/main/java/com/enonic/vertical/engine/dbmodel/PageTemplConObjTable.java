/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class PageTemplConObjTable
    extends Table
{
    private static final PageTemplConObjTable PageTemplConObj = new PageTemplConObjTable( "tPageTemplConObj", "null", "null" );

    public ForeignKeyColumn ptc_pat_lKey =
        new ForeignKeyColumn( "ptc_pat_lKey", "null", true, true, Constants.COLUMN_INTEGER, null, "tPageTemplate", "pat_lKey", false, -1 );

    public ForeignKeyColumn ptc_cob_lKey =
        new ForeignKeyColumn( "ptc_cob_lKey", "null", true, true, Constants.COLUMN_INTEGER, null, "tContentObject", "cob_lKey", false, -1 );

    public Column ptc_lOrder = new Column( "ptc_lOrder", "null", true, false, Constants.COLUMN_INTEGER, null, -1 );

    public ForeignKeyColumn ptc_ptp_lKey =
        new ForeignKeyColumn( "ptc_ptp_lKey", "null", true, false, Constants.COLUMN_INTEGER, null, "tPageTemplParam", "ptp_lKey", false,
                              -1 );

    public Column ptc_dteTimestamp = new Column( "ptc_dteTimestamp", "null", true, false, Constants.COLUMN_CURRENT_TIMESTAMP, null, -1 );

    private PageTemplConObjTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( ptc_pat_lKey );
        addColumn( ptc_cob_lKey );
        addColumn( ptc_lOrder );
        addColumn( ptc_ptp_lKey );
        addColumn( ptc_dteTimestamp );
    }

    public static PageTemplConObjTable getInstance()
    {
        return PageTemplConObj;
    }

}