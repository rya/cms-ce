/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Jul 27, 2010
 */
public class InvalidContentByQueryQueryException
    extends AbstractInvalidContentQueryException
{
    private List<String> issues = new ArrayList<String>();

    private static final String NAME = ContentByQueryQuery.class.getSimpleName();

    public InvalidContentByQueryQueryException( String... issues )
    {
        super( buildMessage( NAME, issues ) );
        this.issues.addAll( Arrays.asList( issues ) );
    }

    public List<String> getIssues()
    {
        return issues;
    }
}