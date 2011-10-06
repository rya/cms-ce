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

public class IndexDefinitionBuilderTest
{
    private IndexDefinitionBuilder builder;

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
        this.builder = new IndexDefinitionBuilder();
        this.inputDoc = loadTestDocument();
    }

    @Test
    public void testBuilder()
    {
        List<IndexDefinition> result = this.builder.buildList( this.inputDoc.getRootElement() );
        Assert.assertEquals( 9, result.size() );
        assertEquals( result.get( 0 ), "data/person/firstName", "contentdata/person/firstName" );
        assertEquals( result.get( 1 ), "data/colorCount", "count(//favouriteColor)" );
        assertEquals( result.get( 2 ), "data/colorList", "string-join(saxon:sort(//favouriteColor), ',')" );
        assertEquals( result.get( 3 ), "data/a", "contentdata/a" );
        assertEquals( result.get( 4 ), "data/b", "contentdata/b" );
        assertEquals( result.get( 5 ), "data/c", "c" );
        assertEquals( result.get( 6 ), "data/d", "contentdata/d" );
        assertEquals( result.get( 7 ), "data/e", "contentdata/e" );
        assertEquals( result.get( 8 ), "data/f", "contentdata/f" );
    }

    private void assertEquals( IndexDefinition def, String name, String xpath )
    {
        Assert.assertNotNull( def );
        Assert.assertEquals( name, def.getName() );
        Assert.assertEquals( xpath, def.getXPath() );
    }
}
