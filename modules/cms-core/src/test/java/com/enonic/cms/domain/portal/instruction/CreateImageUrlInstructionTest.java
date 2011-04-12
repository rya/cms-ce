/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.portal.instruction;

import com.enonic.cms.portal.instruction.CreateImageUrlInstruction;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Nov 25, 2009
 * Time: 11:33:24 AM
 */
public class CreateImageUrlInstructionTest
    extends TestCase
{
    @Test
    public void testDefault()
        throws Exception
    {
        CreateImageUrlInstruction i1 = new CreateImageUrlInstruction();
        CreateImageUrlInstruction i2 = new CreateImageUrlInstruction();

        String result = i1.serialize();

        assertNotNull( result );

        i2.deserialize( result );

        assertTrue( i1.equals( i2 ) );
    }

    @Test
    public void testValues()
        throws Exception
    {
        CreateImageUrlInstruction instruction = new CreateImageUrlInstruction();
        instruction.setKey( "1" );
        instruction.setBackground( "#111111" );
        instruction.setFilter( "rounded:80" );
        instruction.setFormat( "jpg" );
        instruction.setQuality( "0.8" );
        instruction.setRequestedMenuItemKey( "1" );

        String result = instruction.serialize();

        CreateImageUrlInstruction instruction2 = new CreateImageUrlInstruction();
        instruction2.deserialize( result );

        assertEquals( instruction, instruction2 );
        assertEquals( instruction.getBackground(), instruction2.getBackground() );
        assertEquals( instruction.getFilter(), instruction2.getFilter() );
        assertEquals( instruction.getKey(), instruction2.getKey() );
        assertEquals( instruction.getQuality(), instruction2.getQuality() );
        assertEquals( instruction.getRequestedMenuItemKey(), instruction2.getRequestedMenuItemKey() );
    }

}
