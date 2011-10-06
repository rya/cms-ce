/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.transform.JDOMSource;

import net.sf.saxon.om.InscopeNamespaceResolver;
import net.sf.saxon.om.NamespaceResolver;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.sxpath.XPathEvaluator;
import net.sf.saxon.sxpath.XPathExpression;
import net.sf.saxon.trans.XPathException;

import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfigType;
import com.enonic.cms.core.content.contenttype.dataentryconfig.RelatedContentDataEntryConfig;

public class ContentTypeImportConfigParser
{
    private String importName;

    public static List<CtyImportConfig> parseAllImports( final CtyFormConfig form, final Element configEl )
        throws InvalidImportConfigException
    {
        final List<CtyImportConfig> importConfigs = new ArrayList<CtyImportConfig>();
        final Element importsEl = configEl.getChild( "imports" );

        if ( importsEl != null )
        {
            final List<Element> importEls = importsEl.getChildren( "import" );
            for ( Element importEl : importEls )
            {
                final String importName = importEl.getAttributeValue( "name" );
                if ( importName == null || importName.length() == 0 )
                {
                    throw new InvalidImportConfigException( "Could not find required attribute \"name\" in an import configuration." );
                }

                ContentTypeImportConfigParser parser = new ContentTypeImportConfigParser( importName );
                importConfigs.add( parser.parseImport( form, importEl ) );
            }
        }

        verifyImports( importConfigs, configEl );

        return importConfigs;
    }

    private ContentTypeImportConfigParser( String importName )
    {
        this.importName = importName;
    }

    private CtyImportConfig parseImport( final CtyFormConfig form, final Element importEl )
        throws InvalidImportConfigException
    {

        final String syncInputField = importEl.getAttributeValue( "sync" );

        final CtyImportConfig importConfig = new CtyImportConfig( form, importName, syncInputField, getNamespaceResolver( importEl ) );

        parseModeSetting( importEl, importConfig );

        if ( importConfig.getMode() == CtyImportModeConfig.CSV )
        {
            parseCSVSeparator( importEl, importConfig );
            parseCSVSkip( importEl, importConfig );

            if ( importEl.getAttribute( "base" ) != null )
            {
                throw new InvalidImportConfigException( importName,
                                                        "The base setting is only used when the import source is of XML format." );
            }
        }
        else if ( importConfig.getMode() == CtyImportModeConfig.XML )
        {
            if ( importEl.getAttribute( "separator" ) != null )
            {
                throw new InvalidImportConfigException( importName,
                                                        "The separator setting is only used when the import source is of CSV format." );
            }
            if ( importEl.getAttribute( "skip" ) != null )
            {
                throw new InvalidImportConfigException( importName,
                                                        "The skip setting is only used when the import source is of CSV format." );
            }

            parseBase( importEl, importConfig );
        }

        final String diff = importEl.getAttributeValue( "diff" );
        if ( diff != null )
        {
            throw new InvalidImportConfigException( importName, "The diff setting is obsolote, please remove it." );
        }

        if ( !importConfig.isSyncEnabled() && importEl.getAttribute( "purge" ) != null )
        {
            throw new InvalidImportConfigException( importName, "Purge setting is only needed when synchronization is enabled." );
        }
        final CtyImportPurgeConfig impurtPurgeConfig =
            CtyImportPurgeConfig.parse( importEl.getAttributeValue( "purge" ), importConfig.getName() );
        importConfig.setPurge( impurtPurgeConfig );

        final CtyImportStatusConfig importStatusConfig = CtyImportStatusConfig.parse( importName, importEl.getAttributeValue( "status" ) );
        importConfig.setStatus( importStatusConfig );

        Attribute updateStrategyAttribute = importEl.getAttribute( "update-strategy" );
        if ( updateStrategyAttribute != null )
        {
            if ( StringUtils.isEmpty( updateStrategyAttribute.getValue() ) )
            {
                throw new InvalidImportConfigException( importName, "Missing value for 'update-strategy' setting" );
            }
            importConfig.setUpdateStrategy( CtyImportUpdateStrategyConfig.parse( importName, updateStrategyAttribute.getValue() ) );
        }

        parseImportMappings( importConfig, importEl, importEl );
        parseImportMappingBlocks( importConfig, importEl );

        verifyImportConfig( importConfig );

        return importConfig;
    }

