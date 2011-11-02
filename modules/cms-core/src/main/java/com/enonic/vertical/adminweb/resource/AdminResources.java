package com.enonic.vertical.adminweb.resource;

import com.enonic.cms.framework.xml.XMLDocument;
import javax.servlet.http.HttpSession;
import javax.xml.transform.URIResolver;

public abstract class AdminResources
{
    private static AdminResources INSTANCE;

    public AdminResources()
    {
        INSTANCE = this;
    }

    public final XMLDocument getStylesheet(final HttpSession session, final String path)
    {
        return getStylesheet(getLanguageCode(session), path);
    }

    public final XMLDocument getXml(final HttpSession session, final String name)
    {
        return getXml(getLanguageCode(session), name);
    }

    private String getLanguageCode(final HttpSession session)
    {
        return (String)session.getAttribute( "languageCode" );
    }

    public abstract XMLDocument getStylesheet(final String languageCode, final String path);

    public abstract URIResolver getURIResolver(final String languageCode);

    public abstract XMLDocument getXml(final String languageCode, final String name);

    public static AdminResources get()
    {
        return INSTANCE;
    }
}
