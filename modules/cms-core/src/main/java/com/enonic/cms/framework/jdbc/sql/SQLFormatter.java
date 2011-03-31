/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc.sql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Converts single-line SQL strings into nicely-formatted multi-line, indented statements.
 */
public final class SQLFormatter
{
    /**
     * Select separators.
     */
    private final static String[] SELECT_SEP = {"FROM ", "WHERE ", "ORDER BY ", "GROUP BY ", "HAVING "};

    /**
     * Insert separators.
     */
    private final static String[] INSERT_SEP = {"VALUES "};

    /**
     * Update separators.
     */
    private final static String[] UPDATE_SEP = {"SET ", "WHERE "};

    /**
     * Delete separators.
     */
    private final static String[] DELETE_SEP = {"WHERE "};

    /**
     * Create table separators.
     */
    private final static String[] CREATE_TABLE_SEP = {"( "};

    /**
     * Create index separators.
     */
    private final static String[] CREATE_INDEX_SEP = {"ON ", "( "};

    /**
     * Create view separators.
     */
    private final static String[] CREATE_VIEW_SEP = {"( ", "AS SELECT ", "FROM ", "WHERE ", "ORDER BY ", "GROUP BY ", "HAVING "};

    /**
     * Alter table separators.
     */
    private final static String[] ALTER_TABLE_SEP = {"( "};

    /**
     * Default clause indent.
     */
    private final static String DEFAULT_CLAUSE_INDENT = "    ";

    /**
     * Default wrap indent.
     */
    private final static String DEFAULT_WRAP_INDENT = "        ";

    /**
     * Default newline.
     */
    private final static String DEFAULT_NEWLINE = "\r\n";

    /**
     * Default line length.
     */
    private final static int DEFAULT_LINE_LENGTH = 72;

    /**
     * Newline character sequence.
     */
    private String newline = DEFAULT_NEWLINE;

    /**
     * Line length.
     */
    private int lineLength = DEFAULT_LINE_LENGTH;

    /**
     * Wrap indent.
     */
    private String wrapIndent = DEFAULT_WRAP_INDENT;

    /**
     * Clause indent.
     */
    private String clauseIndent = DEFAULT_CLAUSE_INDENT;

    /**
     * Set newline character sequence.
     */
    public void setNewline( String newline )
    {
        this.newline = newline != null ? newline : DEFAULT_NEWLINE;
    }

    /**
     * Set the line length.
     */
    public void setLineLength( int lineLength )
    {
        this.lineLength = lineLength;
    }

    /**
     * Set the wrap indent.
     */
    public void setWrapIndent( String wrapIndent )
    {
        this.wrapIndent = wrapIndent != null ? wrapIndent : DEFAULT_WRAP_INDENT;
    }

    /**
     * Set the clause indent.
     */
    public void setClauseIndent( String clauseIndent )
    {
        this.clauseIndent = clauseIndent != null ? clauseIndent : DEFAULT_CLAUSE_INDENT;
    }

    /**
     * Pretty print sql.
     */
    public String prettyPrint( String statement )
    {
        String sql = statement.trim();
        String lowerCaseSql = sql.toLowerCase();

        String[] separators;
        if ( lowerCaseSql.startsWith( "select" ) )
        {
            separators = SELECT_SEP;
        }
        else if ( lowerCaseSql.startsWith( "insert" ) )
        {
            separators = INSERT_SEP;
        }
        else if ( lowerCaseSql.startsWith( "update" ) )
        {
            separators = UPDATE_SEP;
        }
        else if ( lowerCaseSql.startsWith( "delete" ) )
        {
            separators = DELETE_SEP;
        }
        else if ( lowerCaseSql.startsWith( "create table" ) )
        {
            separators = CREATE_TABLE_SEP;
        }
        else if ( lowerCaseSql.startsWith( "create index" ) )
        {
            separators = CREATE_INDEX_SEP;
        }
        else if ( lowerCaseSql.startsWith( "create view" ) )
        {
            separators = CREATE_VIEW_SEP;
        }
        else if ( lowerCaseSql.startsWith( "alter table" ) )
        {
            separators = ALTER_TABLE_SEP;
        }
        else
        {
            separators = new String[0];
        }

        int start = 0;
        int end = -1;
        StringBuffer clause;
        List<StringBuffer> clauses = new ArrayList<StringBuffer>();
        clauses.add( new StringBuffer() );
        for ( int i = 0; i < separators.length; i++ )
        {
            end = lowerCaseSql.indexOf( " " + separators[i].toLowerCase(), start );
            if ( end == -1 )
            {
                break;
            }

            clause = clauses.get( clauses.size() - 1 );
            clause.append( sql.substring( start, end ) );

            clause = new StringBuffer();
            clauses.add( clause );
            clause.append( this.clauseIndent );
            clause.append( separators[i] );

            start = end + 1 + separators[i].length();
        }

        clause = clauses.get( clauses.size() - 1 );
        clause.append( sql.substring( start ) );

        StringBuffer pp = new StringBuffer( sql.length() );
        for ( Iterator<StringBuffer> iter = clauses.iterator(); iter.hasNext(); )
        {
            pp.append( wrapLine( iter.next().toString() ) );
            if ( iter.hasNext() )
            {
                pp.append( this.newline );
            }
        }

        return pp.toString();
    }

    /**
     * Wrap line.
     */
    private String wrapLine( String line )
    {
        StringBuffer lines = new StringBuffer( line.length() );

        // ensure that any leading whitespace is preserved.
        for ( int i = 0; i < line.length() && ( line.charAt( i ) == ' ' || line.charAt( i ) == '\t' ); i++ )
        {
            lines.append( line.charAt( i ) );
        }

        StringTokenizer tok = new StringTokenizer( line );
        int length = 0;
        String elem;
        while ( tok.hasMoreTokens() )
        {
            elem = tok.nextToken();
            length += elem.length();

            // if we would have exceeded the max, write out a newline
            // before writing the elem.
            if ( length >= this.lineLength )
            {
                lines.append( this.newline );
                lines.append( this.wrapIndent );
                lines.append( elem );
                lines.append( ' ' );
                length = this.wrapIndent.length() + elem.length() + 1;
                continue;
            }

            // if the current length is greater than the max, then the
            // last word alone was too long, so just write out a
            // newline and move on.
            if ( elem.length() >= this.lineLength )
            {
                lines.append( elem );
                if ( tok.hasMoreTokens() )
                {
                    lines.append( this.newline );
                }

                lines.append( this.wrapIndent );
                length = this.wrapIndent.length();
                continue;
            }

            lines.append( elem );
            lines.append( ' ' );
            length++;
        }

        return lines.toString();
    }
}
