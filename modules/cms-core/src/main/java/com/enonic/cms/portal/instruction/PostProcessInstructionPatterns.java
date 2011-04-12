/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.instruction;

import java.util.regex.Pattern;

import org.springframework.web.util.HtmlUtils;

import com.enonic.cms.framework.util.UrlPathEncoder;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Dec 15, 2010
 * Time: 10:51:39 AM
 */
public class PostProcessInstructionPatterns
{
    public static final String PPI_PREFIX = "__##";

    public static final String PPI_POSTFIX = "##__";

    public static final String PPI_PREFIX_ENCODED = UrlPathEncoder.encode( PPI_PREFIX );

    private static final String PREFIX_VARIANTS = PPI_PREFIX + "|" + PPI_PREFIX_ENCODED;

    private static final String POSTFIX_VARIANTS = PPI_POSTFIX + "|" + UrlPathEncoder.encode( PPI_POSTFIX );

    public static final String PPI_SEPARATOR = "&";

    private static final String PPI_SEPARATOR_ESCAPED = HtmlUtils.htmlEscape( PPI_SEPARATOR );

    private static final String PPI_SEPARATOR_ENCODED = UrlPathEncoder.encode( PPI_SEPARATOR );

    private static final String PPI_SEPARATOR_ESCAPED_AND_ENCODED = UrlPathEncoder.encode( PPI_SEPARATOR_ESCAPED );

    private static final String SEPARATOR_VARIANTS =
        PPI_SEPARATOR + "|" + PPI_SEPARATOR_ESCAPED + "|" + PPI_SEPARATOR_ESCAPED_AND_ENCODED + "|" + PPI_SEPARATOR_ENCODED;

    private static final String PPI_NAME_ALLOWED_CHARS = "[a-zA-Z_]";

    private static final String PPI_VALUE_ALLOWED_CHARS = "0-9a-zA-Z\\+=/";

    private static final String EQUALS_ENC = UrlPathEncoder.encode( "=" );

    private static final String PLUS_ENC = UrlPathEncoder.encode( "+" );

    private static final String BACKSLASH_ENC = UrlPathEncoder.encode( "\\" );

    private static final String SLASH_ENC = UrlPathEncoder.encode( "/" );

    private static final String SEMICOLON_ENC = UrlPathEncoder.encode( ";" );

    public static final String SERIALIZED_VALUE_PATTERN =
        PPI_VALUE_ALLOWED_CHARS + "|" + EQUALS_ENC + "|" + PLUS_ENC + "|" + BACKSLASH_ENC + "|" + SLASH_ENC + "|" + SEMICOLON_ENC;

    public static final Pattern SERIALIZED_PATTERN =
        Pattern.compile( "(" + PPI_NAME_ALLOWED_CHARS + "+)(" + SEPARATOR_VARIANTS + ")(" + "[" + SERIALIZED_VALUE_PATTERN + "]+)" );

    public static final Pattern POST_PROCESS_INSTRUCTION_PATTERN =
        Pattern.compile( "(" + PREFIX_VARIANTS + ")" + SERIALIZED_PATTERN + "(" + POSTFIX_VARIANTS + ")" );


    public static boolean instructionHasBeenUrlEncoded( String prefix )
    {
        return prefix.equals( PPI_PREFIX_ENCODED );
    }


    public static boolean instructionHtmlEscaped( String separator )
    {
        if ( separator.equals( PostProcessInstructionPatterns.PPI_SEPARATOR_ESCAPED ) )
        {
            return true;
        }

        return false;
    }

}
