/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;


public class CreateContentException
    extends RuntimeException
{

    public CreateContentException( RuntimeException cause )
    {
        super( "Failed to created content: " + cause.getMessage(), cause );
    }

    public RuntimeException getRuntimeExceptionCause()
    {
        return (RuntimeException) getCause();
    }

}
