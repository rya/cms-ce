package com.enonic.cms.core.content.category;

import org.junit.Test;

import com.enonic.cms.core.AbstractEqualsTest;


public class UnitKeyEqualsTest
    extends AbstractEqualsTest
{
    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        return new UnitKey( 1 );
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        final UnitKey instance1 = new UnitKey( 2 );
        final UnitKey instance2 = new UnitKey( 3 );

        return new Object[]{instance1, instance2};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        return new UnitKey( 1 );
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        return new UnitKey( 1 );
    }
}