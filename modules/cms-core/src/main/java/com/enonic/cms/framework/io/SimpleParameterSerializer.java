/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.io;

import org.apache.commons.codec.binary.Hex;

/**
 * This parameter serializer inverts the value and encodes it with base64 and URL encoding.
 */
public final class SimpleParameterSerializer
    extends AbstractParameterSerializer
{
    public String serialize( String value )
    {
        return encode( value );
    }

    public String deserialize( String value )
    {
        return decode( value );
    }

    private String encode( String value )
    {
        return new String( Hex.encodeHex( value.getBytes() ) );
    }

    private String decode( String value )
    {
        try
        {
            return new String( Hex.decodeHex( value.toCharArray() ) );
        }
        catch ( Exception e )
        {
            return null;
        }
    }
}
