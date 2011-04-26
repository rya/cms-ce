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
 * vaadin scope
 *
 * prototype scope is not enough for vaadin: AccountsTab and FilterTreePanel will have different UserPanel objects
 *
 * references can be stored-found in map kept in session (fast but spends memory),
 * or searched in vaadin application (that already stored in session - this is slow)
 *
 * session scope may be used too, but this solution will increase session map.
 *
 * anyway if vaadin scope may just
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
        Map<String, Object> objects = (Map<String, Object>)super.get( VAADIN_OBJECTS_MAP, factory );

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
        Map<String, Object> objects = (Map<String, Object>)super.get( VAADIN_OBJECTS_MAP, factory );
        return objects.remove( name );
    }

}