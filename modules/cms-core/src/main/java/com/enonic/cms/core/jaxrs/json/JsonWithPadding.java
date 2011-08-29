package com.enonic.cms.core.jaxrs.json;

public final class JsonWithPadding
{
    private final Object jsonSource;
    private final String callbackName;

    public JsonWithPadding(final Object jsonSource, final String callbackName)
    {
        this.jsonSource = jsonSource;
        this.callbackName = callbackName;
    }

    public String getCallbackName()
    {
        return this.callbackName;
    }

    public Object getJsonSource()
    {
        return this.jsonSource;
    }
}
