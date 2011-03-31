/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.processors;

import org.w3c.dom.Element;

import com.enonic.vertical.engine.handlers.ContentHandler;

import com.enonic.cms.framework.util.TIntObjectHashMap;

public class VersionKeyContentMapProcessor
    implements ElementProcessor
{
    private ContentHandler contentHandler;

    private TIntObjectHashMap versionKeyContentMap = new TIntObjectHashMap();

    public VersionKeyContentMapProcessor( ContentHandler contentHandler )
    {
        this.contentHandler = contentHandler;
    }

    public void process( Element elem )
    {
        int contentKey = Integer.parseInt( elem.getAttribute( "key" ) );
        int versionKey;
        String versionKeyStr = elem.getAttribute( "versionkey" );
        if ( versionKeyStr.length() > 0 )
        {
            versionKey = Integer.parseInt( versionKeyStr );
        }
        else
        {
            versionKey = contentHandler.getCurrentVersionKey( contentKey );
        }
        versionKeyContentMap.put( versionKey, elem );
    }

    /**
     * @return Returns the versionKeyContentMap.
     */
    public TIntObjectHashMap getVersionKeyContentMap()
    {
        return versionKeyContentMap;
    }
}
