/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class PageTemplateCtyTable
    extends Table
{
    private static final PageTemplateCtyTable PageTemplateCty = new PageTemplateCtyTable( "tPageTemplateCty", "null", "null" );

    public ForeignKeyColumn ptt_pat_lKey =
        new ForeignKeyColumn( "ptt_pat_lKey", "null", true, true, Constants.COLUMN_INTEGER, null, "tPageTemplate", "pat_lKey", true, -1 );

    public ForeignKeyColumn ptt_cty_lKey =
        new ForeignKeyColumn( "ptt_cty_lKey", "null", true, true, Constants.COLUMN_INTEGER, null, "tContentType", "cty_lKey", true, -1 );

    private PageTemplateCtyTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( ptt_pat_lKey );
        addColumn( ptt_cty_lKey );
    }

    public static PageTemplateCtyTable getInstance()
    {
        return PageTemplateCty;
    }

}