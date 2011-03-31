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

    public Date publishFrom;

    public Date publishTo;

    public boolean createNewVersion = true;

    public boolean setAsCurrentVersion = true;

    /**
     * If not set, the status of the given or current version will be used.
     *
     * @see com.enonic.cms.api.client.model.content.ContentStatus
     */
    public Integer status;

    public FileContentDataInput fileContentData;
}