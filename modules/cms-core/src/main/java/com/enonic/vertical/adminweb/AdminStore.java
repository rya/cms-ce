/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import javax.servlet.http.HttpSession;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.vertical.adminweb.resource.AdminResources;

public final class AdminStore
{
    public static Source getStylesheet(String languageCode, String path)
    {
        return AdminResources.get().getStylesheet(languageCode, path).getAsSource();
    }

    public static XMLDocument getStylesheetAsDocument(final String languageCode, final String path)
    {
        return AdminResources.get().getStylesheet(languageCode, path);
    }

    public static Source getStylesheet( final HttpSession session, final String path )
    {
        return AdminResources.get().getStylesheet(session, path).getAsSource();
    }

    public static XMLDocument getXml( final HttpSession session, final String name )
    {
        return AdminResources.get().getXml(session, name);
    }

    public static URIResolver getURIResolver( final String languageCode )
    {
        return AdminResources.get().getURIResolver(languageCode);
    }
}
