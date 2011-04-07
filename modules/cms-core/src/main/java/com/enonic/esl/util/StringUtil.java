/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.StringTokenizer;

public final class StringUtil
{
    /**
     * Hex characters.
     */
    private final static char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

    // Extended Control Characters (used by Windows for special characters)

    private static final int ECC_START = 0x80; // 128

    private static final int ECC_END = 0x9f; // 159

    private static final String[] ECC_MAP;

    static
    {
        ECC_MAP = new String[32]; // hex  (int)
        ECC_MAP[2] = "&lsquor;"; // 0x82 (130)
        ECC_MAP[3] = "&fnof;"; // 0x83 (131)
        ECC_MAP[4] = "&ldquor;"; // 0x84 (132)
        ECC_MAP[5] = "&ldots;"; // 0x85 (133)
        ECC_MAP[6] = "&dagger;"; // 0x86 (134)
        ECC_MAP[7] = "&Dagger;"; // 0x87 (135)
        ECC_MAP[9] = "&permil;"; // 0x89 (137)
        ECC_MAP[10] = "&Scaron;"; // 0x8A (138)
        ECC_MAP[11] = "&lsaquo;"; // 0x8B (139)
        ECC_MAP[12] = "&OElig;"; // 0x8C (140)
        ECC_MAP[17] = "&lsquo;"; // 0x91 (145)
        ECC_MAP[18] = "&rsquo;"; // 0x92 (146)
        ECC_MAP[19] = "&ldquo;"; // 0x93 (147)
        ECC_MAP[20] = "&rdquo;"; // 0x94 (148)
        ECC_MAP[21] = "&bull;"; // 0x95 (149)
        ECC_MAP[22] = "&ndash;"; // 0x96 (150)
        ECC_MAP[23] = "&mdash;"; // 0x97 (151)
        ECC_MAP[24] = "&tilde;"; // 0x98 (152)
        ECC_MAP[25] = "&trade;"; // 0x99 (153)
        ECC_MAP[26] = "&scaron;"; // 0x9A (154)
        ECC_MAP[27] = "&rsaquo;"; // 0x9B (155)
        ECC_MAP[28] = "&oelig;"; // 0x9C (156)
        ECC_MAP[31] = "&Yuml;"; // 0x9F (159)
    }

    /**
     * StringUtil constructor comment.
     */
    private StringUtil()
    {
        super();
    }

    public static boolean isIntegerString( String str )
    {

        if ( str == null || str.length() == 0 )
        {
            return false;
        }
        for ( int i = 0; i < str.length(); i++ )
        {
            if ( ( str.charAt( i ) < '0' || str.charAt( i ) > '9' ) && !( i == 0 && str.charAt( i ) == '-' ) )
            {
                return false;
            }
        }

        // check for leading zeros
        if ( str.charAt( 0 ) == '0' && str.length() > 1 )
        {
            return false;
        }

        return true;
    }

    public static String expandString( String baseString, Object[] objects, Throwable throwable )
    {

        StringBuffer string = new StringBuffer( baseString );
        if ( objects != null )
        {
            for ( int i = objects.length - 1; i >= 0; i-- )
            {
                String indexStr = "%" + String.valueOf( i );
                int index = baseString.indexOf( indexStr );

                // skip loop if the index string was not found in the base string:
                if ( index == -1 )
                {
                    continue;
                }

                // replace the index string with the object:
                Object obj = objects[i];
                if ( obj != null )
                {
                    string.replace( index, index + 1 + String.valueOf( i ).length(), obj.toString() );
                }
                else
                {
                    string.replace( index, index + 1 + String.valueOf( i ).length(), "null" );
                }

            }
        }

        // replace "%t" with the throwable's message
        if ( throwable != null )
        {
            int index = string.toString().indexOf( "%t" );
            if ( index >= 0 )
            {
                String msg = throwable.getMessage();
                if ( msg != null )
                {
                    string.replace( index, index + 2, msg );
                }
                else
                {
                    string.replace( index, index + 2, "null" );
                }
            }
        }

        return string.toString();
    }

    public static String expandString( String baseString, Object object )
    {

        return expandString( baseString, object, null );
    }

    public static String expandString( String baseString, Object object, Throwable throwable )
    {

        StringBuffer string = new StringBuffer( baseString );
        int index = baseString.indexOf( "%0" );
        if ( index >= 0 )
        {
            if ( object != null )
            {
                string.replace( index, index + 2, object.toString() );
            }
            else
            {
                string.replace( index, index + 2, "null" );
            }
        }

        // replace "%t" with the throwable's message
        if ( throwable != null )
        {
            index = string.toString().indexOf( "%t" );
            if ( index >= 0 )
            {
                String msg = throwable.getMessage();
                if ( msg != null )
                {
                    string.replace( index, index + 2, msg );
                }
                else
                {
                    string.replace( index, index + 2, "null" );
                }
            }
        }

        return string.toString();
    }

