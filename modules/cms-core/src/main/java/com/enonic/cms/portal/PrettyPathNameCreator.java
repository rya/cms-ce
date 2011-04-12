/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;


/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Mar 16, 2010
 * Time: 3:11:34 PM
 */
public class PrettyPathNameCreator
{

    public final static char[] ADDITIONAL_ALLOWED_CHARS = {'.', '-', ' '};

    public final static char[] REMOVE_CHARS = {'?'};

    public final static char[] REPLACE_WITH_HYPHEN_CHARS =
        {'$', '&', '|', ':', ';', '#', '/', '\\', '<', '>', '\"', '*', '+', ',', '=', '@', '%', '{', '}', '[', ']', '`', '~', '^', '_'};

    public final static String DEFAULT_PATHNAME = "page";

    private final static Pattern STRIP_BEGINNING_PATTERN = Pattern.compile( "^([\\.|\\-|_]+)(.*)$" );

    private final static Pattern STRIP_ENDING_PATTERN = Pattern.compile( "(.*[^\\.|\\-|_])([\\.|\\-|_]+)$" );

    public static String generatePrettyPathName( String originalName )
    {
        if ( StringUtils.isBlank( originalName ) )
        {
            //throw new IllegalArgumentException( "Generate name failed; Original name cannot be empty or blank" );
            return DEFAULT_PATHNAME;
        }

        String prettifiedPathName = originalName;

        prettifiedPathName = makeLowerCase( prettifiedPathName );
        prettifiedPathName = replaceWithHyphens( prettifiedPathName );
        prettifiedPathName = removeUnsafeCharacters( prettifiedPathName );
        prettifiedPathName = replaceBlankSpaces( prettifiedPathName );
        prettifiedPathName = replaceTrailingHyphens( prettifiedPathName );
        prettifiedPathName = replaceHyphensAroundDot( prettifiedPathName );
        prettifiedPathName = ensureNiceBeginningAndEnding( prettifiedPathName );

        if ( StringUtils.isBlank( prettifiedPathName ) )
        {
            return DEFAULT_PATHNAME;
        }

        return prettifiedPathName;
    }

    private static String replaceTrailingHyphens( String prettifiedName )
    {
        if ( StringUtils.isBlank( prettifiedName ) )
        {
            return "";
        }

        prettifiedName = prettifiedName.replaceAll( "-[-]+", "-" );

        return prettifiedName;
    }

    private static String replaceHyphensAroundDot( String prettifiedName )
    {
        if ( StringUtils.isBlank( prettifiedName ) )
        {
            return "";
        }

        prettifiedName = prettifiedName.replaceAll( "-?\\.-?", "." );

        return prettifiedName;
    }

    private static String ensureNiceBeginningAndEnding( String prettifiedName )
    {
        if ( StringUtils.isBlank( prettifiedName ) )
        {
            return "";
        }

        Matcher m = STRIP_BEGINNING_PATTERN.matcher( prettifiedName );

        if ( m.matches() )
        {
            prettifiedName = m.replaceFirst( m.group( 2 ) );
        }

        m = STRIP_ENDING_PATTERN.matcher( prettifiedName );

        if ( m.matches() )
        {
            prettifiedName = m.replaceFirst( m.group( 1 ) );
        }

        return prettifiedName;
    }

    private static String replaceWithHyphens( String prettifiedName )
    {
        if ( StringUtils.isEmpty( prettifiedName ) )
        {
            return "";
        }

        for ( char toBeReplaced : REPLACE_WITH_HYPHEN_CHARS )
        {
            prettifiedName = prettifiedName.replace( toBeReplaced, '-' );
        }

        return prettifiedName;
    }

    private static String makeLowerCase( String prettifiedName )
    {
        if ( StringUtils.isEmpty( prettifiedName ) )
        {
            return "";
        }

        prettifiedName = prettifiedName.toLowerCase();
        return prettifiedName;
    }

    private static String replaceBlankSpaces( String prettifiedName )
    {
        if ( StringUtils.isEmpty( prettifiedName ) )
        {
            return "";
        }

        String trimmedName = prettifiedName.trim();

        trimmedName = trimmedName.replaceAll( "\\s+", "-" );

        return trimmedName;
    }

    private static String removeUnsafeCharacters( String prettifiedName )
    {
        if ( StringUtils.isEmpty( prettifiedName ) )
        {
            return "";
        }

        String safeName = convertName( prettifiedName );

        return safeName;
    }

    public static String convertName( String str )
    {
        return convertName( str.toCharArray() );
    }

    public static String convertName( char[] chars )
    {
        final StringBuffer str = new StringBuffer();
        for ( char ch : chars )
        {
            if ( isValidChar( ch ) )
            {
                str.append( ch );
            }
        }

        return str.toString();
    }

    public static boolean isValidChar( char ch )
    {
        for ( char unsafe : REMOVE_CHARS )
        {
            if ( ch == unsafe )
            {
                return false;
            }
        }

        if ( Character.isJavaIdentifierPart( ch ) )
        {
            return true;
        }

        for ( char other : ADDITIONAL_ALLOWED_CHARS )
        {
            if ( ch == other )
            {
                return true;
            }
        }

        return false;
    }


}
