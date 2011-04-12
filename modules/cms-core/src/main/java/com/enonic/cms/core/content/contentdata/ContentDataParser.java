/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata;

import java.util.List;

import org.jdom.Document;

import com.enonic.cms.core.content.binary.BinaryDataKey;
import com.enonic.cms.core.content.contentdata.custom.support.CustomContentDataXmlParser;
import com.enonic.cms.core.content.contentdata.legacy.support.ArticleContentDataParser;
import com.enonic.cms.core.content.contentdata.legacy.support.CatalogContentDataParser;
import com.enonic.cms.core.content.contentdata.legacy.support.DiscussionContentDataParser;
import com.enonic.cms.core.content.contentdata.legacy.support.DocumentContentDataParser;
import com.enonic.cms.core.content.contentdata.legacy.support.FileContentDataParser;
import com.enonic.cms.core.content.contentdata.legacy.support.FormContentDataParser;
import com.enonic.cms.core.content.contentdata.legacy.support.ImageContentDataParser;
import com.enonic.cms.core.content.contentdata.legacy.support.LeadsContentDataParser;
import com.enonic.cms.core.content.contentdata.legacy.support.NewsletterContentDataParser;
import com.enonic.cms.core.content.contentdata.legacy.support.OrderContentDataParser;
import com.enonic.cms.core.content.contentdata.legacy.support.PersonContentDataParser;
import com.enonic.cms.core.content.contentdata.legacy.support.PollContentDataParser;
import com.enonic.cms.core.content.contentdata.legacy.support.ProductContentDataParser;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;


public class ContentDataParser
{
    public static ContentData parse( final Document contentDataXmlDoc, final ContentTypeEntity contentType,
                                     final List<BinaryDataKey> binaryDatas )
    {
        ContentData contentData;

        switch ( contentType.getContentHandlerName() )
        {

            case CUSTOM:
                ContentTypeConfig contentTypeConfig = contentType.getContentTypeConfig();
                contentData = CustomContentDataXmlParser.parse( contentDataXmlDoc, contentTypeConfig, binaryDatas );
                break;

            case IMAGE:
                contentData = ImageContentDataParser.parse( contentDataXmlDoc, binaryDatas );
                break;

            case FILE:
                contentData = FileContentDataParser.parse( contentDataXmlDoc, binaryDatas );
                break;

            case ARTICLE:
                contentData = ArticleContentDataParser.parse( contentDataXmlDoc, binaryDatas );
                break;

            case CATALOG:
                contentData = CatalogContentDataParser.parse( contentDataXmlDoc, binaryDatas );
                break;

            case DISCUSSION:
                contentData = DiscussionContentDataParser.parse( contentDataXmlDoc, binaryDatas );
                break;

            case DOCUMENT:
                contentData = DocumentContentDataParser.parse( contentDataXmlDoc, binaryDatas );
                break;

            case FORM:
                contentData = FormContentDataParser.parse( contentDataXmlDoc, binaryDatas );
                break;

            case LEADS:
                contentData = LeadsContentDataParser.parse( contentDataXmlDoc, binaryDatas );
                break;

            case NEWSLETTER:
                contentData = NewsletterContentDataParser.parse( contentDataXmlDoc, binaryDatas );
                break;

            case ORDER:
                contentData = OrderContentDataParser.parse( contentDataXmlDoc, binaryDatas );
                break;

            case PERSON:
                contentData = PersonContentDataParser.parse( contentDataXmlDoc, binaryDatas );
                break;

            case POLL:
                contentData = PollContentDataParser.parse( contentDataXmlDoc, binaryDatas );
                break;

            case PRODUCT:
                contentData = ProductContentDataParser.parse( contentDataXmlDoc, binaryDatas );
                break;

            default:
                throw new IllegalArgumentException( "Content handler not supported: " + contentType.getContentHandlerName() );

        }

        return contentData;
    }
}
