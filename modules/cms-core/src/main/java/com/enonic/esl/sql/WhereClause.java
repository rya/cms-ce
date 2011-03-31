/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql;

public abstract class WhereClause
{
    private boolean not;

    protected WhereClause()
    {
    }

    protected WhereClause( boolean not )
    {
        this.not = not;
    }

    public abstract String generateSql();

    public boolean isNot()
    {
        return not;
    }

    public void setNot( boolean not )
    {
        this.not = not;
    }
}
