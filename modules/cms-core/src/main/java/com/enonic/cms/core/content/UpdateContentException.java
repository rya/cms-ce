/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Jun 21, 2010
 * Time: 1:07:15 PM
 */
public class UpdateContentException
    extends RuntimeException

{
    public UpdateContentException( RuntimeException cause )
    {
        super( "Failed to update content: " + cause.getMessage(), cause );
    }


    public RuntimeException getRuntimeExceptionCause()
    {
        return (RuntimeException) getCause();
    }
}
