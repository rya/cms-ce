/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.business;

//import org.jdom.Verifier;

/**
 * Created by rmy - Date: May 5, 2009
 */
public class SpecialCharacterTestStrings
{

    public static final String NORWEGIAN = "\u00c6\u00d8\u00c5\u00e6\u00f8\u00e5";     // AE, OE, AA, ae, oe, aa

    public static final String CHINESE = "\u306d\u304e\u30de\u30e8\u713c\u304d";

    // ASCII control characters that are not allowed in XML:

    public static final String ASCII_CC_XML_ILLEGAL = "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\u0008\u000b\u000c\u000e" +
        "\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f";

    // ASCII Extended Characters which used to be translated to &#110; notation for HTML in older versions of Enonic CMS:

    public static final String AEC_SELECTION = "\u0082\u0099\u009f";

    public static final String AEC_ALL = "\u0082\u0083\u0084\u0085\u0086\u0087\u0089\u008a\u008b\u008c\u0091\u0092\u0093\u0094" +
        "\u0095\u0096\u0097\u0098\u0099\u009a\u009b\u009c\u009f";

    /*
    public static void main( String[] args )
    {
        System.out.println( "JDom testing null:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the NUL: \u0000" ) );
        System.out.println( "JDom testing Start of Header:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the SOH: \u0001" ) );
        System.out.println( "JDom testing start of text:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the STX: \u0002" ) );
        System.out.println( "JDom testing end of text:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the ETX: \u0003" ) );
        System.out.println( "JDom testing end of transmission:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the EOT: \u0004" ) );
        System.out.println( "JDom testing enquiry:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the ENQ: \u0005" ) );
        System.out.println( "JDom testing acknowledgement:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the ACK: \u0006" ) );
        System.out.println( "JDom testing the bell:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the BEL: \u0007" ) );
        System.out.println( "JDom testing backspace:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the BS: \u0008" ) );
        // Only legal JDom character, except CR and LF below.
        System.out.println( "JDom testing Horizontal Tab:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the HT: \u0009" ) );
        // Can not test Line feed: Illegal Java character
//        System.out.println("JDom testing Line feed:");
//        System.out.println(" - " + Verifier.checkCharacterData( "Here comes the LF: 000a" ));
        System.out.println( "JDom testing Vertical Tab:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the VT: \u000b" ) );
        System.out.println( "JDom testing Form feed:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the FF: \u000c" ) );
        // Can not test carriage return: Illegal Java character
//        System.out.println("JDom testing carriage return:");
//        System.out.println(" - " + Verifier.checkCharacterData( "Here comes the CR: 000d" ));
        System.out.println( "JDom testing shift out:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the SO: \u000e" ) );
        System.out.println( "JDom testing shift in:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the SI: \u000f" ) );
        System.out.println( "JDom testing data link escape:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the DLE: \u0010" ) );
        System.out.println( "JDom testing device control 1:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the DC1: \u0011" ) );
        System.out.println( "JDom testing device control 2:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the DC2: \u0012" ) );
        System.out.println( "JDom testing device control 3:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the DC3: \u0013" ) );
        System.out.println( "JDom testing device control 4:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the DC4: \u0014" ) );
        System.out.println( "JDom testing negativ acknowledgement:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the NAK: \u0015" ) );
        System.out.println( "JDom testing synchronous idle:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the SYN: \u0016" ) );
        System.out.println( "JDom testing End of transmission block:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the ETB: \u0017" ) );
        System.out.println( "JDom testing cancel:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the CAN: \u0018" ) );
        System.out.println( "JDom testing end of medium:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the EM: \u0019" ) );
        System.out.println( "JDom testing substitute:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the SUB: \u001a" ) );
        System.out.println( "JDom testing escape:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the ESC: \u001b" ) );
        System.out.println( "JDom testing file separator:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the FS: \u001c" ) );
        System.out.println( "JDom testing group separator:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the GS: \u001d" ) );
        System.out.println( "JDom testing record separator:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the RS: \u001e" ) );
        System.out.println( "JDom testing unit separator:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the US: \u001f" ) );
        System.out.println( "JDom testing delete:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the DEL: \u007f" ) );
        System.out.println();
        System.out.println( "Extended control characters:" );
        System.out.println();
        System.out.println( "JDom testing \u0082:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the : \u0082" ) );
        System.out.println( "JDom testing \u0083:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the : \u0083" ) );
        System.out.println( "JDom testing \u0084:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the : \u0084" ) );
        System.out.println( "JDom testing \u0085:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the : \u0085" ) );
        System.out.println( "JDom testing \u0086:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the : \u0086" ) );
        System.out.println( "JDom testing \u0087:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the : \u0087" ) );
        System.out.println( "JDom testing \u0089:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the : \u0089" ) );
        System.out.println( "JDom testing \u008a:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the : \u008a" ) );
        System.out.println( "JDom testing \u008b:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the : \u008b" ) );
        System.out.println( "JDom testing \u008c:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the : \u008c" ) );
        System.out.println( "JDom testing \u0091:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the : \u0091" ) );
        System.out.println( "JDom testing \u0092:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the : \u0092" ) );
        System.out.println( "JDom testing \u0093:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the : \u0093" ) );
        System.out.println( "JDom testing \u0094:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the : \u0094" ) );
        System.out.println( "JDom testing \u0095:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the : \u0095" ) );
        System.out.println( "JDom testing \u0096:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the : \u0096" ) );
        System.out.println( "JDom testing \u0097:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the : \u0097" ) );
        System.out.println( "JDom testing \u0098:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the : \u0098" ) );
        System.out.println( "JDom testing \u0099:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the : \u0099" ) );
        System.out.println( "JDom testing \u009a:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the : \u009a" ) );
        System.out.println( "JDom testing \u009b:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the : \u009b" ) );
        System.out.println( "JDom testing \u009c:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the : \u009c" ) );
        System.out.println( "JDom testing \u009f:" );
        System.out.println( " - " + Verifier.checkCharacterData( "Here comes the : \u009f" ) );
    }
//*/
}
