/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.contentkeybased;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.contentdata.custom.AbstractInputDataEntry;
import com.enonic.cms.core.content.contentdata.custom.DataEntryType;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;


public abstract class AbstractContentKeyBasedInputDataEntry
    extends AbstractInputDataEntry
{
    protected ContentKey contentKey;

    public AbstractContentKeyBasedInputDataEntry( DataEntryConfig config, DataEntryType type, ContentKey contentKey )
    {
        super( config, type );
        this.contentKey = contentKey;
    }

    public ContentKey getContentKey()
    {
        return contentKey;
    }

    public boolean hasValue()
    {
        return contentKey != null;
    }

    protected abstract void customValidate();

    /**
     * {@inheritDoc}
     *
     * @see com.enonic.cms.core.content.contentdata.custom.AbstractDataEntry#validate()
     */
    @Override
    public final void validate()
    {
        customValidate();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof AbstractContentKeyBasedInputDataEntry ) )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }

        AbstractContentKeyBasedInputDataEntry that = (AbstractContentKeyBasedInputDataEntry) o;

        if ( contentKey != null ? !contentKey.equals( that.contentKey ) : that.contentKey != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder( 651, 321 ).appendSuper( super.hashCode() ).append( contentKey ).toHashCode();
    }
}
