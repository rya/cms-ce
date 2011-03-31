/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class ContentHomeTable
    extends Table
{
    private static final ContentHomeTable ContentHome = new ContentHomeTable( "tContentHome", "contenthome", "contenthomes" );

    public ForeignKeyColumn cho_con_lKey =
        new ForeignKeyColumn( "cho_con_lKey", "@contentkey", true, true, Constants.COLUMN_INTEGER, null, "tContent", "con_lKey", true, -1 );

    public ForeignKeyColumn cho_men_lKey =
        new ForeignKeyColumn( "cho_men_lKey", "@menukey", true, true, Constants.COLUMN_INTEGER, null, "tMenu", "men_lKey", true, -1 );

    public ForeignKeyColumn cho_mei_lKey =
        new ForeignKeyColumn( "cho_mei_lKey", "@menuitemkey", false, false, Constants.COLUMN_INTEGER, null, "tMenuItem", "mei_lKey", true,
                              -1 );

    public ForeignKeyColumn cho_pat_lKey =
        new ForeignKeyColumn( "cho_pat_lKey", "@pagetemplatekey", false, false, Constants.COLUMN_INTEGER, null, "tPageTemplate", "pat_lKey",
                              true, -1 );

    private ContentHomeTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( cho_con_lKey );
        addColumn( cho_men_lKey );
        addColumn( cho_mei_lKey );
        addColumn( cho_pat_lKey );
    }

    public static ContentHomeTable getInstance()
    {
        return ContentHome;
    }

}