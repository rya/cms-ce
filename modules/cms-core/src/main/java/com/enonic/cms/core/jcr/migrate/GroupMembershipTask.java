package com.enonic.cms.core.jcr.migrate;

import org.apache.jackrabbit.JcrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.jcr.JcrCmsConstants;
import com.enonic.cms.core.jcr.wrapper.JcrNode;
import com.enonic.cms.core.jcr.wrapper.JcrNodeIterator;
import com.enonic.cms.core.jcr.wrapper.JcrRepository;
import com.enonic.cms.core.jcr.wrapper.JcrSession;
import com.enonic.cms.core.jdbc.JdbcDynaRow;

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
        String groupKey = row.getString( "ggm_grp_hkey" );
        String groupMemberKey = row.getString( "ggm_mbr_grp_hkey" );

        JcrSession session = null;

        try
        {
            session = jcrRepository.login();

            JcrNode parentGroupNode = getGroupNode( groupKey, session );
            JcrNode memberGroupNode = getGroupNode( groupMemberKey, session );
            if ( ( parentGroupNode != null ) && ( memberGroupNode != null ) )
            {
                this.logInfo( "Importing group membership: {0} , {1}", parentGroupNode.getName(), memberGroupNode.getName() );

                JcrNode membersNode = parentGroupNode.getChild( "members" );
                JcrNode memberNode = membersNode.addNode( "member", JcrConstants.NT_UNSTRUCTURED );
                memberNode.setPropertyReference( "ref", memberGroupNode, true );
            }

            session.save();
        }
        finally
        {
            jcrRepository.logout( session );
        }
    }

    private JcrNode getGroupNode( String key, JcrSession session )
    {
        String sql = "SELECT * " + "FROM [" + JcrCmsConstants.GROUP_NODE_TYPE + "] " + "WHERE key = $key ";

        JcrNodeIterator nodes = session.createQuery( sql ).bindValue( "key", key ).execute();
        if ( nodes.hasNext() )
        {
            return nodes.nextNode();
        }
        return null;
    }
}
