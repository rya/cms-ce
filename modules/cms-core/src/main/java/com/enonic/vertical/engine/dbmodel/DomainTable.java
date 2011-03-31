/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.Table;

public final class DomainTable
    extends Table
{
    private static final DomainTable Domain = new DomainTable( "tDomain", "domain", "domains" );

    public Column dom_lKey = new Column( "dom_lKey", "@key", true, true, Constants.COLUMN_INTEGER, null, -1 );

    public Column dom_sName = new Column( "dom_sName", "name", true, false, Constants.COLUMN_VARCHAR, null, 256 );

    public Column dom_bIsDeleted = new Column( "dom_bIsDeleted", "@deleted", true, false, Constants.COLUMN_BOOLEAN, Boolean.FALSE, -1 );

    public Column dom_bDefaultStore = new Column( "dom_bDefaultStore", "@defaultstore", false, false, Constants.COLUMN_BOOLEAN, null, -1 );

    public Column dom_sConfigName = new Column( "dom_sConfigName", "config", false, false, Constants.COLUMN_VARCHAR, null, 64 );

    public Column dom_xmlData = new Column( "dom_xmlData", "null", false, false, Constants.COLUMN_XML, null, 1 );

    private DomainTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( dom_lKey );
        addColumn( dom_sName );
        addColumn( dom_bIsDeleted );
        addColumn( dom_bDefaultStore );
        addColumn( dom_sConfigName );
        addColumn( dom_xmlData );
    }

    public static DomainTable getInstance()
    {
        return Domain;
    }

}