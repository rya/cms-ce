package com.enonic.cms.admin;

import com.google.common.io.ByteStreams;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.*;
import java.io.InputStream;
import java.net.URI;

@Path("/admin")
@Component
public final class AdminResource
{
    @Context
    private ServletContext context;

    @GET
    public Response handleGet()
    {
        final URI uri = UriBuilder.fromResource(AdminResource.class).segment("index.html").build();
        return Response.temporaryRedirect(uri).build();
    }

    @GET
    @Path("{path:.+}")
    public Response handleResource(@PathParam("path") final String path)
        throws Exception
    {
        final InputStream in = this.context.getResourceAsStream("/admin/" + path);
        if (in == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        final String type = this.context.getMimeType(path);
        final MediaType mediaType = type != null ? MediaType.valueOf(type) : MediaType.APPLICATION_OCTET_STREAM_TYPE;
        final byte[] data = ByteStreams.toByteArray(in);

        return Response.ok().type(mediaType).entity(data).build();
    }
}
