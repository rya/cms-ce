/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.queryexpression;

/**
 * This class defines the expression.
 */
public interface Expression
{
    /**
     * Evaluate the expression.
     */
    public Object evaluate( QueryEvaluator evaluator );
}
