/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.instruction;

import com.enonic.cms.domain.portal.instruction.PostProcessInstruction;

/**
 * Created by rmy - Date: Nov 18, 2009
 */
public interface PostProcessInstructionExecutor
{
    public String execute( PostProcessInstruction instruction, PostProcessInstructionContext context );

}                                  
