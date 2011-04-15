package com.enonic.cms.core.plugin.spring;

import org.osgi.framework.Bundle;
import org.springframework.osgi.context.support.OsgiBundleXmlApplicationContext;

final class XmlAppContext
    extends OsgiBundleXmlApplicationContext
{
    public XmlAppContext( final Bundle bundle, final String... locations )
    {
        setConfigLocations( locations );
        setBundleContext( bundle.getBundleContext() );
        setPublishContextAsService( false );
        setClassLoader( new BridgeClassLoader( getClass().getClassLoader(), bundle ) );
    }
}
