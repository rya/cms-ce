/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Jun 22, 2010
 * Time: 8:22:06 AM
 */
public class UpdateAssignmentException
    extends RuntimeException
{

    public UpdateAssignmentException( RuntimeException cause )
    {
        super( "Failed to update assignment: " + cause.getMessage(), cause );
    }


    public RuntimeException getRuntimeExceptionCause()
    {
        return (RuntimeException) getCause();
    }


}
