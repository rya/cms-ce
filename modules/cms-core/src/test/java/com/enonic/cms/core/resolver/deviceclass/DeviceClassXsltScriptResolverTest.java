/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver.deviceclass;

import com.enonic.cms.core.resolver.ResolverContext;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentImpl;

import com.enonic.cms.core.resolver.deviceclass.mock.DeviceClassResolverXMLCreatorMock;
import com.enonic.cms.core.xslt.XsltProcessorManagerAccessor;
import com.enonic.cms.core.xslt.saxon.SaxonProcessorManager;

import com.enonic.cms.core.resolver.ScriptResolverResult;
import com.enonic.cms.core.resource.ResourceFile;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.*;

/**
 * Created by rmy - Date: Apr 14, 2009
 */
public class DeviceClassXsltScriptResolverTest
{
    private MockHttpServletRequest request;

    private DeviceClassXsltScriptResolver deviceClassXsltScriptResolver;

    private ResourceFile resourceFile;

    private DeviceClassResolverXMLCreatorMock deviceClassResolverXMLCreator;

    @Before
    public void setUp()
    {
        resourceFile = createMock( ResourceFile.class );

        request = new MockHttpServletRequest();
        request.setRequestURI( "/site/0/Frontpage" );

        deviceClassXsltScriptResolver = new DeviceClassXsltScriptResolver();
    }

    @Test
    public void testScriptResolvedDeviceClassIphone()
    {
        setupResourceAndXsltManager();

        deviceClassResolverXMLCreator = new DeviceClassResolverXMLCreatorMock( UserAgentTestEnums.IPHONE );
        deviceClassXsltScriptResolver.setResolverInputXMLCreator( deviceClassResolverXMLCreator );

        ResolverContext context = new ResolverContext( request, null );

        ScriptResolverResult result = deviceClassXsltScriptResolver.resolveValue( context, resourceFile );
        String deviceClass = (String) result.getResolverReturnValues().get( DeviceClassXsltScriptResolver.DEVICE_CLASS_RETURN_VALUE_KEY );
        assertEquals( "DeviceClass should be resolved from script", "iphone", deviceClass );
    }

    @Test
    public void testScriptResolvedDeviceClassAndroid()
    {
        setupResourceAndXsltManager();

        deviceClassResolverXMLCreator = new DeviceClassResolverXMLCreatorMock( UserAgentTestEnums.ANDROID );
        deviceClassXsltScriptResolver.setResolverInputXMLCreator( deviceClassResolverXMLCreator );

        ResolverContext context = new ResolverContext( request, null );

        ScriptResolverResult result = deviceClassXsltScriptResolver.resolveValue( context, resourceFile );
        String deviceClass = (String) result.getResolverReturnValues().get( DeviceClassXsltScriptResolver.DEVICE_CLASS_RETURN_VALUE_KEY );
        assertEquals( "DeviceClass should be resolved from script", "android", deviceClass );
    }

    @Test
    public void testScriptResolvedDeviceClassScreen()
    {
        setupResourceAndXsltManager();

        deviceClassResolverXMLCreator = new DeviceClassResolverXMLCreatorMock( UserAgentTestEnums.FIREFOX3 );
        deviceClassXsltScriptResolver.setResolverInputXMLCreator( deviceClassResolverXMLCreator );

        ResolverContext context = new ResolverContext( request, null );

        ScriptResolverResult result = deviceClassXsltScriptResolver.resolveValue( context, resourceFile );
        String deviceClass = (String) result.getResolverReturnValues().get( DeviceClassXsltScriptResolver.DEVICE_CLASS_RETURN_VALUE_KEY );
        assertEquals( "DeviceClass should be resolved from script", "screen", deviceClass );
    }

    private void setupResourceAndXsltManager()
    {
        expect( resourceFile.getDataAsXml() ).andReturn( createXMLDocument() ).anyTimes();
        replay( resourceFile );

        SaxonProcessorManager xsltProcessorManager = new SaxonProcessorManager();
        XsltProcessorManagerAccessor.setProcessorManager( xsltProcessorManager );
    }

    private XMLDocument createXMLDocument()
    {
        XMLDocument xmlDoc = new XMLDocumentImpl( createDeviceClassResolverXslt() );
        return xmlDoc;
    }

    private String createDeviceClassResolverXslt()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append( "<?xml version=\"1.0\" encoding=\"utf-8\"?>" );
        buffer.append( "<xsl:stylesheet exclude-result-prefixes=\"xs portal\" version=\"2.0\" xmlns=\"http://www.w3.org/1999/xhtml\"" );
        buffer.append( " xmlns:portal=\"http://www.enonic.com/cms/xslt/portal\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"" );
        buffer.append( " xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">" );
        buffer.append( "<xsl:output indent=\"no\" media-type=\"text\" method=\"text\" omit-xml-declaration=\"yes\"/>" );
        buffer.append( "<xsl:template match=\"/\">" );
        buffer.append( "<xsl:variable name=\"userAgent\" select=\"lower-case(/context/request/user-agent)\"/>" + "<xsl:choose>" );
        buffer.append( "<xsl:when test='matches($userAgent, \"iphone\")'>" + "iphone" + " </xsl:when>" );
        buffer.append( "<xsl:when test='matches($userAgent, \"android\")'>" + "android" + " </xsl:when>" );
        buffer.append( "<xsl:when test='matches($userAgent, \"opera mini\")'>" + "operamini" + " </xsl:when>" );
        buffer.append( "<xsl:when test='matches($userAgent, \"blackberry\")'>" + "blackberry" + " </xsl:when>" );
        buffer.append(
            "<xsl:when test='matches($userAgent, \"palm os|palm|hiptop|avantgo|plucker|xiino|blazer|elaine\")'> palm" + " </xsl:when>" );
        buffer.append( "<xsl:when test='matches($userAgent, \"windows ce; ppc;|windows ce; smartphone;|windows ce; iemobile\")'>" );
        buffer.append( "       windowsmobile" + "    </xsl:when>" );
        buffer.append(
            "<xsl:when test='matches($userAgent, \"up.browser|up.link|mmp|symbian|smartphone|midp|wap|vodafone|o2|pocket|kindle|mobile|pda|psp|treo\")'>" );
        buffer.append( "  generichandheld" + "</xsl:when>" + "<xsl:otherwise>" + "screen" );
        buffer.append( "</xsl:otherwise>" + "</xsl:choose>" + "</xsl:template>" + "</xsl:stylesheet>" + "" );

        return buffer.toString();
    }


}
