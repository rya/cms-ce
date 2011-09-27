/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jcr.migrate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.jcr.JcrCmsConstants;
import com.enonic.cms.core.jcr.wrapper.JcrNode;
import com.enonic.cms.core.jcr.wrapper.JcrNodeIterator;
import com.enonic.cms.core.jcr.wrapper.JcrRepository;
import com.enonic.cms.core.jcr.wrapper.JcrSession;
import com.enonic.cms.core.security.group.GroupType;

@Component
public final class CleanUpTask
{

    @Autowired
    private JcrRepository jcrRepository;

    private Log log;

    public CleanUpTask()
    {
    }

    public void execute()
    {
        final JcrSession session = jcrRepository.login();
        try
        {
            log.logInfo( "Removing temporary nodes and properties" );

            final JcrNodeIterator groupIterator = getGroups( session );
            while ( groupIterator.hasNext() )
            {
                final JcrNode groupNode = groupIterator.nextNode();
                log.logInfo( "Removing temporary group node: " + groupNode.getName() );
                groupNode.remove();
            }
            session.save();
        }
        finally
        {
            jcrRepository.logout( session );
        }
    }

    private JcrNodeIterator getGroups( JcrSession session )
    {
        final String sql = "SELECT * FROM [" + JcrCmsConstants.GROUP_NODE_TYPE + "] WHERE type = $type";
        return session.createQuery( sql ).bindValue( "type", GroupType.USER.toInteger() ).execute();
    }

    public void setLog( Log log )
    {
        this.log = log;
    }

}
