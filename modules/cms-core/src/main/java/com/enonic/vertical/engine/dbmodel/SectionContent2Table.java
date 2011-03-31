/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class SectionContent2Table
    extends Table
{
    private static final SectionContent2Table SectionContent2 = new SectionContent2Table( "tSectionContent2", "null", "null" );

    public Column sco_lkey = new Column( "sco_lkey", "null", true, true, Constants.COLUMN_INTEGER, null, -1 );

    public ForeignKeyColumn sco_con_lKey =
        new ForeignKeyColumn( "sco_con_lKey", "@contentkey", true, false, Constants.COLUMN_INTEGER, null, "tContent", "con_lKey", true,
                              -1 );

    public ForeignKeyColumn sco_mei_lKey =
        new ForeignKeyColumn( "sco_mei_lKey", "@sectionkey", true, false, Constants.COLUMN_INTEGER, null, "tMenuItem", "mei_lKey", true,
                              -1 );

    public Column sco_lOrder = new Column( "sco_lOrder", "@order", true, false, Constants.COLUMN_INTEGER, null, -1 );

    public Column sco_bApproved = new Column( "sco_bApproved", "@approved", true, false, Constants.COLUMN_BOOLEAN, null, -1 );

    public Column sco_dteTimestamp =
        new Column( "sco_dteTimestamp", "@timestamp", true, false, Constants.COLUMN_CURRENT_TIMESTAMP, null, -1 );

    private SectionContent2Table( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( sco_lkey );
        addColumn( sco_con_lKey );
        addColumn( sco_mei_lKey );
        addColumn( sco_lOrder );
        addColumn( sco_bApproved );
        addColumn( sco_dteTimestamp );
    }

    public static SectionContent2Table getInstance()
    {
        return SectionContent2;
    }

}