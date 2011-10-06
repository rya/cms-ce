/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.enonic.cms.core.content.binary.BinaryDataKey;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;

public class BinaryDataEntry
    extends AbstractInputDataEntry
{

    private byte[] binary = null;

    /**
     * NB: Only used when saving.
     */
    private String binaryName;

    private Integer existingBinaryKey;

    private String binaryKeyPlaceholder;

    private boolean hasNullBinaryKey;

    public BinaryDataEntry( DataEntryConfig config )
    {
        super( config, DataEntryType.BINARY );
        this.hasNullBinaryKey = true;
    }

    public BinaryDataEntry( DataEntryConfig config, String binaryKeyPlaceholder )
    {
        super( config, DataEntryType.BINARY );
        this.binaryKeyPlaceholder = binaryKeyPlaceholder;
    }

    public BinaryDataEntry( DataEntryConfig config, String binaryKeyPlaceholder, byte[] binary, String binaryName )
    {
        super( config, DataEntryType.BINARY );
        this.binaryKeyPlaceholder = binaryKeyPlaceholder;
        this.binary = binary;
        this.binaryName = binaryName;
    }

    public BinaryDataEntry( DataEntryConfig config, int existingBinaryKey )
    {
        super( config, DataEntryType.BINARY );
        this.existingBinaryKey = existingBinaryKey;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( !super.equals( o ) )
        {
            return false;
        }

        if ( !( o instanceof BinaryDataEntry ) )
        {
            return false;
        }

        BinaryDataEntry that = (BinaryDataEntry) o;

        if ( this.getBinaryKeyPlaceholder() != null
            ? !this.getBinaryKeyPlaceholder().equals( that.getBinaryKeyPlaceholder() )
            : that.getBinaryKeyPlaceholder() != null )
        {
            return false;
        }

        if ( this.getExistingBinaryKey() != null
            ? !this.getExistingBinaryKey().equals( that.getExistingBinaryKey() )
            : that.getExistingBinaryKey() != null )
        {
            return false;
        }

        if ( this.getBinaryName() != null ? !this.getBinaryName().equals( that.getBinaryName() ) : that.getBinaryName() != null )
        {
            return false;
        }

        if ( this.getBinary() != null ? !Arrays.equals( this.getBinary(), that.getBinary() ) : that.getBinary() != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public void validate()
    {
    }

    public boolean breaksRequiredContract()
    {
        return !hasValue();
    }

    public void setExistingBinaryKey( Integer value )
    {
        this.existingBinaryKey = value;
        this.binaryKeyPlaceholder = null;
    }

    public void setBinaryKeyPlaceholder( String binaryKeyPlaceholder )
    {
        this.binaryKeyPlaceholder = binaryKeyPlaceholder;
    }

    public byte[] getBinary()
    {
        return binary;
    }

    public boolean hasValue()
    {
        final boolean hasNewBinary = binary != null;
        return hasNewBinary || hasExistingBinaryKey() || hasBinaryKeyPlaceholder();
    }

    public String getBinaryName()
    {
        return binaryName;
    }

    public Integer getExistingBinaryKey()
    {
        return existingBinaryKey;
    }

    public String getExistingBinaryKeyAsString()
    {
        return String.valueOf( existingBinaryKey );
    }

    public boolean hasExistingBinaryKey()
    {
        return existingBinaryKey != null;
    }

    public boolean hasBinaryKeyPlaceholder()
    {
        return binaryKeyPlaceholder != null;
    }

    public String getBinaryKeyPlaceholder()
    {
        return binaryKeyPlaceholder;
    }

    public boolean hasNullBinaryKey()
    {
        return hasNullBinaryKey;
    }

    public static List<BinaryDataKey> createBinaryDataKeyList( List<BinaryDataEntry> binaryDataEntries )
    {

        List<BinaryDataKey> keys = new ArrayList<BinaryDataKey>();
        if ( binaryDataEntries == null )
        {
            return keys;
        }

        for ( BinaryDataEntry binaryDataEntry : binaryDataEntries )
        {
            keys.add( new BinaryDataKey( binaryDataEntry.getExistingBinaryKey() ) );
        }

        return keys;
    }
}