/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.user.field;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

public final class UserFieldMap
    implements Iterable<UserField>
{
    private final boolean mutlipleAddresses;

    private final Multimap<UserFieldType, UserField> fields;

    public UserFieldMap( boolean mutlipleAddresses )
    {
        this.mutlipleAddresses = mutlipleAddresses;
        this.fields = LinkedHashMultimap.create();
    }

    public UserField getField( UserFieldType type )
    {
        Collection<UserField> result = this.fields.get( type );
        if ( ( result != null ) && !result.isEmpty() )
        {
            return result.iterator().next();
        }
        else
        {
            return null;
        }
    }

    public Collection<UserField> getFields( UserFieldType type )
    {
        return this.fields.get( type );
    }

    public boolean hasField( final UserFieldType type )
    {
        return this.fields.containsKey( type );
    }

    public Iterator<UserField> iterator()
    {
        return this.fields.values().iterator();
    }

    public void add( final UserField field )
    {
        final UserFieldType type = field.getType();
        if ( type == UserFieldType.ADDRESS )
        {
            if ( this.mutlipleAddresses || !this.fields.containsKey( type ) )
            {
                this.fields.put( type, field );
            }
        }
        else
        {
            this.fields.removeAll( type );
            this.fields.put( type, field );
        }
    }

    public void addAll( Collection<UserField> fields )
    {
        for ( UserField field : fields )
        {
            add( field );
        }
    }

    public Collection<UserField> getAll()
    {
        return this.fields.values();
    }

    public void clear()
    {
        this.fields.clear();
    }

    public int getSize()
    {
        return this.fields.size();
    }

    public void remove( UserFieldType type )
    {
        this.fields.removeAll( type );
    }

    public void remove( Collection<UserFieldType> types )
    {
        for ( UserFieldType type : types )
        {
            this.fields.removeAll( type );
        }
    }

    public void retain( Collection<UserFieldType> types )
    {
        Set<UserFieldType> set = new HashSet<UserFieldType>( this.fields.keySet() );
        set.removeAll( types );
        remove( set );
    }
}
