package com.enonic.cms.core.plugin.container;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.Constants;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

import com.enonic.cms.api.util.LogFacade;

public abstract class OsgiContainer
    implements Constants
{
    protected final LogFacade logger;

    private final List<BundleActivator> activators;

    private Map<String, String> properties;

    private ServletContext servletContext;

    public OsgiContainer()
    {
        this.logger = LogFacade.get( getClass() );
        this.properties = Maps.newHashMap();
        this.activators = Lists.newArrayList();
    }

    public final void addActivator( final BundleActivator activator )
    {
        this.activators.add( activator );
    }

    public final void setProperties( final Properties properties )
    {
        this.properties = Maps.fromProperties( properties );
    }

    protected final List<BundleActivator> getActivators()
    {
        return this.activators;
    }

    protected Map<String, Object> getConfigMap()
        throws Exception
    {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.putAll( this.properties );

        map.put( FRAMEWORK_SYSTEMPACKAGES_EXTRA, getSystemPackagesExtra() );
        map.put( FRAMEWORK_STORAGE_CLEAN, FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT );
        map.put( FRAMEWORK_STORAGE, Files.createTempDir().getAbsolutePath() );

        if ( this.properties.get( FRAMEWORK_BOOTDELEGATION ) == null )
        {
            map.put( FRAMEWORK_BOOTDELEGATION, getBootDelegationPackages() );
        }

        return map;
    }

    private String getSystemPackagesExtra()
        throws Exception
    {
        final ExportsBuilder builder = new ExportsBuilder( this.servletContext );
        return builder.getExports();
    }

    private String getBootDelegationPackages()
    {
        return "com.yourkit,com.yourkit.*," + "com.jprofiler,com.jprofiler.*," + "org.apache.xerces,org.apache.xerces.*," +
            "org.apache.xalan,org.apache.xalan.*," + "org.apache.xml.serializer," + "sun.*," + "com.sun.xml.bind.v2";
    }

    public void setServletContext( final ServletContext servletContext )
    {
        this.servletContext = servletContext;
    }

    public abstract void start()
        throws Exception;

    public abstract void stop()
        throws Exception;
}
