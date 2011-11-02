/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image.param;

import org.apache.commons.codec.binary.Hex;

/**
 * This parameter serializer inverts the value and encodes it with base64 and URL encoding.
 */
public final class EncryptedParameterSerializer
    extends AbstractParameterSerializer
{
    public String serialize( String value )
    {
        return encode( reverse( value ) );
    }

    public String deserialize( String value )
    {
        return reverse( decode( value ) );
    }

    private String reverse( String value )
    {
        int i, len = value.length();
        StringBuffer dest = new StringBuffer( len );

        for ( i = ( len - 1 ); i >= 0; i-- )
        {
            dest.append( value.charAt( i ) );
        }

        return dest.toString();
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