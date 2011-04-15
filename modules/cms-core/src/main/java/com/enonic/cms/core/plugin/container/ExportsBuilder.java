package com.enonic.cms.core.plugin.container;

import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import org.twdata.pkgscanner.ExportPackage;
import org.twdata.pkgscanner.PackageScanner;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.enonic.cms.api.util.LogFacade;

import static org.twdata.pkgscanner.PackageScanner.include;
import static org.twdata.pkgscanner.PackageScanner.jars;
import static org.twdata.pkgscanner.PackageScanner.packages;

final class ExportsBuilder
{
    private final static LogFacade LOG = LogFacade.get( ExportsBuilder.class );

    private final ServletContext context;

    public ExportsBuilder( final ServletContext context )
    {
        this.context = context;
    }

    public String getExports()
        throws Exception
    {
        final Map<String, String> map = findExportMap();

        LOG.info( "Package scanning found [{0}] packages for export", map.size() );
        LOG.debug( "The following packages is to be exported...\n  {0}", Joiner.on( "\n  " ).withKeyValueSeparator( " -> " ).join( map ) );

        return Joiner.on( "," ).withKeyValueSeparator( ";version=" ).join( map );
    }

    private Map<String, String> findExportMap()
        throws Exception
    {
        final Map<String, String> map = Maps.newTreeMap();
        for ( final ExportPackage pck : scanExports() )
        {
            map.put( pck.getPackageName(), pck.getVersion() );
        }

        map.put( "org.apache.log4j", "1.2.14" );
        map.put( "org.apache.commons.logging", "1.1.1" );

        final String servletVersion = this.context.getMajorVersion() + "." + this.context.getMinorVersion();
        map.put( "javax.servlet", servletVersion );
        map.put( "javax.servlet.resources", servletVersion );
        map.put( "javax.servlet.http", servletVersion );

        return map;
    }

    private Collection<ExportPackage> scanExports()
        throws Exception
    {
        final PackageScanner scanner = new PackageScanner();

        scanner.select( jars( include( "*" ) ), packages(
            include( "com.enonic.cms.api*", "org.jdom*", "org.jaxen*", "org.slf4j", "org.slf4j.helpers", "org.slf4j.spi",
                     "org.aopalliance*", "org.springframework.aop*", "org.springframework.beans*", "org.springframework.context*",
                     "org.springframework.core*", "javax.mail*" ) ) );

        return scanner.scan( findUrls() );
    }

    private URL[] findUrls()
        throws Exception
    {
        final Set<URL> set = Sets.newHashSet();

        for ( final Object o : this.context.getResourcePaths( "/WEB-INF/lib/" ) )
        {
            final String path = (String) o;

            if ( path.endsWith( ".jar" ) )
            {
                final URL url = this.context.getResource( path );
                set.add( url );
            }
        }

        return set.toArray( new URL[set.size()] );
    }
}
