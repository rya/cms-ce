/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.stringbased;

import java.util.Set;

import com.enonic.cms.core.content.ContentKey;
import org.junit.Test;

import com.enonic.cms.core.content.contentdata.InvalidContentDataException;
import com.enonic.cms.core.content.contenttype.dataentryconfig.HtmlAreaDataEntryConfig;

import static org.junit.Assert.*;

public class HtmlAreaDataEntryTest
{
    private HtmlAreaDataEntryConfig config = new HtmlAreaDataEntryConfig( "myHtml", false, "My html", "contentdata/myhtml" );

    @Test
    public void testPrologIsRemoved()
    {
        HtmlAreaDataEntry html = new HtmlAreaDataEntry( config, "<?xml version=\"1.0\" encoding=\"utf-8\"?><root><a>A</a><b>B</b></root>" );
        assertEquals( "<root><a>A</a><b>B</b></root>", html.getValue() );
    }

    @Test
    public void testEquals_WithAndWithoutProlog()
    {
        HtmlAreaDataEntry html = new HtmlAreaDataEntry( config, "<?xml version=\"1.0\" encoding=\"utf-8\"?><root><a>A</a><b>B</b></root>" );
        HtmlAreaDataEntry sameHtmlWithoutProlog = new HtmlAreaDataEntry( config, "<root><a>A</a><b>B</b></root>" );

        html.validate();
        sameHtmlWithoutProlog.validate();

        assertTrue( html.getValue().equals( sameHtmlWithoutProlog.getValue() ) );
        assertTrue( html.equals( sameHtmlWithoutProlog ) );
    }

    @Test
    public void testNotEquals_WithDifferenteElementOrder()
    {
        HtmlAreaDataEntry html = new HtmlAreaDataEntry( config, "<root><a>A</a><b>B</b></root>" );
        HtmlAreaDataEntry sameHtmlButWithDifferentElementOrder = new HtmlAreaDataEntry( config, "<root><b>B</b><a>A</a></root>" );

        html.validate();
        sameHtmlButWithDifferentElementOrder.validate();

        assertFalse( html.equals( sameHtmlButWithDifferentElementOrder ) );
    }

    @Test
    public void testDobbleRootElementsAreValid()
    {
        HtmlAreaDataEntry html1 = new HtmlAreaDataEntry( config, "<p><strong>A</strong></p><p><strong>B</strong></p>" );

        html1.validate();

        assertTrue( html1.hasValue() );
        assertEquals( "<p><strong>A</strong></p><p><strong>B</strong></p>", html1.getValue() );
    }

    @Test
    public void testNullIsValid()
    {
        HtmlAreaDataEntry html = new HtmlAreaDataEntry( config, null );
        html.validate();

        assertFalse( html.hasValue() );
    }

    @Test
    public void testEmptyIsValid()
    {
        HtmlAreaDataEntry html = new HtmlAreaDataEntry( config, "" );
        html.validate();

        assertTrue( html.hasValue() );
    }

    @Test
    public void testNotClosingEmptyElements()
    {
        assertEquals( "<textarea></textarea>", new HtmlAreaDataEntry( config, "<textarea></textarea>" ).getValue() );
        assertEquals( "<button></button>", new HtmlAreaDataEntry( config, "<button></button>" ).getValue() );
        assertEquals( "<p></p>", new HtmlAreaDataEntry( config, "<p></p>" ).getValue() );
        assertEquals( "<div></div>", new HtmlAreaDataEntry( config, "<div></div>" ).getValue() );
        assertEquals( "<pre></pre>", new HtmlAreaDataEntry( config, "<pre></pre>" ).getValue() );
        assertEquals( "<blockquote></blockquote>", new HtmlAreaDataEntry( config, "<blockquote></blockquote>" ).getValue() );
        assertEquals( "<td></td>", new HtmlAreaDataEntry( config, "<td></td>" ).getValue() );
        assertEquals( "<th></th>", new HtmlAreaDataEntry( config, "<th></th>" ).getValue() );
        assertEquals( "<h1></h1>", new HtmlAreaDataEntry( config, "<h1></h1>" ).getValue() );
    }

    @Test
    public void testEquals_WithDobbleRootElements()
    {
        HtmlAreaDataEntry html1 = new HtmlAreaDataEntry( config, "<p><strong>A</strong></p><p><strong>B</strong></p>" );
        HtmlAreaDataEntry html2 = new HtmlAreaDataEntry( config, "<p><strong>A</strong></p><p><strong>B</strong></p>" );

        html1.validate();
        html2.validate();

        assertTrue( html1.equals( html2 ) );
    }

    @Test(expected = InvalidContentDataException.class)
    public void testInvalidContentDataException_InvalidRoot()
    {
        HtmlAreaDataEntry html = new HtmlAreaDataEntry( config, "<root" );
        html.validate();
    }

    @Test(expected = InvalidContentDataException.class)
    public void testInvalidContentDataException_MissingEndElement()
    {
        HtmlAreaDataEntry html = new HtmlAreaDataEntry( config, "<root><a></root>" );
        html.validate();
    }

    @Test(expected = InvalidContentDataException.class)
    public void testInvalidContentDataException_MissingAttributeValue()
    {
        HtmlAreaDataEntry html = new HtmlAreaDataEntry( config, "<root a/>" );
        html.validate();
    }

    @Test(expected = InvalidContentDataException.class)
    public void testInvalidContentDataException_MissingAttributeValue2()
    {
        HtmlAreaDataEntry html = new HtmlAreaDataEntry( config, "<root a=/>" );
        html.validate();
    }

    @Test(expected = InvalidContentDataException.class)
    public void testInvalidContentDataException_DuplicateAttribute()
    {
        HtmlAreaDataEntry html = new HtmlAreaDataEntry( config, "<root a=\"a\" a=\"b\"/>" );
        html.validate();
    }

    @Test
    public void testResolveRelatedContentKeys()
    {
        HtmlAreaDataEntry html1 = new HtmlAreaDataEntry( config, null );
        HtmlAreaDataEntry html2 = new HtmlAreaDataEntry( config, "<p><strong>A</strong></p><p><strong>B</strong></p>" );
        HtmlAreaDataEntry html3 = new HtmlAreaDataEntry( config, "<p><a href=\"content://89\">ABC</a></p>" );
        HtmlAreaDataEntry html4 = new HtmlAreaDataEntry( config, "<p><a href=\"content://89\">ABC</a></p><p><img src=\"image://71\"/>" +
            "  --  <a href=\"content://73\">DEF</a></p>" );

        Set<ContentKey> relatedKeySet1 = html1.resolveRelatedContentKeys();
        Set<ContentKey> relatedKeySet2 = html2.resolveRelatedContentKeys();
        Set<ContentKey> relatedKeySet3 = html3.resolveRelatedContentKeys();
        Set<ContentKey> relatedKeySet4 = html4.resolveRelatedContentKeys();

        assertEquals( 0, relatedKeySet1.size() );
        assertEquals( 0, relatedKeySet2.size() );
        assertEquals( 1, relatedKeySet3.size() );
        assertEquals( 2, relatedKeySet4.size() );
        assertTrue( relatedKeySet3.contains( new ContentKey( 89 ) ) );
        assertTrue( relatedKeySet4.contains( new ContentKey( 89 ) ) );
        assertFalse( relatedKeySet4.contains( new ContentKey( 71 ) ) );
        assertTrue( relatedKeySet4.contains( new ContentKey( 73 ) ) );
    }
}