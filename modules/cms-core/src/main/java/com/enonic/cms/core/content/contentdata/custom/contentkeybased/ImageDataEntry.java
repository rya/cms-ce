/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.contentkeybased;

import com.enonic.cms.core.content.ContentKey;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.core.content.contentdata.custom.DataEntryType;
import com.enonic.cms.core.content.contentdata.custom.RelationDataEntry;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;

public class ImageDataEntry
    extends AbstractContentKeyBasedInputDataEntry
    implements RelationDataEntry
{
    private String imageText;

    public ImageDataEntry( DataEntryConfig config, ContentKey contentKey )
    {
        super( config, DataEntryType.IMAGE, contentKey );
    }

    public ImageDataEntry( DataEntryConfig config, ContentKey contentKey, String imageText )
    {
        super( config, DataEntryType.IMAGE, contentKey );
        this.imageText = imageText;
    }

    protected void customValidate()
    {
        //Validation not implemented
    }

    public boolean breaksRequiredContract()
    {
        return contentKey == null;
    }


    public String getImageText()
    {
        return imageText;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }

        ImageDataEntry that = (ImageDataEntry) o;

        if ( imageText != null ? !imageText.equals( that.imageText ) : that.imageText != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder( 429, 179 ).appendSuper( super.hashCode() ).append( imageText ).toHashCode();
    }
}