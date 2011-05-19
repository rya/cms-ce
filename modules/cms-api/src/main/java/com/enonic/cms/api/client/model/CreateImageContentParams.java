package com.enonic.cms.api.client.model;

import java.util.Date;

import com.enonic.cms.api.client.model.content.ContentStatus;
import com.enonic.cms.api.client.model.content.image.ImageContentDataInput;

public class CreateImageContentParams
        extends AbstractParams
{
    public Integer categoryKey;

    public Date publishFrom;

    public Date publishTo;

    /**
     * Default is DRAFT.
     *
     * @see com.enonic.cms.api.client.model.content.ContentStatus
     */
    public Integer status = ContentStatus.STATUS_DRAFT;

    public String changeComment;

    public ImageContentDataInput contentData;
}
