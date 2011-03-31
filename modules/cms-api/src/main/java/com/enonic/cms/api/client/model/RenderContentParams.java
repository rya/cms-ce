/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

/**
 * This class implements parameters for renderContent.
 * <p/>
 * Rendering content will render a page that includes the given content key, based on the templates.
 */
public final class RenderContentParams
    extends RenderParams
{
    private static final long serialVersionUID = 8835663063064609797L;

    /**
     * The key of the site in which the content should be rendered.
     * This field is mandatory.  If not set by user, the content will not be rendered, and an exception will be thrown.
     * Also, if no template is found within this site for the given content, an error will occur.
     */
    public int siteKey = -1;

    /**
     * Key of the content to retrieve.
     * This field is mandatory.  If not set by user, the content will not be rendered, and an exception will occur.
     */
    public int contentKey = -1;


}
