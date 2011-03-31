/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class implements the digest utilities.
 */
public final class DigestUtil
{
    /**
     * Private constructor.
     */
    private DigestUtil()
    {
    }

    /**
     * Generate MD5 digest string.
     */
    public static String generateMD5( String value )
    {
        return StringUtil.toHex( generateMD5Bytes( value ) );
    }

    /**
     * Generate MD5 digest string.
     */
    public static byte[] generateMD5Bytes( byte[] bytes )
    {
        MessageDigest digest = getMD5Instance();
        return digest.digest( bytes );

    }

    /**
     * Generate MD5 digest string.
     */
    public static byte[] generateMD5Bytes( String value )
    {
        return generateMD5Bytes( value.getBytes() );
    }

    /**
     * Generate SHA digest string.
     */
    public static String generateSHA( byte[] bytes )
    {
        return StringUtil.toHex( generateSHABytes( bytes ) );
    }

    /**
     * Generate MD5 digest string.
     */
    public static String generateSHA( String value )
    {
        return StringUtil.toHex( generateSHABytes( value ) );
    }

    /**
     * Generate SHA digest string.
     */
    public static byte[] generateSHABytes( byte[] bytes )
    {
        MessageDigest digest = getSHAInstance();
        return digest.digest( bytes );
    }

    /**
     * Generate SHA digest string.
     */
    public static byte[] generateSHABytes( String value )
    {
        return generateSHABytes( value.getBytes() );
    }

    /**
     * Return the MD5 instance.
     */
    private static MessageDigest getMD5Instance()
    {
        try
        {
            return MessageDigest.getInstance( "MD5" );
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new IllegalStateException( e.getMessage() );
        }
    }

    /**
     * Return the SHA instance.
     */
    private static MessageDigest getSHAInstance()
    {
        try
        {
            return MessageDigest.getInstance( "SHA" );
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new IllegalStateException( e.getMessage() );
        }
    }

}
