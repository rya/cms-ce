/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content.imports;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.enonic.cms.core.content.imports.ImportCSVSourceException;
import com.enonic.cms.core.content.imports.ImportDataEntry;
import com.enonic.cms.core.content.imports.ImportDataReaderCsv;
import com.enonic.cms.core.content.imports.sourcevalueholders.StringSourceValue;
import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.CtyFormConfig;
import com.enonic.cms.core.content.contenttype.CtyImportConfig;
import com.enonic.cms.core.content.contenttype.CtyImportMappingConfig;
import com.enonic.cms.core.content.contenttype.CtyImportModeConfig;
import com.enonic.cms.core.content.contenttype.CtySetConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextDataEntryConfig;
import com.enonic.cms.core.content.imports.sourcevalueholders.AbstractSourceValue;

import static org.junit.Assert.*;

/**
 * Feb 6, 2010
 */
public class ImportDataReaderCsvTest
{
    private ContentTypeConfig contentTypeConfig;

    private CtyFormConfig ctyFormConfig;

    private CtySetConfig ctySetConfig;

    private CtyImportConfig importEmployeeCSV;

    private CtyImportMappingConfig employeeNoIMC;

    private CtyImportMappingConfig nameIMC;

    private CtyImportMappingConfig dateOfBirthIMC;

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

        importEmployeeCSV = new CtyImportConfig( ctyFormConfig, "import-employee-csv", null, null );
        importEmployeeCSV.setMode( CtyImportModeConfig.CSV );

        employeeNoIMC = new CtyImportMappingConfig( importEmployeeCSV, "1", "employee-no" );
        importEmployeeCSV.addMapping( employeeNoIMC );

        nameIMC = new CtyImportMappingConfig( importEmployeeCSV, "2", "name" );
        importEmployeeCSV.addMapping( nameIMC );

