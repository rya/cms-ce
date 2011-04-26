/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.service;

import java.util.List;

import com.enonic.cms.core.security.user.User;

public interface UserServicesService
{
    public void createLogEntries( User user, String xmlData );

    public String getContent( User user, int key, boolean publishOnly );

    public String getContentTypeByCategory( int cKey );

    public String getContentTypeByContent( int contentKey );

    public User getAnonymousUser();

    public String getMenuItem( User user, int mikey );

    public int getCurrentVersionKey( int contentKey );

    List<User> findByCriteria( String nameExpression, String orderBy, boolean orderAscending );

    List<User> findAll();

    Long count();
}
