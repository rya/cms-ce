/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class RelatedContentTable
    extends Table
{
    private static final RelatedContentTable RelatedContent = new RelatedContentTable( "tRelatedContent", "null", "null" );

    public ForeignKeyColumn rco_con_lParent =
        new ForeignKeyColumn( "rco_con_lParent", "null", true, true, Constants.COLUMN_INTEGER, null, "tContentVersion", "cov_lKey", false,
                              -1 );

    public ForeignKeyColumn rco_con_lChild =
        new ForeignKeyColumn( "rco_con_lChild", "null", true, true, Constants.COLUMN_INTEGER, null, "tContent", "con_lKey", false, -1 );

    private RelatedContentTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( rco_con_lParent );
        addColumn( rco_con_lChild );
    }

    public static RelatedContentTable getInstance()
    {
        return RelatedContent;
    }

}