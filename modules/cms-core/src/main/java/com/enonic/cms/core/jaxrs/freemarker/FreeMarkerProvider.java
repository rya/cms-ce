package com.enonic.cms.core.jaxrs.freemarker;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
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

@Provider
@Component
public final class FreeMarkerProvider
    implements MessageBodyWriter<FreeMarkerModel>
{
    private final FreeMarkerConfig config;

    @Autowired
    public FreeMarkerProvider(final FreeMarkerConfig config)
    {
        this.config = config;
    }

    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations,
                               final MediaType mediaType)
    {
        return FreeMarkerModel.class.isAssignableFrom(type);
    }

    public long getSize(final FreeMarkerModel object, final Class<?> type, final Type genericType,
                        final Annotation[] annotations, final MediaType mediaType)
    {
        return -1;
    }

    public void writeTo(final FreeMarkerModel object, final Class<?> type, final Type genericType,
                        final Annotation[] annotations, final MediaType mediaType,
                        final MultivaluedMap<String, Object> headers, final OutputStream out)
        throws IOException, WebApplicationException
    {
        final Template template = this.config.getTemplate(object.getView());

        try {
            template.process(object.getModel(), new OutputStreamWriter(out));
        } catch (final TemplateException e) {
            throw new IOException("Failed to find freemarker view [" + object.getView() + "]", e);
        }
    }
}
