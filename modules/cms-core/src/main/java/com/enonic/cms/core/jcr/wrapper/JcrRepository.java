package com.enonic.cms.core.jcr.wrapper;

public interface JcrRepository
{

    public JcrSession login();

    public void logout(JcrSession session);
}
