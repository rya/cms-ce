package com.enonic.cms.api.plugin;

import java.util.Map;

public interface PluginConfig
    extends Map<String, String>
{
    public String getString(String key);

    public String getString(String key, String defValue);

    public Boolean getBoolean(String key);

    public Boolean getBoolean(String key, Boolean defValue);

    public Integer getInteger(String key);

    public Integer getInteger(String key, Integer defValue);

    public Long getLong(String key);

    public Long getLong(String key, Long defValue);

    public Float getFloat(String key);

    public Float getFloat(String key, Float defValue);

    public Double getDouble(String key);

    public Double getDouble(String key, Double defValue);
}
