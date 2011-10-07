/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.group;

import java.util.Comparator;

/**
 * Jul 20, 2009
 */
public class GroupKeyComparator
    implements Comparator<GroupKey>
{
    public int compare( GroupKey a, GroupKey b )
    {
        return a.toString().compareTo( b.toString() );
    }
}
