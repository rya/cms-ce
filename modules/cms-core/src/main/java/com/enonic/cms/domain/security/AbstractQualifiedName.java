/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.security;

/**
 * Jul 9, 2009
 */
public abstract class AbstractQualifiedName
    implements QualifiedName
{
    protected String stringValue;

    public String toString()
    {
        return stringValue;
    }
}
