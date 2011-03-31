/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;


/**
 * This class implements parameters for renderPage.
 * <p/>
 * Rendering a page, will render the page of the given menu item key, based on the templates.
 */
public final class RenderPageParams
    extends RenderParams
{
    private static final long serialVersionUID = 8835663063064609797L;

    /**
     * The key of the menu item that should be rendered.
     * This field is mandatory.  If not set by user, the page will not be rendered, and an exception will be thrown.
     */
    public int menuItemKey = -1;


    /**
     * Tne 2 character language code to render this page with.
     */
    public String languageCode = null;


}
