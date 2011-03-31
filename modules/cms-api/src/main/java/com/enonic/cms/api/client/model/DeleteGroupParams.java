/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

import java.io.Serializable;

public class DeleteGroupParams
    extends AbstractParams
    implements Serializable
{

    private static final long serialVersionUID = 6426241884658911339L;

    /**
     * Specify group either by qualified group name (&lt;userStoreKey&gt;:&lt;group name&gt;) or key. When specifying a key, prefix with a
     * hash (group = #xxx).
     */
    public String group;

}