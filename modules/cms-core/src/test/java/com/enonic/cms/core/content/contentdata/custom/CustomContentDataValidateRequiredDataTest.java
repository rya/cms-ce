/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.enonic.cms.core.content.ContentHandlerName;
import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.core.content.contentdata.MissingRequiredContentDataException;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.FileDataEntry;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.RelatedContentDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.UrlDataEntry;
import com.enonic.cms.core.content.contentdata.custom.xmlbased.XmlDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.CtyFormConfig;
import com.enonic.cms.core.content.contenttype.CtySetConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.CheckboxDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.FileDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.RelatedContentDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.UrlDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.XmlDataEntryConfig;

import static org.junit.Assert.*;


public class CustomContentDataValidateRequiredDataTest
{
    @Before
    public void before()
        throws IOException, JDOMException
    {
        contentTypeConfig = new ContentTypeConfig( ContentHandlerName.CUSTOM, "MyContentType" );
        formConfig = new CtyFormConfig( contentTypeConfig );
        contentTypeConfig.setForm( formConfig );
        generalBlockConfig = new CtySetConfig( formConfig, "General", null );
        formConfig.addBlock( generalBlockConfig );
    }

    private ContentTypeConfig contentTypeConfig;

    private CtyFormConfig formConfig;

    private CtySetConfig generalBlockConfig;