    private void verifyImportConfig( CtyImportConfig importConfig )
    {
        verifyReferredSyncFieldExist( importConfig );

        verifyMappingDestinationsExist( importConfig );

        verifyMappingSettingEXSRCIsOnlyUsedWhenInputTypeIsImageOrUploadfile( importConfig );

        final CtyFormConfig form = importConfig.getForm();

        /* Check that mapping to binary type is not used in cvs import */
        if ( importConfig.getMode() == CtyImportModeConfig.CSV )
        {

            for ( CtyImportMappingConfig mappingConfig : importConfig.getMappings() )
            {
                final DataEntryConfigType destinationType = form.getInputConfig( mappingConfig.getDestination() ).getType();
                if ( destinationType == DataEntryConfigType.BINARY )
                {
                    throw new InvalidImportConfigException( importName,
                                                            "Mapping to content input field '" + mappingConfig.getDestination() +
                                                                "' is not allowed." + " Mapping to fields of type '" + destinationType +
                                                                "' is only allowed when importing from XML formatted sources." );
                }
            }
        }
    }

    private void verifyMappingSettingEXSRCIsOnlyUsedWhenInputTypeIsImageOrUploadfile( CtyImportConfig importConfig )
    {
        final CtyFormConfig form = importConfig.getForm();
        /* Check that mapping attribute exsrc is only used on mappings of type image or binary (used as imagetext and binaryname) */
        for ( CtyImportMappingConfig mappingConfig : importConfig.getMappings() )
        {
            final DataEntryConfigType destinationType = form.getInputConfig( mappingConfig.getDestination() ).getType();
            if ( mappingConfig.getAdditionalSource() != null &&
                !( destinationType == DataEntryConfigType.IMAGE || destinationType == DataEntryConfigType.BINARY ) )
            {
                throw new InvalidImportConfigException( importName,
                                                        "Mapping setting exsrc is only applicaple for mappings whose destinations refers to a content input field of type '" +
                                                            DataEntryConfigType.IMAGE.getName() + "' or \'" +
                                                            DataEntryConfigType.BINARY.getName() + "'. Type was: " + destinationType );
            }
        }
    }

    private void verifyMappingDestinationsExist( CtyImportConfig importConfig )
    {
        final CtyFormConfig form = importConfig.getForm();

        /* Check that import mappings exist */
        for ( CtyImportMappingConfig mappingConfig : importConfig.getMappings() )
        {
            String dest = mappingConfig.getDestination();
            if ( form.getInputConfig( dest ) == null )
            {
                throw new InvalidImportConfigException( importName, "Mapping destination (content input field) does not exist: " + dest );
            }
        }
    }

    private void verifyReferredSyncFieldExist( CtyImportConfig importConfig )
    {
        if ( importConfig.isSyncEnabled() && !importConfig.isSyncMappedToContentKey() && importConfig.getSyncMapping() == null )
        {
            throw new InvalidImportConfigException( importName, "Referred input field '" + importConfig.getSync() +
                "' in sync setting does not exist." );
        }
    }

    private void parseBase( Element importEl, CtyImportConfig importConfig )
    {
        final String base = importEl.getAttributeValue( "base" );
        if ( base != null )
        {
            importConfig.setBase( base );
        }
    }

    private void parseCSVSkip( Element importEl, CtyImportConfig importConfig )
    {
        final String skip = importEl.getAttributeValue( "skip" );
        if ( skip != null )
        {
            importConfig.setSkip( Integer.valueOf( skip ) );
        }
    }

    private void parseCSVSeparator( Element importEl, CtyImportConfig importConfig )
    {
        final String separator = importEl.getAttributeValue( "separator" );
        if ( separator != null )
        {
            importConfig.setSeparator( separator );
        }
    }

    private void parseModeSetting( Element importEl, CtyImportConfig importConfig )
    {
        final String mode = importEl.getAttributeValue( "mode" );
        final CtyImportModeConfig impurtModeConfig = CtyImportModeConfig.parse( mode );
        if ( mode == null )
        {
            throw new InvalidImportConfigException( importName, "Invalid mode setting: " + mode );
        }
        importConfig.setMode( impurtModeConfig );
    }

