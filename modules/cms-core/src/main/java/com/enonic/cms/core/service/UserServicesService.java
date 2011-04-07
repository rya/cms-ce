/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.service;

import com.enonic.vertical.engine.VerticalCreateException;
import com.enonic.vertical.engine.VerticalSecurityException;

import com.enonic.cms.domain.security.user.User;

public interface UserServicesService
{

    public void createLogEntries( User user, String xmlData )
        throws VerticalCreateException, VerticalSecurityException;

    public String getContent( User user, int key, boolean publishOnly, int parenLevel, int childrenLevel, int parenChildrenLevel );

    public String getContentTypeByCategory( int cKey );

    /**
     * Retrieve the contenttype XML associated with a content.
     *
     * @param contentKey The content key.
     * @return The content type XML.
     */
    public String getContentTypeByContent( int contentKey );

    public User getAnonymousUser();

    public String getMenuItem( User user, int mikey );

    public int getCurrentVersionKey( int contentKey );

}
