package com.enonic.cms.business.core.content;

import java.util.Iterator;

/**
 * iterate next name based on template:
 *
 * name(1) // first
 * name(2) // second
 * name(3) // third
 *
 * counter starts from 1 ( or 2 if name in constructor is name(1) )
 * so, name with counter that comes with constructor is skipped.
 *
 * unit test: ContentNameForCopiesIteratorTest
 */
public class ContentNameForCopiesIterator
        implements Iterable<String>, Iterator<String>
{
    private String prefix = "";

    private int number = 0;
    private int original = 0;

    public ContentNameForCopiesIterator( String name )
    {
        int length = name.length() - 1;

        int exprStart = name.lastIndexOf( '(' ) + 1;
        int exprEnd = name.lastIndexOf( ')' );

        boolean touchCounter = exprStart != 0 && exprEnd == length;

        if ( touchCounter )
        {
            prefix = name.substring( 0, exprStart );
            String postfix = name.substring( exprStart, length );

            try
            {
                original = Integer.parseInt( postfix );
            }
            catch ( NumberFormatException e )
            {
                // some text inside. do not touch it
                touchCounter = false;
            }
        }

        if ( !touchCounter )
        {
            prefix = name + "(";
        }

    }

    public String currentName()
    {
        return prefix + number + ')';
    }

    @Override
    public String next()
    {
        number++;

        // skip original name (may reduce count of database calls)
        if (number == original) number++;

        return currentName();
    }


    @Override
    public Iterator<String> iterator()
    {
        return this;
    }

    @Override
    public boolean hasNext()
    {
        return true;
    }


    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }

}
