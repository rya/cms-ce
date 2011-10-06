/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Jun 21, 2010
 * Time: 1:13:31 PM
 */
public class SnapshotContentException
    extends RuntimeException
{

    public SnapshotContentException( RuntimeException cause )
    {
        super( "Failed to snapshot content: " + cause.getMessage(), cause );
    }


    public RuntimeException getRuntimeExceptionCause()
    {
        return (RuntimeException) getCause();
    }
}
