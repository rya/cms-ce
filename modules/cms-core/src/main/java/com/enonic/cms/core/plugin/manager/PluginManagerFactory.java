package com.enonic.cms.core.plugin.manager;

import java.io.File;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;

import com.enonic.cms.api.client.LocalClient;
import com.enonic.cms.api.plugin.PluginEnvironment;
import com.enonic.cms.core.plugin.ExtensionListener;
import com.enonic.cms.core.plugin.PluginRegistry;
import com.enonic.cms.core.plugin.config.PluginConfigManager;
import com.enonic.cms.core.plugin.container.FelixOsgiContainer;
import com.enonic.cms.core.plugin.context.PluginContextManager;
import com.enonic.cms.core.plugin.deploy.HotDeployer;
import com.enonic.cms.core.plugin.host.HostActivator;
import com.enonic.cms.core.plugin.logger.LoggerActivator;
import com.enonic.cms.core.plugin.spring.SpringActivator;

public final class PluginManagerFactory
    implements FactoryBean, InitializingBean, DisposableBean, ServletContextAware
{
    private final FelixOsgiContainer container;

    private final PluginManager manager;

    private final HostActivator hostServices;

    private final PluginConfigManager configManager;

    private final HotDeployer hotDeployer;


    public PluginManagerFactory()
    {
        this.container = new FelixOsgiContainer();
        this.manager = new PluginManager();
        this.hostServices = new HostActivator();
        this.configManager = new PluginConfigManager();
        this.hotDeployer = new HotDeployer();

        this.container.addActivator( new LoggerActivator() );
        this.container.addActivator( this.hostServices );
        this.container.addActivator( this.manager );
        this.container.addActivator( this.configManager );
        this.container.addActivator( new PluginContextManager() );
        this.container.addActivator( this.hotDeployer );
        this.container.addActivator( new SpringActivator() );
    }

    public Object getObject()
        throws Exception
    {
        return this.manager;
    }

    public Class getObjectType()
    {
        return PluginRegistry.class;
    }

    public boolean isSingleton()
    {
        return true;
    }

    public void afterPropertiesSet()
        throws Exception
    {
        this.container.start();
    }

    public void destroy()
        throws Exception
    {
        this.container.stop();
    }

    public void setProperties( final Properties props )
    {
        this.container.setProperties( props );
    }

    public void setLocalClient( final LocalClient service )
    {
        this.hostServices.addService( service );
    }

    public void setPluginEnvironment( final PluginEnvironment service )
    {
        this.hostServices.addService( service );
    }

    public void setConfigDir( final File configDir )
    {
        this.configManager.setConfigDir( configDir );
    }

    public void setDeployDir( final File deployDir )
    {
        this.hotDeployer.setDeployDir( deployDir );
    }

    public void setScanPeriod( final long scanPeriod )
    {
        this.hotDeployer.setScanPeriod( scanPeriod );
    }

    public void setServletContext( final ServletContext context )
    {
        this.container.setServletContext( context );
    }

    @Autowired
    public void setExtensionListeners( List<ExtensionListener> listeners )
    {
        this.manager.setListeners( listeners );
    }
}
