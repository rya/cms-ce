/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content.index.queryexpression;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import com.enonic.cms.domain.content.index.ContentIndexConstants;

/**
 * This class implements the field expression.
 */
public final class FieldExpr
    implements Expression
{
    private static final Set<String> DATE_FIELDS = new TreeSet<String>( String.CASE_INSENSITIVE_ORDER );

    static
    {
        DATE_FIELDS.addAll( Arrays.asList( ContentIndexConstants.DATE_FIELDS ) );
    }

    /**
     * Field expression.
     */
    private final String path;

    /**
     * Construct the field.
     */
    public FieldExpr( String path )
    {
        this.path = path;
    }

    /**
     * Return the field.
     */
    public String getPath()
    {
        return this.path;
    }

    /**
     * Return the expression as string.
     */
    public String toString()
    {
        return this.path;
    }

    public boolean isDateField()
    {
        return DATE_FIELDS.contains( path );
    }

    /**
     * Evaluate the expression.
     */
    public Object evaluate( QueryEvaluator evaluator )
    {
        return evaluator.evaluate( this );
    }
}
