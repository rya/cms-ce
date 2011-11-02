/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.Table;

public final class ContentIndexTable
    extends Table
{
    private static final ContentIndexTable ContentIndex = new ContentIndexTable( "tContentIndex", "null", "null" );

    public Column cix_sKey = new Column( "cix_sKey", "null", true, true, Constants.COLUMN_VARCHAR, 36 );

    public Column cix_lContentKey = new Column( "cix_lContentKey", "null", true, false, Constants.COLUMN_INTEGER, -1 );

    public Column cix_lContentStatus = new Column( "cix_lContentStatus", "null", true, false, Constants.COLUMN_INTEGER, -1 );

    public Column cix_dtePublishFrom = new Column( "cix_dtePublishFrom", "null", false, false, Constants.COLUMN_TIMESTAMP, -1 );

    public Column cix_dtePublishTo = new Column( "cix_dtePublishTo", "null", false, false, Constants.COLUMN_TIMESTAMP, -1 );

    public Column cix_lContentTypeKey = new Column( "cix_lContentTypeKey", "null", true, false, Constants.COLUMN_INTEGER, -1 );

    public Column cix_lCategoryKey = new Column( "cix_lCategoryKey", "null", true, false, Constants.COLUMN_INTEGER, -1 );

    public Column cix_sPath = new Column( "cix_sPath", "null", true, false, Constants.COLUMN_VARCHAR, 256 );

    public Column cix_sValue = new Column( "cix_sValue", "null", true, false, Constants.COLUMN_VARCHAR, 2048 );

    public Column cix_sOrderValue = new Column( "cix_sOrderValue", "null", true, false, Constants.COLUMN_VARCHAR, 64 );

    public Column cix_fNumValue = new Column( "cix_fNumValue", "null", false, false, Constants.COLUMN_FLOAT, -1 );

    private ContentIndexTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( cix_sKey );
        addColumn( cix_lContentKey );
        addColumn( cix_lContentStatus );
        addColumn( cix_dtePublishFrom );
        addColumn( cix_dtePublishTo );
        addColumn( cix_lContentTypeKey );
        addColumn( cix_lCategoryKey );
        addColumn( cix_sPath );
        addColumn( cix_sValue );
        addColumn( cix_sOrderValue );
        addColumn( cix_fNumValue );
    }

    public static ContentIndexTable getInstance()
    {
        return ContentIndex;
    }

}