/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.mail;

import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.internal.service.CmsCoreServicesSpringManagedBeansBridge;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.query.ContentByContentQuery;
import com.enonic.cms.domain.content.resultset.ContentResultSet;
import com.enonic.cms.domain.security.user.UserEntity;

public class ApproveAndRejectMailTemplate
    extends AbstractMailTemplate
{
    private String originalBody;

    private ContentKey contentKey;

    private UserEntity user;

    private ContentEntity content;

    private boolean reject;

    /**
     * Creates a new instance of ApproveAndRejectMailFormatter
     *
     * @param originalBody
     * @param contentKey
     * @param user
     */
    public ApproveAndRejectMailTemplate( String originalBody, ContentKey contentKey, UserEntity user )
    {
        this.originalBody = originalBody;
        this.contentKey = contentKey;
        this.user = user;

        content = getContent( contentKey, user );

    }

    private ContentEntity getContent( final ContentKey contentKey, final UserEntity user )
    {
        List<ContentKey> keyList = new ArrayList<ContentKey>();
        keyList.add( contentKey );

        ContentService contentService = CmsCoreServicesSpringManagedBeansBridge.getContentService();

        ContentByContentQuery contentByContentQuery = new ContentByContentQuery();
        contentByContentQuery.setUser( user );
        contentByContentQuery.setContentKeyFilter( keyList );

        ContentResultSet resultSet = contentService.queryContent( contentByContentQuery );
        if ( resultSet.getLength() < 1 )
        {
            throw new RuntimeException( "Content does not is exist" );
        }
        if ( resultSet.getLength() > 1 )
        {
            throw new RuntimeException( "getContent returned multiple contents for single content key" );
        }
        return resultSet.getContent( 0 );

        // ContentDao contentDao = CmsCoreServicesSpringManagedBeansBridge.getContentDao();
        // assert(contentDao != null);
        //
        // return contentDao.findByKey( new ContentKey (contentKey ) );
    }

    @Override
    public String getBody()
    {
        String contentPath = content.getPathAsString();

        String body = "";
        if ( originalBody != null )
        {
            body = originalBody;
        }

        if ( contentPath != null )
        {
            body = body + "\n\nYou will find the content at the following path in the content archive:\n" + contentPath;
        }

        String adminUrl = getAdminUrl( contentKey );
        if ( adminUrl != null )
        {
            body = body + "\n\n" + adminUrl;
        }

        return body;
    }

    @Override
    public String getSubject()
    {
        return isReject() ? createRejectSubject() : createApprovalSubject();
    }

    private String createRejectSubject()
    {
        return "Content was rejected: " + content.getMainVersion().getTitle();
    }

    private String createApprovalSubject()
    {
        return "Content waiting for approval: " + content.getMainVersion().getTitle();
    }

    public boolean isReject()
    {
        return reject;
    }

    public void setReject( boolean reject )
    {
        this.reject = reject;
    }
}
