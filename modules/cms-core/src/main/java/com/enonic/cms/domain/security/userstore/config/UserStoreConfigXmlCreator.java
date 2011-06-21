/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.security.userstore.config;

import java.util.Collection;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.domain.user.field.UserFieldType;

public class UserStoreConfigXmlCreator
{
    public static Document createDocument( final UserStoreConfig userStoreConfig )
    {
        return new Document( doCreateUserStoreConfigElement( userStoreConfig ) );
    }

    public static Element createElement( final UserStoreConfig userStoreConfig )
    {
        return doCreateUserStoreConfigElement( userStoreConfig );
    }

    public static Element createEmptyConfigElement()
    {
        final Element configEl = new Element( "config" );
        final Element userFieldsEl = new Element( "user-fields" );
        configEl.addContent( userFieldsEl );
        return configEl;
    }

    private static Element doCreateUserStoreConfigElement( final UserStoreConfig userStoreConfig )
    {
        final Element configEl = new Element( "config" );
        final Element customFieldsEl = new Element( "user-fields" );
        configEl.addContent( customFieldsEl );

        final Collection<UserStoreUserFieldConfig> fieldConfigs = userStoreConfig.getUserFieldConfigs();

        for ( final UserStoreUserFieldConfig fieldConfig : fieldConfigs )
        {
            customFieldsEl.addContent( doCreateFieldConfigElement( fieldConfig ) );
        }

        return configEl;
    }

    private static Element doCreateFieldConfigElement( final UserStoreUserFieldConfig fieldConfig )
    {

        final Element userFieldConfigEl = new Element( fieldConfig.getType().getName() );

        userFieldConfigEl.setAttribute( "readonly", String.valueOf( fieldConfig.isReadOnly() ) );
        userFieldConfigEl.setAttribute( "required", String.valueOf( fieldConfig.isRequired() ) );
        userFieldConfigEl.setAttribute( "remote", String.valueOf( fieldConfig.isRemote() ) );

        if ( fieldConfig.getType() == UserFieldType.ADDRESS )
        {
            userFieldConfigEl.setAttribute( "iso", Boolean.valueOf( fieldConfig.useIso() ).toString() );
        }
        return userFieldConfigEl;
    }
}
