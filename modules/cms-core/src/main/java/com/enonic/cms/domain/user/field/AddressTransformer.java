/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.user.field;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.enonic.cms.api.client.model.user.Address;

public final class AddressTransformer
{
    private final static int MAX_INDEX = 100;

    private final static String F_COUNTRY = "country";

    private final static String F_ISO_COUNTRY = "iso-country";

    private final static String F_REGION = "region";

    private final static String F_ISO_REGION = "iso-region";

    private final static String F_LABEL = "label";

    private final static String F_STREET = "street";

    private final static String F_POSTAL_CODE = "postal-code";

    private final static String F_POSTAL_ADDRESS = "postal-address";

    private Address[] toAddresses( UserFieldMap fields )
    {
        LinkedList<Address> list = new LinkedList<Address>();
        for ( UserField field : fields.getFields( UserFieldType.ADDRESS ) )
        {
            list.add( (Address) field.getValue() );
        }

        return list.toArray( new Address[list.size()] );
    }

    public Map<String, String> toStoreableMap( UserFieldMap fields )
    {
        HashMap<String, String> result = new HashMap<String, String>();
        Address[] addresses = toAddresses( fields );
        for ( int i = 0; i < addresses.length; i++ )
        {
            addAddress( result, addresses[i], i );
        }

        return result;
    }

    private void addAddress( Map<String, String> result, Address address, int index )
    {
        String prefix = UserFieldType.ADDRESS.getName() + "[" + index + "].";
        addIfNotNull( result, prefix + F_COUNTRY, address.getCountry() );
        addIfNotNull( result, prefix + F_ISO_COUNTRY, address.getIsoCountry() );
        addIfNotNull( result, prefix + F_ISO_REGION, address.getIsoRegion() );
        addIfNotNull( result, prefix + F_LABEL, address.getLabel() );
        addIfNotNull( result, prefix + F_POSTAL_CODE, address.getPostalCode() );
        addIfNotNull( result, prefix + F_POSTAL_ADDRESS, address.getPostalAddress() );
        addIfNotNull( result, prefix + F_REGION, address.getRegion() );
        addIfNotNull( result, prefix + F_STREET, address.getStreet() );
    }

    private static void addIfNotNull( Map<String, String> result, String name, String value )
    {
        if ( value != null )
        {
            result.put( name, value );
        }
    }

    private LinkedList<Address> parseAddresses( Map<String, String> map )
    {
        LinkedList<Address> result = new LinkedList<Address>();
        for ( int i = 0; i < MAX_INDEX; i++ )
        {
            Address address = parseAddress( map, i );
            if ( address != null )
            {
                result.add( address );
            }
        }

        return result;
    }

    private Address parseAddress( Map<String, String> map, int index )
    {
        String prefix = UserFieldType.ADDRESS.getName() + "[" + index + "].";

        if ( !hasKeysWithPrefix( map, prefix ) )
        {
            return null;
        }

        Address address = new Address();
        address.setLabel( map.get( prefix + F_LABEL ) );
        address.setCountry( map.get( prefix + F_COUNTRY ) );
        address.setIsoCountry( map.get( prefix + F_ISO_COUNTRY ) );
        address.setRegion( map.get( prefix + F_REGION ) );
        address.setIsoRegion( map.get( prefix + F_ISO_REGION ) );
        address.setStreet( map.get( prefix + F_STREET ) );
        address.setPostalCode( map.get( prefix + F_POSTAL_CODE ) );
        address.setPostalAddress( map.get( prefix + F_POSTAL_ADDRESS ) );
        return address;
    }

    public UserFieldMap fromStoreableMap( Map<String, String> map )
    {
        UserFieldMap fields = new UserFieldMap( true );
        for ( Address address : parseAddresses( map ) )
        {
            fields.add( new UserField( UserFieldType.ADDRESS, address ) );
        }

        return fields;
    }

    private boolean hasKeysWithPrefix( final Map<String, String> map, final String prefix )
    {
        for ( final String key : map.keySet() )
        {
            if ( key.startsWith( prefix ) )
            {
                return true;
            }
        }
        return false;
    }
}
