/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.legacy;

import org.jdom.Document;
import org.jdom.Element;
import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;


public class LegacyCatalogContentDataEqualsTest
    extends AbstractEqualsTest
{

    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        Element xmlContentdataEl = new Element( "contentdata" ).addContent( new Element( "title" ).setText( "xml1 tittel" ) );
        return new LegacyCatalogContentData( new Document( xmlContentdataEl ) );
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        Element xml1ContentdataEl = new Element( "contentdata" ).addContent( new Element( "title" ).setText( "xml2 tittel" ) );
        LegacyCatalogContentData instance1 = new LegacyCatalogContentData( new Document( xml1ContentdataEl ) );

        Element xml2ContentdataEl = new Element( "contentdata" ).addContent( new Element( "title" ).setText( "xml3 tittel" ) );
        LegacyCatalogContentData instance2 = new LegacyCatalogContentData( new Document( xml2ContentdataEl ) );

        return new Object[]{instance1, instance2};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        Element xmlContentdataEl = new Element( "contentdata" ).addContent( new Element( "title" ).setText( "xml1 tittel" ) );
        return new LegacyCatalogContentData( new Document( xmlContentdataEl ) );
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        Element xmlContentdataEl = new Element( "contentdata" ).addContent( new Element( "title" ).setText( "xml1 tittel" ) );
        return new LegacyCatalogContentData( new Document( xmlContentdataEl ) );
    }
}