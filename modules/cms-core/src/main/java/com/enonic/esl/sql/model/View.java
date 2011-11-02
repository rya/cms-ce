/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql.model;


/**
 * This class implements the view.
 */
public class View
    extends Table
{
    /**
     * Select sql.
     */
    private final String selectSql;

    private final int viewNum;

    /**
     * Construct the view.
     */
    public View( String tableName, String elementName, String parentName, String selectSql )
    {
        this( tableName, elementName, parentName, selectSql, -1 );
    }

    /**
     * Construct the view.
     */
    public View( String tableName, String elementName, String parentName, String selectSql, int viewNum )
    {
        super( tableName, elementName, parentName );
        this.selectSql = selectSql;
        this.viewNum = viewNum;
    }

    /**
     * Return the select sql.
     */
    public String getSelectSql()
    {
        return this.selectSql;
    }

    public String getReplacementSql()
    {
        return "(" + this.selectSql + ") view" + this.viewNum;
    }

    public boolean hasReplacementSql()
    {
        return this.selectSql != null;
    }
}
