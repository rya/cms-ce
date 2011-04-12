/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Attribute;
import org.jdom.Element;

import com.enonic.cms.core.content.contenttype.dataentryconfig.BinaryDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.CheckboxDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfigType;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DateDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DropdownDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.FileDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.FilesDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.HtmlAreaDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.ImageDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.ImagesDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.KeywordsDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.MultipleChoiceDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.RadioButtonDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.RelatedContentDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextAreaDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.UrlDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.XmlDataEntryConfig;

/**
 * Jun 7, 2009
 */
public class InputConfigParser
{
    private int inputConfigPosition;

    private String inputName;

    public InputConfigParser( int inputConfigPosition )
    {
        this.inputConfigPosition = inputConfigPosition;
    }

    public DataEntryConfig parserInputConfigElement( Element inputEl )
    {
        inputName = parseInputName( inputEl );
        DataEntryConfigType type = parseType( inputEl );

        if ( type == DataEntryConfigType.HTMLAREA )
        {
            return parseHtmlAreaInputConfig( inputEl );
        }
        else if ( type == DataEntryConfigType.TEXT )
        {
            return parseTextInputConfig( inputEl );
        }
        else if ( type == DataEntryConfigType.XML )
        {
            return parseXmlInputConfig( inputEl );
        }
        else if ( type == DataEntryConfigType.URL )
        {
            return parseUrlInputConfig( inputEl );
        }
        else if ( type == DataEntryConfigType.RELATEDCONTENT )
        {
            return parseRelatedContentInputConfig( inputEl );
        }
        else if ( type == DataEntryConfigType.BINARY )
        {
            return parseBinaryInputConfig( inputEl );
        }
        else if ( type == DataEntryConfigType.CHECKBOX )
        {
            return parseCheckboxInputConfig( inputEl );
        }
        else if ( type == DataEntryConfigType.DATE )
        {
            return parseDateInputConfig( inputEl );
        }
        else if ( type == DataEntryConfigType.DROPDOWN )
        {
            return parseDropdownInputConfig( inputEl );
        }
        else if ( type == DataEntryConfigType.FILE )
        {
            return parseFileInputConfig( inputEl );
        }
        else if ( type == DataEntryConfigType.FILES )
        {
            return parseFilesInputConfig( inputEl );
        }
        else if ( type == DataEntryConfigType.IMAGE )
        {
            return parseImageInputConfig( inputEl );
        }
        else if ( type == DataEntryConfigType.IMAGES )
        {
            return parseImagesInputConfig( inputEl );
        }
        else if ( type == DataEntryConfigType.KEYWORDS )
        {
            return parseKeywordsInputConfig( inputEl );
        }
        else if ( type == DataEntryConfigType.MULTIPLE_CHOICE )
        {
            return parseMultipleChoiceInputConfig( inputEl );
        }
        else if ( type == DataEntryConfigType.RADIOBUTTON )
        {
            return parseRadioButtonInputConfig( inputEl );
        }
        else if ( type == DataEntryConfigType.TEXT_AREA )
        {
            return parseTextAreaInputConfig( inputEl );
        }
        else
        {
            throw new InvalidContentTypeConfigException(
                "Type not supported in input config '" + inputName + "' at position: " + inputConfigPosition );
        }
    }

    private TextAreaDataEntryConfig parseTextAreaInputConfig( Element inputEl )
    {
        boolean required = parseInputReqiured( inputEl );
        String displayName = parseInputDisplayName( inputEl );
        String xpath = parseInputXpath( inputEl );

        return new TextAreaDataEntryConfig( inputName, required, displayName, xpath );
    }

    private RadioButtonDataEntryConfig parseRadioButtonInputConfig( Element inputEl )
    {
        boolean required = parseInputReqiured( inputEl );
        String displayName = parseInputDisplayName( inputEl );
        String xpath = parseInputXpath( inputEl );

        return new RadioButtonDataEntryConfig( inputName, required, displayName, xpath );
    }

    private MultipleChoiceDataEntryConfig parseMultipleChoiceInputConfig( Element inputEl )
    {
        boolean required = parseInputReqiured( inputEl );
        String displayName = parseInputDisplayName( inputEl );
        String xpath = parseInputXpath( inputEl );

        return new MultipleChoiceDataEntryConfig( inputName, required, displayName, xpath );
    }

