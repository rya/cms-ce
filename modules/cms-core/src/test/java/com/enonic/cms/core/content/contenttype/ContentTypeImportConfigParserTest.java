/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype;

import java.util.List;

import com.enonic.cms.core.content.ContentHandlerName;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.contenttype.dataentryconfig.TextDataEntryConfig;

import static org.junit.Assert.*;

/**
 * Feb 7, 2010
 */
public class ContentTypeImportConfigParserTest
{
    private ContentTypeConfig contentTypeConfig;

    private CtyFormConfig ctyFormConfig;

    private CtySetConfig ctySetConfig;

    @Before
    public void before()
    {
        contentTypeConfig = new ContentTypeConfig( ContentHandlerName.CUSTOM, "Employee" );
        ctyFormConfig = new CtyFormConfig( contentTypeConfig );
        ctySetConfig = new CtySetConfig( ctyFormConfig, "Employee", null );
        ctySetConfig.addInput( new TextDataEntryConfig( "employee-no", false, "Employee number", "contentdata/employee-no" ) );
        ctySetConfig.addInput( new TextDataEntryConfig( "name", false, "Name", "contentdata/name" ) );
        ctySetConfig.addInput( new TextDataEntryConfig( "date-of-birth", false, "Date of Birth", "contentdata/date-of-birth" ) );
        ctyFormConfig.addBlock( ctySetConfig );
    }

    @Test
    public void default_update_strategy_setting_is_keep_same()
    {
        StringBuffer xml = new StringBuffer();
        xml.append( "<config>" );
        xml.append( "<imports>" );
        xml.append( "<import" );
        xml.append( " name='import-test' mode='csv'" );
        xml.append( ">" );
        xml.append( "<mapping src='@id' dest='employee-no'/>" );
        xml.append( "<mapping src='name' dest='name'/>" );
        xml.append( "<mapping src='birth' dest='date-of-birth'/>" );
        xml.append( "</import>" );
        xml.append( "</imports>" );
        xml.append( "</config>" );

        List<CtyImportConfig> ctyImportConfigs =
            ContentTypeImportConfigParser.parseAllImports( ctyFormConfig, xmlStringToRootElement( xml.toString() ) );
        CtyImportConfig importConfig = ctyImportConfigs.get( 0 );
        assertEquals( CtyImportUpdateStrategyConfig.UPDATE_CONTENT_KEEP_STATUS, importConfig.getUpdateStrategy() );
    }

    @Test
    public void exception_is_thrown_when_update_strategy_setting_is_empty()
    {
        StringBuffer xml = new StringBuffer();
        xml.append( "<config>" );
        xml.append( "<imports>" );
        xml.append( "<import" );
        xml.append( " name='import-test' mode='csv' update-strategy=''" );
        xml.append( ">" );
        xml.append( "<mapping src='@id' dest='employee-no'/>" );
        xml.append( "<mapping src='name' dest='name'/>" );
        xml.append( "<mapping src='birth' dest='date-of-birth'/>" );
        xml.append( "</import>" );
        xml.append( "</imports>" );
        xml.append( "</config>" );

        try
        {
            ContentTypeImportConfigParser.parseAllImports( ctyFormConfig, xmlStringToRootElement( xml.toString() ) );
            fail( "Expected exception" );
        }
        catch ( InvalidImportConfigException e )
        {
            assertEquals( "Import config 'import-test' is invalid: Missing value for 'update-strategy' setting", e.getMessage() );
        }
        catch ( Exception e )
        {
            fail( "Expected InvalidImportConfig" );
        }
    }


