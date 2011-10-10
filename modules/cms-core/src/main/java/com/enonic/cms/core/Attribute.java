/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

/**
 * These are the attributes available on the ServletRequest on a site request.
 */
public class Attribute
{

    public static final String ORIGINAL_URL = "com.enonic.render.url.original";

    public static final String ORIGINAL_SITEPATH = "com.enonic.render.sitePath.original";

    public static final String CURRENT_SITEPATH = "com.enonic.render.sitePath.current";

    public static final String ENCODE_URIS = "com.enonic.render.encodeURIs";

    public static final String BASEPATH_OVERRIDE_ATTRIBUTE_NAME = "com.enonic.render.basePathOverride";

    public static final String PREVIEW_ENABLED = "com.enonic.render.previewEnabled";

    public static final String LOCALIZATION_RESOLVED_LOCALE = "com.enonic.render.locale";
}
