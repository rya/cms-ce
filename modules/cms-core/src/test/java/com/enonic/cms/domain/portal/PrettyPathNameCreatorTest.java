/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.portal;

import com.enonic.cms.portal.PrettyPathNameCreator;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Mar 17, 2010
 * Time: 9:51:04 AM
 */
public class PrettyPathNameCreatorTest
{

    public static final char[] REPLACE_WITH_HYPHENS_URL_RESERVED = {'$', '&', '+', ',', '/', ':', ';', '=', '@'};

    public static final char[] REPLACE_WITH_HYPHENS_URL_UNSAFE =
        {' ', '\"', '>', '<', '#', '%', '{', '}', '|', '\\', '^', '~', '[', ']', '`'};

    @Test
    public void testEmptyName()
    {
        generateAndVerify( "", PrettyPathNameCreator.DEFAULT_PATHNAME );
    }

    @Test
    public void testNullName()
    {
        generateAndVerify( null, PrettyPathNameCreator.DEFAULT_PATHNAME );
    }

    @Test
    public void testBlankName()
    {
        generateAndVerify( "  ", PrettyPathNameCreator.DEFAULT_PATHNAME );
    }

    @Test
    public void testBlankSpaces()
    {
        generateAndVerify( "test", "test" );
        generateAndVerify( " test ", "test" );
        generateAndVerify( " t e s t ", "t-e-s-t" );
    }

    @Test
    public void testLowerCase()
    {
        generateAndVerify( "TEST", "test" );
        generateAndVerify( " Test ", "test" );
        generateAndVerify( " T E S T ", "t-e-s-t" );
    }

    @Test
    public void testSpecialCharacters()
    {
        generateAndVerify( "test" + SpecialCharacterTestStrings.NORWEGIAN, "test" + SpecialCharacterTestStrings.NORWEGIAN.toLowerCase() );
        generateAndVerify( "test" + SpecialCharacterTestStrings.CHINESE, "test" + SpecialCharacterTestStrings.CHINESE.toLowerCase() );
        generateAndVerify( "test" + SpecialCharacterTestStrings.AEC_ALL, "test" + SpecialCharacterTestStrings.AEC_ALL.toLowerCase() );
    }

    @Test
    public void testNiceBeginningAndEnd()
    {
        generateAndVerify( "--Donald + & + Dolly--", "donald-dolly" );
        generateAndVerify( "test" + "/\\#;", "test" );
        generateAndVerify( "/\\#;" + "test" + "/\\#;", "test" );
        generateAndVerify( "/\\#;" + "t--e--s--t" + "/\\#;", "t-e-s-t" );
        generateAndVerify( "...te-st.in-gs...--....", "te-st.in-gs" );
        generateAndVerify( "___te-st.in-gs____", "te-st.in-gs" );
    }


    @Test
    public void testTrailingSpaces()
    {
        generateAndVerify( "Donald + & + Dolly", "donald-dolly" );

    }

    @Test
    public void testTrailingHyphens()
    {
        generateAndVerify( "Donald--Dolly -- ", "donald-dolly" );
    }


    @Test
    public void testNoHyphensAroundDot()
    {
        generateAndVerify( "Donald . Dolly", "donald.dolly" );

        generateAndVerify( "Donald. Dolly", "donald.dolly" );

        generateAndVerify( "Donald .Dolly", "donald.dolly" );

        generateAndVerify( "Donald.Dolly", "donald.dolly" );
    }

    @Test
    public void testUnsafeCharacters()
    {
        generateAndVerify( "Test%/", "test" );

        generateAndVerify( " -t--_e?s--_t-  ", "t-es-t" );

        generateAndVerify( "?t?e?s?t?", "test" );
    }


    @Test
    public void testUnsafeCharactersOnly()
    {
        generateAndVerify( "%/", PrettyPathNameCreator.DEFAULT_PATHNAME );

        generateAndVerify( "  --?--  ", PrettyPathNameCreator.DEFAULT_PATHNAME );

        generateAndVerify( "?", PrettyPathNameCreator.DEFAULT_PATHNAME );
    }

    @Test
    public void testReplaceReservedWithHyphens()
    {
        doCheckArrayForHyphenReplacement( REPLACE_WITH_HYPHENS_URL_RESERVED );
    }

    @Test
    public void testTrailingUnderScores()
    {
        generateAndVerify( "runar___myklebust", "runar-myklebust" );
    }


    @Test
    public void testReplaceUnsafeWithHyphens()
    {
        doCheckArrayForHyphenReplacement( REPLACE_WITH_HYPHENS_URL_UNSAFE );
    }

    private void doCheckArrayForHyphenReplacement( char[] replaceChars )
    {
        StringBuffer replaced = new StringBuffer();
        StringBuffer expected = new StringBuffer();

        for ( char replaceChar : replaceChars )
        {
            replaced.append( "a" );
            replaced.append( replaceChar );
            expected.append( "a" );
            expected.append( '-' );
        }

        replaced.append( "a" );
        expected.append( "a" );

        generateAndVerify( replaced.toString(), expected.toString() );
    }

    private void generateAndVerify( String suggestedName, String result )
    {
        String generatedName = PrettyPathNameCreator.generatePrettyPathName(suggestedName);
        assertEquals( "Unexpected result for : " + suggestedName, result, generatedName );
    }

    private class SpecialCharacterTestStrings
    {
        public static final String NORWEGIAN = "\u00c6\u00d8\u00c5\u00e6\u00f8\u00e5";

        public static final String CHINESE = "\u306d\u304e\u30de\u30e8\u713c\u304d";

        public static final String AEC_ALL = "\u0082\u0083\u0084\u0085\u0086\u0087\u0089\u008a\u008b\u008c\u0091\u0092\u0093" +
            "\u0094\u0095\u0096\u0097\u0098\u0099\u009a\u009b\u009c\u009f";
    }


}


