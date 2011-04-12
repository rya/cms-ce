/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.portal.instruction;

import java.io.IOException;
import java.util.Arrays;

import com.enonic.cms.portal.instruction.CreateAttachmentUrlInstruction;
import com.enonic.cms.portal.instruction.PostProcessInstruction;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Nov 23, 2009
 * Time: 12:41:10 PM
 */
public class CreateAttachmentUrlInstructionTest
    extends TestCase
{
    private static final Logger LOG = LoggerFactory.getLogger( CreateAttachmentUrlInstructionTest.class.getName() );

    @Before
    public void setUp()
    {

    }

    @Test
    public void testDefault()
        throws Exception
    {
        CreateAttachmentUrlInstruction i1 = new CreateAttachmentUrlInstruction();
        CreateAttachmentUrlInstruction i2 = new CreateAttachmentUrlInstruction();

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
        String nativeLinkKey = "Attachment:38";

        String[] params = new String[3];
        params[0] = "param1";
        params[1] = "param2";
        params[2] = "param3";

        CreateAttachmentUrlInstruction instruction = new CreateAttachmentUrlInstruction();

        instruction.setNativeLinkKey( nativeLinkKey );
        instruction.setParams( params );
        instruction.setRequestedMenuItemKey( "1" );

        String result = instruction.serialize();

        CreateAttachmentUrlInstruction instruction2 = new CreateAttachmentUrlInstruction();
        instruction2.deserialize( result );

        assertTrue( instruction.equals( instruction2 ) );
        assertTrue( Arrays.equals( instruction.getParams(), instruction2.getParams() ) );
        assertEquals( instruction.getNativeLinkKey(), instruction2.getNativeLinkKey() );
        assertEquals( instruction.getRequestedMenuItemKey(), instruction2.getRequestedMenuItemKey() );
    }

    @Test
    public void testTestTest()
        throws Exception
    {
        CreateAttachmentUrlInstruction instruction = new CreateAttachmentUrlInstruction();
        instruction.deserialize( "rO0ABXcdAAI5OQAAAAIACGRvd25sb2FkAAR0cnVlAAMzMjg=" );

        LOG.info( instruction.getNativeLinkKey() );

    }

    @Test
    public void testEquals()
        throws IOException
    {
        CreateAttachmentUrlInstruction i1 = new CreateAttachmentUrlInstruction();

        PostProcessInstruction i2 = null;

        assertTrue( !i1.equals( i2 ) );

        i2 = new CreateAttachmentUrlInstruction();

        assertTrue( i1.equals( i2 ) );

        CreateAttachmentUrlInstruction i3 = new CreateAttachmentUrlInstruction();
        i1.setNativeLinkKey( "Dummy" );
        i3.setNativeLinkKey( "Dummy2" );

        assertTrue( !i1.equals( i3 ) );

        i3.setNativeLinkKey( "Dummy" );

        assertTrue( i1.equals( i3 ) );
    }


}
