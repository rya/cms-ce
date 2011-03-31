/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;


public abstract class RenderParams
    extends AbstractParams
{


    /**
     * Where there are templates for multiple devices, this value is used to determine which profile that should be rendered.
     */
    public String profile = null;

    /**
     * Whether or not URIs are encoded during rendering.  Default is <code>false<|code>.
     */
    public boolean encodeURIs = false;

    /**
     * Parameters or variables to the rendering engine, that are used in the templates.
     * Each parameter should be a string with an equal sign separating the name and the value of the parameter.
     */
    public String[] parameters = null;

    /**
     * The path within the site which the page should be rendered to.  This will affect how URIs that reference images and other pages
     * on the same site are rendered.
     */
    public String basePath = null;

    public String serverName;

    public int portNumber = 80;


}
