/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jcr.wrapper;

public interface JcrSession
{
    public JcrNode getRootNode();

    public void save();

    public JcrQuery createQuery(String statement);

}
