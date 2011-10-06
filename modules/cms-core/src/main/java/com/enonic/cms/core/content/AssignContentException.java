/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Jun 21, 2010
 * Time: 1:11:34 PM
 */
public class AssignContentException
    extends RuntimeException
{

    public AssignContentException( RuntimeException cause )
    {
        super( "Failed to assign content: " + cause.getMessage(), cause );
    }


    public RuntimeException getRuntimeExceptionCause()
    {
        return (RuntimeException) getCause();
    }
}
