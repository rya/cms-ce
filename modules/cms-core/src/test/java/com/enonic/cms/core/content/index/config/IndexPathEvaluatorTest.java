/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.config;

import java.util.List;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class IndexPathEvaluatorTest
{
    private IndexPathEvaluator evaluator;

    private Document inputDoc;

    private Document loadTestDocument()
        throws Exception
    {
        SAXBuilder builder = new SAXBuilder();
        return builder.build( getClass().getResourceAsStream( getClass().getSimpleName() + ".xml" ) );
    }

    @Before
    public void init()
        throws Exception
    {
        this.evaluator = new IndexPathEvaluator();
        this.inputDoc = loadTestDocument();
    }

    @Test
    public void testSimple()
    {
        assertEquals( this.evaluator.evaluate( "/person/firstName", this.inputDoc ), "Ola" );
        assertEquals( this.evaluator.evaluate( "/person/lastName", this.inputDoc ), "Normann" );
        assertEquals( this.evaluator.evaluate( "lower-case(/person/firstName)", this.inputDoc ), "ola" );
        assertEquals( this.evaluator.evaluate( "lower-case(/person/lastName)", this.inputDoc ), "normann" );
    }

    @Test
    public void testSequence()
    {
        assertEquals( this.evaluator.evaluate( "//favouriteColor", this.inputDoc ), "red", "green", "blue" );
        assertEquals( this.evaluator.evaluate( "count(//favouriteColor)", this.inputDoc ), "3" );
        assertEquals( this.evaluator.evaluate( "string-join(//favouriteColor, ',')", this.inputDoc ), "red,green,blue" );
    }

    @Test
    public void testXSNamespace()
    {
        assertEquals( this.evaluator.evaluate( "xs:string(count(//favouriteColor))", this.inputDoc ), "3" );
    }

    @Test
    public void testSaxonNamespace()
    {
        assertEquals( this.evaluator.evaluate( "saxon:sort(//favouriteColor)", this.inputDoc ), "blue", "green", "red" );
    }

    private void assertEquals( List<String> real, String... expected )
    {
        Assert.assertArrayEquals( expected, real.toArray( new String[real.size()] ) );
    }
}
