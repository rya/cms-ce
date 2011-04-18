/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver.locale.mock;

import java.util.Locale;

import org.jdom.Document;

import com.enonic.cms.framework.xml.XMLBuilder;

import com.enonic.cms.core.resolver.ResolverContext;
import com.enonic.cms.core.resolver.ResolverHttpRequestInputXMLCreator;
import com.enonic.cms.core.resolver.ResolverInputXMLCreator;

/**
 * Created by rmy - Date: Aug 25, 2009
 */
public class LocaleResolverInputXMLCreatorMock
    extends ResolverInputXMLCreator
{

    private Locale locale;

    public LocaleResolverInputXMLCreatorMock( String language )
    {

        this.locale = new Locale( language );
    }

    @Override
    public Document buildResolverInputXML( ResolverContext context )
    {
        XMLBuilder builder = new XMLBuilder();

        builder.startElement( ROOT_ELEMENT_NAME );

        builder.startElement( ResolverHttpRequestInputXMLCreator.REQUEST_ROOT_ELEMENT_NAME );

        builder.endElement();

        builder.startElement( "user" );

        builder.startElement( "block" );

        // Add user-stuff here, when ready, set locale in context
        builder.addContentElement( "locale", locale.getLanguage() );

        builder.endElement();

        builder.endElement();

        builder.endElement();

        return builder.getDocument();
    }

    public Locale getLocale()
    {
        return locale;
    }

    public void setLocale( Locale locale )
    {
        this.locale = locale;
    }


}
