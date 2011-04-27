/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin.spring;

import java.util.Hashtable;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.web.context.request.SessionScope;


/**
 * <p>
 * vaadin scope
 * </p><p>
 * prototype scope is not enough for vaadin: AccountsTab and FilterTreePanel will have different UserPanel objects
 * </p><p>
 * references can be stored-found in map kept in session (fast but spends memory - this solution),
 * or searched in vaadin application - that is already stored in session - this is slow
 * </p><p>
 * session scope may be used instead, but this solution will increase session map.
 * </p><p>
 * anyway vaadin scope may just keep object in session adding prefix to name
 * </p>
 */
public class VaadinScope
        extends SessionScope
{
    private static final String VAADIN_OBJECTS_MAP = "vaadinObjectsMap";
    private static final ObjectFactory<Map<String, Object>> factory = new ObjectsMapFactory();

    private static class ObjectsMapFactory
            implements ObjectFactory<Map<String, Object>>
    {
        public Map<String, Object> getObject()
                throws BeansException
        {
            return new Hashtable<String, Object>();
        }
    }


    @Override
    public Object get( String name, ObjectFactory objectFactory )
    {
        Map<String, Object> objects = getObjects();

        Object object = objects.get( name );

        if (object == null) {
            object = objectFactory.getObject();
            objects.put( name, object );
        }

        return object;
    }

    @Override
    public Object remove( String name )
    {
        return getObjects().remove( name );
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getObjects()
    {
        return (Map<String, Object>) super.get( VAADIN_OBJECTS_MAP, factory );
    }

}