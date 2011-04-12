/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.instruction;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Nov 27, 2009
 * Time: 9:03:47 AM
 */
public class PostProcessInstructionSerializer
{


    public static String serialize( PostProcessInstruction instruction )
        throws IOException
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append( PostProcessInstructionPatterns.PPI_PREFIX );
        buffer.append( instruction.getType() );
        buffer.append( PostProcessInstructionPatterns.PPI_SEPARATOR );
        buffer.append( instruction.serialize() );
        buffer.append( PostProcessInstructionPatterns.PPI_POSTFIX );

        return buffer.toString();

    }

}
