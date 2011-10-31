package com.enonic.cms.core.plugin.installer;

import com.enonic.cms.core.plugin.spring.SpringActivator;
import com.google.common.base.Strings;
import java.io.IOException;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

final class ManifestTransformerImpl
    implements ManifestTransformer
{
    public void transform(final Manifest mf, final Set<String> entries)
        throws IOException
    {
        final Attributes attr = mf.getMainAttributes();

        final String pluginId = findPluginId(attr);
        final String pluginName = findPluginName(attr, pluginId);
        final String pluginVersion = findPluginVersion(attr);

        if (Strings.isNullOrEmpty(pluginId)) {
            throw new IOException("Required metadata not found. Plugin-Id or Bundle-SymbolicName is required.");
        }

        attr.putValue("Bundle-SymbolicName", pluginId);
        attr.putValue("Bundle-Name", pluginName);
        attr.putValue("Bundle-Version", pluginVersion);
        attr.putValue("Bundle-ManifestVersion", "2");
        attr.putValue("Require-Bundle", "org.apache.felix.framework");
        attr.putValue("Bundle-Activator", SpringActivator.class.getName());
        attr.putValue("Bundle-ClassPath", findClassPath(entries));

        // Remove old bundle metadata
        attr.putValue("Import-Package", null);
        attr.putValue("Export-Package", null);
    }

    private String findPluginId(final Attributes attr)
    {
        String value = attr.getValue("Plugin-Id");

        if (Strings.isNullOrEmpty(value)) {
            value = attr.getValue("Bundle-SymbolicName");
        }

        return value;
    }

    private String findPluginName(final Attributes attr, final String defValue)
    {
        String value = attr.getValue("Plugin-Name");

        if (Strings.isNullOrEmpty(value)) {
            value = attr.getValue("Bundle-Name");
        }

        if (Strings.isNullOrEmpty(value)) {
            value = defValue;
        }

        return value;
    }

    private String findPluginVersion(final Attributes attr)
    {
        String value = attr.getValue("Plugin-Version");

        if (Strings.isNullOrEmpty(value)) {
            value = attr.getValue("Bundle-Version");
        }

        if (Strings.isNullOrEmpty(value)) {
            value = "0.0.0";
        }

        return value.replace('-', '.');
    }

    private String findClassPath(final Set<String> entries)
    {
        final StringBuilder str = new StringBuilder(".");

        for (final String entry : entries) {
            if (entry.endsWith(".jar") && entry.startsWith("META-INF/lib/")) {
                str.append(",/").append(entry);
            }
        }

        return str.toString();
    }
}
