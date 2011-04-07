/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver.locale;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentImpl;

import com.enonic.cms.core.xslt.XsltProcessorManagerAccessor;
import com.enonic.cms.core.xslt.saxon.SaxonProcessorManager;

import com.enonic.cms.core.resolver.locale.mock.LocaleResolverInputXMLCreatorMock;

import com.enonic.cms.domain.resolver.ResolverContext;
import com.enonic.cms.domain.resolver.ScriptResolverResult;
import com.enonic.cms.domain.resource.ResourceFile;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.*;

/**
 * Created by rmy - Date: Aug 25, 2009
 */
public class LocaleXsltScriptResolverTest
{

    private MockHttpServletRequest request;

    private LocaleXsltScriptResolver localeXsltScriptResolver;

    private ResourceFile resourceFile;

    private LocaleResolverInputXMLCreatorMock localeResolverInputInputXMLCreator;

    @Before
    public void setUp()
    {
        resourceFile = createMock( ResourceFile.class );

        request = new MockHttpServletRequest();
        request.setRequestURI( "/site/0/Frontpage" );

        localeXsltScriptResolver = new LocaleXsltScriptResolver();
    }


    @Test
    public void testScriptResolvedLocale()
    {
        setupResourceAndXsltManager();

        String testLanguageCode = "sz";

        localeResolverInputInputXMLCreator = new LocaleResolverInputXMLCreatorMock( testLanguageCode );
        localeXsltScriptResolver.setResolverInputXMLCreator( localeResolverInputInputXMLCreator );

        ResolverContext context = new ResolverContext( null, null );

        ScriptResolverResult result = null;

        try
        {
            result = localeXsltScriptResolver.resolveValue( context, resourceFile );
        }
        catch ( Exception e )
        {
            fail( "XSLT-processing failed, probably an error in script" );
        }

        Locale locale = (Locale) result.getResolverReturnValues().get( LocaleXsltScriptResolver.LOCALE_RETURN_VALUE_KEY );

        assertNotNull( "Locale should be set in script", locale );
        assertEquals( "Locale should be resolved from script", testLanguageCode, locale.getLanguage() );

    }

    private void setupResourceAndXsltManager()
    {
        expect( resourceFile.getDataAsXml() ).andReturn( createXMLDocument() ).anyTimes();
        expect( resourceFile.getPath() ).andReturn( "scriptpath" ).anyTimes();
        replay( resourceFile );

        SaxonProcessorManager xsltProcessorManager = new SaxonProcessorManager();
        XsltProcessorManagerAccessor.setProcessorManager( xsltProcessorManager );
    }

    private XMLDocument createXMLDocument()
    {
        XMLDocument xmlDoc = new XMLDocumentImpl( createLocaleResolverXslt() );
        return xmlDoc;
    }

    private String createLocaleResolverXslt()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append( "<?xml version=\"1.0\" encoding=\"utf-8\"?>" );
        buffer.append( "<xsl:stylesheet exclude-result-prefixes=\"xs portal\" version=\"2.0\" xmlns=\"http://www.w3.org/1999/xhtml\"" );
        buffer.append( " xmlns:portal=\"http://www.enonic.com/cms/xslt/portal\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"" );
        buffer.append( " xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">" );
        buffer.append( "<xsl:output indent=\"no\" media-type=\"text\" method=\"text\" omit-xml-declaration=\"yes\"/>" );
        buffer.append( "<xsl:template match=\"/\">" );
        buffer.append( " <xsl:variable name=\"userLocale\" select=\"/context/user/block/locale\"/>" );
        buffer.append( "   <xsl:choose>" );
        buffer.append( "       <xsl:when test=\"$userLocale != ''\"><xsl:value-of select=\"$userLocale\"/></xsl:when>" );
        buffer.append( "    <xsl:otherwise>default</xsl:otherwise>" );
        buffer.append( " </xsl:choose>" );
        buffer.append( "</xsl:template>" );
        buffer.append( "</xsl:stylesheet>" );

        return buffer.toString();
    }
}
