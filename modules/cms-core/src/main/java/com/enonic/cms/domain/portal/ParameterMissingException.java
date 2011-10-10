/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.portal;

import com.enonic.cms.core.StacktraceLoggingUnrequired;
import com.enonic.cms.core.BadRequestErrorType;

public class ParameterMissingException
    extends RuntimeException
    implements BadRequestErrorType, StacktraceLoggingUnrequired
{
    private String parameterName;

    private String message;


    public ParameterMissingException( String parameterName )
    {
        this.parameterName = parameterName;
        message = "Parameter missing: '" + parameterName + "'";
    }

    public String getParameterName()
    {
        return parameterName;
    }

    public String getMessage()
    {
        return message;
    }
}
