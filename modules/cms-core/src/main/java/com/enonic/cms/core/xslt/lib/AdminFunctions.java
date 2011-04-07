/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.xslt.lib;

import java.net.URLEncoder;
import java.util.List;

import org.jdom.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.enonic.cms.framework.util.JDOMUtil;

public final class AdminFunctions
{
    private static final Logger LOG = LoggerFactory.getLogger( PortalFunctions.class );

    public static String urlEncode(final String uri)
    {
        try {
            return URLEncoder.encode( uri, "UTF-8" );
        } catch (Exception e) {
            throw new IllegalStateException( e );
        }
    }

    public static String serialize( final Node node, final boolean includeSelf )
    {
        org.jdom.Document doc = JDOMUtil.toDocument( node );

        recursiveRemoveNamespaces( doc.getRootElement() );

        String serialize = includeSelf ? JDOMUtil.serialize( doc, 4, true ) : JDOMUtil.serializeChildren( doc, 4 );

        LOG.debug( "xml came in:\n***\n{}\n***", serialize );

        return serialize;
    }

    private static void recursiveRemoveNamespaces( org.jdom.Element element )
    {
        List<Namespace> additionalNamespaces = element.getAdditionalNamespaces();

        // prevent concurrent modification (iterator will not work correctly)
        for ( int i = additionalNamespaces.size(); i-- > 0; )
        {
            element.removeNamespaceDeclaration( additionalNamespaces.get( 0 ) );
        }

        for ( Object content : element.getContent() )
        {
            if ( content instanceof org.jdom.Element )
            {
                recursiveRemoveNamespaces( org.jdom.Element.class.cast( content ) );
            }
        }
    }

    public static String random()
    {
        return Double.toString( Math.random() );
    }
}
