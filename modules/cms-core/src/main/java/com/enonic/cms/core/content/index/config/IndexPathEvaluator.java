/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.config;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.transform.JDOMSource;

import net.sf.saxon.om.Item;
import net.sf.saxon.sxpath.XPathEvaluator;
import net.sf.saxon.sxpath.XPathExpression;

/**
 * This class implements a xpath 2.0 evaluator that is used to evaluate index paths.
 */
public final class IndexPathEvaluator
{
    /**
     * Shared evaluator.
     */
    private final static IndexPathEvaluator SHARED_EVALUATOR = new IndexPathEvaluator();

    /**
     * Evaluator instance.
     */
    private final XPathEvaluator evaluator;

    /**
     * Constructs the evaluator.
     */
    public IndexPathEvaluator()
    {
        this.evaluator = new XPathEvaluator();
    }

    /**
     * Evaluate the xpath on specified document.
     */
    public List<String> evaluate( String xpath, Document doc )
    {
        try
        {
            return doEvaluate( xpath, doc );
        }
        catch ( Exception e )
        {
            throw new IndexPathException( e );
        }
    }

    /**
     * Internal evaluator method.
     */
    private List<String> doEvaluate( String xpath, Document doc )
        throws Exception
    {
        ArrayList<String> values = new ArrayList<String>();
        XPathExpression expr = this.evaluator.createExpression( xpath );
        List result = expr.evaluate( new JDOMSource( doc ) );

        for ( Object o : result )
        {
            if ( o instanceof Item )
            {
                values.add( ( (Item) o ).getStringValue() );
            }
            else
            {
                values.add( o.toString() );
            }
        }

        return values;
    }

    /**
     * Evaluate xpath based on shared instance. For more speed.
     */
    public static List<String> evaluateShared( String xpath, Document doc )
    {
        synchronized ( SHARED_EVALUATOR )
        {
            return SHARED_EVALUATOR.evaluate( xpath, doc );
        }
    }
}
