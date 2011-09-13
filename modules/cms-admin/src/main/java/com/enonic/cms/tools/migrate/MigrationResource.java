/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.tools.migrate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.api.Version;
import com.enonic.cms.core.jaxrs.freemarker.FreeMarkerModel;
import com.enonic.cms.core.jcr.JcrUtils;
import com.enonic.cms.core.jcr.migrate.Migration;
import com.enonic.cms.core.jcr.wrapper.JcrNode;
import com.enonic.cms.core.jcr.wrapper.JcrNodeIterator;
import com.enonic.cms.core.jcr.wrapper.JcrRepository;
import com.enonic.cms.core.jcr.wrapper.JcrSession;
import com.enonic.cms.core.jcr.wrapper.JcrWrappers;

@Path("/migration/")
@Component
public final class MigrationResource
{
    private static final Logger LOG = LoggerFactory.getLogger( MigrationResource.class );

    @Autowired
    private Migration migration;

    @Autowired
    private JcrRepository jcrRepository;


    @GET
    public FreeMarkerModel handleGet( final @Context UriInfo info, final @QueryParam("start") boolean start )
    {
        final String baseUrl = info.getBaseUri().toString();

        if ( start )
        {
            migration.start();
        }

        return FreeMarkerModel.create( "migratePage.ftl" )
            .put( "versionTitle", Version.getTitle() )
            .put( "versionTitleVersion", Version.getTitleAndVersion() )
            .put( "migrationInProgress", migration.isInProgress() )
            .put( "migrationOk", migration.isSuccessful() )
            .put( "baseUrl", baseUrl );
    }

    @GET
    @Path("jcrtree")
    public FreeMarkerModel jcrTreeView( final @Context UriInfo info )
    {
        final String baseUrl = info.getBaseUri().toString();

        return FreeMarkerModel.create( "jcrPage.ftl" )
            .put( "versionTitle", Version.getTitle() )
            .put( "versionTitleVersion", Version.getTitleAndVersion() )
            .put( "baseUrl", baseUrl );
    }

    @GET
    @Produces(MediaType.TEXT_XML)
    @Path("jcrxml")
    public String jcrTree()
    {
        JcrSession session = null;
        try
        {
            session = jcrRepository.login();
            return JcrUtils.sessionViewToXml( session, "/", true );
        }
        finally
        {
            jcrRepository.logout( session );
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("jcrjson")
    public Map<String, Object> jcrTreeJson(final @QueryParam("path") String path )
    {
        JcrSession session = null;
        try
        {
            session = jcrRepository.login();
            Map<String, Object> rootJson = new HashMap<String, Object>();

            JcrNode root;
            if ( StringUtils.isBlank( path ) )
            {
                root = session.getRootNode();
            }
            else
            {
                root = session.getRootNode().getNode( path );
            }
            buildJsonTree( root, rootJson );

            return rootJson;
        }
        finally
        {
            jcrRepository.logout( session );
        }
    }

    private void buildJsonTree( JcrNode node, Map<String, Object> nodeJson )
    {
        nodeJson.put( "name", node.getName() );
        nodeJson.put( "path", node.getPath() );
        try
        {
            nodeJson.put( "type", JcrWrappers.unwrap( node ).getDefinition().getDefaultPrimaryTypeName() );
        }
        catch ( RepositoryException e )
        {
            e.printStackTrace();
        }

        if ( node.hasProperties() )
        {
            Map<String, Object> properties = new HashMap<String, Object>();
            for ( String propName : node.getPropertyNames() )
            {
                Object propValue = node.getProperty( propName );
                if ( propValue instanceof JcrNode )
                {
                    propValue = ( (JcrNode) propValue ).getPath();
                }
                properties.put( propName, propValue );
            }
            nodeJson.put( "properties", properties );
        }

        List<Map<String, Object>> childrenList = new ArrayList<Map<String, Object>>();
        nodeJson.put( "children", childrenList );

        JcrNodeIterator children = node.getChildren();
        while ( children.hasNext() )
        {
            JcrNode child = children.nextNode();
            Map<String, Object> childJson = new HashMap<String, Object>();
            buildJsonTree( child, childJson );
            childrenList.add( childJson );
        }
    }

}
