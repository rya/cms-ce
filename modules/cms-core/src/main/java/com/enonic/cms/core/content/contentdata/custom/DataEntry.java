/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;


public interface DataEntry
{
    String getName();

    DataEntryType getType();

    String getXPath();

    void validate();

    /**
     * Returns whether the data entry is intanciated with a value or not.
     * Ables us to differentiate between a data entry present with no value (hasValue = false)
     * and not present at all.
     */
    boolean hasValue();

    /**
     * Returns whether the data entry breaks the required contract of it's type. I.e. the required contract is broken for a text data entry
     * if the text is not larger than zero.
     */
    boolean breaksRequiredContract();
}