        dateOfBirthIMC = new CtyImportMappingConfig( importEmployeeCSV, "4", "date-of-birth" );
        importEmployeeCSV.addMapping( dateOfBirthIMC );

    }

    @Test
    public void defaultSeparatorIsSemicolon()
        throws UnsupportedEncodingException
    {
        StringBuffer importSource = new StringBuffer();
        importSource.append( "1001;Kjell Gunnersen;Sarpsborggata 9;1974-12-12" );

        ImportDataReaderCsv reader = new ImportDataReaderCsv( importEmployeeCSV, stringBufferToInputStream( importSource ) );

        ImportDataEntry importEntry;
        Map<CtyImportMappingConfig, AbstractSourceValue> configAndValueMap;

        assertTrue( reader.hasMoreEntries() );
        importEntry = reader.getNextEntry();
        configAndValueMap = importEntry.getConfigAndValueMap();
        assertEquals( "1001", ( (StringSourceValue) configAndValueMap.get( employeeNoIMC ) ).getValue() );
        assertEquals( "Kjell Gunnersen", ( (StringSourceValue) configAndValueMap.get( nameIMC ) ).getValue() );
        assertEquals( "1974-12-12", ( (StringSourceValue) configAndValueMap.get( dateOfBirthIMC ) ).getValue() );

        assertFalse( reader.hasMoreEntries() );
    }

    @Test
    public void basic()
        throws UnsupportedEncodingException
    {
        StringBuffer importSource = new StringBuffer();
        importSource.append( "1001;Kjell Gunnersen;Sarpsborggata 9;1974-12-12\n" );
        importSource.append( "1002;Kjersti Hansen;Bergensgata 5;1977-04-12;\n" );
        importSource.append( "1003;Kristian Olsen;Svenskegata 32;1981-02-03\n" );

        ImportDataReaderCsv reader = new ImportDataReaderCsv( importEmployeeCSV, stringBufferToInputStream( importSource ) );

        ImportDataEntry importEntry;
        Map<CtyImportMappingConfig, AbstractSourceValue> configAndValueMap;

        assertTrue( reader.hasMoreEntries() );
        importEntry = reader.getNextEntry();
        configAndValueMap = importEntry.getConfigAndValueMap();
        assertEquals( "1001", ( (StringSourceValue) configAndValueMap.get( employeeNoIMC ) ).getValue() );
        assertEquals( "Kjell Gunnersen", ( (StringSourceValue) configAndValueMap.get( nameIMC ) ).getValue() );
        assertEquals( "1974-12-12", ( (StringSourceValue) configAndValueMap.get( dateOfBirthIMC ) ).getValue() );

        assertTrue( reader.hasMoreEntries() );
        importEntry = reader.getNextEntry();
        configAndValueMap = importEntry.getConfigAndValueMap();
        assertEquals( "1002", ( (StringSourceValue) configAndValueMap.get( employeeNoIMC ) ).getValue() );
        assertEquals( "Kjersti Hansen", ( (StringSourceValue) configAndValueMap.get( nameIMC ) ).getValue() );
        assertEquals( "1977-04-12", ( (StringSourceValue) configAndValueMap.get( dateOfBirthIMC ) ).getValue() );

        assertTrue( reader.hasMoreEntries() );
        importEntry = reader.getNextEntry();
        configAndValueMap = importEntry.getConfigAndValueMap();
        assertEquals( "1003", ( (StringSourceValue) configAndValueMap.get( employeeNoIMC ) ).getValue() );
        assertEquals( "Kristian Olsen", ( (StringSourceValue) configAndValueMap.get( nameIMC ) ).getValue() );
        assertEquals( "1981-02-03", ( (StringSourceValue) configAndValueMap.get( dateOfBirthIMC ) ).getValue() );

        assertFalse( reader.hasMoreEntries() );
    }

    @Test
    public void nonDefaultSeparator()
        throws UnsupportedEncodingException
    {
        importEmployeeCSV.setSeparator( "$" );
        StringBuffer importSource = new StringBuffer();
        importSource.append( "1001$Kjell Gunnersen$Sarpsborggata 9$1974-12-12" );

        ImportDataReaderCsv reader = new ImportDataReaderCsv( importEmployeeCSV, stringBufferToInputStream( importSource ) );

        ImportDataEntry importEntry;
        Map<CtyImportMappingConfig, AbstractSourceValue> configAndValueMap;

        assertTrue( reader.hasMoreEntries() );
        importEntry = reader.getNextEntry();
        configAndValueMap = importEntry.getConfigAndValueMap();
        assertEquals( "1001", ( (StringSourceValue) configAndValueMap.get( employeeNoIMC ) ).getValue() );
        assertEquals( "Kjell Gunnersen", ( (StringSourceValue) configAndValueMap.get( nameIMC ) ).getValue() );
        assertEquals( "1974-12-12", ( (StringSourceValue) configAndValueMap.get( dateOfBirthIMC ) ).getValue() );

        assertFalse( reader.hasMoreEntries() );
    }

    @Test
    public void emptyValueInLastColumnIsParsedAsEmpty()
        throws UnsupportedEncodingException
    {
        StringBuffer importSource = new StringBuffer();
        importSource.append( "1001;Kjell Gunnersen;Sarpsborggata 9;" );

        ImportDataReaderCsv reader = new ImportDataReaderCsv( importEmployeeCSV, stringBufferToInputStream( importSource ) );

        ImportDataEntry importEntry;
        Map<CtyImportMappingConfig, AbstractSourceValue> configAndValueMap;

        assertTrue( reader.hasMoreEntries() );
        importEntry = reader.getNextEntry();
        configAndValueMap = importEntry.getConfigAndValueMap();
        assertEquals( "1001", ( (StringSourceValue) configAndValueMap.get( employeeNoIMC ) ).getValue() );
        assertEquals( "Kjell Gunnersen", ( (StringSourceValue) configAndValueMap.get( nameIMC ) ).getValue() );
        assertEquals( "", ( (StringSourceValue) configAndValueMap.get( dateOfBirthIMC ) ).getValue() );

        assertFalse( reader.hasMoreEntries() );
    }

    @Test
    public void emptyValueInFirstColumnIsParsedAsEmpty()
        throws UnsupportedEncodingException
    {
        StringBuffer importSource = new StringBuffer();
        importSource.append( ";Kjell Gunnersen;Sarpsborggata 9;1974-12-12" );

        ImportDataReaderCsv reader = new ImportDataReaderCsv( importEmployeeCSV, stringBufferToInputStream( importSource ) );

        ImportDataEntry importEntry;
        Map<CtyImportMappingConfig, AbstractSourceValue> configAndValueMap;

        assertTrue( reader.hasMoreEntries() );
        importEntry = reader.getNextEntry();
        configAndValueMap = importEntry.getConfigAndValueMap();
        assertEquals( "", ( (StringSourceValue) configAndValueMap.get( employeeNoIMC ) ).getValue() );
        assertEquals( "Kjell Gunnersen", ( (StringSourceValue) configAndValueMap.get( nameIMC ) ).getValue() );
        assertEquals( "1974-12-12", ( (StringSourceValue) configAndValueMap.get( dateOfBirthIMC ) ).getValue() );

        assertFalse( reader.hasMoreEntries() );
    }

    @Test
    public void emptyValueInMiddleColumnIsParsedAsEmpty()
        throws UnsupportedEncodingException
    {
        StringBuffer importSource = new StringBuffer();
        importSource.append( "1001;;Sarpsborggata 9;1974-12-12" );

        ImportDataReaderCsv reader = new ImportDataReaderCsv( importEmployeeCSV, stringBufferToInputStream( importSource ) );

        ImportDataEntry importEntry;
        Map<CtyImportMappingConfig, AbstractSourceValue> configAndValueMap;

        assertTrue( reader.hasMoreEntries() );
        importEntry = reader.getNextEntry();
        configAndValueMap = importEntry.getConfigAndValueMap();
        assertEquals( "1001", ( (StringSourceValue) configAndValueMap.get( employeeNoIMC ) ).getValue() );
        assertEquals( "", ( (StringSourceValue) configAndValueMap.get( nameIMC ) ).getValue() );
        assertEquals( "1974-12-12", ( (StringSourceValue) configAndValueMap.get( dateOfBirthIMC ) ).getValue() );

        assertFalse( reader.hasMoreEntries() );
    }

    @Test
    public void missingLastColumn()
        throws UnsupportedEncodingException
    {
        StringBuffer importSource = new StringBuffer();
        importSource.append( "1001;Kjell Gunnersen;Sarpsborggata 9" );

        try
        {
            ImportDataReaderCsv reader = new ImportDataReaderCsv( importEmployeeCSV, stringBufferToInputStream( importSource ) );
            fail( "Expected " + ImportCSVSourceException.class.getName() );
        }
        catch ( ImportCSVSourceException e )
        {
            assertEquals(
                "Error at line 1: No column at position 4 (destination = 'date-of-birth'). Line was: 1001;Kjell Gunnersen;Sarpsborggata 9",
                e.getMessage() );
        }
        catch ( Exception e )
        {
            fail( "Expected ImportCSVSourceException, got: " + e.getClass().getName() );
        }
    }

    @Test
    public void skipOneLine()
        throws UnsupportedEncodingException
    {
        importEmployeeCSV.setSkip( 1 );

        StringBuffer importSource = new StringBuffer();
        importSource.append( "#this is a comment\n" );
        importSource.append( "1002;Kjersti Hansen;Bergensgata 5;1977-04-12;\n" );
        importSource.append( "1003;Kristian Olsen;Svenskegata 32;1981-02-03\n" );

        ImportDataReaderCsv reader = new ImportDataReaderCsv( importEmployeeCSV, stringBufferToInputStream( importSource ) );

        assertTrue( reader.hasMoreEntries() );
        assertNotNull( reader.getNextEntry() );

        assertTrue( reader.hasMoreEntries() );
        assertNotNull( reader.getNextEntry() );

        assertFalse( reader.hasMoreEntries() );
    }

    @Test
    public void skipFourLines()
        throws UnsupportedEncodingException
    {
        importEmployeeCSV.setSkip( 4 );

        StringBuffer importSource = new StringBuffer();
        importSource.append( "line 1\n" );
        importSource.append( "line 2\n" );
        importSource.append( "line 3\n" );
        importSource.append( "line 4\n" );
        importSource.append( "1002;Kjersti Hansen;Bergensgata 5;1977-04-12;\n" );
        importSource.append( "1003;Kristian Olsen;Svenskegata 32;1981-02-03\n" );

        ImportDataReaderCsv reader = new ImportDataReaderCsv( importEmployeeCSV, stringBufferToInputStream( importSource ) );

        assertTrue( reader.hasMoreEntries() );
        assertNotNull( reader.getNextEntry() );

        assertTrue( reader.hasMoreEntries() );
        assertNotNull( reader.getNextEntry() );

        assertFalse( reader.hasMoreEntries() );
    }

    @Test
    public void read_import_source_with_only_a_single_column()
        throws UnsupportedEncodingException
    {
        ContentTypeConfig contentTypeConfig = new ContentTypeConfig( ContentHandlerName.CUSTOM, "SingleInputField" );
        CtyFormConfig ctyFormConfig = new CtyFormConfig( contentTypeConfig );
        CtySetConfig ctySetConfig = new CtySetConfig( ctyFormConfig, "A Content having only a single input field", null );
        ctySetConfig.addInput(
            new TextDataEntryConfig( "single-input-field", true, "Single input field", "contentdata/single-input-field" ) );
        ctyFormConfig.addBlock( ctySetConfig );
        ctyFormConfig.setTitleInputName( "single-input-field" );

        CtyImportConfig importSingleInputField = new CtyImportConfig( ctyFormConfig, "import-single-input-field", null, null );
        importSingleInputField.setMode( CtyImportModeConfig.CSV );

        CtyImportMappingConfig singleInputFieldIMC = new CtyImportMappingConfig( importSingleInputField, "1", "single-input-field" );
        importSingleInputField.addMapping( singleInputFieldIMC );

        StringBuffer importSource = new StringBuffer();
        importSource.append( "value 1\n" );
        importSource.append( "value 2\n" );
        importSource.append( "value 3\n" );

        ImportDataReaderCsv reader = new ImportDataReaderCsv( importSingleInputField, stringBufferToInputStream( importSource ) );

        ImportDataEntry importEntry;
        Map<CtyImportMappingConfig, AbstractSourceValue> configAndValueMap;

        assertTrue( reader.hasMoreEntries() );
        importEntry = reader.getNextEntry();
        configAndValueMap = importEntry.getConfigAndValueMap();
        assertEquals( "value 1", ( (StringSourceValue) configAndValueMap.get( singleInputFieldIMC ) ).getValue() );

        assertTrue( reader.hasMoreEntries() );
        importEntry = reader.getNextEntry();
        configAndValueMap = importEntry.getConfigAndValueMap();
        assertEquals( "value 2", ( (StringSourceValue) configAndValueMap.get( singleInputFieldIMC ) ).getValue() );

        assertTrue( reader.hasMoreEntries() );
        importEntry = reader.getNextEntry();
        configAndValueMap = importEntry.getConfigAndValueMap();
        assertEquals( "value 3", ( (StringSourceValue) configAndValueMap.get( singleInputFieldIMC ) ).getValue() );

        assertFalse( reader.hasMoreEntries() );
    }

    private InputStream stringBufferToInputStream( StringBuffer buffer )
        throws UnsupportedEncodingException
    {
        return new ByteArrayInputStream( buffer.toString().getBytes( "UTF-8" ) );
    }
}
