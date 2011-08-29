package com.enonic.cms.core.jaxrs.freemarker;

import com.google.common.collect.Maps;
import java.util.Map;

public final class FreeMarkerModel
{
    private final String view;
    private final Map<String, Object> model;

    private FreeMarkerModel(final String view)
    {
        this.view = view;
        this.model = Maps.newHashMap();
    }

    public String getView()
    {
        return this.view;
    }

    public Map<String, Object> getModel()
    {
        return this.model;
    }

    public FreeMarkerModel put(final String key, final Object value)
    {
        this.model.put(key, value);
        return this;
    }

    public static FreeMarkerModel create(final String view)
    {
        return new FreeMarkerModel(view);
    }
}