    @Test
    public void update_strategy_setting_keep_same()
    {
        StringBuffer xml = new StringBuffer();
        xml.append( "<config>" );
        xml.append( "<imports>" );
        xml.append( "<import" );
        xml.append( " name='import-test' mode='csv' update-strategy='UPDATE-CONTENT-KEEP-STATUS'" );
        xml.append( ">" );
        xml.append( "<mapping src='@id' dest='employee-no'/>" );
        xml.append( "<mapping src='name' dest='name'/>" );
        xml.append( "<mapping src='birth' dest='date-of-birth'/>" );
        xml.append( "</import>" );
        xml.append( "</imports>" );
        xml.append( "</config>" );

        List<CtyImportConfig> ctyImportConfigs =
            ContentTypeImportConfigParser.parseAllImports( ctyFormConfig, xmlStringToRootElement( xml.toString() ) );
        CtyImportConfig importConfig = ctyImportConfigs.get( 0 );
        assertEquals( CtyImportUpdateStrategyConfig.UPDATE_CONTENT_KEEP_STATUS, importConfig.getUpdateStrategy() );
    }

    @Test
    public void update_strategy_setting_draft()
    {
        StringBuffer xml = new StringBuffer();
        xml.append( "<config>" );
        xml.append( "<imports>" );
        xml.append( "<import" );
        xml.append( " name='import-test' mode='csv' update-strategy='UPDATE-CONTENT-DRAFT'" );
        xml.append( ">" );
        xml.append( "<mapping src='@id' dest='employee-no'/>" );
        xml.append( "<mapping src='name' dest='name'/>" );
        xml.append( "<mapping src='birth' dest='date-of-birth'/>" );
        xml.append( "</import>" );
        xml.append( "</imports>" );
        xml.append( "</config>" );

        List<CtyImportConfig> ctyImportConfigs =
            ContentTypeImportConfigParser.parseAllImports( ctyFormConfig, xmlStringToRootElement( xml.toString() ) );
        CtyImportConfig importConfig = ctyImportConfigs.get( 0 );
        assertEquals( CtyImportUpdateStrategyConfig.UPDATE_CONTENT_DRAFT, importConfig.getUpdateStrategy() );
    }

    @Test
    public void update_strategy_setting_archive()
    {
        StringBuffer xml = new StringBuffer();
        xml.append( "<config>" );
        xml.append( "<imports>" );
        xml.append( "<import" );
        xml.append( " name='import-test' mode='csv' update-strategy='UPDATE-AND-ARCHIVE-CONTENT'" );
        xml.append( ">" );
        xml.append( "<mapping src='@id' dest='employee-no'/>" );
        xml.append( "<mapping src='name' dest='name'/>" );
        xml.append( "<mapping src='birth' dest='date-of-birth'/>" );
        xml.append( "</import>" );
        xml.append( "</imports>" );
        xml.append( "</config>" );

        List<CtyImportConfig> ctyImportConfigs =
            ContentTypeImportConfigParser.parseAllImports( ctyFormConfig, xmlStringToRootElement( xml.toString() ) );
        CtyImportConfig importConfig = ctyImportConfigs.get( 0 );
        assertEquals( CtyImportUpdateStrategyConfig.UPDATE_AND_ARCHIVE_CONTENT, importConfig.getUpdateStrategy() );
    }

    @Test
    public void update_strategy_setting_approve()
    {
        StringBuffer xml = new StringBuffer();
        xml.append( "<config>" );
        xml.append( "<imports>" );
        xml.append( "<import" );
        xml.append( " name='import-test' mode='csv' update-strategy='UPDATE-AND-APPROVE-CONTENT'" );
        xml.append( ">" );
        xml.append( "<mapping src='@id' dest='employee-no'/>" );
        xml.append( "<mapping src='name' dest='name'/>" );
        xml.append( "<mapping src='birth' dest='date-of-birth'/>" );
        xml.append( "</import>" );
        xml.append( "</imports>" );
        xml.append( "</config>" );

        List<CtyImportConfig> ctyImportConfigs =
            ContentTypeImportConfigParser.parseAllImports( ctyFormConfig, xmlStringToRootElement( xml.toString() ) );
        CtyImportConfig importConfig = ctyImportConfigs.get( 0 );
        assertEquals( CtyImportUpdateStrategyConfig.UPDATE_AND_APPROVE_CONTENT, importConfig.getUpdateStrategy() );
    }

