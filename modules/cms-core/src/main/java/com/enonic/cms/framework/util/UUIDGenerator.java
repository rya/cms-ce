/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.UUID;

import org.apache.commons.codec.binary.Hex;

/**
 * This class generates a compact uuid.
 */
public final class UUIDGenerator
{
    /**
     * Generate random uuid.
     */
    public static String randomUUID()
    {
        UUID uuid = UUID.randomUUID();

        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DataOutputStream dataOut = new DataOutputStream( out );
            dataOut.writeLong( uuid.getMostSignificantBits() );
            dataOut.writeLong( uuid.getLeastSignificantBits() );
            dataOut.close();
            return new String( Hex.encodeHex( out.toByteArray() ) );
        }
        catch ( Exception e )
        {
            return uuid.toString();
        }
    }
}
