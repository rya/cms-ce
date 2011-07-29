/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

import java.io.Serializable;
import java.util.Date;

import com.enonic.cms.api.client.model.content.file.FileContentDataInput;


public class UpdateFileContentParams
    extends AbstractParams
    implements Serializable
{

    private static final long serialVersionUID = 4040833141662856676L;

    /**
     * The key of the content to update.
     */
    public Integer contentKey;

    /**
     * Optional. The key of the content's version to update. Set this if you want to update a specific version. This is only usable when
     * createNewVersion is set to false.
     */
    public Integer contentVersionKey;

    /**
     * The time from which the updated content will be online.  Should be set for every content with status, approved.
     */
    public Date publishFrom;

    /**
     * The time from which the updated content will go offline.  Set to null if there is no end date.
     * Does not need to be set for content that is not approved.
     */
    public Date publishTo;

    public boolean createNewVersion = true;

    /**
     * Set this to true if you want the given version or the new version to be the current version (main version) for the content.
     * Default is true.
     */
    public boolean setAsCurrentVersion = true;

    /**
     * If not set, the status of the given or current version will be used.
     *
     * @see com.enonic.cms.api.client.model.content.ContentStatus
     */
    public Integer status;

    public FileContentDataInput fileContentData;
}