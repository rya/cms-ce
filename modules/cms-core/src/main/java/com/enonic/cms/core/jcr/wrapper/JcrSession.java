package com.enonic.cms.core.jcr.wrapper;

public interface JcrSession
{
    public JcrNode getRootNode();

    public void save();

    public JcrQuery createQuery(String statement);

}
