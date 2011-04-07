/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure;

import org.jdom.Element;

import com.enonic.cms.domain.structure.DefaultSiteAccumulatedAccessRights;

/**
 * Nov 19, 2009
 */
public class DefaultSiteAccessRightsAccumulatedXmlCreator
{
    public void setUserRightAttributes( Element element, DefaultSiteAccumulatedAccessRights accumulatedAccessRights )
    {
        element.setAttribute( "userread", Boolean.toString( accumulatedAccessRights.isReadAccess() ) );
        element.setAttribute( "usercreate", Boolean.toString( accumulatedAccessRights.isCreateAccess() ) );
        element.setAttribute( "useradd", Boolean.toString( accumulatedAccessRights.isAddAccess() ) );
        element.setAttribute( "userpublish", Boolean.toString( accumulatedAccessRights.isPublishAccess() ) );
        element.setAttribute( "userupdate", Boolean.toString( accumulatedAccessRights.isUpdateAccess() ) );
        element.setAttribute( "userdelete", Boolean.toString( accumulatedAccessRights.isDeleteAccess() ) );
        element.setAttribute( "useradministrate", Boolean.toString( accumulatedAccessRights.isAdminAccess() ) );
    }
}