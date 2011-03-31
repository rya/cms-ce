/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.View;

public final class ContentPubKeyView
    extends View
{
    public Column con_lKey = new Column( "con_lKey", "@key", Constants.COLUMN_INTEGER );

    private final static String SQL = "select con_lKey from tContent left join tContentVersion on con_cov_lKey = cov_lKey " +
        "where con_bDeleted = 0 and cov_lStatus = 2 " +
        "and con_dtePublishFrom <= @currentTimestamp@ and ( con_dtePublishTo is null OR con_dtePublishTo > @currentTimestamp@)";

    private static final ContentPubKeyView ContentPubKey = new ContentPubKeyView( "vContentPubKey" );

    private ContentPubKeyView( String tableName )
    {
        super( tableName, "null", "null", SQL, 6 );
        addColumn( con_lKey );
    }

    public static ContentPubKeyView getInstance()
    {
        return ContentPubKey;
    }

}