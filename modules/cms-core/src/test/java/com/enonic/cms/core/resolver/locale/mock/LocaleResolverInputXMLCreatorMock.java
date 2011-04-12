/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver.locale.mock;

import java.util.Locale;

import com.enonic.cms.core.resolver.ResolverContext;
import com.enonic.cms.core.resolver.ResolverHttpRequestInputXMLCreator;
import com.enonic.cms.framework.xml.XMLBuilder;
import com.enonic.cms.framework.xml.XMLDocument;

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
    public XMLDocument buildResolverInputXML( ResolverContext context )
    {
        XMLBuilder xmlDoc = new XMLBuilder();

        xmlDoc.startElement( ROOT_ELEMENT_NAME );

        xmlDoc.startElement( ResolverHttpRequestInputXMLCreator.REQUEST_ROOT_ELEMENT_NAME );

        xmlDoc.endElement();

        xmlDoc.startElement( "user" );

        xmlDoc.startElement( "block" );

        // Add user-stuff here, when ready, set locale in context
        xmlDoc.addContentElement( "locale", locale.getLanguage() );

        xmlDoc.endElement();

        xmlDoc.endElement();

        xmlDoc.endElement();

        return xmlDoc.getDocument();
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
