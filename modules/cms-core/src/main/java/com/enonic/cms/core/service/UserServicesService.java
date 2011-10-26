/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.service;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.vertical.engine.VerticalCreateException;
import com.enonic.vertical.engine.VerticalSecurityException;

import com.enonic.cms.core.security.user.User;

public interface UserServicesService
{
    public XMLDocument getContent( User user, int key, boolean publishOnly, int parenLevel, int childrenLevel, int parenChildrenLevel );

    public XMLDocument getContentTypeByCategory( int cKey );

    /**
     * Retrieve the contenttype XML associated with a content.
     *
     * @param contentKey The content key.
     * @return The content type XML.
     */
    public XMLDocument getContentTypeByContent( int contentKey );

    public User getAnonymousUser();

    public XMLDocument getMenuItem( User user, int mikey );

    public int getCurrentVersionKey( int contentKey );

}