    @Test
    public void basicCSVImportConfiguration()
    {
        StringBuffer xml = new StringBuffer();
        xml.append( "<config>" );
        xml.append( "<imports>" );
        xml.append( "<import" );
        xml.append( " name='import-test' mode='csv'" );
        xml.append( ">" );
        xml.append( "<mapping src='@id' dest='employee-no'/>" );
        xml.append( "<mapping src='name' dest='name'/>" );
        xml.append( "<mapping src='birth' dest='date-of-birth'/>" );
        xml.append( "</import>" );
        xml.append( "</imports>" );
        xml.append( "</config>" );

        List<CtyImportConfig> ctyImportConfigs =
            ContentTypeImportConfigParser.parseAllImports( ctyFormConfig, xmlStringToRootElement( xml.toString() ) );
        assertEquals( 1, ctyImportConfigs.size() );
    }

    @Test
    public void exception_is_thrown_when_mode_setting_is_missing()
    {
        StringBuffer xml = new StringBuffer();
        xml.append( "<config>" );
        xml.append( "<imports>" );
        xml.append( "<import" );
        xml.append( " name='import-test'" );
        xml.append( ">" );
        xml.append( "<mapping src='@id' dest='employee-no'/>" );
        xml.append( "<mapping src='name' dest='name'/>" );
        xml.append( "<mapping src='birth' dest='date-of-birth'/>" );
        xml.append( "</import>" );
        xml.append( "</imports>" );
        xml.append( "</config>" );

        try
        {
            ContentTypeImportConfigParser.parseAllImports( ctyFormConfig, xmlStringToRootElement( xml.toString() ) );
            fail( "Expected exception" );
        }
        catch ( InvalidImportConfigException e )
        {
            assertEquals( "Import config 'import-test' is invalid: Invalid mode setting: null", e.getMessage() );
        }
        catch ( Exception e )
        {
            fail( "Expected InvalidImportConfig" );
        }
    }

    @Test
    public void exception_is_thrown_when_mappings_are_missing()
    {
        StringBuffer xml = new StringBuffer();
        xml.append( "<config>" );
        xml.append( "<imports>" );
        xml.append( "<import" );
        xml.append( " name='import-test' mode='csv'" );
        xml.append( ">" );
        xml.append( "</import>" );
        xml.append( "</imports>" );
        xml.append( "</config>" );

        try
        {
            ContentTypeImportConfigParser.parseAllImports( ctyFormConfig, xmlStringToRootElement( xml.toString() ) );
            fail( "Expected exception" );
        }
        catch ( InvalidImportConfigException e )
        {
            assertEquals( "Import config 'import-test' is invalid: No mapping elements found.", e.getMessage() );
        }
        catch ( Exception e )
        {
            fail( "Expected InvalidImportConfig" );
        }
    }

    @Test
    public void exception_is_thrown_when_mapping_destination_setting_is_missing()
    {
        StringBuffer xml = new StringBuffer();
        xml.append( "<config>" );
        xml.append( "<imports>" );
        xml.append( "<import" );
        xml.append( " name='import-test' mode='csv'" );
        xml.append( ">" );
        xml.append( "<mapping src='@id' dest='employee-no'/>" );
        xml.append( "<mapping src='name'/>" );
        xml.append( "<mapping src='birth' dest='date-of-birth'/>" );
        xml.append( "</import>" );
        xml.append( "</imports>" );
        xml.append( "</config>" );

        try
        {
            ContentTypeImportConfigParser.parseAllImports( ctyFormConfig, xmlStringToRootElement( xml.toString() ) );
            fail( "Expected InvalidImportConfig" );
        }
        catch ( InvalidImportConfigException e )
        {
            assertEquals( "Import config 'import-test' is invalid: Missing destination attribute (\"dest\") in mapping number 2.",
                          e.getMessage() );
        }
        catch ( Exception e )
        {
            fail( "Expected InvalidImportConfig" );
        }
    }