    private NamespaceResolver getNamespaceResolver( final Element importEl )
    {
        try
        {
            final XPathEvaluator evaluator = new XPathEvaluator();
            final XPathExpression expr = evaluator.createExpression( "*" );
            final Object o = expr.evaluateSingle( new JDOMSource( importEl ) );

            if ( o instanceof NodeInfo )
            {
                NodeInfo node = (NodeInfo) o;
                if ( node != null )
                {
                    return new InscopeNamespaceResolver( node );
                }
            }
        }
        catch ( XPathException ex )
        {
        }
        return null;
    }


    private void parseImportMappings( final CtyImportMappingContainer mappingContainer, final Element importEl, final Element mappingsEl )
    {
        final List<Element> mappingEls = mappingsEl.getChildren( "mapping" );
        if ( mappingEls.size() == 0 )
        {
            throw new InvalidImportConfigException( importName, "No mapping elements found." );
        }

        int mappingCount = 0;
        for ( final Element mappingEl : mappingEls )
        {
            mappingCount++;
            final CtyImportMappingConfig mapping = parseImportMapping( mappingContainer, mappingEl, mappingCount );
            final boolean mappingAdded = mappingContainer.addMapping( mapping );
            if ( !mappingAdded )
            {
                throw new InvalidImportConfigException( importName,
                                                        "Duplicate mapping destination \"" + mapping.getDestination() + "\" found." );
            }
        }
    }

    private void parseImportMappingBlocks( final CtyImportConfig importConfig, final Element importEl )
    {
        final List<Element> blockEls = importEl.getChildren( "block" );

        if ( blockEls.size() > 0 && importConfig.getMode() == CtyImportModeConfig.CSV )
        {
            throw new InvalidImportConfigException( importName, "The \"block\" functionality cannot be used in csv mode" );
        }

        for ( final Element blockEl : blockEls )
        {
            final String base = blockEl.getAttributeValue( "base" );
            if ( base == null )
            {
                throw new InvalidImportConfigException( importName, "Could not find the required \"base\" attribute in block" );
            }

            final String sync = blockEl.getAttributeValue( "sync" );
            final String dest = blockEl.getAttributeValue( "dest" );
            String purgeStr = blockEl.getAttributeValue( "purge" );
            boolean purge = false;
            if ( purgeStr != null )
            {
                purge = Boolean.parseBoolean( purgeStr );
            }
            final CtyImportBlockConfig block = new CtyImportBlockConfig( importConfig, base, dest, sync, purge );
            parseImportMappings( block, importEl, blockEl );

            importConfig.addBlock( block );
        }
    }

    private CtyImportMappingConfig parseImportMapping( final CtyImportMappingContainer mappingContainer, final Element mappingEl,
                                                       final int mappingCount )
    {
        final String dest = mappingEl.getAttributeValue( "dest" );
        if ( StringUtils.isEmpty( dest ) )
        {
            throw new InvalidImportConfigException( importName,
                                                    "Missing destination attribute (\"dest\") in mapping number " + mappingCount + "." );
        }

        final String src = mappingEl.getAttributeValue( "src" );
        if ( StringUtils.isEmpty( src ) )
        {
            throw new InvalidImportConfigException( importName,
                                                    "Missing source attribute (\"src\") in mapping number " + mappingCount + "." );
        }

        final CtyImportMappingConfig mapping = new CtyImportMappingConfig( mappingContainer.getImportConfig(), src, dest );

        final String relatedContentType = mappingEl.getAttributeValue( "relatedcontenttype" );
        if ( relatedContentType != null )
        {
            mapping.setRelatedContentType( relatedContentType );
        }

        String relatedField = mappingEl.getAttributeValue( "relatedfield" );
        if ( relatedField != null )
        {
            mapping.setRelatedField( relatedField );
        }

        String format = mappingEl.getAttributeValue( "format" );
        if ( format != null )
        {
            try
            {
                String s = new SimpleDateFormat( format ).format( new Date() );
            }
            catch ( IllegalArgumentException ex )
            {
                throw new InvalidImportConfigException( importName, "Illegal \"format\" value: " + format + " in import \"" +
                    mappingContainer.getName() + "\"" );
            }
            mapping.setFormat( format );
        }

        String separator = mappingEl.getAttributeValue( "separator" );
        if ( separator != null )
        {
            mapping.setSeparator( separator );
        }

        String additionalSrc = mappingEl.getAttributeValue( "exsrc" );
        if ( additionalSrc != null )
        {
            mapping.setAdditionalSource( additionalSrc );
        }

        return mapping;
    }

