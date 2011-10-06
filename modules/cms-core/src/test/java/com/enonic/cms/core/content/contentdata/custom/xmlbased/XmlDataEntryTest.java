/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.xmlbased;

import org.jdom.Element;
import org.junit.Test;

import com.enonic.cms.core.content.contentdata.InvalidContentDataException;
import com.enonic.cms.core.content.contenttype.dataentryconfig.XmlDataEntryConfig;

import static org.junit.Assert.*;

public class XmlDataEntryTest
{
    private XmlDataEntryConfig config = new XmlDataEntryConfig( "myXml", false, "My xml", "contentdata/myxml" );

    @Test
    public void testGetValue_ReturnsClonedDocument()
    {
        final XmlDataEntry xml = new XmlDataEntry( config, "<root a=\"A\" b=\"B\"/>" );

        final Element root1 = xml.getValue().getRootElement();
        final Element root2 = xml.getValue().getRootElement();

        assertNotSame( root1, root2 );
    }

    @Test
    public void testEquals_WithDifferenteAttributeOrder()
    {
        XmlDataEntry xml = new XmlDataEntry( config, "<root a=\"A\" b=\"B\"/>" );
        XmlDataEntry sameXmlButWithDifferentAttributeOrder = new XmlDataEntry( config, "<root b=\"B\" a=\"A\"/>" );

        xml.validate();
        sameXmlButWithDifferentAttributeOrder.validate();

        assertTrue( xml.equals( sameXmlButWithDifferentAttributeOrder ) );
    }

    @Test
    public void testEquals_WithAndWithoutProlog()
    {
        XmlDataEntry xml = new XmlDataEntry( config, "<?xml version=\"1.0\" encoding=\"utf-8\"?><root><a>A</a><b>B</b></root>" );
        XmlDataEntry sameXmlWithoutProlog = new XmlDataEntry( config, "<root><a>A</a><b>B</b></root>" );

        xml.validate();
        sameXmlWithoutProlog.validate();

        assertTrue( xml.equals( sameXmlWithoutProlog ) );
    }

    @Test
    public void testNotEquals_WithDifferenteElementOrder()
    {
        XmlDataEntry xml = new XmlDataEntry( config, "<root><a>A</a><b>B</b></root>" );
        XmlDataEntry sameXmlButWithDefferenteElementOrder = new XmlDataEntry( config, "<root><b>B</b><a>A</a></root>" );

        xml.validate();
        sameXmlButWithDefferenteElementOrder.validate();

        assertFalse( xml.equals( sameXmlButWithDefferenteElementOrder ) );
    }

    @Test(expected = InvalidContentDataException.class)
    public void testInvalidContentDataException_NoRoot()
    {
        XmlDataEntry xml = new XmlDataEntry( config, "<a/><b/>" );
        xml.validate();
    }

    @Test(expected = InvalidContentDataException.class)
    public void testInvalidContentDataException_InvalidRoot()
    {
        XmlDataEntry xml = new XmlDataEntry( config, "<root" );
        xml.validate();
    }

    @Test
    public void testNullIsValid()
    {
        XmlDataEntry xml = new XmlDataEntry( config, null );
        xml.validate();
        assertFalse( xml.hasValue() );
    }

    @Test
    public void testEmptyIsValid()
    {
        XmlDataEntry xml = new XmlDataEntry( config, "" );
        xml.validate();
        assertFalse( xml.hasValue() );
    }

    @Test(expected = InvalidContentDataException.class)
    public void testInvalidContentDataException_MissingEndElement()
    {
        XmlDataEntry xml = new XmlDataEntry( config, "<root><a></root>" );
        xml.validate();
    }

    @Test(expected = InvalidContentDataException.class)
    public void testInvalidContentDataException_MissingAttributeValue()
    {
        XmlDataEntry xml = new XmlDataEntry( config, "<root a/>" );
        xml.validate();
    }

    @Test(expected = InvalidContentDataException.class)
    public void testInvalidContentDataException_MissingAttributeValue2()
    {
        XmlDataEntry xml = new XmlDataEntry( config, "<root a=/>" );
        xml.validate();
    }

    @Test(expected = InvalidContentDataException.class)
    public void testInvalidContentDataException_DuplicateAttribute()
    {
        XmlDataEntry xml = new XmlDataEntry( config, "<root a=\"a\" a=\"b\"/>" );
        xml.validate();
    }
}
