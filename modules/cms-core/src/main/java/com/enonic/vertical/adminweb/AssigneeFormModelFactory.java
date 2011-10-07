/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.SiteDao;

import com.enonic.cms.core.content.ContentAndVersion;
import com.enonic.cms.core.content.ContentVersionEntity;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Apr 30, 2010
 * Time: 11:42:00 AM
 */
public class AssigneeFormModelFactory
{
    SiteDao siteDao;

    ContentDao contentDao;

    public AssigneeFormModelFactory( SiteDao siteDao, ContentDao contentDao )
    {
        this.siteDao = siteDao;
        this.contentDao = contentDao;
    }

    public AssigneeFormModel createAssigneeFormModel( UserEntity user, String contentKey )
    {
        AssigneeFormModel model = new AssigneeFormModel();

        if ( contentKey == null )
        {
            throw new IllegalArgumentException( "ContentKey should not be null" );
        }

        model.setUser( user );

        ContentEntity content = contentDao.findByKey( new ContentKey( contentKey ) );

        if ( content == null )
        {
            throw new IllegalArgumentException( "Content not found" );
        }

        model.setContentAndVersion( createContentAndVersion( content ) );

        return model;
    }

    private ContentAndVersion createContentAndVersion( ContentEntity content )
    {
        ContentVersionEntity version = content.getMainVersion();
        return new ContentAndVersion( content, version );
    }

}