    static public String[] splitString( String str, char delim )
    {
        return splitString( str, String.valueOf( delim ) );
    }

    static public String[] splitString( String str, String delim )
    {
        return splitString( str, delim, false );
    }

    static public String[] splitString( String str, String delim, boolean includeLastIfEmpty )
    {
        ArrayList<String> result = new ArrayList<String>();

        if ( str != null && str.length() > 0 )
        {
            int idx = str.indexOf( delim );

            String substr;
            while ( idx != -1 )
            {
                substr = str.substring( 0, idx );
                result.add( substr );

                str = str.substring( idx + 1 );
                idx = str.indexOf( delim );
            }

            if ( str.length() > 0 || includeLastIfEmpty )
            {
                result.add( str );
            }
        }

        return result.toArray( new String[0] );
    }

    static public String stripControlChars( String text )
    {
        StringBuffer newString = new StringBuffer();
        for ( int i = 0; i < text.length(); i++ )
        {
            char chr = text.charAt( i );
            if ( (int) chr >= 0x20 )
            {
                newString.append( chr );
            }
        }
        return newString.toString();
    }

    /**
     * Replace Unicode extended control characters (ECCs) which Windows uses for special characters with correct HTML entity. See mapping in
     * table below. ECCs in string not used by Windows are removed.
     * <p/>
     * <table border="0" cellspacing="0" cellpadding="2"> <tr> <th>Description</th> <th>Hex Value</th> <th>HTML Entity</th>
     * <th><pre> </pre></th>
     * </tr> <tr> <td>low left rising single quote</td> <td>82</td> <td>&amp;lsquor;</td> <td>&lsquor;</td> </tr> <tr> <td>small italic f,
     * function of, f florin</td> <td>83</td> <td>&amp;fnof;</td> <td>&fnof;</td> </tr> <tr> <td>low left rising double quote</td>
     * <td>84</td> <td>&amp;ldquor;</td> <td>&ldquor;</td> </tr> <tr> <td>low horizontal ellipsis</td> <td>85</td> <td>&amp;ldots;</td>
     * <td>&ldots;</td> </tr> <tr> <td>dagger mark</td> <td>86</td> <td>&amp;dagger;</td> <td>&dagger;</td> </tr> <tr> <td>double dagger
     * mark</td> <td>87</td> <td>&amp;Dagger;</td> <td>&Dagger;</td> </tr> <tr> <td>per thousand (mille) sign</td> <td>89</td>
     * <td>&amp;permil;</td> <td>&permil;</td> </tr> <tr> <td>capital S caron or hacek</td> <td>8A</td> <td>&amp;Scaron;</td>
     * <td>&Scaron;</td> </tr> <tr> <td>left single angle quote mark (guillemet)</td> <td>8B</td> <td>&amp;lsaquo;</td> <td>&lsaquo;</td>
     * </tr> <tr> <td>capital OE ligature</td> <td>8C</td> <td>&amp;OElig;</td> <td>&OElig;</td> </tr> <tr> <td>left single quotation mark,
     * high right rising single quote</td> <td>91</td> <td>&amp;lsquo;</td> <td>&lsquo;</td> </tr> <tr> <td>right single quote mark</td>
     * <td>92</td> <td>&amp;rsquo;</td> <td>&rsquo;</td> </tr> <tr> <td>left double quotation mark, high right rising double quote</td>
     * <td>93</td> <td>&amp;ldquo;</td> <td>&ldquo;</td> </tr> <tr> <td>right double quote mark</td> <td>94</td> <td>&amp;rdquo;</td>
     * <td>&rdquo;</td> </tr> <tr> <td>round filled bullet</td> <td>95</td> <td>&amp;bull;</td> <td>&bull;</td> </tr> <tr> <td>en dash</td>
     * <td>96</td> <td>&amp;ndash;</td> <td>&ndash;</td> </tr> <tr> <td>em dash</td> <td>97</td> <td>&amp;mdash;</td> <td>&mdash;</td> </tr>
     * <tr> <td>small spacing tilde accent</td> <td>98</td> <td>&amp;tilde;</td> <td>&tilde;</td> </tr> <tr> <td>trademark sign</td>
     * <td>99</td> <td>&amp;trade;</td> <td>&trade;</td> </tr> <tr> <td>small s caron or hacek</td> <td>9A</td> <td>&amp;scaron;</td>
     * <td>&scaron;</td> </tr> <tr> <td>right single angle quote mark (guillemet)</td> <td>9B</td> <td>&amp;rsaquo;</td> <td>&rsaquo;</td>
     * </tr> <tr> <td>small oe ligature</td> <td>9C</td> <td>&amp;oelig;</td> <td>&oelig;</td> </tr> <tr> <td>capital Y dieresis or
     * umlaut</td> <td>9F</td> <td>&amp;Yuml;</td> <td>&Yuml;</td> </tr> </table>
     *
     * @param str the string to do the replacing on
     * @return the resulting string
     */
    public static String replaceECC( String str )
    {
        StringBuffer sb = new StringBuffer( str );
        for ( int i = 0; i < sb.length(); i++ )
        {
            char c = sb.charAt( i );
            if ( c >= ECC_START && c <= ECC_END )
            {
                String entity = ECC_MAP[c - ECC_START];
                if ( entity != null )
                {
                    sb.replace( i, i + 1, entity );
                    i += entity.length() - 1;
                }
                else
                {
                    sb.deleteCharAt( i );
                    i--;
                }
            }
        }

        return sb.toString();
    }

