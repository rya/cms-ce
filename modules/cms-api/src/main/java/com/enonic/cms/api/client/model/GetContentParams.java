/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

/**
 * This class implements parameters for getContent.  In a method call, it is the content keys specified that defines which content to get,
 * so obviously, call makes no sense, unless at least one content key is provided.
 */
public final class GetContentParams
    extends AbstractParams
{
    private static final long serialVersionUID = 8835663063064609797L;

    /**
     * Key(s) of the content to retrieve.
     * This field is mandatory.  If not set by user, no content will be returned.
     */
    public int[] contentKeys = new int[]{-1};

    /**
     * Syntax as specified in the Content Query Language (se documentation).
     * Default is empty, which, if not overruled, will return every content for the given categories.
     */
    public String query = "";

    /**
     * Syntax as specified in the Content Query Language (se documentation).
     * Default is empty, which means there are no ordering defined.
     */
    public String orderBy = "";

    /**
     * Specifies start index of the result.
     * Default is 0.
     */
    public int index = 0;

    /**
     * Specifies the count of how many content elements to include in the result.
     * Default is 100.
     */
    public int count = 100;

    /**
     * Specifies if the data element of the contents should be included in the XML.
     * Default is false.
     */
    public boolean includeData = true;

    /**
     * How many levels of children related contents to retrieve.
     * Default is 0.
     */
    public int childrenLevel = 0;

    /**
     * How many levels of parent related contents to retrieve.
     * Default is 0.
     */
    public int parentLevel = 0;

    /**
     * Specifies if user rights information should be included in the XML.
     * Default is false.
     */
    public boolean includeUserRights = false;

    /**
     * Specifies if content that matches the search, but is offline, should be included in the result set.
     * Default is false.
     */
    public boolean includeOfflineContent = false;
}
