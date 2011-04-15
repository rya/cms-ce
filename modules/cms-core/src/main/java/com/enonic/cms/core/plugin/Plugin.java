package com.enonic.cms.core.plugin;

import java.util.List;

import org.joda.time.DateTime;
import org.osgi.framework.Bundle;

import com.enonic.cms.api.plugin.ext.Extension;

public interface Plugin
{
    public long getKey();

    public String getId();

    public String getName();

    public String getVersion();

    public boolean isActive();

    public List<Extension> getExtensions();

    public DateTime getTimestamp();

    public Bundle getBundle();

    public boolean isFramework();

    public void update();
}
