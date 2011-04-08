/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.tools;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.enonic.cms.framework.util.HttpServletUtil;

/**
 * This class implements the properties info controller.
 */
@RequestMapping(value = "/tools/properties")
public final class PropertiesInfoController
    extends AbstractToolController
{

    private Properties properties;

    private DataSourceInfoResolver infoResolver;

    private File homeDir;

    @Value("${cms.home}")
    public void setHomeDir( File homeDir )
    {
        this.homeDir = homeDir;
    }

    public void setProperties( Properties properties )
    {
        this.properties = properties;
    }

    @Autowired
    public void setDataSourceInfoResolver( DataSourceInfoResolver infoResolver )
    {
        this.infoResolver = infoResolver;
    }

    private File getHomeDir()
    {
        return this.homeDir;
    }

    private File getConfigDir()
        throws Exception
    {
        for ( File file : getHomeDir().listFiles() )
        {
            if ( file.getName().equals( "config" ) )
            {
                return file;
            }
        }

        throw new IllegalStateException( "Config directory not found" );
    }

    private File getConfigFile( String name )
        throws Exception
    {
        for ( File file : getConfigDir().listFiles() )
        {
            if ( file.getName().equals( name ) )
            {
                return file;
            }
        }

        throw new IllegalStateException( "Config file [" + name + "] not found" );
    }

    private List<String> getConfigFiles()
        throws Exception
    {
        ArrayList<String> files = new ArrayList<String>();
        for ( File children : getConfigDir().listFiles() )
        {
            String name = children.getName();
            if ( name.endsWith( ".xml" ) || ( name.endsWith( ".properties" ) ) )
            {
                files.add( name );
            }
        }

        return files;
    }

    /**
     * Handle the request.
     */
    protected ModelAndView doHandleRequest( HttpServletRequest req, HttpServletResponse res )
        throws Exception
    {
        String downloadFile = req.getParameter( "download" );
        if ( downloadFile != null )
        {
            handleDownloadFile( downloadFile, res );
            return null;
        }

        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put( "homeDir", getHomeDir().getCanonicalPath() );
        model.put( "configDir", getConfigDir().getCanonicalPath() );
        model.put( "configFiles", getConfigFiles() );
        model.put( "properties", stripPasswords( this.properties ) );
        model.put( "systemProperties", stripPasswords( System.getProperties() ) );
        model.put( "dataSourceInfo", this.infoResolver.getInfo( false ) );
        return new ModelAndView( "propertiesInfoPage", model );
    }

    private Properties stripPasswords( Properties secretProperties )
    {
        Properties publicProperties = new Properties();
        for ( Map.Entry<Object, Object> prop : secretProperties.entrySet() )
        {
            if ( prop.getKey() instanceof String )
            {
                String key = (String) prop.getKey();
                if ( key.matches( ".*[Pp][Aa][Ss][Ss][Ww][Oo][Rr][Dd]$" ) )
                {
                    publicProperties.put( key, "****" );
                }
                else
                {
                    publicProperties.put( key, prop.getValue() );
                }
            }
            else
            {
                publicProperties.put( prop.getKey(), prop.getValue() );
            }
        }

        return publicProperties;
    }

    private void handleDownloadFile( String file, HttpServletResponse response )
        throws Exception
    {
        File child = getConfigFile( file );
        if ( child.exists() )
        {
            if ( file.endsWith( ".xml" ) )
            {
                response.setContentType( "text/xml" );
                HttpServletUtil.copyNoCloseOut( new FileInputStream( child ), response.getOutputStream() );
            }
            else
            {
                response.setContentType( "text/plain" );
                if ( file.endsWith( ".properties" ) )
                {
                    Properties props = new Properties();
                    props.load( new FileInputStream( child ) );
                    Properties secretProps = stripPasswords( props );
                    HttpServletUtil.outputProperitesNoCloseOut( secretProps, response.getWriter() );
                }
                else
                {
                    HttpServletUtil.copyNoCloseOut( new FileInputStream( child ), response.getOutputStream() );
                }
            }
        }
    }
}
