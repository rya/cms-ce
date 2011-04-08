/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.instruction;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.util.HtmlUtils;

import com.enonic.cms.framework.util.UrlPathEncoder;

import com.enonic.cms.domain.portal.instruction.CreateContentUrlInstruction;
import com.enonic.cms.domain.portal.instruction.PostProcessInstruction;
import com.enonic.cms.domain.portal.instruction.PostProcessInstructionSerializer;

import static org.junit.Assert.*;

/**
 * Created by rmy - Date: Nov 18, 2009
 */
public class PostProcessInstructionParserTest
{

    private PostProcessInstructionParser parser;

    @Before
    public void setUp()
    {

    }

    @Test
    public void testParseInstruction()
        throws Exception
    {
        CreateContentUrlInstruction instruction = new CreateContentUrlInstruction();
        instruction.setContentKey( "1234" );
        instruction.setParams( new String[]{"param1", "value1"} );

        String document = createDocument( instruction, false, false );

        parser = new PostProcessInstructionParser( document );

        PostProcessInstruction processedInstruction = parser.next();

        assertNotNull( instruction );
        assertEquals( instruction, processedInstruction );
        assertTrue( processedInstruction.doDisableOutputEscaping() == true );
        assertTrue( processedInstruction.doUrlEncodeResult() == false );
    }

    @Test
    public void testParseEncodedInstruction()
        throws Exception
    {
        CreateContentUrlInstruction instruction = new CreateContentUrlInstruction();
        instruction.setContentKey( "1234" );
        instruction.setParams( new String[]{"param1", "value1"} );

        String document = createDocument( instruction, true, false );

        parser = new PostProcessInstructionParser( document );

        PostProcessInstruction processedInstruction = parser.next();

        assertNotNull( instruction );
        assertEquals( instruction, processedInstruction );
        assertTrue( processedInstruction.doDisableOutputEscaping() == true );
        assertTrue( processedInstruction.doUrlEncodeResult() == true );
    }

    @Test
    public void testParseEscapedInstruction()
        throws Exception
    {
        CreateContentUrlInstruction instruction = new CreateContentUrlInstruction();
        instruction.setContentKey( "1234" );
        instruction.setParams( new String[]{"param1", "value1"} );

        String document = createDocument( instruction, false, true );

        parser = new PostProcessInstructionParser( document );

        PostProcessInstruction processedInstruction = parser.next();

        assertNotNull( instruction );
        assertEquals( instruction, processedInstruction );
        assertTrue( processedInstruction.doDisableOutputEscaping() == false );
        assertTrue( processedInstruction.doUrlEncodeResult() == false );
    }

    @Test
    public void testParseEscapedAndEncodedInstruction()
        throws Exception
    {
        CreateContentUrlInstruction instruction = new CreateContentUrlInstruction();
        instruction.setContentKey( "1234" );
        instruction.setParams( new String[]{"param1", "value1"} );

        String document = createDocument( instruction, true, true );

        parser = new PostProcessInstructionParser( document );

        PostProcessInstruction processedInstruction = parser.next();

        assertNotNull( instruction );
        assertEquals( instruction, processedInstruction );
        assertTrue( processedInstruction.doDisableOutputEscaping() == false );
        assertTrue( processedInstruction.doUrlEncodeResult() == true );
    }


    private String createDocument( CreateContentUrlInstruction instruction, boolean enocode, boolean escape )
        throws IOException
    {
        StringBuffer buf = new StringBuffer();
        buf.append( "<html><body>" );
        buf.append( "<a href=\"" );

        String serializedInstruction = createSerializedInstruction( instruction );

        if ( escape )
        {
            serializedInstruction = HtmlUtils.htmlEscape( serializedInstruction );
        }

        if ( enocode )
        {
            serializedInstruction = UrlPathEncoder.encode( serializedInstruction );
        }

        buf.append( serializedInstruction );
        buf.append( "\">test</a>" );
        buf.append( "</body></html>" );
        String document = buf.toString();
        return document;
    }


    private String createSerializedInstruction( PostProcessInstruction instruction )
        throws IOException
    {
        return PostProcessInstructionSerializer.serialize( instruction );
    }


}