    private KeywordsDataEntryConfig parseKeywordsInputConfig( Element inputEl )
    {
        boolean required = parseInputReqiured( inputEl );
        String displayName = parseInputDisplayName( inputEl );
        String xpath = parseInputXpath( inputEl );

        return new KeywordsDataEntryConfig( inputName, required, displayName, xpath );
    }

    private BinaryDataEntryConfig parseBinaryInputConfig( Element inputEl )
    {
        boolean required = parseInputReqiured( inputEl );
        String displayName = parseInputDisplayName( inputEl );
        String xpath = parseInputXpath( inputEl );

        return new BinaryDataEntryConfig( inputName, required, displayName, xpath );
    }

    private ImagesDataEntryConfig parseImagesInputConfig( Element inputEl )
    {
        boolean required = parseInputReqiured( inputEl );
        String displayName = parseInputDisplayName( inputEl );
        String xpath = parseInputXpath( inputEl );

        return new ImagesDataEntryConfig( inputName, required, displayName, xpath );
    }

    private ImageDataEntryConfig parseImageInputConfig( Element inputEl )
    {
        boolean required = parseInputReqiured( inputEl );
        String displayName = parseInputDisplayName( inputEl );
        String xpath = parseInputXpath( inputEl );

        return new ImageDataEntryConfig( inputName, required, displayName, xpath );
    }

    private FilesDataEntryConfig parseFilesInputConfig( Element inputEl )
    {
        boolean required = parseInputReqiured( inputEl );
        String displayName = parseInputDisplayName( inputEl );
        String xpath = parseInputXpath( inputEl );

        return new FilesDataEntryConfig( inputName, required, displayName, xpath );
    }

    private FileDataEntryConfig parseFileInputConfig( Element inputEl )
    {
        boolean required = parseInputReqiured( inputEl );
        String displayName = parseInputDisplayName( inputEl );
        String xpath = parseInputXpath( inputEl );

        return new FileDataEntryConfig( inputName, required, displayName, xpath );
    }

    private DropdownDataEntryConfig parseDropdownInputConfig( Element inputEl )
    {
        boolean required = parseInputReqiured( inputEl );
        String displayName = parseInputDisplayName( inputEl );
        String xpath = parseInputXpath( inputEl );

        return new DropdownDataEntryConfig( inputName, required, displayName, xpath );
    }

    private DateDataEntryConfig parseDateInputConfig( Element inputEl )
    {
        boolean required = parseInputReqiured( inputEl );
        String displayName = parseInputDisplayName( inputEl );
        String xpath = parseInputXpath( inputEl );

        return new DateDataEntryConfig( inputName, required, displayName, xpath );
    }

    private CheckboxDataEntryConfig parseCheckboxInputConfig( Element inputEl )
    {
        boolean required = parseInputReqiured( inputEl );
        String displayName = parseInputDisplayName( inputEl );
        String xpath = parseInputXpath( inputEl );

        return new CheckboxDataEntryConfig( inputName, required, displayName, xpath );
    }

    private XmlDataEntryConfig parseXmlInputConfig( Element inputEl )
    {
        boolean required = parseInputReqiured( inputEl );
        String displayName = parseInputDisplayName( inputEl );
        String xpath = parseInputXpath( inputEl );

        return new XmlDataEntryConfig( inputName, required, displayName, xpath );
    }

    private RelatedContentDataEntryConfig parseRelatedContentInputConfig( Element inputEl )
    {
        boolean required = parseInputReqiured( inputEl );
        String displayName = parseInputDisplayName( inputEl );
        String xpath = parseInputXpath( inputEl );

        boolean multiple = true; // multiple is true by default
        String multipleStr = inputEl.getAttributeValue( "multiple" );
        if ( multipleStr != null )
        {
            multiple = Boolean.parseBoolean( multipleStr );
        }

        List<Element> contentTypeElements = inputEl.getChildren( "contenttype" );

        List<String> contentTypeNames = new ArrayList<String>();

        if ( contentTypeElements != null && !contentTypeElements.isEmpty() )
        {
            for ( Element contentTypeElement : contentTypeElements )
            {
                Attribute nameAttr = contentTypeElement.getAttribute( "name" );
                if ( nameAttr == null )
                {
                    throw new InvalidContentTypeConfigException(
                        "Missing name attribute for contenttype element in input config '" + inputName + "' in position: " +
                            inputConfigPosition );
                }

                String contentTypeName = nameAttr.getValue();

                if ( StringUtils.isBlank( contentTypeName ) )
                {
                    throw new InvalidContentTypeConfigException( "Missing content type name ('" + contentTypeName +
                        "') in name attribute for contenttype element in input config '" + inputName + "' in position: " +
                        inputConfigPosition );
                }

                contentTypeNames.add( contentTypeName );

            }

        }

        return new RelatedContentDataEntryConfig( inputName, required, displayName, xpath, multiple, contentTypeNames );
    }

