/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.instruction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.cms.portal.rendering.WindowRendererContext;

import com.enonic.cms.domain.portal.instruction.CreateAttachmentUrlInstruction;
import com.enonic.cms.domain.portal.instruction.CreateContentUrlInstruction;
import com.enonic.cms.domain.portal.instruction.CreateImageUrlInstruction;
import com.enonic.cms.domain.portal.instruction.CreateResourceUrlInstruction;
import com.enonic.cms.domain.portal.instruction.PostProcessInstruction;
import com.enonic.cms.domain.portal.instruction.PostProcessInstructionPatterns;
import com.enonic.cms.domain.portal.instruction.PostProcessInstructionSerializer;
import com.enonic.cms.domain.portal.instruction.RenderWindowInstruction;
import com.enonic.cms.domain.structure.SiteEntity;

import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Dec 15, 2010
 * Time: 4:15:52 PM
 */
public class PostProcessInstructionProcessorTest
{

    private PostProcessInstructionProcessor processor;

    private PostProcessInstructionExecutor executor;

    private static final String TROUBLE_REGEXP_REPLACEMENT_STRING = "$(function() \\ { $1})";

    private static final String PLACEHOLDER_PREFIX = PostProcessInstructionPatterns.PPI_PREFIX;

    private static final String PLACEHOLDER_SUFFIX = PostProcessInstructionPatterns.PPI_POSTFIX;

    private static final String ELEMENT_HEADER = TROUBLE_REGEXP_REPLACEMENT_STRING + "<html><body><p>";

    private static final String ELEMENT_RENDER_WINDOW_REPLACEMENT = TROUBLE_REGEXP_REPLACEMENT_STRING + "replaced:windowplaceholder";

    private static final String ELEMENT_CREATE_RESOURCEURL_REPLACEMENT = "$replaced:createresourceurlplaceholder";

    private static final String ELEMENT_CREATE_ATTACHMENTURL_REPLACEMENT = "$replaced:createattachmenturlplaceholder";

    private static final String ELEMENT_CREATE_IMAGEURL_REPLACEMENT = "$replaced:createimageurlplaceholder";

    private static final String ELEMENT_CREATE_CONTENTURL_REPLACEMENT = "$replaced:createcontenturlplaceholder";

    private static final String ELEMENT_MATCH_BUT_NO_INSTRUCTION_IN_TAG = PLACEHOLDER_PREFIX + "Bogus:bogus" + PLACEHOLDER_SUFFIX;

    private static final String ELEMENT_NOMATCH_IN_TAG = PLACEHOLDER_PREFIX + "this:willnotmatch--_" + PLACEHOLDER_SUFFIX;

    private static final String ELEMENT_UNCLOSED_TAG = "I am unclosed" + PLACEHOLDER_PREFIX;

    private static final String ELEMENT_TAIL = "</p></html>";

    @Before
    public void setUp()
    {
        PostProcessInstructionContext context = setUpContext();

        executor = Mockito.mock( PostProcessInstructionExecutor.class );

        setUpExecutorMocks();

        processor = new PostProcessInstructionProcessor( context, executor );
    }

    @Test
    public void testNoInstructions()
        throws Exception
    {
        final String document = createInputDoc( Lists.<PostProcessInstruction>newArrayList() );

        String processedDocument = processor.processInstructions( document );

        assertEquals( document, processedDocument );
    }

    @Test
    public void testProcessInstructions()
        throws IOException
    {
        assertTrue( true );

        List<PostProcessInstruction> instructions = createPostProcessInstructionList();

        String inputDoc = createInputDoc( instructions );

        String result = processor.processInstructions( inputDoc );

        // The resulting document should contain a certain number of each element based on the instruction-list
        assertEquals( "Wrong number of headers", 1, numberOfMatches( ELEMENT_HEADER, result ) );
        assertEquals( "Wrong number of unclosed tags", 4, numberOfMatches( ELEMENT_UNCLOSED_TAG, result ) );
        assertEquals( "Wrong number of windowsPlaceholder's", 2, numberOfMatches( ELEMENT_RENDER_WINDOW_REPLACEMENT, result ) );
        assertEquals( "Wrong number of createResourceUrl's", 3, numberOfMatches( ELEMENT_CREATE_RESOURCEURL_REPLACEMENT, result ) );
        assertEquals( "Wrong number of createContentUrl's", 2, numberOfMatches( ELEMENT_CREATE_CONTENTURL_REPLACEMENT, result ) );
        assertEquals( "Wrong number of createAttachmetUrl's", 2, numberOfMatches( ELEMENT_CREATE_ATTACHMENTURL_REPLACEMENT, result ) );
        assertEquals( "Wrong number of createImageUrl's", 2, numberOfMatches( ELEMENT_CREATE_IMAGEURL_REPLACEMENT, result ) );
        assertEquals( "Wrong number of bogus instructions", 1, numberOfMatches( ELEMENT_MATCH_BUT_NO_INSTRUCTION_IN_TAG, result ) );
        assertEquals( "Wrong number of no-match in tags", 2, numberOfMatches( ELEMENT_NOMATCH_IN_TAG, result ) );
        assertEquals( "Wrong number of tails", 1, numberOfMatches( ELEMENT_TAIL, result ) );
    }


