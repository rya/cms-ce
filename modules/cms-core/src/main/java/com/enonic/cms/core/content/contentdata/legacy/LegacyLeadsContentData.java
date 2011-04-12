/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.legacy;

import java.util.List;
import java.util.Map;

import com.enonic.cms.core.content.binary.BinaryDataKey;
import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.core.content.binary.BinaryDataAndBinary;

/**
 *
 */
public class LegacyLeadsContentData
    extends AbstractBaseLegacyContentData
{
    public LegacyLeadsContentData( Document contentDataXml )
    {
        super( contentDataXml );
    }

    protected String resolveTitle()
    {
        final Element nameEl = contentDataEl.getChild( "name" );
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
}
