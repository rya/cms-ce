package com.enonic.cms.core.plugin;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.enonic.cms.api.plugin.PluginEnvironment;
import com.enonic.cms.core.servlet.ServletRequestAccessor;

/**
 * This class implements the plugin environment.
 */
public final class PluginEnvironmentImpl
    implements PluginEnvironment
{
    /**
     * Shared object map.
     */
    private final Map<String, Serializable> sharedObjects;

    /**
     * Construct the environment.
     */
    public PluginEnvironmentImpl()
    {
        this.sharedObjects = new HashMap<String, Serializable>();
    }

    /**
     * Return the current http servlet request.
     */
    public HttpServletRequest getCurrentRequest()
    {
        return ServletRequestAccessor.getRequest();
    }

    /**
     * Return the current http servlet session.
     */
    public HttpSession getCurrentSession()
    {
        return ServletRequestAccessor.getSession();
    }

    /**
     * Return a global shared object.
     */
    public Serializable getSharedObject( String name )
    {
        return this.sharedObjects.get( name );
    }

    /**
     * Set a global shared object.
     */
    public void setSharedObject( String name, Serializable object )
    {
        this.sharedObjects.put( name, object );
    }

    /**
     * Return all shared object names that starts with prefix.
     */
    public Set<String> getSharedObjectNames( String prefix )
    {
        HashSet<String> list = new HashSet<String>();
        for ( String key : this.sharedObjects.keySet() )
        {
            if ( ( prefix == null ) || key.startsWith( prefix ) )
            {
                list.add( key );
            }
        }

        return list;
    }
}
