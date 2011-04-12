/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

/**
 * This interface defines common constants.
 */
public interface ContentIndexConstants
{
    /**
     * Field names.
     */
    public static final String F_CREATED = "created";

    public static final String F_CONTENT_TYPE_NAME = "contentType";

    public static final String F_TIMESTAMP = "timestamp";

    public static final String F_MODIFIED = "modified";

    public static final String F_OWNER_KEY = "owner/key";

    public static final String F_OWNER_QUALIFIEDNAME = "owner/qualifiedName";

    public static final String F_MODIFIER_KEY = "modifier/key";

    public static final String F_MODIFIER_QUALIFIEDNAME = "modifier/qualifiedName";

    public static final String F_ASSIGNEE_QUALIFIEDNAME = "assignee/qualifiedName";

    public static final String F_ASSIGNER_QUALIFIEDNAME = "assigner/qualifiedName";

    public static final String F_ASSIGNMENT_DUE_DATE = "assignmentDueDate";

    public static final String F_TITLE = "title";

    public static final String F_FULLTEXT = "fullText";

    public static final String F_PRIORITY = "priority";

    /**
     * Meta fields.
     */
    public static final String M_KEY = "key";

    public static final String M_STATUS = "status";

    public static final String M_CATEGORY_KEY = "categoryKey";

    public static final String M_CONTENT_TYPE_KEY = "contentTypeKey";

    public static final String M_PUBLISH_FROM = "publishFrom";

    public static final String M_PUBLISH_TO = "publishTo";

    /**
     * All fields.
     */
    public static final String[] ALL_FIELDS =
        {M_KEY, F_CREATED, F_CONTENT_TYPE_NAME, F_TIMESTAMP, F_MODIFIED, M_PUBLISH_TO, M_PUBLISH_FROM, F_OWNER_KEY, F_OWNER_QUALIFIEDNAME,
            F_MODIFIER_KEY, F_MODIFIER_QUALIFIEDNAME, F_ASSIGNEE_QUALIFIEDNAME, F_ASSIGNER_QUALIFIEDNAME, F_ASSIGNMENT_DUE_DATE, F_TITLE,
            F_FULLTEXT, M_STATUS, F_PRIORITY, M_CONTENT_TYPE_KEY, M_CATEGORY_KEY};

    public static final String BLANK_REPLACER = "#";

    public static final String BLANK_PUBLISH_FROM_REPLACER = Long.toString( Long.MIN_VALUE );

    public static final String BLANK_PUBLISH_TO_REPLACER = Long.toString( Long.MAX_VALUE );
}
