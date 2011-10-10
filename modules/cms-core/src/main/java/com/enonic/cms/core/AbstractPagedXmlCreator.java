/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

public abstract class AbstractPagedXmlCreator
{

    public Document createPagedDocument( List objects, int index, int count )
    {
        Document doc = new Document();
        Element root = new Element( getRootName() );

        int totalCount = objects.size();

        index = Math.min( index, totalCount );
        count = Math.min( count, totalCount - index );

        List subset = objects.subList( index, index + count );
        for ( Object object : subset )
        {
            Element elem = createElement( object );
            root.addContent( elem );
        }

        root.setAttribute( "index", String.valueOf( index ) );
        root.setAttribute( "count", String.valueOf( count ) );
        root.setAttribute( "totalCount", String.valueOf( totalCount ) );
        doc.setRootElement( root );
        return doc;
    }

    public abstract String getRootName();

    public abstract Element createElement( Object obj );

}
