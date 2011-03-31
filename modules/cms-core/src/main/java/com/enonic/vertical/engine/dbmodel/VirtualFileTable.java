/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.Table;

public final class VirtualFileTable
    extends Table
{
    private static final VirtualFileTable VirtualFile = new VirtualFileTable( "tVirtualFile", "null", "null" );

    public Column vf_sKey = new Column( "vf_sKey", "null", true, true, Constants.COLUMN_VARCHAR, null, 40 );

    public Column vf_sParentKey = new Column( "vf_sParentKey", "null", false, false, Constants.COLUMN_VARCHAR, null, 40 );

    public Column vf_sName = new Column( "vf_sName", "null", true, false, Constants.COLUMN_VARCHAR, null, 255 );

    public Column vf_lLastModified = new Column( "vf_lLastModified", "null", true, false, Constants.COLUMN_BIGINT, null, -1 );

    public Column vf_lLength = new Column( "vf_lLength", "null", true, false, Constants.COLUMN_BIGINT, null, -1 );

    public Column vf_sBlobKey = new Column( "vf_sBlobKey", "null", false, false, Constants.COLUMN_VARCHAR, null, 40 );

    private VirtualFileTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( vf_sKey );
        addColumn( vf_sParentKey );
        addColumn( vf_sName );
        addColumn( vf_lLastModified );
        addColumn( vf_lLength );
        addColumn( vf_sBlobKey );
    }

    public static VirtualFileTable getInstance()
    {
        return VirtualFile;
    }

}