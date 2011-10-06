/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.enonic.cms.framework.xml.IllegalCharacterCleaner;

public class BigText
{

    private static final String SPECIAL_XML_CHARS_TO_REPLACE = ".,=<>*@/\n";

    private static final String CHARS_TO_REPLACE_WITH = " ";

    private IllegalCharacterCleaner xmlCleaner = new IllegalCharacterCleaner();

    private String text = "";

    private Set<String> words;

    public BigText( String text )
    {
        if ( text == null )
        {
            throw new IllegalArgumentException( "Given text cannot be null" );
        }

        this.text = xmlCleaner.cleanXml( text );
        this.text = this.text.replaceAll( "[" + SPECIAL_XML_CHARS_TO_REPLACE + "]", CHARS_TO_REPLACE_WITH );
        this.text = this.text.trim();

    }

    public String getText()
    {
        return text;
    }

    private void initWords()
    {

        words = new LinkedHashSet<String>();

        BreakIterator wb = BreakIterator.getWordInstance();
        wb.setText( text );
        int start = wb.first();
        for ( int end = wb.next(); end != BreakIterator.DONE; start = end, end = wb.next() )
        {
            String word = text.substring( start, end );
            word = word.toLowerCase().trim();
            if ( word.length() > 0 && !".".equals( word ) && !":".equals( word ) && !")".equals( word ) )
            {
                words.add( word.toLowerCase() );
            }
        }
    }

    public Collection<String> getWords()
    {

        if ( words == null )
        {
            initWords();
        }

        return words;
    }

    public List<String> getTextSplitted( int splitTreshold, String lineSeparator )
    {

        String value = getText();
        ArrayList<String> values = new ArrayList<String>();
        while ( value.length() > splitTreshold )
        {
            int index = findSplitIndex( value, splitTreshold, lineSeparator );
            values.add( value.substring( 0, index ).trim() );
            value = value.substring( index + 1 ).trim();
        }
        values.add( value );

        return values;
    }

    /**
     * Tries to make a smart split, by looking for spaces and line feeds in the text, to find a place to split the string at the last word
     * break before the limit of 255 characters.
     *
     * @param value         The string to figure out where to split.
     * @param splitTreshold The maximum length of the the text that may be spilt off.
     * @param lineSeparator The character used to spilt lines.
     * @return A number between 0 and the split threshold, which is the best place to split the given string.
     */
    private int findSplitIndex( String value, int splitTreshold, String lineSeparator )
    {
        String valueMax = value.substring( 0, splitTreshold );
        int index = valueMax.lastIndexOf( ' ' );
        if ( ( index < 0 ) || ( index > splitTreshold ) )
        {
            index = value.lastIndexOf( lineSeparator );
            if ( ( index < 0 ) || ( index > splitTreshold ) )
            {
                index = splitTreshold;
            }
        }
        return index;
    }


}
