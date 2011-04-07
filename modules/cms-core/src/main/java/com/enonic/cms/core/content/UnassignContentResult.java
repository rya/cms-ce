/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.security.user.UserKey;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Jun 22, 2010
 * Time: 1:09:21 PM
 */
public class UnassignContentResult
{
    private UserKey originalAssigner;

    private ContentKey unassignedContent;

    public UserKey getOriginalAssigner()
    {
        return originalAssigner;
    }

    public void setOriginalAssigner( UserKey originalAssigner )
    {
        this.originalAssigner = originalAssigner;
    }

    public ContentKey getUnassignedContent()
    {
        return unassignedContent;
    }

    public void setUnassignedContent( ContentKey unassignedContent )
    {
        this.unassignedContent = unassignedContent;
    }
}
