/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.config;

import java.util.Collections;
import java.util.List;

import org.jdom.Document;

/**
 * This class defines one index definition (name, xpath pair).
 */
public final class IndexDefinition
{
    /**
     * Name of index.
     */
    private final String name;

    /**
     * XPath expression.
     */
    private final String xpath;

    /**
     * Construct the definition.
     */
    public IndexDefinition( String name, String xpath )
    {
        this.xpath = xpath.trim();
        this.name = name.trim();
    }

    /**
     * Return the name.
     */
    public String getName()
    {
        if ( this.name.startsWith( "data/" ) )
        {
            return this.name;
        }
        else
        {
            return "data/" + this.name;
        }
    }

    /**
     * Return the xpath.
     */
    public String getXPath()
    {
        return this.xpath;
    }

    /**
     * Return the hash code.
     */
    public int hashCode()
    {
        return this.name.hashCode();
    }

    /**
     * Evaluate.
     */
    public List<String> evaluate( Document doc )
    {
        List<String> result = IndexPathEvaluator.evaluateShared( this.xpath, doc );
        if ( result.isEmpty() )
        {
            return Collections.singletonList( "" );
        }
        else
        {
            return result;
        }
    }
}
