/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

import com.enonic.cms.api.client.model.preference.PreferenceScope;

public class GetPreferenceParams
    extends AbstractParams
{
    private static final long serialVersionUID = 8835663063064609797L;

    /**
     * Optionally specify scope from which to fetch preference. If no scope is specified the first matching preference in the hierarchy
     * is retrieved.
     * Default is none.
     */
    public PreferenceScope scope;

    /**
     * Specify which keys to collect, wild card * is permitted - i.e. for music* will fetch all entries with a key starting with music.
     * Default is none.
     */
    public String key;
}
