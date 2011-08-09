/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Jun 10, 2010
 * Time: 4:49:10 PM
 */
public class SnapshotContentParams
    extends AbstractParams
{
    private static final long serialVersionUID = -5753543969892262043L;

    public int contentKey;

    public String snapshotComment;

    public boolean clearCommentInDraft = false;

}
