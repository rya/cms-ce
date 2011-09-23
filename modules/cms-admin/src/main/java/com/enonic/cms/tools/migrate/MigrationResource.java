/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.tools.migrate;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.PropertyType;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
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

@Path("/migration/")
@Component
public final class MigrationResource
{
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

        return FreeMarkerModel.create( "migratePage.ftl" ).
            put( "versionTitle", Version.getTitle() ).
            put( "versionTitleVersion", Version.getTitleAndVersion() ).
            put( "migrationInProgress", migration.isInProgress() ).
            put( "migrationOk", migration.isSuccessful() ).
            put( "log", migration.getLogEntries() ).
            put( "baseUrl", baseUrl );
    }

    @GET
    @Path("jcrtree")
    public FreeMarkerModel jcrTreeViewPage( final @Context UriInfo info )
    {
        final String baseUrl = info.getBaseUri().toString();

        return FreeMarkerModel.create( "jcrPage.ftl" ).
            put( "versionTitle", Version.getTitle() ).
            put( "versionTitleVersion", Version.getTitleAndVersion() ).
            put( "baseUrl", baseUrl );
    }

    @GET
    @Produces(MediaType.TEXT_XML)
    @Path("jcrxml")
    public String jcrTreeAsXml()
    {
        final JcrSession session = jcrRepository.login();
        try
        {
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
    public Map<String, Object> jcrTreeAsJson( final @QueryParam("path") String path )
    {
        final JcrSession session = jcrRepository.login();
        try
        {
            Map<String, Object> rootJson = new HashMap<String, Object>();

            final JcrNode root;
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

    private void buildJsonTree( final JcrNode node, final Map<String, Object> nodeJson )
    {
        nodeJson.put( "_name", node.getName() );
        nodeJson.put( "_path", node.getPath() );
        nodeJson.put( "_type", node.getNodeType() );

        if ( node.hasProperties() )
        {
            final List<Map<String, Object>> properties = new ArrayList<Map<String, Object>>();
            for ( String propName : node.getPropertyNames() )
            {
                final Object propValue;
                switch ( node.getPropertyType( propName ) )
                {
                    case PropertyType.BINARY:
                        propValue = MessageFormat.format( "({0} bytes)", node.getBinaryProperty( propName ).getSize() );
                        break;
                    case PropertyType.REFERENCE:
                    case PropertyType.WEAKREFERENCE:
                        propValue = ( (JcrNode) node.getProperty( propName ) ).getPath();
                        break;
                    default:
                        propValue = node.getProperty( propName );
                }

                final Map<String, Object> property = new HashMap<String, Object>();
                property.put( "name", propName );
                property.put( "value", propValue );
                property.put( "type", getPropertyTypeName( node, propName ) );
                properties.add( property );
            }
            nodeJson.put( "_properties", properties );
        }
        else
        {
            nodeJson.put( "leaf", true );
        }

        final List<Map<String, Object>> childrenList = new ArrayList<Map<String, Object>>();
        nodeJson.put( "_children", childrenList );

        final JcrNodeIterator children = node.getChildren();
        while ( children.hasNext() )
        {
            final JcrNode child = children.nextNode();
            final Map<String, Object> childJson = new HashMap<String, Object>();
            buildJsonTree( child, childJson );
            childrenList.add( childJson );
        }
    }

    private String getPropertyTypeName( JcrNode node, String propertyName )
    {
        final int type = node.getPropertyType( propertyName );
        return PropertyType.nameFromValue( type );
    }

}
