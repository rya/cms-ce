/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.portal.instruction;

import java.io.IOException;
import java.util.Arrays;

import com.enonic.cms.portal.instruction.CreateResourceUrlInstruction;
import com.enonic.cms.portal.instruction.PostProcessInstruction;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Nov 20, 2009
 * Time: 9:10:09 AM
 */
public class CreateResourceUrlInstructionTest
    extends TestCase
{
    @Test
    public void testDefault()
        throws Exception
    {
        CreateResourceUrlInstruction i1 = new CreateResourceUrlInstruction();
        CreateResourceUrlInstruction i2 = new CreateResourceUrlInstruction();

        String result = i1.serialize();

        assertNotNull( result );

        i2.deserialize( result );

        assertTrue( i1.equals( i2 ) );

    }

    @Test
    public void testValues()
        throws Exception
    {
        String resolvedPath = "/_resources/test";

        String[] params = new String[3];
        params[0] = "param1";
        params[1] = "param2";
        params[2] = "param3";

        CreateResourceUrlInstruction instruction = new CreateResourceUrlInstruction();

        instruction.setResolvedPath( resolvedPath );
        instruction.setParams( params );

        String result = instruction.serialize();

        CreateResourceUrlInstruction instruction2 = new CreateResourceUrlInstruction();
        instruction2.deserialize( result );

        assertTrue( instruction.equals( instruction2 ) );
        assertTrue( Arrays.equals( instruction.getParams(), instruction2.getParams() ) );
        assertTrue( instruction.getResolvedPath().equals( instruction2.getResolvedPath() ) );
    }


    @Test
    public void testEquals()
        throws IOException
    {
        CreateResourceUrlInstruction i1 = new CreateResourceUrlInstruction();

        PostProcessInstruction i2 = null;

        assertTrue( !i1.equals( i2 ) );

        i2 = new CreateResourceUrlInstruction();

        assertTrue( i1.equals( i2 ) );

        CreateResourceUrlInstruction i3 = new CreateResourceUrlInstruction();
        i1.setResolvedPath( "Dummy" );
        i3.setResolvedPath( "Dummy2" );

        assertTrue( !i1.equals( i3 ) );

        i3.setResolvedPath( "Dummy" );

        assertTrue( i1.equals( i3 ) );
    }


}
