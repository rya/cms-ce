package com.enonic.cms.core.jcr;

import java.io.File;
import java.net.URI;

import org.apache.jackrabbit.api.JackrabbitRepository;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.io.Files;

import com.enonic.cms.core.jcr.wrapper.JcrRepository;
import com.enonic.cms.core.jcr.wrapper.JcrWrappers;

public final class JcrRepositoryFactory
    implements FactoryBean<JcrRepository>, InitializingBean, DisposableBean
{
    private final static String REPOSITORY_CONFIG = "/META-INF/jackrabbit/repository.xml";

    private JackrabbitRepository repository;
    private File homeDir;

    public JcrRepository getObject()
    {
        return JcrWrappers.wrap( this.repository );
    }

    public Class<?> getObjectType()
    {
        return JcrRepository.class;
    }

    public boolean isSingleton()
    {
        return true;
    }

    public void afterPropertiesSet()
        throws Exception
    {
        this.repository = createRepository();
    }

    public void destroy()
    {
        this.repository.shutdown();
    }

    private JackrabbitRepository createRepository()
        throws Exception
    {
        final RepositoryConfig config = loadConfig();
        return RepositoryImpl.create(config);
    }

    private RepositoryConfig loadConfig()
        throws Exception
    {
        if (this.homeDir.exists()) {
            Files.deleteRecursively(this.homeDir);
        }

        final URI configUri = getClass().getResource(REPOSITORY_CONFIG).toURI();
        return RepositoryConfig.create(configUri, this.homeDir.getCanonicalPath());
    }

    @Value("${cms.home}/jackrabbit")
    public void setHomeDir(final File homeDir)
    {
        this.homeDir = homeDir;
    }
}
