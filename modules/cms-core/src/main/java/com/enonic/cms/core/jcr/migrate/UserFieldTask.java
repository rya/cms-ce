/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jcr.migrate;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.api.client.model.user.Gender;
import com.enonic.cms.core.jcr.JcrCmsConstants;
import com.enonic.cms.core.jcr.wrapper.JcrNode;
import com.enonic.cms.core.jcr.wrapper.JcrNodeIterator;
import com.enonic.cms.core.jcr.wrapper.JcrRepository;
import com.enonic.cms.core.jcr.wrapper.JcrSession;
import com.enonic.cms.core.jdbc.JdbcDynaRow;

import com.enonic.cms.domain.user.field.UserFieldHelper;
import com.enonic.cms.domain.user.field.UserFieldType;

@Component
public final class UserFieldTask
    extends OneToOneTask
{
    @Autowired
    private JcrRepository jcrRepository;

    public UserFieldTask()
    {
        super( "User field", "tUserField" );
    }

    @Override
    protected void importRow( final JdbcDynaRow row )
    {
        this.logInfo( "Importing user field: {0} {1}={2}", row.getString( "usf_usr_hkey" ), row.getString( "usf_name" ),
                      row.getString( "usf_value" ) );

        JcrSession session = null;

        try
        {
            session = jcrRepository.login();

            String userKey = row.getString( "usf_usr_hkey" );
            String fieldName = row.getString( "usf_name" );
            String fieldValue = row.getString( "usf_value" );

            JcrNode userNode = getUserStoreNode( userKey, session );
            if ( isAddressField( fieldName ) )
            {
                addAddressField(userNode, fieldName, fieldValue);
            }
            else
            {
                addUserField( userNode, fieldName, fieldValue );
            }

            session.save();
        }
        finally
        {
            jcrRepository.logout( session );
        }
    }

    private void addAddressField( JcrNode userNode, String fieldName, String fieldValue )
    {
        String addressId = StringUtils.substringBetween( fieldName, UserFieldType.ADDRESS.getName() + "[", "]" );
        String addressField = StringUtils.substringAfter( fieldName, "." );
        String addressNodeName = "address" + addressId;
        if ( !userNode.hasNode( addressNodeName ) )
        {
            userNode.addNode( addressNodeName, JcrConstants.NT_UNSTRUCTURED );
        }
        JcrNode addressNode = userNode.getNode( addressNodeName );
        addressNode.setProperty( addressField, fieldValue );
    }

    private void addUserField( JcrNode userNode, String fieldName, String fieldValue )
    {
        Object value = convertUserField( fieldName, fieldValue );
        if ( value == null )
        {
            return;
        }
        userNode.setProperty( fieldName, value );
    }

    private boolean isAddressField( String userFieldName )
    {
        return userFieldName.startsWith( UserFieldType.ADDRESS.getName() + "[" );
    }

    private Object convertUserField( String userFieldName, String userFieldValue )
    {
        UserFieldType type = UserFieldType.fromName( userFieldName );
        if ( type == null )
        {
            return null;
        }

        UserFieldHelper userFieldHelper = new UserFieldHelper();
        Object value =userFieldHelper.fromString( type, userFieldValue );

        if ( value instanceof Locale )
        {
            return ( (Locale) value ).getISO3Language();
        }
        else if ( value instanceof Gender )
        {
            return value.toString();
        }

        return value;
    }

    private JcrNode getUserStoreNode( String userKey, JcrSession session )
    {
        String sql = "SELECT * FROM [" + JcrCmsConstants.USER_NODE_TYPE + "] WHERE key = $key ";

        JcrNodeIterator nodes = session.createQuery( sql ).bindValue( "key", userKey ).execute();
        if ( nodes.hasNext() )
        {
            return nodes.nextNode();
        }
        return null;
    }
}
