/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql;

public class JoinCondition
{
    String leftTableAlias;

    String leftColumn;

    String rightTableAlias;

    String rightColumn;

    /**
     * Insert the method's description here.
     *
     * @param leftColumn  java.lang.String
     * @param rightColumn java.lang.String
     */
    public JoinCondition( String leftColumn, String rightColumn )
    {
        this( null, leftColumn, null, rightColumn );
    }

    /**
     * Insert the method's description here.
     *
     * @param leftColumn  java.lang.String
     * @param rightColumn java.lang.String
     */
    public JoinCondition( String leftTableAlias, String leftColumn, String rightTableAlias, String rightColumn )
    {

        if ( leftTableAlias != null && leftTableAlias.trim().length() == 0 )
        {
            this.leftTableAlias = null;
        }
        else
        {
            this.leftTableAlias = leftTableAlias;
        }

        this.leftColumn = leftColumn;

        if ( rightTableAlias != null && rightTableAlias.trim().length() == 0 )
        {
            this.rightTableAlias = null;
        }
        else
        {
            this.rightTableAlias = rightTableAlias;
        }

        this.rightColumn = rightColumn;
    }
}
