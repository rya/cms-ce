/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.portal.instruction;

import java.io.IOException;
import java.util.Arrays;

import com.enonic.cms.portal.instruction.PostProcessInstruction;
import com.enonic.cms.portal.instruction.RenderWindowInstruction;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by rmy - Date: Nov 18, 2009
 */
public class WindowPlaceholderInstructionTest
{
    @Test
    public void testDefault()
        throws Exception
    {
        RenderWindowInstruction i1 = new RenderWindowInstruction();
        RenderWindowInstruction i2 = new RenderWindowInstruction();

        String result = i1.serialize();

        assertNotNull( result );

        i2.deserialize( result );

        assertTrue( i1.equals( i2 ) );

    }

    @Test
    public void testValues()
        throws Exception
    {
        String portletWindowKey = "1";

        String[] params = new String[3];
        params[0] = "param1";
        params[1] = "param2";
        params[2] = "param3";

        RenderWindowInstruction instruction = new RenderWindowInstruction();

        instruction.setPortletWindowKey( portletWindowKey );
        instruction.setParams( params );

        String result = instruction.serialize();

        RenderWindowInstruction instruction2 = new RenderWindowInstruction();
        instruction2.deserialize( result );

        assertTrue( instruction.equals( instruction2 ) );
        assertTrue( Arrays.equals( instruction.getParams(), instruction2.getParams() ) );
        assertTrue( instruction.getPortletWindowKey().equals( instruction2.getPortletWindowKey() ) );
    }


    @Test
    public void testEquals()
        throws IOException
    {
        RenderWindowInstruction i1 = new RenderWindowInstruction();

        PostProcessInstruction i2 = null;

        assertTrue( !i1.equals( i2 ) );

        i2 = new RenderWindowInstruction();

        assertTrue( i1.equals( i2 ) );

        RenderWindowInstruction i3 = new RenderWindowInstruction();
        i1.setPortletWindowKey( "Dummy" );
        i3.setPortletWindowKey( "Dummy2" );

        assertTrue( !i1.equals( i3 ) );

        i3.setPortletWindowKey( "Dummy" );

        assertTrue( i1.equals( i3 ) );
    }
}