    private TextDataEntryConfig parseTextInputConfig( Element inputEl )
    {
        boolean required = parseInputReqiured( inputEl );
        String displayName = parseInputDisplayName( inputEl );
        String xpath = parseInputXpath( inputEl );

        Integer maxLength = null;
        Element maxlengthEl = inputEl.getChild( "maxlength" );
        if ( maxlengthEl != null )
        {
            String strValue = maxlengthEl.getAttributeValue( "value" );
            if ( strValue != null )
            {
                maxLength = new Integer( strValue );
            }
        }

        return new TextDataEntryConfig( inputName, required, displayName, xpath ).setMaxLength( maxLength );
    }

    private UrlDataEntryConfig parseUrlInputConfig( Element inputEl )
    {
        boolean required = parseInputReqiured( inputEl );
        String displayName = parseInputDisplayName( inputEl );
        String xpath = parseInputXpath( inputEl );

        Integer maxLength = null;
        Element maxlengthEl = inputEl.getChild( "maxlength" );
        if ( maxlengthEl != null )
        {
            String strValue = maxlengthEl.getAttributeValue( "value" );
            if ( strValue != null )
            {
                maxLength = new Integer( strValue );
            }
        }

        return new UrlDataEntryConfig( inputName, required, displayName, xpath, maxLength );
    }

    private HtmlAreaDataEntryConfig parseHtmlAreaInputConfig( Element inputEl )
    {
        boolean required = parseInputReqiured( inputEl );
        String displayName = parseInputDisplayName( inputEl );
        String xpath = parseInputXpath( inputEl );

        return new HtmlAreaDataEntryConfig( inputName, required, displayName, xpath );
    }

    private String parseInputName( Element inputEl )
    {
        String nameAttribute = inputEl.getAttributeValue( "name" );
        if ( nameAttribute == null || nameAttribute.trim().equals( "" ) )
        {
            throw new InvalidContentTypeConfigException( "Missing name attribute in input config position: " + inputConfigPosition );
        }
        return nameAttribute;
    }

    private DataEntryConfigType parseType( Element inputEl )
    {
        Attribute typeAttr = inputEl.getAttribute( "type" );
        if ( typeAttr == null )
        {
            throw new InvalidContentTypeConfigException( "Missing type attribute in input config position: " + inputConfigPosition );
        }
        String typeName = typeAttr.getValue();
        if ( typeName == null )
        {
            throw new InvalidContentTypeConfigException(
                "Missing value in type attribute in input config position: " + inputConfigPosition );
        }

        DataEntryConfigType type = DataEntryConfigType.parse( typeName );
        if ( type == null )
        {
            throw new InvalidContentTypeConfigException(
                "Unknown type '" + typeName + "' in input config position: " + inputConfigPosition );
        }
        return type;
    }

    private String parseInputDisplayName( Element inputEl )
    {
        Element displayElement = inputEl.getChild( "display" );
        if ( displayElement != null )
        {
            return displayElement.getText();
        }
        else
        {
            throw new InvalidContentTypeConfigException(
                "Missing display element for input config '" + inputName + "' in position: " + inputConfigPosition );
        }
    }

    private String parseInputXpath( Element inputEl )
    {
        Element xPathElement = inputEl.getChild( "xpath" );
        if ( xPathElement != null )
        {
            return xPathElement.getText();
        }
        else
        {
            throw new InvalidContentTypeConfigException(
                "Missing xpath element for input config '" + inputName + "' in position: " + inputConfigPosition );
        }
    }

    private boolean parseInputReqiured( Element inputEl )
    {
        return Boolean.valueOf( inputEl.getAttributeValue( "required", "false" ) );
    }
}
