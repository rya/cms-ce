/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.instruction;

import com.enonic.cms.domain.portal.instruction.PostProcessInstruction;
import com.enonic.cms.domain.portal.instruction.PostProcessInstructionSerializingException;
import com.enonic.cms.domain.portal.instruction.PostProcessInstructionUnknownTypeException;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Dec 15, 2010
 * Time: 2:48:29 PM
 */
public class PostProcessInstructionProcessor
{
    private final PostProcessInstructionContext context;

    private final PostProcessInstructionExecutor executor;

    public PostProcessInstructionProcessor( final PostProcessInstructionContext context, final PostProcessInstructionExecutor executor )
    {
        this.context = context;
        this.executor = executor;
    }

    public String processInstructions( String document )
    {
        PostProcessInstructionParser parser = new PostProcessInstructionParser( document );

        StringBuffer resultBuffer = new StringBuffer();

        PostProcessInstruction instruction = parser.next();

        while ( instruction != null )
        {
            try
            {
                String processedInstructionResult = executor.execute( instruction, context );

                parser.replaceInInput( resultBuffer, processedInstructionResult );
            }
            catch ( PostProcessInstructionUnknownTypeException e )
            {
                parser.replaceInInput( resultBuffer, parser.getCurrentParsedInstuctionString() );
            }
            catch ( PostProcessInstructionSerializingException e )
            {
                parser.replaceInInput( resultBuffer, parser.getCurrentParsedInstuctionString() );
            }

            instruction = parser.next();
        }

        parser.appendTail( resultBuffer );

        return resultBuffer.toString();
    }


}

