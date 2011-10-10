/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata;

import org.jdom.Document;

import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.support.CustomContentDataXmlCreator;
import com.enonic.cms.core.content.contentdata.legacy.AbstractBaseLegacyContentData;


public class ContentDataXmlCreator
{

    public static Document createContentDataDocument( ContentData contentData )
    {
        if ( contentData instanceof CustomContentData )
        {
            return CustomContentDataXmlCreator.createContentDataDocument( (CustomContentData) contentData );
        }
        else
        {
            AbstractBaseLegacyContentData legacyContentData = (AbstractBaseLegacyContentData) contentData;
            final Document doc = legacyContentData.getContentDataXml();
            return (Document) doc.clone();
        }
    }
}
