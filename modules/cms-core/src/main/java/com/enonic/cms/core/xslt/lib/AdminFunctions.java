/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.xslt.lib;

import java.net.URLEncoder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.enonic.cms.framework.util.JDOMUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class AdminFunctions
{
    public static String urlEncode(final String uri)
    {
        try {
            return URLEncoder.encode( uri, "UTF-8" );
        } catch (Exception e) {
            throw new IllegalStateException( e );
        }
    }

    public static String serialize(final Node node, final boolean includeSelf)
    {
        if (includeSelf) {
            return JDOMUtil.serialize( JDOMUtil.toDocument( node ), 4, true );
        } else {
            return JDOMUtil.serializeChildren( JDOMUtil.toDocument( node ), 4 );
        }
    }

    public static String random()
    {
        return Double.toString( Math.random() );
    }
}