    private PostProcessInstructionContext setUpContext()
    {
        HttpServletRequest request = Mockito.mock( HttpServletRequest.class );

        PostProcessInstructionContext context = new PostProcessInstructionContext();
        WindowRendererContext windowRendererContext = new WindowRendererContext();
        context.setHttpRequest( request );

        SiteEntity site = new SiteEntity();
        site.setKey( 1 );

        context.setSite( site );

        context.setWindowRendererContext( windowRendererContext );

        return context;
    }

    private List<PostProcessInstruction> createPostProcessInstructionList()
    {
        List<PostProcessInstruction> instructions = new ArrayList<PostProcessInstruction>();
        instructions.add( new RenderWindowInstruction() );
        instructions.add( new CreateImageUrlInstruction() );
        instructions.add( new CreateImageUrlInstruction() );
        instructions.add( new CreateResourceUrlInstruction() );
        instructions.add( new CreateContentUrlInstruction() );
        instructions.add( new CreateAttachmentUrlInstruction() );
        instructions.add( new RenderWindowInstruction() );
        instructions.add( new CreateAttachmentUrlInstruction() );
        instructions.add( new CreateResourceUrlInstruction() );
        instructions.add( new CreateResourceUrlInstruction() );
        instructions.add( new CreateContentUrlInstruction() );
        return instructions;
    }

    private int numberOfMatches( String pattern, String matchString )
    {
        Matcher matcher = Pattern.compile( Pattern.quote( pattern ) ).matcher( matchString );

        int count = 0;

        while ( matcher.find() )
        {
            count++;
        }

        return count;
    }

    private String createInputDoc( List<PostProcessInstruction> instructions )
        throws IOException
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append( ELEMENT_HEADER );
        buffer.append( ELEMENT_NOMATCH_IN_TAG );
        buffer.append( ELEMENT_UNCLOSED_TAG );

        for ( PostProcessInstruction instruction : instructions )
        {
            buffer.append( "<a href=\"" );
            buffer.append( createSerializedInstruction( instruction ) );
            buffer.append( "\">test</a>" );
        }
        buffer.append( ELEMENT_UNCLOSED_TAG );
        buffer.append( ELEMENT_NOMATCH_IN_TAG );
        buffer.append( ELEMENT_UNCLOSED_TAG );
        buffer.append( ELEMENT_MATCH_BUT_NO_INSTRUCTION_IN_TAG );
        buffer.append( ELEMENT_UNCLOSED_TAG );
        buffer.append( ELEMENT_TAIL );

        return buffer.toString();
    }

    private void setUpExecutorMocks()
    {
        executor = Mockito.mock( PostProcessInstructionExecutor.class );

        Mockito.when( executor.execute( Mockito.isA( CreateResourceUrlInstruction.class ),
                                        Mockito.isA( PostProcessInstructionContext.class ) ) ).thenReturn(
            ELEMENT_CREATE_RESOURCEURL_REPLACEMENT );

        Mockito.when( executor.execute( Mockito.isA( RenderWindowInstruction.class ),
                                        Mockito.isA( PostProcessInstructionContext.class ) ) ).thenReturn(
            ELEMENT_RENDER_WINDOW_REPLACEMENT );

        Mockito.when( executor.execute( Mockito.isA( CreateAttachmentUrlInstruction.class ),
                                        Mockito.isA( PostProcessInstructionContext.class ) ) ).thenReturn(
            ELEMENT_CREATE_ATTACHMENTURL_REPLACEMENT );

        Mockito.when( executor.execute( Mockito.isA( CreateImageUrlInstruction.class ),
                                        Mockito.isA( PostProcessInstructionContext.class ) ) ).thenReturn(
            ELEMENT_CREATE_IMAGEURL_REPLACEMENT );

        Mockito.when( executor.execute( Mockito.isA( CreateContentUrlInstruction.class ),
                                        Mockito.isA( PostProcessInstructionContext.class ) ) ).thenReturn(
            ELEMENT_CREATE_CONTENTURL_REPLACEMENT );
    }

    private String createSerializedInstruction( PostProcessInstruction instruction )
        throws IOException
    {
        return PostProcessInstructionSerializer.serialize( instruction );
    }


}
