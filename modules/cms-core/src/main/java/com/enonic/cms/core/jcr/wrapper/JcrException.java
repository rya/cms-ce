/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jcr.wrapper;

import javax.jcr.RepositoryException;

public final class JcrException
    extends RuntimeException
{
    private JcrException( final RepositoryException e )
    {
        super( e );
    }

    public static JcrException wrap( final RepositoryException e )
    {
        return new JcrException( e );
    }
}
