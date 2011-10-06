/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype.dataentryconfig;

import java.util.List;

public class RelatedContentDataEntryConfig
    extends AbstractBaseDataEntryConfig
{
    private boolean multiple;

    private List<String> contentTypeNames;

    public RelatedContentDataEntryConfig( String name, boolean required, String displayName, String xpath, boolean multiple,
                                          List<String> contentTypeNames )
    {
        super( name, required, DataEntryConfigType.RELATEDCONTENT, displayName, xpath );
        this.multiple = multiple;
        this.contentTypeNames = contentTypeNames;


    }

    public boolean isMultiple()
    {
        return multiple;
    }

    public List<String> getContentTypeNames()
    {
        return contentTypeNames;
    }

    public boolean hasContentTypeRestrictions()
    {
        return contentTypeNames != null && !contentTypeNames.isEmpty();
    }

    public boolean isContentTypeNameSupported( String contentTypeName )
    {
        if ( !hasContentTypeRestrictions() )
        {
            return true;
        }

        return contentTypeNames.contains( contentTypeName );
    }


}
