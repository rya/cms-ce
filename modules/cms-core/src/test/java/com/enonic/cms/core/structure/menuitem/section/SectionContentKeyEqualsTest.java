package com.enonic.cms.core.structure.menuitem.section;


import org.junit.Test;

import com.enonic.cms.core.AbstractEqualsTest;


public class SectionContentKeyEqualsTest
    extends AbstractEqualsTest
{
    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        return new SectionContentKey( 1 );
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        return new Object[]{new SectionContentKey( 2 )};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        return new SectionContentKey( 1 );
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        return new SectionContentKey( 1 );
    }
}
