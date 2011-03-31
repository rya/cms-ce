/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain;

import java.util.List;

/**
 * This class defines the content result set.
 */
public interface ResultSet
{
    /**
     * @return The number of hits.
     */
    int getLength();

    /**
     * @return The from index.
     */
    int getFromIndex();

    /**
     * @return The total count.
     */
    int getTotalCount();


    boolean hasErrors();

    List<String> getErrors();
}