    public static String replaceAll( String text, String what, String with )
    {
        int startPos = text.indexOf( what );
        if ( startPos < 0 )
        {
            return text;
        }

        int currentPos = 0;
        StringBuffer result = new StringBuffer();
        //char[] chars = text.toCharArray();
        do
        {
            if ( currentPos < startPos )
            {
                result.append( text.substring( currentPos, startPos ) );
            }
            result.append( with );
            currentPos = startPos + what.length();
            startPos = text.indexOf( what, currentPos );
        }
        while ( startPos >= 0 );

        if ( currentPos < text.length() )
        {
            result.append( text.substring( currentPos ) );
        }

        return result.toString();
    }

    public static void replaceString( StringBuffer text, String what, String with )
    {

        String s = text.toString();
        int startPos = s.indexOf( what );
        if ( startPos == -1 )
        {
            return;
        }

        replaceString( text, what, with, startPos );
    }

    public static void replaceString( StringBuffer text, String what, String with, int startPos )
    {

        text.replace( startPos, ( startPos + what.length() ), with );
    }

    public static String upperCaseWord( String string, String word, boolean firstAlso )
    {
        int pos = -1;

        if ( firstAlso )
        {
            pos = string.indexOf( word );
        }
        else
        {
            pos = string.indexOf( word, 1 );
        }

        while ( pos > -1 )
        {
            string =
                string.substring( 0, pos ).concat( string.substring( pos, pos + 1 ).toUpperCase() ).concat( string.substring( pos + 1 ) );
            pos = string.indexOf( word, pos );
        }
        return string;
    }

    /**
     * Return value as hex.
     */
    public static String toHex( byte[] value )
    {
        char[] chars = new char[value.length * 2];

        for ( int i = 0; i < value.length; i++ )
        {
            int a = ( value[i] >> 4 ) & 0x0F;
            int b = value[i] & 0x0F;

            chars[i * 2] = HEX_CHARS[a];
            chars[i * 2 + 1] = HEX_CHARS[b];
        }

        return new String( chars );
    }

    /**
     * Return value as hex.
     */
    public static String toHex( short value )
    {
        byte[] bytes = new byte[2];

        bytes[0] = (byte) ( ( value >> 8 ) & 0xFF );
        bytes[1] = (byte) ( value & 0xFF );

        return toHex( bytes );
    }

    /**
     * Return value as hex.
     */
    public static String toHex( int value )
    {
        byte[] bytes = new byte[4];

        bytes[0] = (byte) ( ( value >> 24 ) & 0xFF );
        bytes[1] = (byte) ( ( value >> 16 ) & 0xFF );
        bytes[2] = (byte) ( ( value >> 8 ) & 0xFF );
        bytes[3] = (byte) ( value & 0xFF );

        return toHex( bytes );
    }

    /**
     * Return value as hex.
     */
    public static String toHex( long value )
    {
        byte[] bytes = new byte[8];

        bytes[0] = (byte) ( ( value >> 56 ) & 0xFF );
        bytes[1] = (byte) ( ( value >> 48 ) & 0xFF );
        bytes[2] = (byte) ( ( value >> 40 ) & 0xFF );
        bytes[3] = (byte) ( ( value >> 32 ) & 0xFF );
        bytes[4] = (byte) ( ( value >> 24 ) & 0xFF );
        bytes[5] = (byte) ( ( value >> 16 ) & 0xFF );
        bytes[6] = (byte) ( ( value >> 8 ) & 0xFF );
        bytes[7] = (byte) ( value & 0xFF );

        return toHex( bytes );
    }

    public static String getXMLSafeString( String input )
    {
        // This ancient stuff was found in XMLTool.createElement(Document doc, Element root, String name, String text, String sortAttribute, String sortValue)
        // Origin unknown, but it gets around a famous problem with xml parsing, Character reference "&#x1a;" is an invalid XML character.
        // It is basically a workaround that replaces a crazy character with ' (&apos;)
        StringBuffer sb = new StringBuffer( input );
        for ( int i = 0; i < sb.length(); i++ )
        {
            int c = sb.charAt( i );
            if ( c == 26 ) // illegal character (special single quote)
            {
                sb.replace( i, i + 1, "'" );
            }
            else if ( c < 33 && c != '\t' && c != '\n' && c != '\r' )
            {
                sb.replace( i, i + 1, " " );
            }
        }
        return sb.toString();
    }

}
