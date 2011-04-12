/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.portal.instruction;

import java.util.Arrays;

import com.enonic.cms.portal.instruction.CreateContentUrlInstruction;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Oct 28, 2010
 * Time: 9:21:22 PM
 */
public class CreateContentUrlInstructionTest
    extends TestCase
{

    @Test
    public void testDefault()
        throws Exception
    {
        CreateContentUrlInstruction i1 = new CreateContentUrlInstruction();
        CreateContentUrlInstruction i2 = new CreateContentUrlInstruction();

        String result = i1.serialize();

        assertNotNull( result );

        i2.deserialize( result );

        assertTrue( i1.equals( i2 ) );

    }

    @Test
    public void testValues()
        throws Exception
    {
        String contentKey = "123";

        String[] params = new String[3];
        params[0] = "param1";
        params[1] = "param2";
        params[2] = "param3";

        boolean createAsPermalink = true;

        CreateContentUrlInstruction instruction = new CreateContentUrlInstruction();

        instruction.setContentKey( contentKey );
        instruction.setParams( params );
        instruction.setCreateAsPermalink( createAsPermalink );

        String result = instruction.serialize();

        CreateContentUrlInstruction instruction2 = new CreateContentUrlInstruction();
        instruction2.deserialize( result );

        assertTrue( "Serialized and deserialized should be equals", instruction.equals( instruction2 ) );
        assertTrue( Arrays.equals( instruction.getParams(), instruction2.getParams() ) );
        assertTrue( instruction.getContentKey().equals( instruction2.getContentKey() ) );
        assertTrue( "createAsPermalink should be true", instruction2.isCreateAsPermalink() );
    }


}
