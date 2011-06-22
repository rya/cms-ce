package com.enonic.cms.admin.mapper;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class JsonDateTimeSerializer
    extends JsonSerializer<Date>
{
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void serialize(final Date date, final JsonGenerator gen, final SerializerProvider provider)
        throws IOException
    {
        gen.writeString(DATE_FORMAT.format(date));
    }
}
