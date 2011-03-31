/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.content;

import java.io.Serializable;

public class RelatedContentInput
    extends AbstractIntValueInput
    implements Serializable
{
    private static final long serialVersionUID = -2063735399278309582L;

    /**
     * @param name
     * @param contentKey If you supply null as input value, the existing value will be removed in a 'replace new' scenario.
     */
    public RelatedContentInput( String name, Integer contentKey )
    {
        super( InputType.RELATED_CONTENT, name, contentKey );
    }
}