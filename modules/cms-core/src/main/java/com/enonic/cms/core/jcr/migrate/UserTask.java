/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jcr.migrate;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.jcr.JcrCmsConstants;
import com.enonic.cms.core.jcr.wrapper.JcrNode;
import com.enonic.cms.core.jcr.wrapper.JcrNodeIterator;
import com.enonic.cms.core.jcr.wrapper.JcrRepository;
import com.enonic.cms.core.jcr.wrapper.JcrSession;
import com.enonic.cms.core.jdbc.JdbcDynaRow;

@Component
public final class UserTask
    extends OneToOneTask
{
    @Autowired
    private JcrRepository jcrRepository;

    public UserTask()
    {
        super( "User", "tUser where usr_bisdeleted = 0" );
    }

    @Override
    protected void importRow( final JdbcDynaRow row )
    {
        this.logInfo( "Importing user: {0}", row.getString( "usr_suid" ) );

        final JcrSession session = jcrRepository.login();
        try
        {
            String userName = row.getString( "USR_SUID" );
            Integer userStoreKey = row.getInteger( "USR_DOM_LKEY" );
            if ( userStoreKey == null )
            {
                return; // legacy built-in user, skip
            }

            JcrNode userStoreNode = getUserStoreNode( userStoreKey, session );
            if ( userStoreNode == null )
            {
                this.logInfo( "Could not find userstore with key: " + userStoreKey + ". Skipping import of user " + userName );
                return;
            }

            JcrNode usersNode = userStoreNode.getNode( JcrCmsConstants.USERS_NODE );
            JcrNode userNode = usersNode.addNode( userName, JcrCmsConstants.USER_NODE_TYPE );

            String qualifiedName = row.getString( "USR_SUID" );
            String displayName = row.getString( "USR_SFULLNAME" );
            String email = row.getString( "USR_SEMAIL" );
            String password = row.getString( "USR_SPASSWORD" );
            String key = row.getString( "USR_HKEY" );
            Date lastModified = row.getDate( "USR_DTETIMESTAMP" );
            String syncValue = row.getString( "USR_SSYNCVALUE" );
            byte[] photo = row.getBytes( "USR_PHOTO" );
            String userGroupKey = row.getString( "usr_grp_hkey" );

            userNode.setProperty( "qualifiedName", qualifiedName );
            userNode.setProperty( "displayname", displayName );
            userNode.setProperty( "email", email );
            userNode.setProperty( "key", key );
            userNode.setProperty( "lastModified", lastModified );
            userNode.setProperty( "syncValue", syncValue );
            userNode.setProperty( "photo", photo );
            userNode.setProperty( "password", password );
            userNode.setProperty( "groupKey", userGroupKey );

            session.save();
        }
        finally
        {
            jcrRepository.logout( session );
        }
    }

    private JcrNode getUserStoreNode( Integer userStoreKey, JcrSession session )
    {
        String sql = "SELECT * FROM [" + JcrCmsConstants.USERSTORE_NODE_TYPE + "] WHERE key = $key";

        JcrNodeIterator nodes = session.createQuery( sql ).bindValue( "key", userStoreKey ).execute();
        if ( nodes.hasNext() )
        {
            return nodes.nextNode();
        }
        return null;
    }
}
