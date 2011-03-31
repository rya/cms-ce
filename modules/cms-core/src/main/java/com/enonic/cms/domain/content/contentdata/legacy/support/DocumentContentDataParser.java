/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content.contentdata.legacy.support;

import java.util.List;

import org.jdom.Document;

import com.enonic.cms.domain.content.binary.BinaryDataKey;
import com.enonic.cms.domain.content.contentdata.ContentData;
import com.enonic.cms.domain.content.contentdata.legacy.LegacyDocumentContentData;

/**
 *
 */
public class DocumentContentDataParser
{
    private Document contentDataXml;

    private List<BinaryDataKey> binaryDatas;


    @SuppressWarnings({"unchecked"})
    public static ContentData parse( Document contentDataXml, final List<BinaryDataKey> binaryDatas )
    {
        DocumentContentDataParser parser = new DocumentContentDataParser( contentDataXml );
        parser.binaryDatas = binaryDatas;
        return parser.parse();
    }

    public DocumentContentDataParser( Document contentDataXml )
    {
        if ( contentDataXml == null )
        {
            throw new IllegalArgumentException( "Given contentDataXml cannot be null" );
        }

        this.contentDataXml = contentDataXml;
    }

    public ContentData parse()
    {
        LegacyDocumentContentData contentData = new LegacyDocumentContentData( contentDataXml );
        contentData.replaceBinaryKeyPlaceholders( binaryDatas );
        return contentData;
    }
}
