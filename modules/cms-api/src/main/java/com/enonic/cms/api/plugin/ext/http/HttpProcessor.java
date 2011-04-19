package com.enonic.cms.api.plugin.ext.http;

import com.enonic.cms.api.plugin.ext.ExtensionBase;
import com.enonic.cms.api.util.Preconditions;

/**
 * This class implements the base for all http processor plugins.
 */
public abstract class HttpProcessor
    extends ExtensionBase
    implements Comparable<HttpProcessor>
{
    /**
     * Url patterns which determines when this plugin is executed.  If the pattern of the requested URL match any of the patters in this
     * list, this plugin is executed.
     * <p/>
     * This property may be set using either of the setPattern or setPatterns methods, but do not use both.  The settings of the first
     * method executed, will be overridden by the second method executed.
     */
    private String[] patterns = new String[0];

    /**
     * The order in which several plugins that all match the same request URL should be executed.
     */
    private int priority = 0;

    public final String[] getUrlPatterns()
    {
        return this.patterns;
    }

    /**
     * Sets a single URL pattern which will determine when this plugin is executed.  Using this method will override any previous settings
     * made with this method, or the <code>setUrlPatters</code> method.
     *
     * @param pattern A single string pattern that must be matched for this plugin to be executed.
     */
    public final void setUrlPattern( final String pattern )
    {
        Preconditions.checkNotNull( pattern );
        setUrlPatterns( new String[]{pattern} );
    }

    /**
     * Sets multiple URL patterns which will determine when this plugin is executed.  Using this method will override any previous settings
     * made with this method, or the <code>setUrlPatter</code> method.
     *
     * @param patterns A list of patterns that this processor can match to be executed.
     */
    public final void setUrlPatterns( final String[] patterns )
    {
        Preconditions.checkNotNull( patterns );
        this.patterns = patterns;
    }

    public final boolean matchesUrlPattern( final String path )
    {
        if ( path == null )
        {
            return false;
        }
        for ( String p : this.patterns )
        {
            if ( ( p != null ) && ( path.matches( p ) ) )
            {
                return true;
            }
        }
        return false;
    }

    public final int getPriority()
    {
        return this.priority;
    }

    public final void setPriority( int priority )
    {
        this.priority = priority;
    }

    /**
     * Compare to other object based on priority, in order to find the correct order of plugin execution.
     */
    public final int compareTo( final HttpProcessor other )
    {
        Preconditions.checkNotNull( other );
        return this.priority - other.priority;
    }
}
