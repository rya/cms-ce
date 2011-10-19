/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.util;

import org.apache.commons.codec.binary.Base64;

public final class Base64Util
{
    public static String encode( byte[] data )
    {
        return new String(Base64.encodeBase64(data));
    }

    public static byte[] decode( String data )
    {
        return Base64.decodeBase64(data.getBytes());
    }
}
