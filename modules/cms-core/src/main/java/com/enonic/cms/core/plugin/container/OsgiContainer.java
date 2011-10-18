package com.enonic.cms.core.plugin.container;

import java.util.*;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.google.common.io.Files;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.util.FelixConstants;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.enonic.cms.api.util.LogFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class OsgiContainer
    implements FelixConstants
{
    protected final static LogFacade LOG = LogFacade.get(OsgiContainer.class);

    private List<OsgiContributor> contributors;
    private Map<String, String> properties;
    private Felix felix = null;

    @Autowired
    public void setContributors(final List<OsgiContributor> list)
    {
        this.contributors = Lists.newArrayList(list);
        Collections.sort(this.contributors);
    }

    @Value("#{config.map}")
    public void setProperties( final Map<String, String> properties )
    {
        this.properties = properties;
    }

    private Map<String, Object> createConfigMap()
    {
        final FelixLogBridge logBridge = new FelixLogBridge();

        final Map<String, Object> map = Maps.newHashMap();
        map.putAll( this.properties );

        map.put( FRAMEWORK_STORAGE, Files.createTempDir().getAbsolutePath() );
        map.put( FRAMEWORK_STORAGE_CLEAN, FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT );
        map.put( LOG_LEVEL_PROP, String.valueOf( logBridge.getLogLevel() ) );
        map.put( LOG_LOGGER_PROP, logBridge );
        map.put( IMPLICIT_BOOT_DELEGATION_PROP, "true" );
        map.put( FRAMEWORK_BOOTDELEGATION, "*" );
        map.put( FRAMEWORK_BUNDLE_PARENT, FRAMEWORK_BUNDLE_PARENT_FRAMEWORK );
        map.put( SYSTEMBUNDLE_ACTIVATORS_PROP, this.contributors );

        return map;
    }

    @PostConstruct
    public void start()
        throws Exception
    {
        this.felix = new Felix( createConfigMap() );
        LOG.info( "Starting Felix OSGi Container ({0})", this.felix.getVersion() );
        this.felix.start();

        LOG.info( "OSGi container started and running" );
    }

    @PreDestroy
    public void stop()
        throws Exception
    {
        this.felix.stop();
        this.felix.waitForStop( 5000 );
    }
}
