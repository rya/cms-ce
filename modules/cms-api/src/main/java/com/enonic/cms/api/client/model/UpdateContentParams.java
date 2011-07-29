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
     * There are two possible settings for the updateStrategy: <code>REPLACE_ALL</code> and <code>REPLACE_NEW</code>.
     * <code>REPLACE_NEW</code> may be used, when only one or a few fields should to be changed.  The provided values
     * will be changed, and all others will be left unchanged.
     * With <code>REPLACE_ALL</code>, every field in the new content must have a value, and will be set to whatever
     * value is provided.  This is the only way to remove the data for a field that has had a value that should be
     * changed to a blank value or no value.  If <code>REPLACE_NEW</code> is used, a field will no value will not be
     * changed.
     *
     * These strategies apply only to the data set in the <code>contentData</code> field.
     * <code>publishFrom</code>, <code>publishTo</code>, <code>createNewVersion</code> and <code>setAsCurrentVersion</code>
     * are metadata that are not affected by this update strategy.
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