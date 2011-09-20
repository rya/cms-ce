/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jcr.migrate;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import javax.jcr.Workspace;
import javax.jcr.nodetype.NodeType;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.springframework.core.io.Resource;

import com.enonic.cms.core.jcr.wrapper.JcrNode;
import com.enonic.cms.core.jcr.wrapper.JcrRepository;
import com.enonic.cms.core.jcr.wrapper.JcrSession;
import com.enonic.cms.core.jcr.wrapper.JcrWrappers;

import static com.enonic.cms.core.jcr.JcrCmsConstants.*;

public class RepositoryBootstrap
{
    private JcrRepository repository;

    private Resource compactNodeDefinitionFile;

    private static final String[] SYSTEM_ROLES =
        new String[]{"ea", "developer", "administrator", "contributor", "expert", "everyone", "authenticated"};

    private Log log;

    public void initializeRepository()
        throws RepositoryException, IOException
    {
        log.logInfo( "Initializing JCR repository..." );

        JcrSession session = null;
        try
        {
            session = repository.login();

            registerNamespaces( session );
            registerCustomNodeTypes( session );

            createTreeStructure( session );

            session.save();
        }
        finally
        {
            repository.logout( session );
        }
        log.logInfo( "JCR repository initialized" );
    }

    private void createTreeStructure( JcrSession jcrSession )
        throws RepositoryException
    {
        JcrNode root = jcrSession.getRootNode();

        if ( root.hasNode( ROOT_NODE ) )
        {
            root.remove( ROOT_NODE );
            jcrSession.save();
        }
        JcrNode enonic = root.addNode( ROOT_NODE, JcrConstants.NT_UNSTRUCTURED );
        JcrNode userstores = enonic.addNode( USERSTORES_NODE, USERSTORES_NODE_TYPE );

        JcrNode systemUserstore = userstores.addNode( SYSTEM_USERSTORE_NODE, USERSTORE_NODE_TYPE );
        JcrNode groupsRoles = systemUserstore.addNode( GROUPS_NODE, GROUPS_NODE_TYPE );
        JcrNode usersRoles = systemUserstore.addNode( USERS_NODE, USERS_NODE_TYPE );
        JcrNode systemRoles = systemUserstore.addNode( ROLES_NODE, ROLES_NODE_TYPE );

        for ( String systemRole : SYSTEM_ROLES )
        {
            systemRoles.addNode( systemRole, ROLE_NODE_TYPE );
        }
    }

    private void registerCustomNodeTypes( JcrSession jcrSession )
        throws IOException

    {
        Reader fileReader = new InputStreamReader( compactNodeDefinitionFile.getInputStream() );
        try
        {
            NodeType[] nodeTypes = CndImporter.registerNodeTypes( fileReader, JcrWrappers.unwrap( jcrSession ) );
            for ( NodeType nt : nodeTypes )
            {
                log.logInfo( "Registered node type: " + nt.getName() );
            }
        }
        catch ( Exception e )
        {
            log.logError( e.getMessage(), e );
        }
    }

    private void registerNamespaces( JcrSession jcrSession )
        throws RepositoryException
    {
        Workspace workspace = JcrWrappers.unwrap( jcrSession ).getWorkspace();
        NamespaceRegistry reg = workspace.getNamespaceRegistry();

        String[] prefixes = reg.getPrefixes();
        Set<String> registeredPrefixes = new HashSet( Arrays.<String>asList( prefixes ) );

        registerNamespace( reg, registeredPrefixes, ENONIC_CMS_NAMESPACE_PREFIX, ENONIC_CMS_NAMESPACE );
    }

    private void registerNamespace( NamespaceRegistry reg, Set<String> registeredPrefixes, String prefix, String uri )
        throws RepositoryException
    {
        if ( !registeredPrefixes.contains( prefix ) )
        {
            reg.registerNamespace( prefix, uri );
            log.logInfo( "JCR namespace registered " + prefix + ":" + uri );
        }
        else
        {
            String registeredUri = reg.getURI( prefix );
            if ( !uri.equals( registeredUri ) )
            {
                log.logError( "Namespace prefix is already registered with a different URI: " + prefix + ":" + registeredUri );
            }
        }
    }

    public void setCompactNodeDefinitionFile( Resource compactNodeDefinitionFile )
    {
        this.compactNodeDefinitionFile = compactNodeDefinitionFile;
    }

    public void setRepository( JcrRepository repository )
    {
        this.repository = repository;
    }

    public void setLog( Log log )
    {
        this.log = log;
    }
}
