package com.enonic.cms.core.plugin.installer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;

final class BundleTransformerImpl
    implements BundleTransformer
{
    private final ManifestTransformer manifestTransformer;

    public BundleTransformerImpl(final ManifestTransformer manifestTransformer)
    {
        this.manifestTransformer = manifestTransformer;
    }

    public InputStream transform(final InputStream in)
        throws IOException
    {
        final JarInputStream jarIn = new JarInputStream(in);

        try {
            final byte[] data = doTransform(jarIn);
            return new ByteArrayInputStream(data);
        } finally {
            jarIn.close();
        }
    }

    private byte[] doTransform(final JarInputStream in)
        throws IOException
    {
        final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        final JarOutputStream jarOut = new JarOutputStream(byteOut);

        try {
            doTransform(in, jarOut);
        } finally {
            jarOut.close();
        }

        return byteOut.toByteArray();
    }

    private void doTransform(final JarInputStream in, final JarOutputStream out)
        throws IOException
    {
        final Manifest mf = in.getManifest();
        if (mf == null) {
            throw new IOException("Not a valid jar file");
        }
        
        final Set<String> entries = Sets.newHashSet();

        while (true) {
            final JarEntry entry = in.getNextJarEntry();
            if (entry == null) {
                break;
            }

            entries.add(entry.getName());
            writeEntry(in, out, entry);
        }

        out.putNextEntry(new JarEntry(JarFile.MANIFEST_NAME));
        this.manifestTransformer.transform(mf, entries);
        mf.write(out);
        out.closeEntry();
    }

    private void writeEntry(final JarInputStream in, final JarOutputStream out, final JarEntry entry)
        throws IOException
    {
        out.putNextEntry(entry);

        if (!entry.isDirectory()) {
            ByteStreams.copy(in, out);
        }

        out.closeEntry();
    }
}
