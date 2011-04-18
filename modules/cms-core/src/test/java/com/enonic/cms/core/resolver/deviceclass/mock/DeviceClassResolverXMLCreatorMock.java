/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver.deviceclass.mock;

import org.jdom.Document;

import com.enonic.cms.framework.xml.XMLBuilder;

import com.enonic.cms.core.resolver.ResolverContext;
import com.enonic.cms.core.resolver.ResolverHttpRequestInputXMLCreator;
import com.enonic.cms.core.resolver.ResolverInputXMLCreator;
import com.enonic.cms.core.resolver.deviceclass.UserAgentTestEnums;

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
    public Document buildResolverInputXML( ResolverContext context )
    {
        XMLBuilder builder = new XMLBuilder();

        builder.startElement( ROOT_ELEMENT_NAME );

        builder.startElement( ResolverHttpRequestInputXMLCreator.REQUEST_ROOT_ELEMENT_NAME );

        builder.addContentElement( "user-agent", userAgent.userAgent );

        builder.endElement();

        builder.startElement( "user" );

        builder.endElement();

        builder.endElement();

        return builder.getDocument();
    }

}
