package com.enonic.cms.core.jaxrs.json;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sun.jersey.spi.MessageBodyWorkers;
import org.springframework.stereotype.Component;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

@Component
@Provider
public final class JsonWithPaddingProvider
    implements MessageBodyWriter<JsonWithPadding>
{
    private final Map<String, Set<String>> javascriptTypes;

    @Context
    private MessageBodyWorkers bodyWorker;

    public JsonWithPaddingProvider()
    {
        this.javascriptTypes = Maps.newHashMap();
        this.javascriptTypes.put("application", Sets.newHashSet("x-javascript", "ecmascript", "javascript"));
        this.javascriptTypes.put("text", Sets.newHashSet("ecmascript", "jscript"));
    }

    private boolean isJavascript(final MediaType type)
    {
        final Set<String> subtypes = this.javascriptTypes.get(type.getType());
        return (subtypes != null) && subtypes.contains(type.getSubtype());
    }

    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations,
                               final MediaType mediaType)
    {
        return (type == JsonWithPadding.class) && isJavascript(mediaType);
    }

    public long getSize(final JsonWithPadding object, final Class<?> type, final Type genericType,
                        final Annotation[] annotations, final MediaType mediaType)
    {
        return -1;
    }

    public void writeTo(final JsonWithPadding object, final Class<?> type, final Type genericType,
                        final Annotation[] annotations, final MediaType mediaType,
                        final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream)
        throws IOException, WebApplicationException
    {
        final Object jsonEntity = object.getJsonSource();
        final Class<?> entityType = jsonEntity.getClass();

        final MessageBodyWriter bw = this.bodyWorker.getMessageBodyWriter(entityType, entityType, annotations,
                MediaType.APPLICATION_JSON_TYPE);
        if (bw == null) {
            throw new WebApplicationException(500);
        }

        entityStream.write(object.getCallbackName().getBytes());
        entityStream.write('(');

        bw.writeTo(jsonEntity, entityType, entityType, annotations, MediaType.APPLICATION_JSON_TYPE, httpHeaders,
                entityStream);

        entityStream.write(')');
    }
}