    @Test
    public void requiredCheckboxIsValidatedAsMissing()
        throws IOException, JDOMException
    {
        generalBlockConfig.addInput( new TextDataEntryConfig( "myTitle", true, "My title", "contentdata/mytitle" ) );
        generalBlockConfig.addInput( new CheckboxDataEntryConfig( "myRequired", true, "My required", "contentdata/myrequired" ) );

        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "myTitle" ), "title" ) );

        try
        {
            contentData.validate();
            fail( "Expected MissingRequiredContentDataException" );
        }
        catch ( MissingRequiredContentDataException e )
        {
            assertEquals( "myRequired", e.getInputName() );
        }
    }

    @Test
    public void requiredCheckboxIsValidatedAsMissingWhenWithNullValue()
        throws IOException, JDOMException
    {
        generalBlockConfig.addInput( new TextDataEntryConfig( "myTitle", true, "My title", "contentdata/mytitle" ) );
        generalBlockConfig.addInput( new CheckboxDataEntryConfig( "myRequired", true, "My required", "contentdata/myrequired" ) );

        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "myTitle" ), "title" ) );
        contentData.add( new BooleanDataEntry( contentData.getInputConfig( "myRequired" ), null ) );

        try
        {
            contentData.validate();
            fail( "Expected MissingRequiredContentDataException" );
        }
        catch ( MissingRequiredContentDataException e )
        {
            assertEquals( "myRequired", e.getInputName() );
        }
    }

    @Test
    public void requiredTextIsValidatedAsMissing()
        throws IOException, JDOMException
    {
        generalBlockConfig.addInput( new TextDataEntryConfig( "myTitle", true, "My title", "contentdata/mytitle" ) );
        generalBlockConfig.addInput( new TextDataEntryConfig( "myRequired", true, "My required", "contentdata/myrequired" ) );

        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "myTitle" ), "title" ) );

        try
        {
            contentData.validate();
            fail( "Expected MissingRequiredContentDataException" );
        }
        catch ( MissingRequiredContentDataException e )
        {
            assertEquals( "myRequired", e.getInputName() );
        }
    }

    @Test
    public void requiredTextIsValidatedAsMissingWhenWithNullValue()
        throws IOException, JDOMException
    {
        generalBlockConfig.addInput( new TextDataEntryConfig( "myTitle", true, "My title", "contentdata/mytitle" ) );
        generalBlockConfig.addInput( new TextDataEntryConfig( "myRequired", true, "My required", "contentdata/myrequired" ) );

        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "myTitle" ), "title" ) );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "myRequired" ), null ) );

        try
        {
            contentData.validate();
            fail( "Expected MissingRequiredContentDataException" );
        }
        catch ( MissingRequiredContentDataException e )
        {
            assertEquals( "myRequired", e.getInputName() );
        }
    }

    @Test
    public void requiredXmlIsValidatedAsMissing()
        throws IOException, JDOMException
    {
        generalBlockConfig.addInput( new TextDataEntryConfig( "myTitle", true, "My title", "contentdata/mytitle" ) );
        generalBlockConfig.addInput( new XmlDataEntryConfig( "myRequired", true, "My required", "contentdata/myrequired" ) );

        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "myTitle" ), "title" ) );

        try
        {
            contentData.validate();
            fail( "Expected MissingRequiredContentDataException" );
        }
        catch ( MissingRequiredContentDataException e )
        {
            assertEquals( "myRequired", e.getInputName() );
        }
    }

    @Test
    public void requiredXmlIsValidatedAsMissingWhenWithNullValue()
        throws IOException, JDOMException
    {
        generalBlockConfig.addInput( new TextDataEntryConfig( "myTitle", true, "My title", "contentdata/mytitle" ) );
        generalBlockConfig.addInput( new XmlDataEntryConfig( "myRequired", true, "My required", "contentdata/myrequired" ) );

        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "myTitle" ), "title" ) );
        contentData.add( new XmlDataEntry( contentData.getInputConfig( "myRequired" ), null ) );

        try
        {
            contentData.validate();
            fail( "Expected MissingRequiredContentDataException" );
        }
        catch ( MissingRequiredContentDataException e )
        {
            assertEquals( "myRequired", e.getInputName() );
        }
    }

    @Test
    public void requiredUrlIsValidatedAsMissing()
        throws IOException, JDOMException
    {
        generalBlockConfig.addInput( new TextDataEntryConfig( "myTitle", true, "My title", "contentdata/mytitle" ) );
        generalBlockConfig.addInput( new UrlDataEntryConfig( "myRequired", true, "My required", "contentdata/myrequired", 1000 ) );

        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "myTitle" ), "title" ) );

        try
        {
            contentData.validate();
            fail( "Expected MissingRequiredContentDataException" );
        }
        catch ( MissingRequiredContentDataException e )
        {
            assertEquals( "myRequired", e.getInputName() );
        }
    }

    @Test
    public void requiredUrlIsValidatedAsMissingWhenWithNullValue()
        throws IOException, JDOMException
    {
        generalBlockConfig.addInput( new TextDataEntryConfig( "myTitle", true, "My title", "contentdata/mytitle" ) );
        generalBlockConfig.addInput( new UrlDataEntryConfig( "myRequired", true, "My required", "contentdata/myrequired", 1000 ) );

        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "myTitle" ), "title" ) );
        contentData.add( new UrlDataEntry( contentData.getInputConfig( "myRequired" ), null ) );

        try
        {
            contentData.validate();
            fail( "Expected MissingRequiredContentDataException" );
        }
        catch ( MissingRequiredContentDataException e )
        {
            assertEquals( "myRequired", e.getInputName() );
        }
    }

    @Test
    public void requiredFileIsValidatedAsMissing()
        throws IOException, JDOMException
    {
        generalBlockConfig.addInput( new TextDataEntryConfig( "myTitle", true, "My title", "contentdata/mytitle" ) );
        generalBlockConfig.addInput( new FileDataEntryConfig( "myRequired", true, "My required", "contentdata/myrequired" ) );

        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "myTitle" ), "title" ) );

        try
        {
            contentData.validate();
            fail( "Expected MissingRequiredContentDataException" );
        }
        catch ( MissingRequiredContentDataException e )
        {
            assertEquals( "myRequired", e.getInputName() );
        }
    }

    @Test
    public void requiredFileIsValidatedAsMissingWhenWithNullValue()
        throws IOException, JDOMException
    {
        generalBlockConfig.addInput( new TextDataEntryConfig( "myTitle", true, "My title", "contentdata/mytitle" ) );
        generalBlockConfig.addInput( new FileDataEntryConfig( "myRequired", true, "My required", "contentdata/myrequired" ) );

        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "myTitle" ), "title" ) );
        contentData.add( new FileDataEntry( contentData.getInputConfig( "myRequired" ), null ) );

        try
        {
            contentData.validate();
            fail( "Expected MissingRequiredContentDataException" );
        }
        catch ( MissingRequiredContentDataException e )
        {
            assertEquals( "myRequired", e.getInputName() );
        }
    }

    @Test
    public void requiredRelatedContentIsValidatedAsMissing()
        throws IOException, JDOMException
    {
        List<String> relatedContentTypes = Arrays.asList( new String[]{"MyContenType"} );

        generalBlockConfig.addInput( new TextDataEntryConfig( "myTitle", true, "My title", "contentdata/mytitle" ) );
        generalBlockConfig.addInput(
            new RelatedContentDataEntryConfig( "myRequired", true, "My required", "contentdata/myrequired", false, relatedContentTypes ) );

        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "myTitle" ), "title" ) );

        try
        {
            contentData.validate();
            fail( "Expected MissingRequiredContentDataException" );
        }
        catch ( MissingRequiredContentDataException e )
        {
            assertEquals( "myRequired", e.getInputName() );
        }
    }

    @Test
    public void requiredRelatedContentIsValidatedAsMissingWhenWithNullValue()
        throws IOException, JDOMException
    {
        List<String> relatedContentTypes = Arrays.asList( new String[]{"MyContenType"} );

        generalBlockConfig.addInput( new TextDataEntryConfig( "myTitle", true, "My title", "contentdata/mytitle" ) );
        generalBlockConfig.addInput(
            new RelatedContentDataEntryConfig( "myRequired", true, "My required", "contentdata/myrequired", false, relatedContentTypes ) );

        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "myTitle" ), "title" ) );
        contentData.add( new RelatedContentDataEntry( contentData.getInputConfig( "myRequired" ), null ) );

        try
        {
            contentData.validate();
            fail( "Expected MissingRequiredContentDataException" );
        }
        catch ( MissingRequiredContentDataException e )
        {
            assertEquals( "myRequired", e.getInputName() );
        }
    }

    @Test
    public void requiredCheckboxInAGroupIsValidatedAsMissing()
        throws IOException, JDOMException
    {
        generalBlockConfig.addInput( new TextDataEntryConfig( "myTitle", true, "My title", "contentdata/mytitle" ) );

        CtySetConfig myGroupBlockConfig = new CtySetConfig( formConfig, "MyGroup", "contentdata/mygroup" );
        formConfig.addBlock( myGroupBlockConfig );
        myGroupBlockConfig.addInput( new CheckboxDataEntryConfig( "myUnRequiredCheckbox", false, "My unrequired", "myunrequired" ) );
        myGroupBlockConfig.addInput( new CheckboxDataEntryConfig( "myRequiredCheckbox", true, "My required", "myrequired" ) );

        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "myTitle" ), "title" ) );
        GroupDataEntry myGroupDataEntry = new GroupDataEntry( "MyGroup", "contentdata/mygroup", 1 );
        myGroupDataEntry.setConfig( myGroupBlockConfig );
        contentData.add( myGroupDataEntry );

        try
        {
            contentData.validate();
            fail( "Expected MissingRequiredContentDataException" );
        }
        catch ( MissingRequiredContentDataException e )
        {
            assertEquals( "Missing data for required input (missing data entry): myRequiredCheckbox in group MyGroup[1]", e.getMessage() );
        }
    }

    @Test
    public void requiredCheckboxInAGroupIsValidatedAsMissingWhenWithNullValue()
        throws IOException, JDOMException
    {
        generalBlockConfig.addInput( new TextDataEntryConfig( "myTitle", true, "My title", "contentdata/mytitle" ) );

        CtySetConfig myGroupBlockConfig = new CtySetConfig( formConfig, "MyGroup", "contentdata/mygroup" );
        formConfig.addBlock( myGroupBlockConfig );
        myGroupBlockConfig.addInput( new CheckboxDataEntryConfig( "myUnRequired", false, "My unrequired", "myunrequired" ) );
        myGroupBlockConfig.addInput( new CheckboxDataEntryConfig( "myRequiredCheckbox", true, "My required", "myrequired" ) );

        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "myTitle" ), "title" ) );
        GroupDataEntry myGroupDataEntry = new GroupDataEntry( "MyGroup", "contentdata/mygroup", 1 );
        myGroupDataEntry.setConfig( myGroupBlockConfig );
        contentData.add( myGroupDataEntry );
        myGroupDataEntry.add( new BooleanDataEntry( myGroupBlockConfig.getInputConfig( "myRequiredCheckbox" ), null ) );

        try
        {
            contentData.validate();
            fail( "Expected MissingRequiredContentDataException" );
        }
        catch ( MissingRequiredContentDataException e )
        {
            assertEquals( "Missing data for required input (missing value in data entry): myRequiredCheckbox in group MyGroup[1]",
                          e.getMessage() );
        }
    }

    @Test
    public void requiredTextInAGroupIsValidatedAsMissingWhenWithEmptyStringValue()
        throws IOException, JDOMException
    {
        generalBlockConfig.addInput( new TextDataEntryConfig( "myTitle", true, "My title", "contentdata/mytitle" ) );

        CtySetConfig myGroupBlockConfig = new CtySetConfig( formConfig, "MyGroup", "contentdata/mygroup" );
        formConfig.addBlock( myGroupBlockConfig );
        myGroupBlockConfig.addInput( new TextDataEntryConfig( "myUnRequired", false, "My unrequired", "myunrequired" ) );
        myGroupBlockConfig.addInput( new TextDataEntryConfig( "myRequiredText", true, "My required", "myrequired" ) );

        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "myTitle" ), "title" ) );
        GroupDataEntry myGroupDataEntry = new GroupDataEntry( "MyGroup", "contentdata/mygroup", 1 );
        myGroupDataEntry.setConfig( myGroupBlockConfig );
        contentData.add( myGroupDataEntry );
        myGroupDataEntry.add( new TextDataEntry( myGroupBlockConfig.getInputConfig( "myRequiredText" ), "" ) );

        try
        {
            contentData.validate();
            fail( "Expected MissingRequiredContentDataException" );
        }
        catch ( MissingRequiredContentDataException e )
        {
            assertEquals( "Missing data for required input (missing value in data entry): myRequiredText in group MyGroup[1]",
                          e.getMessage() );
        }
    }

}