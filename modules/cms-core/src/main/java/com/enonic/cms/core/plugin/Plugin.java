package com.enonic.cms.core.plugin;

import java.util.List;
import org.joda.time.DateTime;
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

    public boolean isFramework();

    public void update();
}
