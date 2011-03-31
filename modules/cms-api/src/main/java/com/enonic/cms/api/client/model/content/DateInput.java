/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.content;

import java.io.Serializable;
import java.util.Date;

public class DateInput
    extends AbstractInput
    implements Serializable
{
    private static final long serialVersionUID = -6797314928288406401L;

    private Date date;

    /**
     * @param name
     * @param value If you supply null as input value, the existing value will be removed in a 'replace new' scenario.
     */
    public DateInput( String name, Date value )
    {
        super( InputType.DATE, name );
        this.date = value;
    }

    public Date getValueAsDate()
    {
        return date;
    }
}