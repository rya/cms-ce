/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.group;

import com.enonic.cms.domain.NotFoundErrorType;

public class GroupNotFoundException
    extends RuntimeException
    implements NotFoundErrorType
{

    private GroupKey groupKey;

    private QualifiedGroupname qname;

    private String message;

    private boolean isKey = false;

    public GroupNotFoundException( QualifiedGroupname qname )
    {
        this.qname = qname;
        message = "Group not found, qualified group name: '" + qname + "'";
    }

    public GroupNotFoundException( GroupKey groupKey )
    {
        this.groupKey = groupKey;
        message = "Group not found, key: '" + groupKey + "'";
        this.isKey = true;
    }

    public boolean isKey()
    {
        return isKey;
    }

    public GroupKey getUserKey()
    {
        return groupKey;
    }

    public QualifiedGroupname getQualifiedName()
    {
        return qname;
    }

    public String getMessage()
    {
        return message;
    }
}