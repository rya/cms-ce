/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain;

import java.util.List;
import java.util.Map;

import com.enonic.cms.portal.ContentPath;
import com.enonic.cms.portal.ContentPathResolver;
import com.enonic.cms.portal.WindowReference;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;

public class SitePath
{
    private SiteKey siteKey;

    private Path localPath;

    private String cachedParamsAsString;

    private RequestParameters requestParameters = new RequestParameters();

    private ContentPath contentPath;

    private WindowReference windowReference;

    public SitePath( SiteKey siteKey, String localPath, Map<String, String[]> params )
    {
        this( siteKey, new Path( localPath ), params );
    }

    public SitePath( SiteKey siteKey, Path localPath, Map<String, String[]> params )
    {
        this( siteKey, localPath );

        for ( Map.Entry<String, String[]> entry : params.entrySet() )
        {
            String key = entry.getKey();
            Object ov = entry.getValue();
            if ( ov instanceof String )
            {
                this.requestParameters.addParameterValue( key, (String) ov );
            }
            else if ( ov instanceof String[] )
            {
                this.requestParameters.addParameterValues( key, (String[]) ov );
            }
            else
            {
                throw new IllegalArgumentException( "Given Map params must have values of either String or String[]" );
            }
        }
    }

    public SitePath( SiteKey siteKey, String localPath )
    {
        this( siteKey, new Path( localPath ) );
    }

    public SitePath( SiteKey siteKey, Path localPath, RequestParameters requestParameters )
    {
        this( siteKey, localPath );

        this.requestParameters = requestParameters;
    }

    public SitePath( SiteKey siteKey, Path localPath )
    {
        Assert.notNull( siteKey, "Given siteKey cannot be null" );
        Assert.notNull( localPath, "Given localPath cannot be null" );

        if ( localPath.isRelative() && !localPath.isEmpty() )
        {
            this.localPath = Path.ROOT.appendPath( localPath );
        }
        else
        {
            this.localPath = localPath;
        }
        this.siteKey = siteKey;

        try
        {
            this.contentPath = ContentPathResolver.resolveContentPath(this.localPath);
        }
        catch ( Exception e )
        {
            // Do nothing
        }

        try
        {
            if ( this.localPath.contains( WindowReference.WINDOW_PATH_PREFIX ) )
            {
                this.windowReference = WindowReference.parse(this.localPath);
            }
        }
        catch ( Exception e )
        {
            // Do Nothing
        }
    }

    public SiteKey getSiteKey()
    {
        return siteKey;
    }

    public Path getLocalPath()
    {
        return localPath;
    }

    public boolean hasPathToContent()
    {
        return contentPath != null;
    }

    public boolean hasReferenceToWindow()
    {
        return windowReference != null;
    }

    public WindowReference getPortletReference()
    {
        return windowReference;
    }

    public ContentPath getContentPath()
    {
        return contentPath;
    }

    public Path resolvePathToMenuItem()
    {
        if ( hasReferenceToWindow() )
        {
            return getPortletReference().getPathToMenuItem();
        }
        else if ( hasPathToContent() )
        {
            return getContentPath().getPathToMenuItem();
        }

        return getLocalPath();
    }

    public List<String> getLocalPathElements()
    {
        return localPath.getPathElements();
    }

    public PathAndParams getPathAndParams()
    {
        return new PathAndParams( getLocalPath(), getRequestParameters() );
    }

    public SitePath setParam( String name, String value )
    {
        requestParameters.setParameterValue( name, value );
        // invalidating
        cachedParamsAsString = null;
        return this;
    }

    public SitePath addParam( String name, String value )
    {
        requestParameters.addParameterValue( name, value );
        // invalidating
        cachedParamsAsString = null;
        return this;
    }

    public SitePath addParams( Map<String, String> params )
    {
        for ( Map.Entry<String, String> entry : params.entrySet() )
        {
            addParam( entry.getKey(), entry.getValue() );
        }

        return this;
    }

    public void removeParam( String name )
    {
        requestParameters.removeParameter( name );
        // invalidating
        cachedParamsAsString = null;
    }

    public Map<String, String[]> getParams()
    {
        return requestParameters.getAsMapWithStringValues();
    }

    public String getParam( String name )
    {
        return requestParameters.getParameterValue( name );
    }

    public SitePath createNewInSameSite( Path localPath )
    {
        return new SitePath( getSiteKey(), localPath );
    }

    public SitePath createNewInSameSite( Path localPath, Map<String, String[]> params )
    {
        return new SitePath( getSiteKey(), localPath, params );
    }

    /**
     * Returns a new SitePath based on this with given path appended. The SitePath will inherit this SitePath's parameters.
     *
     * @param path path to append
     * @return A new <code>SitePath</code> instance, based on the longer path.
     */
    public SitePath appendPath( Path path )
    {

        Path newLocalPath = this.localPath.appendPath( path );
        return new SitePath( getSiteKey(), newLocalPath, getParams() );
    }

    public boolean equals( Object o )
    {

        if ( !( o instanceof SitePath ) )
        {
            return false;
        }
        if ( this == o )
        {
            return true;
        }
        SitePath other = (SitePath) o;
        return new EqualsBuilder().appendSuper( super.equals( o ) ).append( getSiteKey(), other.getSiteKey() ).append( getLocalPath(),
                                                                                                                       other.getLocalPath() ).isEquals();
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 173, 817 ).
            append( getSiteKey() ).
            append( getLocalPath() ).
            toHashCode();
    }

    public String asString()
    {
        SitePathToStringBuilder builder = new SitePathToStringBuilder();
        return builder.toString( this );
    }

    public boolean hasParams()
    {
        return getParams().size() > 0;
    }

    public String getParamsAsString()
    {
        if ( cachedParamsAsString == null )
        {
            RequestParametersToStringBuilder builder = new RequestParametersToStringBuilder();
            cachedParamsAsString = builder.toString( getRequestParameters() );
        }

        return cachedParamsAsString;
    }

    public RequestParameters getRequestParameters()
    {
        return requestParameters;
    }

    /**
     * Returns a new SitePath without any window reference. If this SitePath does not have any window reference, then this is returned.
     *
     * @return
     */
    public SitePath removeWindowReference()
    {
        if ( hasReferenceToWindow() )
        {
            Path pathWithout = getPortletReference().getPathToMenuItem();
            SitePath sitePathWithout = new SitePath( getSiteKey(), pathWithout, getParams() );
            return sitePathWithout;
        }
        else
        {
            return this;
        }
    }

    public String toString()
    {
        ToStringCreator string = new ToStringCreator( this );
        string.append( "siteKey", getSiteKey() );
        string.append( "localPath", getLocalPath() );
        string.append( "params", getParamsAsString() );
        return string.toString();
    }

    public void setContentPath( ContentPath contentPath )
    {
        this.contentPath = contentPath;
    }

}
