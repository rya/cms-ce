package com.enonic.cms.api.plugin;

import java.io.Serializable;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * This defines a set of plugin environment functions.
 */
public interface PluginEnvironment
{
    /**
     * Return the current http servlet request.
     */
    public HttpServletRequest getCurrentRequest();

    /**
     * Return the current http servlet session.
     */
    public HttpSession getCurrentSession();

    /**
     * Return a global shared object.
     */
    public Serializable getSharedObject(String name);

    /**
     * Set a global shared object.
     */
    public void setSharedObject(String name, Serializable object);

    /**
     * Return all shared object names that starts with prefix.
     */
    public Set<String> getSharedObjectNames(String prefix);
}
