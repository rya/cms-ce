package com.enonic.cms.core.jaxrs.json;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Component;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Component
@Provider
public final class JsonProvider
    implements MessageBodyWriter<Object>
{
    private final Gson gson;

    public JsonProvider()
    {
        final GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.setDateFormat("yyyy-MM-dd hh:mm:ss");
        this.gson = builder.create();
    }

    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations,
                               final MediaType mediaType)
    {
        return mediaType.equals(MediaType.APPLICATION_JSON_TYPE);
    }

    public long getSize(final Object obj, final Class<?> type, final Type genericType, final Annotation[] annotations,
                        final MediaType mediaType)
    {
        return -1;
    }

    public void writeTo(final Object obj, final Class<?> type, final Type genericType, final Annotation[] annotations,
                        final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders,
                        final OutputStream entityStream)
        throws IOException, WebApplicationException
    {
        final OutputStreamWriter out = new OutputStreamWriter(entityStream, Charsets.UTF_8);
        this.gson.toJson(obj, out);
        out.flush();
    }
}
