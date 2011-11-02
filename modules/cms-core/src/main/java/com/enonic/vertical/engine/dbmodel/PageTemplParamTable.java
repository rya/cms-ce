/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class PageTemplParamTable
    extends Table
{
    private static final PageTemplParamTable PageTemplParam = new PageTemplParamTable( "tPageTemplParam", "null", "null" );

    public Column ptp_lKey = new Column( "ptp_lKey", "null", true, true, Constants.COLUMN_INTEGER, -1 );

    public ForeignKeyColumn ptp_pat_lKey =
        new ForeignKeyColumn( "ptp_pat_lKey", "null", true, false, Constants.COLUMN_INTEGER, null, "tPageTemplate", "pat_lKey", false, -1 );

    public Column ptp_sParamName = new Column( "ptp_sParamName", "null", true, false, Constants.COLUMN_VARCHAR, 64 );

    public Column ptp_bMultiple = new Column( "ptp_bMultiple", "null", true, false, Constants.COLUMN_BOOLEAN, -1 );

    public Column ptp_sSeparator = new Column( "ptp_sSeparator", "null", false, false, Constants.COLUMN_VARCHAR, 1024 );

    public Column ptp_bOverride = new Column( "ptp_bOverride", "null", true, false, Constants.COLUMN_BOOLEAN, -1 );

    private PageTemplParamTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( ptp_lKey );
        addColumn( ptp_pat_lKey );
        addColumn( ptp_sParamName );
        addColumn( ptp_bMultiple );
        addColumn( ptp_sSeparator );
        addColumn( ptp_bOverride );
    }

    public static PageTemplParamTable getInstance()
    {
        return PageTemplParam;
    }

}