/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.content;

import java.io.Serializable;

public class BinaryInput
    extends AbstractInput
    implements Serializable
{
    private static final long serialVersionUID = -4565733997868322286L;

    private byte[] binary;

    private String binaryName;

    private Integer existingBinaryKey;

    /**
     * @param name
     * @param existingBinaryKey If you supply null as input value, the existing value will be removed in a 'replace new' scenario.
     */
    public BinaryInput( String name, Integer existingBinaryKey )
    {
        super( InputType.BINARY, name );
        this.existingBinaryKey = existingBinaryKey;
    }

    /**
     * @param name
     * @param binary
     * @param binaryName
     */
    public BinaryInput( String name, byte[] binary, String binaryName )
    {
        super( InputType.BINARY, name );
        this.binary = binary;
        this.binaryName = binaryName;
    }

    public byte[] getBinary()
    {
        return binary;
    }

    public String getBinaryName()
    {
        return binaryName;
    }

    public int getBinarySize()
    {
        if ( binary == null )
        {
            return 0;
        }
        else
        {
            return binary.length;
        }
    }

    public Integer getExistingBinaryKey()
    {
        return existingBinaryKey;
    }

    public boolean hasExistingBinaryKey()
    {
        return existingBinaryKey != null;
    }
}