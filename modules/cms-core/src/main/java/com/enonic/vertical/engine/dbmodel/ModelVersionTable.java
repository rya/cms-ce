/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.Table;

public final class ModelVersionTable
    extends Table
{
    private static final ModelVersionTable ModelVersion = new ModelVersionTable( "tModelVersion", "null", "null" );

    public Column mve_sKey = new Column( "mve_sKey", "null", true, true, Constants.COLUMN_VARCHAR, 32 );

    public Column mve_lVersion = new Column( "mve_lVersion", "null", true, false, Constants.COLUMN_INTEGER, -1 );

    private ModelVersionTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( mve_sKey );
        addColumn( mve_lVersion );
    }

    public static ModelVersionTable getInstance()
    {
        return ModelVersion;
    }

}