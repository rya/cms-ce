/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

/**
 * A class for simple text that needs to have ASCII control characters filtered out.
 */
public class SimpleText
{
    // All ASCII controll characters and some other selected special characters.

    private static String charsToReplace = "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\u0008\u000b\u000c\u000e" +
        "\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f";

    // All occurances of any of the characters listed in the string above will be replaced with the following sequence of characters:

    private static String charsToReplaceWith = " ";

    private boolean replaced = false;

    private String text = "";

    public SimpleText( String text )
    {
        if ( text != null )
        {
            this.text = text;
            cleanUpText();
        }
    }

    public String getText()
    {
        return text;
    }

    private void cleanUpText()
    {

        if ( text != null )
        {
            /* We're using replace instead of remove as we don't want to create "new" words */
            if ( !replaced )
            {
                text = text.replaceAll( "[" + charsToReplace + "]", charsToReplaceWith );
                replaced = true;
            }

            text = text.trim();
        }
    }

    @Override
    public int hashCode()
    {
        return text.hashCode();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( o instanceof SimpleText )
        {
            return text.equals( ( (SimpleText) o ).getText() );
        }
        else
        {
            return false;
        }
    }

    @Override
    public String toString()
    {
        return text;
    }
}
