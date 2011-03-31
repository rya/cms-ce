/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

import java.io.Serializable;
import java.util.Date;

import com.enonic.cms.api.client.model.content.ContentDataInput;


public class UpdateContentParams
    extends AbstractParams
    implements Serializable
{

    private static final long serialVersionUID = -5672180708283513759L;

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

    /**
     * Set this to true if you want the given version or the new version to be the current version (main version) for the content. Default
     * is true.
     */
    public boolean setAsCurrentVersion = true;

    /**
     * If not set, the status of the given or current version will be used.
     *
     * @see com.enonic.cms.api.client.model.content.ContentStatus
     */
    public Integer status;


    /**
     * Change this to 'replace new' if you only supply the input types that should be changed. Leaving this to 'replace all' will force
     * you supply all the input types.
     */
    public ContentDataInputUpdateStrategy updateStrategy = ContentDataInputUpdateStrategy.REPLACE_ALL;


    /**
     * Note:
     * For update strategy 'replace all': You must include all the input types - also those that you do not want to change.
     * <p/>
     * For update strategy 'replace new': You should only include the input types that you would like to change.
     * To remove a input type, it must be included but without any value (null).
     * <p/>
     * If you want to update the contents meta data (like publishFrom, publishTo, currentVersion and status) without creating
     * a new version then you must leave this blank.
     */
    public ContentDataInput contentData;

    public String changeComment;

}