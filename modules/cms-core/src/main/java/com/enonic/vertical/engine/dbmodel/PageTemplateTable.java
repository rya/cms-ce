/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class PageTemplateTable
    extends Table
{
    private static final PageTemplateTable PageTemplate = new PageTemplateTable( "tPageTemplate", "pagetemplate", "pagetemplates" );

    public Column pat_lKey = new Column( "pat_lKey", "@key", true, true, Constants.COLUMN_INTEGER, -1 );

    public ForeignKeyColumn pat_men_lKey =
        new ForeignKeyColumn( "pat_men_lKey", "@menukey", false, false, Constants.COLUMN_INTEGER, null, "tMenu", "men_lKey", false, -1 );

    public Column pat_sName = new Column( "pat_sName", "name", true, false, Constants.COLUMN_VARCHAR, 256 );

    public Column pat_sDescription = new Column( "pat_sDescription", "description", false, false, Constants.COLUMN_VARCHAR, 1024 );

    public Column pat_dteTimestamp =
        new Column( "pat_dteTimestamp", "timestamp", true, false, Constants.COLUMN_CURRENT_TIMESTAMP, -1 );

    public Column pat_xmlData = new Column( "pat_xmlData", "data", false, false, Constants.COLUMN_XML, 1 );

    public Column pat_sStyle = new Column( "pat_sStyle", "@stylesheetkey", true, false, Constants.COLUMN_VARCHAR, 1024 );

    public Column pat_sCSS = new Column( "pat_sCSS", "@csskey", false, false, Constants.COLUMN_VARCHAR, 1024 );

    public Column pat_lType = new Column( "pat_lType", "@type", true, false, Constants.COLUMN_INTEGER, -1 );

    public Column pat_lRunAs = new Column( "pat_lRunAs", "null", false, false, Constants.COLUMN_INTEGER, -1 );

    private PageTemplateTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( pat_lKey );
        addColumn( pat_men_lKey );
        addColumn( pat_sName );
        addColumn( pat_sDescription );
        addColumn( pat_dteTimestamp );
        addColumn( pat_xmlData );
        addColumn( pat_sStyle );
        addColumn( pat_sCSS );
        addColumn( pat_lType );
        addColumn( pat_lRunAs );
    }

    public static PageTemplateTable getInstance()
    {
        return PageTemplate;
    }

}