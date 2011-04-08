/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.instruction;

import java.io.IOException;
import java.util.regex.Matcher;

import com.enonic.cms.framework.util.UrlPathDecoder;

import com.enonic.cms.domain.portal.instruction.CreateAttachmentUrlInstruction;
import com.enonic.cms.domain.portal.instruction.CreateContentUrlInstruction;
import com.enonic.cms.domain.portal.instruction.CreateImageUrlInstruction;
import com.enonic.cms.domain.portal.instruction.CreateResourceUrlInstruction;
import com.enonic.cms.domain.portal.instruction.PostProcessInstruction;
import com.enonic.cms.domain.portal.instruction.PostProcessInstructionPatterns;
import com.enonic.cms.domain.portal.instruction.PostProcessInstructionSerializingException;
import com.enonic.cms.domain.portal.instruction.PostProcessInstructionType;
import com.enonic.cms.domain.portal.instruction.PostProcessInstructionUnknownTypeException;
import com.enonic.cms.domain.portal.instruction.RenderWindowInstruction;

/**
 * Created by rmy - Date: Nov 18, 2009
 */
public class PostProcessInstructionParser
{
    final Matcher matcher;

    String currentFunction;

    public PostProcessInstructionParser( final String inputString )
    {
        matcher = PostProcessInstructionPatterns.POST_PROCESS_INSTRUCTION_PATTERN.matcher( inputString );
    }

    public PostProcessInstruction next()
    {
        if ( !matcher.find() )
        {
            return null;
        }

        currentFunction = matcher.group( 0 );
        String prefix = matcher.group( 1 );
        String instructionTypeString = matcher.group( 2 );
        String separator = matcher.group( 3 );
        String serializedInstruction = matcher.group( 4 );

        final boolean instructionHasBeenUrlEncoded = PostProcessInstructionPatterns.instructionHasBeenUrlEncoded( prefix );

        if ( instructionHasBeenUrlEncoded )
        {
            instructionTypeString = UrlPathDecoder.decode( instructionTypeString );
            separator = UrlPathDecoder.decode( separator );
            serializedInstruction = UrlPathDecoder.decode( serializedInstruction );
        }

        PostProcessInstruction instruction = parsePostProcessInstruction( instructionTypeString, serializedInstruction, separator );
        instruction.setDoUrlEncodeResult( instructionHasBeenUrlEncoded );

        return instruction;
    }

    public void appendTail( StringBuffer buffer )
    {
        matcher.appendTail( buffer );
    }

    public void replaceInInput( StringBuffer resultBuffer, String replaceString )
    {
        // Trick to prevent issues with replacement of results containing regexp placeholder symbols:
        // replace with empty string, then append the real replacement to stringbuffer directly

        matcher.appendReplacement( resultBuffer, "" );

        resultBuffer.append( replaceString );
    }

    public String getCurrentParsedInstuctionString()
    {
        return currentFunction;
    }

    private PostProcessInstruction parsePostProcessInstruction( String instructionTypeString, String serializedInstruction,
                                                                String separator )
    {
        PostProcessInstructionType instructionType = parseInstructionType( instructionTypeString );

        PostProcessInstruction instruction = createNewInstructionOfType( instructionType );
        instruction.setDisableOutputEscaping( !PostProcessInstructionPatterns.instructionHtmlEscaped( separator ) );
        instruction.setDoUrlEncodeResult( false );

        try
        {
            instruction.deserialize( serializedInstruction );
            return instruction;
        }
        catch ( IOException e )
        {
            throw new PostProcessInstructionSerializingException( "Deserialization of PostProcessInstruction failed", e );
        }
        catch ( ClassNotFoundException e )
        {
            throw new PostProcessInstructionSerializingException( "Deserialization of PostProcessInstruction failed", e );
        }
    }


    private PostProcessInstructionType parseInstructionType( String typeString )
    {
        try
        {
            return PostProcessInstructionType.valueOf( typeString );
        }
        catch ( IllegalArgumentException e )
        {
            throw new PostProcessInstructionUnknownTypeException( "Unknown PostProcessInstructionType: " + typeString );
        }
    }

    private PostProcessInstruction createNewInstructionOfType( PostProcessInstructionType type )
    {
        switch ( type )
        {
            case CREATE_WINDOWPLACEHOLDER:
                return new RenderWindowInstruction();
            case CREATE_RESOURCEURL:
                return new CreateResourceUrlInstruction();
            case CREATE_ATTACHMENTURL:
                return new CreateAttachmentUrlInstruction();
            case CREATE_IMAGEURL:
                return new CreateImageUrlInstruction();
            case CREATE_CONTENTURL:
                return new CreateContentUrlInstruction();
        }

        throw new IllegalArgumentException( "PostProcessInstructionType not handled: " + type.name() );
    }


}


