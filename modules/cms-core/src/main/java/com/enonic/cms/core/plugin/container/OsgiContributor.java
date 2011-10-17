package com.enonic.cms.core.plugin.container;

import org.osgi.framework.BundleActivator;

public abstract class OsgiContributor
    implements BundleActivator, Comparable<OsgiContributor>
{
    private final int rank;

    public OsgiContributor(final int rank)
    {
        this.rank = rank;
    }

    public final int compareTo(final OsgiContributor other)
    {
        return this.rank - other.rank;
    }
}
