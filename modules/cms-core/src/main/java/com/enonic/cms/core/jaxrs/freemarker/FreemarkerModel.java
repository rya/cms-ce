package com.enonic.cms.core.jaxrs.freemarker;

import com.google.common.collect.Maps;
import java.util.Map;

public final class FreemarkerModel
{
    private final String view;
    private final Map<String, Object> model;

    private FreemarkerModel(final String view)
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

    public FreemarkerModel put(final String key, final Object value)
    {
        this.model.put(key, value);
        return this;
    }

    public static FreemarkerModel create(final String view)
    {
        return new FreemarkerModel(view);
    }
}
