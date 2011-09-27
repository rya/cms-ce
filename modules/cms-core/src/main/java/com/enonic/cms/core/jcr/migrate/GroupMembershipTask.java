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
import com.enonic.cms.core.jdbc.JdbcDynaRow;
import com.enonic.cms.core.security.group.GroupType;

@Component
public final class GroupMembershipTask
    extends OneToOneTask
{
    @Autowired
    private JcrRepository jcrRepository;

    public GroupMembershipTask()
    {
        super( "Group membership", "tGrpgrpmembership" );
    }

    @Override
    protected void importRow( final JdbcDynaRow row )
    {
        final String groupKey = row.getString( "ggm_grp_hkey" );
        final String groupMemberKey = row.getString( "ggm_mbr_grp_hkey" );

        final JcrSession session = jcrRepository.login();

        try
        {
            final JcrNode parentGroupNode = getGroupNode( groupKey, session );
            JcrNode memberPrincipalNode = getGroupNode( groupMemberKey, session );
            if ( memberPrincipalNode == null )
            {
                this.logWarning( "Could not find group with key {0}. Skipping group member.", groupMemberKey );
                return;
            }
            final GroupType memberGroupType = getGroupType(memberPrincipalNode);
            if (memberGroupType == GroupType.USER ) {
                memberPrincipalNode = getUserNodeByGroupKey( groupMemberKey, session );
            }

            if ( ( parentGroupNode != null ) && ( memberPrincipalNode != null ) )
            {
                this.logInfo( "Importing group membership: {0} <= {1} ({2})", parentGroupNode.getName(), memberPrincipalNode.getName() , memberGroupType.getName());

                parentGroupNode.addPropertyReference( "members", memberPrincipalNode, true );
            }

            session.save();
        }
        finally
        {
            jcrRepository.logout( session );
        }
    }

        private JcrNode getUserNodeByGroupKey( String groupkey, JcrSession session )
    {
        final String sql = "SELECT * FROM [" + JcrCmsConstants.USER_NODE_TYPE + "] " + "WHERE groupKey = $groupkey ";

        final JcrNodeIterator nodes = session.createQuery( sql ).bindValue( "groupkey", groupkey ).execute();
        if ( nodes.hasNext() )
        {
            return nodes.nextNode();
        }
        return null;
    }

    private JcrNode getGroupNode( String key, JcrSession session )
    {
        final String sql = "SELECT * FROM [" + JcrCmsConstants.GROUP_NODE_TYPE + "] " + "WHERE key = $key ";

        final JcrNodeIterator nodes = session.createQuery( sql ).bindValue( "key", key ).execute();
        if ( nodes.hasNext() )
        {
            return nodes.nextNode();
        }
        return null;
    }

    private GroupType getGroupType( JcrNode groupNode )
    {
        final Long typeVal = groupNode.getLongProperty( "type" );
        if ( typeVal == null )
        {
            return GroupType.ANONYMOUS;
        }
        else
        {
            return GroupType.get( typeVal.intValue() );
        }
    }
}
