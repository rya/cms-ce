/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.internal.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.vertical.engine.UserServicesEngine;
import com.enonic.vertical.engine.VerticalCreateException;
import com.enonic.vertical.engine.VerticalSecurityException;

import com.enonic.cms.core.service.UserServicesService;

import com.enonic.cms.core.security.user.User;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class UserServicesServiceImpl
    implements UserServicesService
{

    protected UserServicesEngine userServicesEngine;

    public void setUserServicesEngine( UserServicesEngine userServicesEngine )
    {
        this.userServicesEngine = userServicesEngine;
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void createLogEntries( User user, String xmlData )
        throws VerticalCreateException, VerticalSecurityException
    {
        userServicesEngine.createLogEntries( user, xmlData );
    }

    /**
     * Transaction NB: Denne metoden er hardkodet til å ikke logge, så trenger ikke write.
     */
    public String getContent( User user, int key, boolean publishOnly, int parenLevel, int childrenLevel, int parenChildrenLevel )
    {
        return userServicesEngine.getContent( user, key, publishOnly, parenLevel, childrenLevel, parenChildrenLevel );
    }

    public String getContentTypeByCategory( int cKey )
    {
        return userServicesEngine.getContentTypeByCategory( cKey );
    }

    public String getContentTypeByContent( int contentKey )
    {
        return userServicesEngine.getContentTypeByContent( contentKey );
    }

    public User getAnonymousUser()
    {
        return userServicesEngine.getAnonymousUser();
    }

    public String getMenuItem( User user, int mikey )
    {
        return userServicesEngine.getMenuItem( user, mikey );
    }

    public int getCurrentVersionKey( int contentKey )
    {
        return userServicesEngine.getCurrentVersionKey( contentKey );
    }

}
