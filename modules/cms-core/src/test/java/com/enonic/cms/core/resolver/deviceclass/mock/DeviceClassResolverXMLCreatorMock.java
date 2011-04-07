/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver.deviceclass.mock;

import com.enonic.cms.framework.xml.XMLBuilder;
import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.resolver.ResolverInputXMLCreator;

import com.enonic.cms.core.resolver.deviceclass.UserAgentTestEnums;

import com.enonic.cms.domain.resolver.ResolverContext;
import com.enonic.cms.domain.resolver.ResolverHttpRequestInputXMLCreator;

/**
 * Created by rmy - Date: Apr 15, 2009
 */
public class DeviceClassResolverXMLCreatorMock
    extends ResolverInputXMLCreator
{
    private UserAgentTestEnums userAgent;

    public DeviceClassResolverXMLCreatorMock( UserAgentTestEnums userAgent )
    {
        this.userAgent = userAgent;
    }

    @Override
    public XMLDocument buildResolverInputXML( ResolverContext context )
    {
        XMLBuilder xmlDoc = new XMLBuilder();

        xmlDoc.startElement( ROOT_ELEMENT_NAME );

        xmlDoc.startElement( ResolverHttpRequestInputXMLCreator.REQUEST_ROOT_ELEMENT_NAME );

        xmlDoc.addContentElement( "user-agent", userAgent.userAgent );

        xmlDoc.endElement();

        xmlDoc.startElement( "user" );

        xmlDoc.endElement();

        xmlDoc.endElement();

        return xmlDoc.getDocument();
    }

}