    private static void verifyImports( final List<CtyImportConfig> importConfigs, final Element configEl )
    {
        for ( CtyImportConfig importConfig : importConfigs )
        {
            final CtyFormConfig form = importConfig.getForm();

            /* Check that mapping attribute format is only used on mappings of type date */
            for ( CtyImportMappingConfig mappingConfig : importConfig.getMappings() )
            {
                final DataEntryConfigType destinationType = form.getInputConfig( mappingConfig.getDestination() ).getType();
                if ( mappingConfig.getFormat() != null && destinationType != DataEntryConfigType.DATE )
                {
                    throw new InvalidContentTypeConfigException(
                        "Import mapping attribute \"format\" can only be specified for mapping of type \"" + DataEntryConfigType.DATE +
                            "\". Attribute found in mapping of type \"" + destinationType + "\" in import \"" + importConfig.getName() +
                            "\".", configEl );
                }
            }

            /* Check meta data mapping */
            for ( CtyImportMappingConfig mappingConfig : importConfig.getMetadataMappings() )
            {
                String dest = mappingConfig.getDestination();
                if ( !( dest.equals( "@publishfrom" ) || dest.equals( "@publishto" ) || dest.equals( "@key" ) ) )
                {
                    throw new InvalidContentTypeConfigException(
                        "Invalid metadata mapping \"" + dest + "\" in import \"" + importConfig.getName() +
                            "\". Only \"@publishfrom\" and \"@publishto\" are supported.", configEl );
                }
            }

            /* Check that mapping separator is not used in xml import */
            if ( importConfig.getMode() == CtyImportModeConfig.XML )
            {
                for ( CtyImportMappingConfig mappingConfig : importConfig.getMappings() )
                {
                    if ( mappingConfig.getSeparator() != null )
                    {
                        throw new InvalidContentTypeConfigException(
                            "Invalid mapping attribute \"separator\" in mapping \"" + mappingConfig.getDestination() + "\" in import \"" +
                                importConfig.getName() + ". Attribute can only be specified for import of type \"" +
                                CtyImportModeConfig.CSV + "\". Attribute found in import of type \"" + CtyImportModeConfig.XML + "\".",
                            configEl );
                    }
                }
            }
            else if ( importConfig.getMode() == CtyImportModeConfig.CSV )
            {
                /* Check that mapping separator is only used in mapping of "multiple" types (related content with mulitple = true
                   and keywords) and that it differ from import separator */
                for ( CtyImportMappingConfig mappingConfig : importConfig.getMappings() )
                {
                    final DataEntryConfig inputConfig = form.getInputConfig( mappingConfig.getDestination() );
                    final DataEntryConfigType destinationType = inputConfig.getType();
                    if ( mappingConfig.getSeparator() != null )
                    {
                        boolean multiple = false;
                        if ( destinationType == DataEntryConfigType.RELATEDCONTENT )
                        {
                            multiple = ( (RelatedContentDataEntryConfig) inputConfig ).isMultiple();
                        }
                        else if ( destinationType == DataEntryConfigType.KEYWORDS )
                        {
                            multiple = true;
                        }

                        if ( !multiple )
                        {
                            throw new InvalidContentTypeConfigException(
                                "Invalid mapping attribute \"separator\" in mapping \"" + mappingConfig.getDestination() +
                                    "\" in import \"" + importConfig.getName() +
                                    "\". Attribute can only be specified for mapping of type \"" + DataEntryConfigType.KEYWORDS +
                                    "\" or \"" + DataEntryConfigType.RELATEDCONTENT + "\" (with \"multiple\" set to \"true\"). " +
                                    "Attribute found in mapping of type \"" + destinationType + "\"" +
                                    ( destinationType == DataEntryConfigType.RELATEDCONTENT
                                        ? " (with \"multiple\" set to \"false\")"
                                        : "" ) + ".", configEl );
                        }
                        if ( mappingConfig.getSeparator().equals( importConfig.getSeparator() ) )
                        {
                            throw new InvalidContentTypeConfigException(
                                "Invalid mapping attribute \"separator\" in mapping \"" + mappingConfig.getDestination() +
                                    "\" in import \"" + importConfig.getName() +
                                    "\". Mapping separator value must differ from import separator value.", configEl );
                        }
                    }
                }
            }

            /* Check that mapping attribute "relatedcontenttype" is only used on mappings of type related content and that "relatedfield" are specified as well */
            for ( CtyImportMappingConfig mappingConfig : importConfig.getMappings() )
            {
                if ( mappingConfig.getRelatedContentType() != null )
                {
                    final DataEntryConfigType destinationType = form.getInputConfig( mappingConfig.getDestination() ).getType();
                    if ( destinationType != DataEntryConfigType.RELATEDCONTENT )
                    {
                        throw new InvalidContentTypeConfigException(
                            "Import mapping attribute \"relatedcontenttype\" can only be specified for mapping of type \"" +
                                DataEntryConfigType.RELATEDCONTENT + "\". Attribute found in mapping \"" + mappingConfig.getDestination() +
                                "\" of type \"" + destinationType + "\" in import \"" + importConfig.getName() + "\".", configEl );
                    }
                    if ( mappingConfig.getRelatedField() == null )
                    {
                        throw new InvalidContentTypeConfigException(
                            "Import mapping attribute \"relatedcontenttype\" cannot be specified without specifying mapping attribute \"relatedfield\". " +
                                "Attribute found in mapping \"" + mappingConfig.getDestination() + "\" in import \"" +
                                importConfig.getName() + "\".", configEl );
                    }
                }
            }

            /* Check that mapping attribute "relatedfield" is only used on mappings of type related content and that "relatedcontenttype" are specified as well */
            for ( CtyImportMappingConfig mappingConfig : importConfig.getMappings() )
            {
                if ( mappingConfig.getRelatedField() != null )
                {
                    final DataEntryConfigType destinationType = form.getInputConfig( mappingConfig.getDestination() ).getType();
                    if ( destinationType != DataEntryConfigType.RELATEDCONTENT )
                    {
                        throw new InvalidContentTypeConfigException(
                            "Import mapping attribute \"relatedfield\" can only be specified for mapping of type \"" +
                                DataEntryConfigType.RELATEDCONTENT + "\". Attribute found in mapping \"" + mappingConfig.getDestination() +
                                "\" of type \"" + destinationType + "\" in import \"" + importConfig.getName() + "\".", configEl );
                    }
                    if ( mappingConfig.getRelatedContentType() == null )
                    {
                        throw new InvalidContentTypeConfigException(
                            "Import mapping attribute \"relatedfield\" cannot be specified without specifying mapping attribute \"relatedcontenttype\". " +
                                "Attribute found in mapping \"" + mappingConfig.getDestination() + "\" in import \"" +
                                importConfig.getName() + "\".", configEl );
                    }
                }
            }

            for ( final CtyImportBlockConfig blockConfig : importConfig.getBlocks() )
            {
                /* Check that block attribute "destination" is of type "block group"*/
                final String blockDest = blockConfig.getDestination();
                if ( blockDest != null )
                {
                    final CtySetConfig setConfig = form.getSetConfig( blockDest );
                    if ( setConfig == null )
                    {
                        throw new InvalidContentTypeConfigException(
                            "Could not find input config for destination \"" + blockDest + "\" in import \"" + importConfig.getName() +
                                "\".", configEl );
                    }
                    else if ( setConfig.getGroupXPath() == null )
                    {
                        throw new InvalidContentTypeConfigException(
                            "Invalid block destination \"" + blockDest + "\" in import \"" + importConfig.getName() + "\" found. " +
                                "The destination must be a block group.", configEl );
                    }
                }

                /* Check that block attribute "sync" exists */
                final String blockSync = blockConfig.getSync();
                if ( blockSync != null )
                {
                    if ( form.getInputConfig( blockSync ) == null )
                    {
                        throw new InvalidContentTypeConfigException(
                            "Could not find input config for block sync mapping \"" + blockSync + "\" in import \"" +
                                importConfig.getName() + "\".", configEl );
                    }
                }
            }
        }
    }
}
