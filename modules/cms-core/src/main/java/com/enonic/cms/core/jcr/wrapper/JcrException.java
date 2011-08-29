package com.enonic.cms.core.jcr.wrapper;

import javax.jcr.RepositoryException;

public final class JcrException
    extends RuntimeException
{
    public static JcrException wrap(final RepositoryException e)
    {
        return null;
    }
}
