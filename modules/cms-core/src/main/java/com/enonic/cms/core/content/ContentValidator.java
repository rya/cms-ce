/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.store.dao.ContentDao;

import com.enonic.cms.core.content.contentdata.ContentData;
import com.enonic.cms.core.content.contentdata.InvalidContentDataException;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.DataEntry;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.RelatedContentDataEntry;
import com.enonic.cms.core.content.contentdata.custom.relationdataentrylistbased.RelatedContentsDataEntry;
import com.enonic.cms.core.content.contenttype.dataentryconfig.RelatedContentDataEntryConfig;

public class ContentValidator
{
    @Autowired
    private ContentDao contentDao;

    public void validate( ContentData contentData )
    {
        if ( contentData == null )
        {
            throw new IllegalArgumentException( "Given contentdata cannot be null" );
        }

        if ( contentData instanceof CustomContentData )
        {
            CustomContentData customContentData = (CustomContentData) contentData;
            customContentData.validate();

            for ( DataEntry dataEntry : customContentData.getEntries() )
            {
                if ( dataEntry instanceof RelatedContentDataEntry )
                {
                    validateRelatedContent( (RelatedContentDataEntry) dataEntry );
                }
                else if ( dataEntry instanceof RelatedContentsDataEntry )
                {
                    validateRelatedContents( (RelatedContentsDataEntry) dataEntry );
                }

            }
        }
    }

    private void validateRelatedContents( RelatedContentsDataEntry relatedContents )
    {
        for ( RelatedContentDataEntry dataEntry : relatedContents.getEntries() )
        {
            validateRelatedContent( dataEntry );
        }
    }

    private void validateRelatedContent( RelatedContentDataEntry relatedContentDataEntry )
    {
        ContentKey relatedContentKey = relatedContentDataEntry.getContentKey();
        if ( relatedContentKey == null )
        {
            return; // A null content key is a valid content key
        }
        ContentEntity relatedContent = contentDao.findByKey( relatedContentKey );
        if ( relatedContent == null )
        {
            throw new InvalidContentDataException( "Related content does not exist: " + relatedContentKey );
        }

        RelatedContentDataEntryConfig relatedConfig = (RelatedContentDataEntryConfig) relatedContentDataEntry.getConfig();

        if ( !relatedConfig.isContentTypeNameSupported( relatedContent.getContentType().getName() ) )
        {
            throw new InvalidContentDataException(
                "Related data's config type is not equals to the configuration. Illegal relation to content: " + relatedContentKey );
        }
    }
}
