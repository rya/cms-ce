/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.imports;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.transform.stream.StreamResult;

import com.enonic.cms.core.content.imports.sourcevalueholders.AbstractSourceValue;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.jdom.Document;
import org.jdom.transform.JDOMSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.event.Receiver;
import net.sf.saxon.om.InscopeNamespaceResolver;
import net.sf.saxon.om.NamespaceResolver;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.sxpath.XPathEvaluator;
import net.sf.saxon.sxpath.XPathExpression;
import net.sf.saxon.trans.XPathException;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.contenttype.CtyImportBlockConfig;
import com.enonic.cms.core.content.contenttype.CtyImportConfig;
import com.enonic.cms.core.content.contenttype.CtyImportMappingConfig;
import com.enonic.cms.core.content.imports.sourcevalueholders.BinarySourceValue;
import com.enonic.cms.core.content.imports.sourcevalueholders.StringArraySourceValue;
import com.enonic.cms.core.content.imports.sourcevalueholders.StringSourceValue;

public class ImportDataReaderXml
    extends AbstractImportDataReader
{
    private final List<ImportDataEntry> entries = new ArrayList<ImportDataEntry>();

    private final XPathEvaluator evaluator = new XPathEvaluator();

    private ImportDataEntry prefetchedNextDataEntry;

    public ImportDataReaderXml( final CtyImportConfig config, final InputStream data )
    {
        super( config );

        try
        {
            readData( data );
        }
        catch ( Throwable e )
        {
            throw new ImportException( "Could not read import data: " + e.getMessage(), e );
        }
    }

    public ImportDataEntry getNextEntry()
    {
        return fetchNextEntry();
    }

    public boolean hasMoreEntries()
    {
        ImportDataEntry next;
        if ( prefetchedNextDataEntry != null )
        {
            next = prefetchedNextDataEntry;
        }
        else
        {
            next = fetchNextEntry();
            prefetchedNextDataEntry = next;
        }

        return next != null;
    }

    private ImportDataEntry fetchNextEntry()
    {
        if ( prefetchedNextDataEntry != null )
        {
            ImportDataEntry next = prefetchedNextDataEntry;
            prefetchedNextDataEntry = null;
            return next;
        }

        if ( entries.size() == 0 )
        {
            return null;
        }
        return entries.remove( 0 );
    }

    private void readData( final InputStream data )
        throws Exception
    {
        final List<NodeInfo> baseNodes = getBaseNodes( data );

        if ( baseNodes.size() == 0 )
        {
            throw new ImportException( "No elements found at base: " + config.getBase() );
        }

        for ( NodeInfo baseNode : baseNodes )
        {
            final ImportDataEntry entry = new ImportDataEntry( config.getSyncMapping() );
            addMappings( baseNode, entry );
            addMetadataMappings( baseNode, entry );
            addBlocks( baseNode, entry );
            entries.add( entry );
        }
    }

    private List<NodeInfo> getBaseNodes( final InputStream data )
        throws Exception
    {

        final XMLDocument tep = XMLDocumentFactory.create( new InputStreamReader( data, "UTF-8" ) );
        final Document doc = tep.getAsJDOMDocument();

        this.evaluator.setDefaultElementNamespace( doc.getRootElement().getNamespace().getURI() );

        final XPathExpression exprRoot = this.evaluator.createExpression( "*" );
        final Object resultRoot = exprRoot.evaluateSingle( new JDOMSource( doc.getRootElement() ) );

        return getBaseNodes( (NodeInfo) resultRoot, config.getBase() );
    }

    private List<NodeInfo> getBaseNodes( final NodeInfo nodeInfo, final String xpath )
        throws Exception
    {
        final XPathExpression exprBase = getXPathExpression( nodeInfo, xpath );
        final List resultBase = exprBase.evaluate( nodeInfo );

        final List<NodeInfo> importEls = new LinkedList<NodeInfo>();
        for ( final Object o : resultBase )
        {
            if ( o instanceof NodeInfo )
            {
                importEls.add( ( (NodeInfo) o ) );
            }
        }
        return importEls;
    }

    private void addMappings( final NodeInfo nodeInfo, final ImportDataEntry entry )
        throws Exception
    {
        addMappings( nodeInfo, entry, config.getMappings(), null );
    }

    private void addMappings( final NodeInfo nodeInfo, final ImportDataEntry entry, final List<CtyImportMappingConfig> mapings,
                              final String base )
        throws Exception
    {
        for ( final CtyImportMappingConfig mapping : mapings )
        {
            final AbstractSourceValue value = getSourceValue( nodeInfo, mapping, base );
            if ( value != null )
            {
                entry.add( mapping, value );
            }
        }
    }

    private void addMetadataMappings( final NodeInfo nodeInfo, final ImportDataEntry entry )
        throws Exception
    {
        for ( final CtyImportMappingConfig metadataMapping : config.getMetadataMappings() )
        {
            final AbstractSourceValue value = getSourceValue( nodeInfo, metadataMapping, null );
            if ( value != null )
            {
                entry.addMetadata( metadataMapping, value );
            }
        }
    }

    private void addBlocks( final NodeInfo nodeInfo, final ImportDataEntry entry )
        throws Exception
    {
        for ( final CtyImportBlockConfig block : config.getBlocks() )
        {
            if ( block.getDestination() == null )
            {
                addMappings( nodeInfo, entry, block.getMappings(), block.getBase() );
            }
            else
            {
                for ( final NodeInfo blockNode : getBaseNodes( nodeInfo, block.getBase() ) )
                {
                    final ImportDataEntry blockEntry = new ImportDataEntry( block.getSyncMapping() );
                    addMappings( blockNode, blockEntry, block.getMappings(), null );
                    entry.addBlock( block, blockEntry );
                }
            }
        }
    }

    private AbstractSourceValue getSourceValue( final NodeInfo nodeInfo, final CtyImportMappingConfig mapping, final String base )
        throws Exception
    {
        AbstractSourceValue value = null;
        final String xpath = getXpathValue( base, mapping.getSource() );
        if ( !mapping.isMetaDataMapping() && mapping.isBinary() )
        {
            final byte[] byteValue = getBinaryValue( nodeInfo, xpath );
            if ( byteValue != null )
            {
                value = new BinarySourceValue( byteValue );
            }
        }
        else if ( !mapping.isMetaDataMapping() && mapping.isXml() )
        {
            final String xmlValue = getXmlValue( nodeInfo, xpath );
            if ( xmlValue != null )
            {
                value = new StringSourceValue( xmlValue );
            }
        }
        else if ( !mapping.isMetaDataMapping() && mapping.isHtml() )
        {
            final String htmlValue = getHtmlValue( nodeInfo, xpath );
            if ( htmlValue != null )
            {
                value = new StringSourceValue( htmlValue );
            }
        }
        else if ( !mapping.isMetaDataMapping() && mapping.isMultiple() )
        {
            final Set<String> stringsValue = getStringValues( nodeInfo, xpath );
            if ( stringsValue != null )
            {
                value = new StringArraySourceValue( stringsValue );
            }
        }
        else
        {
            final String stringValue = getStringValue( nodeInfo, xpath );
            if ( stringValue != null )
            {
                value = new StringSourceValue( stringValue );
            }
        }

        if ( value != null && mapping.hasAdditionalSource() )
        {
            value.setAdditionalValue( getStringValue( nodeInfo, getXpathValue( base, mapping.getAdditionalSource() ) ) );
        }
        return value;
    }

    private String getXpathValue( final String base, final String source )
    {
        String xpath = base;
        if ( xpath == null )
        {
            return source;
        }

        if ( !xpath.endsWith( "/" ) )
        {
            xpath += "/";
        }

        return xpath + source;
    }

    private byte[] getBinaryValue( final NodeInfo nodeInfo, final String xpath )
        throws XPathException, DecoderException
    {
        String base64String = getStringValue( nodeInfo, xpath );
        if ( base64String == null )
        {
            return null;
        }
        return Base64.decodeBase64( base64String.getBytes() );
    }

    private String getStringValue( final NodeInfo nodeInfo, final String xpath )
        throws XPathException
    {
        final XPathExpression expr = getXPathExpression( nodeInfo, xpath );
        final Object o = expr.evaluateSingle( nodeInfo );
        return getStringValue( o );
    }

    private String getHtmlValue( final NodeInfo nodeInfo, final String xpath )
        throws XPathException
    {
        final XPathExpression expr = getXPathExpression( nodeInfo, xpath + "/node()" );
        final List<Object> nodes = expr.evaluate( nodeInfo );
        return getHtmlValue( nodes );
    }

    private String getXmlValue( final NodeInfo nodeInfo, final String xpath )
        throws XPathException
    {
        final XPathExpression expr = getXPathExpression( nodeInfo, xpath + "/descendant::*" );
        final Object o = expr.evaluateSingle( nodeInfo );
        return getXmlValue( o );
    }

    private Set<String> getStringValues( final NodeInfo nodeInfo, final String xpath )
        throws XPathException
    {
        final XPathExpression expr = getXPathExpression( nodeInfo, xpath );
        final List<Object> os = expr.evaluate( nodeInfo );

        final Set<String> values = new HashSet<String>();
        for ( Object o : os )
        {
            values.add( getStringValue( o ) );
        }
        return values;
    }

    private XPathExpression getXPathExpression( final NodeInfo nodeInfo, final String xpath )
        throws XPathException
    {
        final NamespaceResolver resolver = new CombinedNamespaceResolver( getNamespaceResolvers( nodeInfo ) );
        this.evaluator.setNamespaceResolver( resolver );
        return this.evaluator.createExpression( xpath );
    }

    private String getStringValue( final Object o )
    {
        if ( o == null )
        {
            return null;
        }

        if ( o instanceof NodeInfo )
        {
            return ( (NodeInfo) o ).getStringValue();

        }
        return String.valueOf( o );
    }

    private String getHtmlValue( final List<Object> nodes )
        throws XPathException
    {
        StringBuffer xhtml = new StringBuffer();
        for ( Object node : nodes )
        {
            xhtml.append( serialize( node, "xhtml", false ) );
        }
        return xhtml.toString();
    }

    private String getXmlValue( final Object o )
        throws XPathException
    {
        return serialize( o, "xml", false );
    }

    private String serialize( final Object o, final String method, final boolean includeProlog )
        throws XPathException
    {
        if ( o instanceof NodeInfo )
        {
            final NodeInfo nodeInfo = ( (NodeInfo) o );
            final Configuration config = nodeInfo.getConfiguration();
            final StringWriter sw = new StringWriter();
            final Properties props = new Properties();
            props.setProperty( "method", method );
            props.setProperty( "indent", "no" );
            if ( !includeProlog )
            {
                props.setProperty( "omit-xml-declaration", "yes" );
            }
            else
            {
                props.setProperty( "omit-xml-declaration", "no" );
            }
            final Receiver serializer =
                config.getSerializerFactory().getReceiver( new StreamResult( sw ), config.makePipelineConfiguration(), props );
            nodeInfo.copy( serializer, NodeInfo.ALL_NAMESPACES, true, 0 );
            return sw.toString();
        }
        return null;
    }

    private Set<NamespaceResolver> getNamespaceResolvers( final NodeInfo nodeInfo )
    {
        final Set<NamespaceResolver> resolvers = new HashSet<NamespaceResolver>();

        // From import data
        resolvers.add( new InscopeNamespaceResolver( nodeInfo ) );

        // From import definition
        final NamespaceResolver importDefNamespace = config.getNamespaceResolver();
        if ( importDefNamespace != null )
        {
            resolvers.add( importDefNamespace );
        }

        // Default
        final NamespaceResolver defaultNamespace = new XPathEvaluator().getNamespaceResolver();
        if ( defaultNamespace != null )
        {
            resolvers.add( defaultNamespace );
        }
        return resolvers;
    }

    private class CombinedNamespaceResolver
        implements NamespaceResolver
    {
        final private Set<NamespaceResolver> resolvers;

        private CombinedNamespaceResolver( final Set<NamespaceResolver> resolvers )
        {
            this.resolvers = resolvers;
        }

        public Iterator iteratePrefixes()
        {
            return null;
        }

        public String getURIForPrefix( final String s, final boolean b )
        {
            for ( NamespaceResolver resolver : resolvers )
            {
                final String ns = resolver.getURIForPrefix( s, b );
                if ( ns != null )
                {
                    return ns;
                }
            }
            return null;
        }
    }
}