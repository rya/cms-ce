package com.enonic.cms.admin;

import com.google.common.io.ByteStreams;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class StaticResource
    implements StreamingOutput
{
    private final InputStream input;
    private final MediaType mediaType;

    public StaticResource(final ServletContext context, final String path)
    {
        this(context.getResourceAsStream(path), context.getMimeType(path));
    }

    public StaticResource(final InputStream input, final String mediaType)
    {
        this.input = input;
        this.mediaType = MediaType.valueOf(mediaType);
    }

    @GET
    public Response handleGet()
    {
        if (input == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok().entity(this).type(this.mediaType).build();
    }

    public void write(final OutputStream out)
        throws IOException, WebApplicationException
    {
        ByteStreams.copy(this.input, out);
    }
}
