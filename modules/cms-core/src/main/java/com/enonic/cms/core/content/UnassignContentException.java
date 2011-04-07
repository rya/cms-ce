/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Jun 21, 2010
 * Time: 1:12:29 PM
 */
public class UnassignContentException
    extends RuntimeException
{
    public UnassignContentException( RuntimeException cause )
    {
        super( "Failed to unassign content: " + cause.getMessage(), cause );
    }


    public RuntimeException getRuntimeExceptionCause()
    {
        return (RuntimeException) getCause();
    }
}
