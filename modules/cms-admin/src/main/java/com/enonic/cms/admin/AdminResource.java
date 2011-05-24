package com.enonic.cms.admin;

import com.enonic.cms.core.spring.PrototypeScope;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.*;
import java.net.URI;

@Path("/admin")
@PrototypeScope
@Component
public final class AdminResource
{
    @Context
    private ServletContext context;

    @GET
    public Response handleGet()
    {
        final URI uri = UriBuilder.fromResource(AdminResource.class).segment("index.html").build();
        return Response.seeOther(uri).build();
    }

    @GET
    @Path("{path:.+}")
    public StaticResource handleResource(@PathParam("path") final String path)
    {
        return new StaticResource(this.context, "/admin/" + path);
    }
}
