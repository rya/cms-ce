/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Attribute;
import org.jdom.Element;

import com.enonic.cms.domain.user.field.UserFieldType;

public class UserStoreConfigParser
{
    private final static String ROOT_ELEMENT_NAME = "config";

    private final static String USER_FIELDS_ELEMENT_NAME = "user-fields";


    private UserStoreConfigParser()
    {
        // no access
    }

    public static UserStoreConfig parse( final Element configEl )
    {
        return doParse( configEl, true );
    }

    public static UserStoreConfig parse( final Element configEl, final boolean remoteConfigAllowed )
    {
        return doParse( configEl, remoteConfigAllowed );
    }

    private static UserStoreConfig doParse( final Element configEl, final boolean remoteConfigAllowed )
    {
        final UserStoreConfig config = new UserStoreConfig();

        if ( configEl == null )
        {
            return config;
        }

        final String elementName = configEl.getName();
        if ( !ROOT_ELEMENT_NAME.equals( elementName ) )
        {
            throw new InvalidUserStoreConfigException( "Illegal root element: " + elementName + ", must be: " + ROOT_ELEMENT_NAME,
                                                       configEl );
        }

        final Element userFieldsEl = configEl.getChild( USER_FIELDS_ELEMENT_NAME );
        config.setUserFieldConfigs( parseUserFields( userFieldsEl, remoteConfigAllowed ) );

        return config;
    }

    private static Collection<UserStoreUserFieldConfig> parseUserFields( final Element userFieldsEl, final boolean remoteConfigAllowed )
    {
        if ( userFieldsEl == null )
        {
            throw new InvalidUserStoreConfigException( "Could not find 'user-fields' element", userFieldsEl );
        }

        final String elementName = userFieldsEl.getName();
        if ( !USER_FIELDS_ELEMENT_NAME.equals( elementName ) )
        {
            throw new InvalidUserStoreConfigException(
                "Given user-fields element is not named '" + USER_FIELDS_ELEMENT_NAME + "': " + elementName, userFieldsEl );
        }

        final Map<UserFieldType, UserStoreUserFieldConfig> fieldConfigs = new HashMap<UserFieldType, UserStoreUserFieldConfig>();

        final List<Element> fieldConfigElements = userFieldsEl.getChildren();
        for ( Element fieldConfigEl : fieldConfigElements )
        {
            final UserStoreUserFieldConfig fieldConfig = parseFieldConfig( fieldConfigEl, remoteConfigAllowed );
            if ( fieldConfigs.containsKey( fieldConfig.getType() ) )
            {
                throw new InvalidUserStoreConfigException( "Duplicate user-field found: " + fieldConfig.getType().getName(),
                                                           fieldConfigEl );
            }
            fieldConfigs.put( fieldConfig.getType(), fieldConfig );
        }
        return fieldConfigs.values();
    }

    private static UserStoreUserFieldConfig parseFieldConfig( final Element fieldConfigEl, final boolean remoteConfigAllowed )
    {
        final String fieldName = fieldConfigEl.getName();
        final UserFieldType type = UserFieldType.fromName( fieldName );
        if ( type == null )
        {
            throw new InvalidUserStoreConfigException( "Unknown user-field: " + fieldName, fieldConfigEl );
        }
        if ( fieldConfigEl.getChildren().size() > 0 )
        {
            throw new InvalidUserStoreConfigException(
                "Illegal user-field element '" + ( (Element) fieldConfigEl.getChildren().get( 0 ) ).getName() + "'", fieldConfigEl );
        }

        final UserStoreUserFieldConfig fieldConfig = new UserStoreUserFieldConfig( type );

        /* Requried */
        final String required = fieldConfigEl.getAttributeValue( "required" );
        if ( required != null )
        {
            if ( Boolean.parseBoolean( required ) && fieldConfig.getType().isOfType( Boolean.class ) )
            {
                throw new InvalidUserStoreConfigException( "Illegal attribute 'required'. Not valid for type: " + Boolean.class,
                                                           fieldConfigEl );
            }

            if ( !"true".equals( required ) && !"false".equals( required ) )
            {
                throw new InvalidUserStoreConfigException( "Illegal attribute value. 'required' must be 'true' or 'false': " + required,
                                                           fieldConfigEl );
            }
            fieldConfig.setRequired( Boolean.parseBoolean( required ) );
        }

        /* Readonly */
        final String readonly = fieldConfigEl.getAttributeValue( "readonly" );
        if ( readonly != null )
        {
            if ( !"true".equals( readonly ) && !"false".equals( readonly ) )
            {
                throw new InvalidUserStoreConfigException( "Illegal attribute value. 'readonly' must be 'true' or 'false': " + readonly,
                                                           fieldConfigEl );
            }
            fieldConfig.setReadOnly( Boolean.parseBoolean( readonly ) );
        }

        /* Required/Readonly combinations */
        if ( "true".equals( required ) && "true".equals( readonly ) )
        {
            throw new InvalidUserStoreConfigException( "Illegal attribute combination. Both 'readonly' and 'requried' cannot be 'true'",
                                                       fieldConfigEl );
        }

        /* Remote */
        final String remote = fieldConfigEl.getAttributeValue( "remote" );
        if ( remote != null )
        {
            if ( !remoteConfigAllowed )
            {
                throw new InvalidUserStoreConfigException(
                    "Illegal attribute. 'remote' attribute cannot be used without a remote connector.", fieldConfigEl );
            }

            if ( !"true".equals( remote ) && !"false".equals( remote ) )
            {
                throw new InvalidUserStoreConfigException( "Illegal attribute value. 'remote' must be 'true' or 'false': " + remote,
                                                           fieldConfigEl );
            }
            fieldConfig.setRemote( Boolean.parseBoolean( remote ) );
        }

        /* Iso */
        final String iso = fieldConfigEl.getAttributeValue( "iso" );
        if ( iso != null )
        {
            if ( type != UserFieldType.ADDRESS )
            {
                throw new InvalidUserStoreConfigException( "Illegal attribute value. 'iso' only valid for type: " + UserFieldType.ADDRESS,
                                                           fieldConfigEl );
            }
            if ( !"true".equals( iso ) && !"false".equals( iso ) )
            {
                throw new InvalidUserStoreConfigException( "Illegal attribute value. 'iso' must be 'true' or 'false': " + iso,
                                                           fieldConfigEl );
            }
            fieldConfig.setIso( Boolean.parseBoolean( iso ) );
        }

        /* Other illegal attributes */
        for ( final Object obj : fieldConfigEl.getAttributes() )
        {
            final Attribute attribute = (Attribute) obj;
            if ( !"required".equals( attribute.getName() ) && !"readonly".equals( attribute.getName() ) &&
                !"remote".equals( attribute.getName() ) && !"iso".equals( attribute.getName() ) )
            {
                throw new InvalidUserStoreConfigException( "Illegal attribute '" + attribute.getName() + "'", fieldConfigEl );
            }
        }

        return fieldConfig;
    }
}