    @Test
    public void exception_is_thrown_when_mapping_source_setting_is_missing()
    {
        StringBuffer xml = new StringBuffer();
        xml.append( "<config>" );
        xml.append( "<imports>" );
        xml.append( "<import" );
        xml.append( " name='import-test' mode='csv'" );
        xml.append( ">" );
        xml.append( "<mapping src='@id' dest='employee-no'/>" );
        xml.append( "<mapping dest='name'/>" );
        xml.append( "<mapping src='birth' dest='date-of-birth'/>" );
        xml.append( "</import>" );
        xml.append( "</imports>" );
        xml.append( "</config>" );

        try
        {
            ContentTypeImportConfigParser.parseAllImports( ctyFormConfig, xmlStringToRootElement( xml.toString() ) );
            fail( "Expected exception" );
        }
        catch ( InvalidImportConfigException e )
        {
            assertEquals( "Import config 'import-test' is invalid: Missing source attribute (\"src\") in mapping number 2.",
                          e.getMessage() );
        }
        catch ( Exception e )
        {
            fail( "Expected InvalidImportConfig" );
        }
    }

    @Test
    public void exeption_is_thrown_when_referred_mapping_destination_not_exist()
    {
        StringBuffer xml = new StringBuffer();
        xml.append( "<config>" );
        xml.append( "<imports>" );
        xml.append( "<import" );
        xml.append( " name='import-test' mode='csv'" );
        xml.append( ">" );
        xml.append( "<mapping src='@id' dest='employee-no'/>" );
        xml.append( "<mapping src='name' dest='name'/>" );
        xml.append( "<mapping src='birth' dest='dummy'/>" );
        xml.append( "</import>" );
        xml.append( "</imports>" );
        xml.append( "</config>" );

        try
        {
            ContentTypeImportConfigParser.parseAllImports( ctyFormConfig, xmlStringToRootElement( xml.toString() ) );
            fail( "Expected exception" );
        }
        catch ( InvalidImportConfigException e )
        {
            assertEquals( "Import config 'import-test' is invalid: Mapping destination (content input field) does not exist: dummy",
                          e.getMessage() );
        }
        catch ( Exception e )
        {
            fail( "Expected InvalidImportConfig" );
        }
    }

    @Test
    public void exeption_is_thrown_when_mapping_setting_exsrc_is_used_for_non_applicable_content_input_field_type()
    {
        StringBuffer xml = new StringBuffer();
        xml.append( "<config>" );
        xml.append( "<imports>" );
        xml.append( "<import" );
        xml.append( " name='import-test' mode='csv'" );
        xml.append( ">" );
        xml.append( "<mapping src='@id' dest='employee-no'/>" );
        xml.append( "<mapping src='name' dest='name' exsrc='extra'/>" );
        xml.append( "<mapping src='birth' dest='date-of-birth'/>" );
        xml.append( "</import>" );
        xml.append( "</imports>" );
        xml.append( "</config>" );

        try
        {
            ContentTypeImportConfigParser.parseAllImports( ctyFormConfig, xmlStringToRootElement( xml.toString() ) );
            fail( "Expected exception" );
        }
        catch ( InvalidImportConfigException e )
        {
            assertEquals(
                "Import config 'import-test' is invalid: Mapping setting exsrc is only applicaple for mappings whose destinations refers to a content input field of type 'image' or 'uploadfile'. Type was: text",
                e.getMessage() );
        }
        catch ( Exception e )
        {
            fail( "Expected InvalidImportConfig" );
        }
    }

    private Element xmlStringToRootElement( String xml )
    {
        XMLDocument xmlDocument = XMLDocumentFactory.create( xml.toString() );
        return xmlDocument.getAsJDOMDocument().getRootElement();
    }
}
