/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfigType;
import com.enonic.cms.core.content.contenttype.dataentryconfig.RelatedContentDataEntryConfig;

public class CtyImportMappingConfig
{
    private final CtyImportConfig importConfig;

    private final String source;

    private String additionalSource = null;

    private final String destination;

    private String format = null;

    private String separator = null;

    private String relatedContentType = null;

    private String relatedField = null;

    public CtyImportMappingConfig( final CtyImportConfig importConfig, final String source, final String destination )
    {
        this.importConfig = importConfig;
        this.source = source;
        this.destination = destination;
    }

    public void setAdditionalSource( String additionalSource )
    {
        this.additionalSource = additionalSource;
    }

    public void setFormat( String format )
    {
        this.format = format;
    }

    public void setSeparator( String separator )
    {
        this.separator = separator;
    }

    public void setRelatedContentType( String relatedContentType )
    {
        this.relatedContentType = relatedContentType;
    }

    public void setRelatedField( String relatedField )
    {
        this.relatedField = relatedField;
    }

    public String getSource()
    {
        return source;
    }

    public String getAdditionalSource()
    {
        return additionalSource;
    }

    public String getDestination()
    {
        return destination;
    }

    public String getFormat()
    {
        return format;
    }

    public String getSeparator()
    {
        return separator;
    }

    public String getRelatedContentType()
    {
        return relatedContentType;
    }

    public String getRelatedField()
    {
        return relatedField;
    }

    public Boolean isMetaDataMapping()
    {
        return destination.startsWith( "@" );
    }

    public Boolean isMultiple()
    {
        final DataEntryConfig config = this.importConfig.getForm().getInputConfig( destination );
        DataEntryConfigType type = config.getType();
        if ( type == DataEntryConfigType.KEYWORDS )
        {
            return true;
        }
        if ( type == DataEntryConfigType.RELATEDCONTENT )
        {
            return ( (RelatedContentDataEntryConfig) config ).isMultiple();
        }
        return false;
    }

    public Boolean isBinary()
    {
        return DataEntryConfigType.BINARY == this.importConfig.getForm().getInputConfig( destination ).getType();
    }

    public Boolean isXml()
    {
        return DataEntryConfigType.XML == this.importConfig.getForm().getInputConfig( destination ).getType();
    }

    public Boolean isHtml()
    {
        return DataEntryConfigType.HTMLAREA == this.importConfig.getForm().getInputConfig( destination ).getType();
    }

    public Boolean hasAdditionalSource()
    {
        return additionalSource != null;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        CtyImportMappingConfig that = (CtyImportMappingConfig) o;

        if ( destination != null ? !destination.equals( that.destination ) : that.destination != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder( 337, 737 ).append( destination ).toHashCode();
    }
}
