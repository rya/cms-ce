/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.preferences;

import java.util.List;

/**
 * This service object allows manipulation of user specific values.  There are two key objects to work with: <code>PreferenceEntity</code>
 * and <code>PreferenceKey</code>.  The key is a non-mutable identifier for the entity, which contains information about which user the
 * entity belongs to, and which scope it is valid for.
 * <p/>
 * The entity is an object connected with the database, that should be carefully modified.  Any changes to the object will be promoted to
 * the database, without the need for explicit storing.  However, this is done when the system finds time for it.  If it is important for a
 * change to be immediately reflected to the database, and explicit call to a <code>setPreference</code> method should be made.  The
 * <code>setPreference</code> methods can also be used to create new preferences.
 * <p/>
 * Removing a preference object from the system, must be done specifically.
 *
 * @see PreferenceKey
 */
public interface PreferenceService
{

    /**
     * Return a user preference by key.
     */
    public PreferenceEntity getPreference( PreferenceKey key );

    public List<PreferenceEntity> getPreferences( PreferenceSpecification spec );

    /**
     * Change an existing preference or create a new one.
     */
    public PreferenceEntity setPreference( PreferenceEntity preference );


    public void removePreference( PreferenceEntity preference );

}
