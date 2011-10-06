/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.binary;

import com.enonic.cms.domain.BadRequestErrorType;
import com.enonic.cms.domain.Path;
import com.enonic.cms.domain.StacktraceLoggingUnrequired;

public class InvalidBinaryPathException
    extends RuntimeException
    implements BadRequestErrorType, StacktraceLoggingUnrequired
{

    private Path binaryPath;

    private String message;


    public InvalidBinaryPathException( Path binaryPath, String detailMessage )
    {
        this.binaryPath = binaryPath;
        message = "Invalid binary path: '" + binaryPath.toString() + "', message: " + detailMessage;
    }

    public Path getBinaryPath()
    {
        return binaryPath;
    }

    public String getMessage()
    {
        return message;
    }
}
