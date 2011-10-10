/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.user.field;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

import com.enonic.esl.containers.ExtendedMap;

import com.enonic.cms.core.security.userstore.config.UserStoreConfig;

public final class UserFieldTransformer
{
    private final AddressTransformer addressTransformer = new AddressTransformer();

    private final UserFieldHelper helper = new UserFieldHelper();

    private boolean transformNullValuesToBlanksForConfiguredFields = false;

    private UserStoreConfig userStoreConfig = null;

    public void transformNullValuesToBlanksForConfiguredFields( UserStoreConfig userStoreConfig )
    {
        transformNullValuesToBlanksForConfiguredFields = true;
        this.userStoreConfig = userStoreConfig;
    }

    public UserFieldMap toUserFields( ExtendedMap form )
    {
        Map<String, String> map = toStringStringMap( form );
        UserFieldMap fields = fromStoreableMap( map );

        FileItem item = form.getFileItem( UserFieldType.PHOTO.getName(), null );
        if ( item != null )
        {
            updatePhoto( fields, UserPhotoHelper.convertPhoto( item.get() ) );
        }

        return fields;
    }

    private Map<String, String> toStringStringMap( ExtendedMap form )
    {
        HashMap<String, String> map = new HashMap<String, String>();
        for ( Object key : form.keySet() )
        {
            String name = key.toString().replace( "_", "-" );
            Object value = form.get( key );

            if ( value instanceof String )
            {
                map.put( name, (String) value );
            }
        }

        return map;
    }

    public UserFieldMap fromStoreableMap( Map<String, String> map )
    {
        UserFieldMap fields = new UserFieldMap( true );
        for ( UserFieldType type : UserFieldType.values() )
        {
            updateUserField( fields, type, map );
        }

        fields.addAll( this.addressTransformer.fromStoreableMap( map ).getAll() );
        return fields;
    }

    private void updateUserField( UserFieldMap fields, UserFieldType type, Map<String, String> map )
    {
        if ( type == UserFieldType.ADDRESS )
        {
            return;
        }

        if ( type != UserFieldType.PHOTO )
        {
            updateSimpleField( fields, type, map );
        }
    }

    public void updatePhoto( UserFieldMap fields, byte[] value )
    {
        if ( value != null )
        {
            fields.add( new UserField( UserFieldType.PHOTO, value ) );
        }
    }

    private void updateSimpleField( UserFieldMap fields, UserFieldType type, Map<String, String> map )
    {
        String value = map.get( type.getName() );

        if ( transformNullValuesToBlanksForConfiguredFields )
        {
            // This fixes issues with not able to empty string based fields from admin console,
            // however it does not fix emptying date
            // To fix this better, some refactoring is needed:
            //  we need to be able to differ from null-as-in-not-set and null-as-in-set-to-null
            boolean fieldIsConfigured = userStoreConfig.getUserFieldConfig( type ) != null;
            if ( value == null && fieldIsConfigured )
            {
                value = "";
            }
        }

        if ( value != null )
        {
            Object typedValue = this.helper.fromString( type, value );
            if ( typedValue != null )
            {
                fields.add( new UserField( type, typedValue ) );
            }
        }
    }

    public Map<String, String> toStoreableMap( UserFieldMap fields )
    {
        HashMap<String, String> result = new HashMap<String, String>();
        for ( UserField field : fields )
        {
            if ( !field.isOfType( UserFieldType.ADDRESS ) && !field.isOfType( UserFieldType.PHOTO ) )
            {
                addSimpleField( result, field );
            }
        }

        result.putAll( this.addressTransformer.toStoreableMap( fields ) );
        return result;
    }

    private void addSimpleField( Map<String, String> result, UserField field )
    {
        UserFieldType type = field.getType();
        String strValue = this.helper.toString( field );
        addIfNotNull( result, type.getName(), strValue );
    }

    private void addIfNotNull( Map<String, String> result, String name, String value )
    {
        if ( value != null )
        {
            result.put( name, value );
        }
    }
}
