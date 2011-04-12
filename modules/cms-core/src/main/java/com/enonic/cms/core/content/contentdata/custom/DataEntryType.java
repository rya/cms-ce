/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;

import com.enonic.cms.core.content.contentdata.custom.contentkeybased.FileDataEntry;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.ImageDataEntry;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.RelatedContentDataEntry;
import com.enonic.cms.core.content.contentdata.custom.relationdataentrylistbased.FilesDataEntry;
import com.enonic.cms.core.content.contentdata.custom.relationdataentrylistbased.ImagesDataEntry;
import com.enonic.cms.core.content.contentdata.custom.relationdataentrylistbased.RelatedContentsDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.HtmlAreaDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.SelectorDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextAreaDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.UrlDataEntry;
import com.enonic.cms.core.content.contentdata.custom.xmlbased.XmlDataEntry;


public enum DataEntryType
{
    BINARY( BinaryDataEntry.class ),
    BOOLEAN( BooleanDataEntry.class ),
    DATE( DateDataEntry.class ),
    FILE( FileDataEntry.class ),
    FILES( FilesDataEntry.class ),
    GROUP( GroupDataEntry.class ),
    HTML_AREA( HtmlAreaDataEntry.class ),
    IMAGE( ImageDataEntry.class ),
    IMAGES( ImagesDataEntry.class ),
    KEYWORDS( KeywordsDataEntry.class ),
    MULTIPLE_CHOICE( MultipleChoiceDataEntry.class ),
    RELATED_CONTENT( RelatedContentDataEntry.class ),
    RELATED_CONTENTS( RelatedContentsDataEntry.class ),
    SELECTOR( SelectorDataEntry.class ),
    TEXT_AREA( TextAreaDataEntry.class ),
    TEXT( TextDataEntry.class ),
    URL( UrlDataEntry.class ),
    XML( XmlDataEntry.class );

    private Class dataEntryClass;

    DataEntryType( Class dataEntryClass )
    {
        this.dataEntryClass = dataEntryClass;
    }

    public void verifyClass( DataEntry entry )
    {
        if ( !entry.getClass().isAssignableFrom( dataEntryClass ) )
        {
            throw new IllegalArgumentException(
                "Input '" + entry.getName() + "' was not of expected class " + dataEntryClass.getName() + ", was " +
                    entry.getClass().getName() );
        }
    }

}
