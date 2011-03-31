/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.preference;

import java.io.Serializable;

public enum PreferenceScopeType
    implements Serializable
{

    GLOBAL,
    SITE,
    PAGE,
    PORTLET,
    WINDOW
}
