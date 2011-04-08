/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering.portalfunctions;

/**
 * Nov 9, 2009
 */
public class PortalFunctionException
    extends RuntimeException
{
    private String failureReason;

    public PortalFunctionException( String failureReason, Throwable e )
    {
        super( failureReason, e );
        this.failureReason = failureReason;
    }

    public PortalFunctionException( String failureReason )
    {
        super( failureReason );
        this.failureReason = failureReason;
    }

    public String getFailureReason()
    {
        return failureReason;
    }
}
