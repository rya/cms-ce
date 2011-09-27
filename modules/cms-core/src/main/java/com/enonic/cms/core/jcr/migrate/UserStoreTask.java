/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jcr.migrate;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.jcr.JcrCmsConstants;
import com.enonic.cms.core.jcr.wrapper.JcrNode;
import com.enonic.cms.core.jcr.wrapper.JcrRepository;
import com.enonic.cms.core.jcr.wrapper.JcrSession;
import com.enonic.cms.core.jdbc.JdbcDynaRow;

@Component
public final class UserStoreTask
    extends OneToOneTask
{

    @Autowired
    private JcrRepository jcrRepository;

    public UserStoreTask()
    {
        super( "UserStore", "tDomain where dom_bisdeleted = 0" );
    }

    @Override
    protected void importRow( final JdbcDynaRow row )
    {
        final JcrSession session = jcrRepository.login();
        try
        {
            this.logInfo( "Importing userstore: {0}", row.getString( "dom_sname" ) );

            String userstoreName = row.getString( "DOM_SNAME" );

            JcrNode userstoresNode = session.getRootNode().getNode( JcrCmsConstants.USERSTORES_PATH );
            if ( ( userstoreName == null ) || userstoresNode.hasNode( userstoreName ) )
            {
                this.logInfo( "Skipping creation of existing user store: " + userstoreName );
                return;
            }

            Integer key = row.getInteger( "DOM_LKEY" );
            boolean defaultUserstore = ( row.getInteger( "DOM_BDEFAULTSTORE" ) == 1 );
            String connectorName = row.getString( "DOM_SCONFIGNAME" );
            byte[] xmlBytes = row.getBytes( "DOM_XMLDATA" );
            String userStoreXmlConfig = byteArrayToString( xmlBytes );

            JcrNode userstoreNode = userstoresNode.addNode( userstoreName, JcrCmsConstants.USERSTORE_NODE_TYPE );
            userstoreNode.setProperty( "key", key );
            userstoreNode.setProperty( "default", defaultUserstore );
            userstoreNode.setProperty( "connector", connectorName );
            userstoreNode.setProperty( "xmlconfig", userStoreXmlConfig );

            userstoreNode.addNode( JcrCmsConstants.GROUPS_NODE, JcrCmsConstants.GROUPS_NODE_TYPE );
            userstoreNode.addNode( JcrCmsConstants.USERS_NODE, JcrCmsConstants.USERS_NODE_TYPE );

            session.save();
        }
        finally
        {
            jcrRepository.logout( session );
        }
    }

    private String byteArrayToString( byte[] bytes )
    {
        try
        {
            return new String( bytes, "UTF-8" );
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new RuntimeException( e.getMessage(), e );
        }
    }

}
