/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class PageTable
    extends Table
{
    private static final PageTable Page = new PageTable( "tPage", "null", "null" );

    public Column pag_lKey = new Column( "pag_lKey", "null", true, true, Constants.COLUMN_INTEGER, -1 );

    public ForeignKeyColumn pag_pat_lKey =
        new ForeignKeyColumn( "pag_pat_lKey", "null", true, false, Constants.COLUMN_INTEGER, null, "tPageTemplate", "pat_lKey", false, -1 );

    public Column pag_sXML = new Column( "pag_sXML", "null", false, false, Constants.COLUMN_XML, 1 );

    private PageTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( pag_lKey );
        addColumn( pag_pat_lKey );
        addColumn( pag_sXML );
    }

    public static PageTable getInstance()
    {
        return Page;
    }

}