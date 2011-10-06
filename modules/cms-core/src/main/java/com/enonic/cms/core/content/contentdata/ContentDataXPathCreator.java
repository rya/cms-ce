/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata;

import java.util.StringTokenizer;

import org.jdom.Element;


public class ContentDataXPathCreator
{

    /**
     * Creates given xpath, not reusing existing matching path.
     */
    public static Element createNewPath( final Element parentEl, final String xpath )
    {
        return doCreatePath( parentEl, xpath, true );
    }

    /**
     * Ensures given xpath, resuing existing matching elements.
     */
    public static Element ensurePath( final Element parentEl, final String xpath )
    {
        return doCreatePath( parentEl, xpath, false );
    }

    private static Element doCreatePath( final Element parentEl, final String wantedXPath, final boolean createNew )
    {

        Element curParentEl = parentEl;
        StringTokenizer st = new StringTokenizer( wantedXPath, "/" );
        while ( st.hasMoreTokens() )
        {
            String wantedElementName = st.nextToken();

            Element child = curParentEl.getChild( wantedElementName );
            if ( child == null || ( createNew && !st.hasMoreTokens() ) )
            {
                child = new Element( wantedElementName );
                curParentEl.addContent( child );
            }

            curParentEl = child;
        }

        return curParentEl;
    }
}
