/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.command;

import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.security.user.UserKey;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: May 5, 2010
 * Time: 11:18:33 AM
 */
public class SnapshotContentCommand
{
    ContentKey contentKey;

    UserKey modifier;

    String snapshotComment;

    boolean clearCommentInDraft = true;

    public ContentKey getContentKey()
    {
        return contentKey;
    }

    public void setContentKey( ContentKey contentKey )
    {
        this.contentKey = contentKey;
    }

    public UserKey getModifier()
    {
        return modifier;
    }

    public void setSnapshotterKey( UserKey snapshotter )
    {
        this.modifier = snapshotter;
    }

    public boolean doWipeComment()
    {
        return clearCommentInDraft;
    }

    public void setClearCommentInDraft( boolean clearCommentInDraft )
    {
        this.clearCommentInDraft = clearCommentInDraft;
    }

    public String getSnapshotComment()
    {
        return snapshotComment;
    }

    public void setSnapshotComment( String snapshotComment )
    {
        this.snapshotComment = snapshotComment;
    }
}
