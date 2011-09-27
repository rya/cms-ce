package com.enonic.cms.core.jcr.accounts;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.jcr.wrapper.JcrBinary;
import com.enonic.cms.core.jcr.wrapper.JcrNode;
import com.enonic.cms.core.jcr.wrapper.JcrNodeIterator;
import com.enonic.cms.core.jcr.wrapper.JcrRepository;
import com.enonic.cms.core.jcr.wrapper.JcrSession;

import com.enonic.cms.domain.EntityPageList;

import static com.enonic.cms.core.jcr.JcrCmsConstants.GROUP_NODE_TYPE;
import static com.enonic.cms.core.jcr.JcrCmsConstants.PRINCIPAL_NODE_TYPE;
import static com.enonic.cms.core.jcr.JcrCmsConstants.USER_NODE_TYPE;

@Component
public class AccountServiceImpl
    implements AccountService
{

    @Autowired
    private JcrRepository jcrRepository;

    @Override
    public EntityPageList<Account> findAccounts( int index, int count, String query, String order )
    {
        final List<Account> list = new ArrayList<Account>();

        final JcrSession session = jcrRepository.login();
        try
        {
            final String sql = "SELECT * FROM [" + PRINCIPAL_NODE_TYPE + "] " + "WHERE [jcr:primaryType] = '" + USER_NODE_TYPE + "' " +
                "OR [jcr:primaryType] = '" + GROUP_NODE_TYPE + "' ";

            final JcrNodeIterator totalCountResult = session.createQuery( sql ).execute();
            final int total = (int) totalCountResult.getSize();

            final JcrNodeIterator nodes = session.createQuery( sql ).setOffset( index ).setLimit( count ).execute();
            while ( nodes.hasNext() )
            {
                JcrNode node = nodes.nextNode();
                Account account = nodeToAccount( node );
                list.add( account );
            }
            return new EntityPageList<Account>( index, total, list );
        }
        finally
        {
            jcrRepository.logout( session );
        }
    }

    @Override
    public Account findAccount( String accountId )
    {
        final JcrSession session = jcrRepository.login();
        try
        {
            final JcrNode node = session.getNodeByIdentifier( accountId );
            return ( node == null ) ? null : nodeToAccount( node );
        }
        finally
        {
            jcrRepository.logout( session );
        }
    }

    private Account nodeToAccount( JcrNode accountNode )
    {
        final Account account;
        if ( accountNode.getNodeType().equals( USER_NODE_TYPE ) )
        {
            account = nodeToUser( accountNode );
        }
        else if ( accountNode.getNodeType().equals( GROUP_NODE_TYPE ) )
        {
            account = nodeToGroup( accountNode );
        }
        else
        {
            throw new IllegalArgumentException( "Invalid JCR node type for account: " + accountNode.getNodeType() );
        }
        setAccountProperties( account, accountNode );

        return account;
    }

    private User nodeToUser( JcrNode userNode )
    {
        final User user = new User();
        user.setEmail( userNode.getStringProperty( "email" ) );
        
        final JcrBinary photo = userNode.getBinaryProperty( "photo" );
        if ( photo != null )
        {
            user.setPhoto( photo.toByteArray() );
        }
        return user;
    }

    private Group nodeToGroup( JcrNode groupNode )
    {
        final Group group = new Group();
        return group;
    }

    private void setAccountProperties( Account account, JcrNode accountNode )
    {
        account.setId( accountNode.getIdentifier() );
        account.setName( accountNode.getName() );
        account.setDisplayName( accountNode.getStringProperty( "display-name" ) );
        account.setLastModified( accountNode.getDateTimeProperty( "lastModified" ) );
        account.setUserStoreName( getAccountUserStoreName( accountNode ) );
    }

    private String getAccountUserStoreName( JcrNode accountNode )
    {
        final JcrNode userStoreNode = accountNode.getParent().getParent();
        return userStoreNode.getName();
    }
}
