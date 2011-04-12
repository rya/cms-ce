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

public class LegacyOrderContentData
    extends AbstractBaseLegacyContentData
{
    public LegacyOrderContentData( Document contentDataXml )
    {
        super( contentDataXml );
    }

    protected String resolveTitle()
    {
        final Element nameEl2 = contentDataEl.getChild( "customer" ).getChild( "surname" );
        final Element nameEl1 = contentDataEl.getChild( "customer" ).getChild( "firstname" );
        return nameEl2.getText() + ", " + nameEl1.getText();
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
