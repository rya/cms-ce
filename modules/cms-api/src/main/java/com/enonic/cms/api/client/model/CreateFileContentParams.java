/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

import java.io.Serializable;
import java.util.Date;

import com.enonic.cms.api.client.model.content.ContentStatus;
import com.enonic.cms.api.client.model.content.file.FileContentDataInput;


public class CreateFileContentParams
    extends AbstractParams
    implements Serializable
{

    private static final long serialVersionUID = 440851205774364145L;

    public Integer categoryKey;

    public Date publishFrom;

    public Date publishTo;

    /**
     * Default is DRAFT.
     *
     * @see com.enonic.cms.api.client.model.content.ContentStatus
     */
    public Integer status = ContentStatus.STATUS_DRAFT;


    public FileContentDataInput fileContentData;

}