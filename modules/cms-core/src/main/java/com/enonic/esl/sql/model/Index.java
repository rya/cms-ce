/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql.model;

import java.util.ArrayList;

public class Index
{
    private String name;

    private ArrayList<Column> columns = new ArrayList<Column>();

    public Index( String name )
    {
        this.name = name;
    }

    public void addColumn( Column localColumn )
    {
        columns.add( localColumn );
    }

    public String getName()
    {
        return name;
    }

    public ArrayList<Column> getColumns()
    {
        return columns;
    }

}
