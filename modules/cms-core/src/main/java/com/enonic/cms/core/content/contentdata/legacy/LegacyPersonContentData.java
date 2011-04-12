/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.legacy;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.binary.BinaryDataAndBinary;
import com.enonic.cms.core.content.binary.BinaryDataKey;
import org.jdom.Document;
import org.jdom.Element;

/**
 *
 */
public class LegacyPersonContentData
    extends AbstractBaseLegacyContentData
{
    public LegacyPersonContentData( Document contentDataXml )
    {
        super( contentDataXml );
    }

    protected String resolveTitle()
    {
        final Element nameEl = contentDataEl.getChild( "lastname" );
        return nameEl.getText();
    }

    protected List<BinaryDataAndBinary> resolveBinaryDataAndBinaryList()
    {
        return null;
    }

    public void replaceBinaryKeyPlaceholders( List<BinaryDataKey> binaryDatas )
    {
        // nothing to do for this type
    }

    public void turnBinaryKeysIntoPlaceHolders( Map<BinaryDataKey, Integer> indexByBinaryDataKey )
    {
        // nothing to do for this type
    }

    @Override
    public Set<ContentKey> resolveRelatedContentKeys()
    {
        final Set<ContentKey> contentKeys = new HashSet<ContentKey>();

        contentKeys.addAll( resolveContentKeysByXPath( "/contentdata/image/@key" ) );

        return contentKeys;
    }
}
