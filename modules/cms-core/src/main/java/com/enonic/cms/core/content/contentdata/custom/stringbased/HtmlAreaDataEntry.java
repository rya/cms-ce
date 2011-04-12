/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.stringbased;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentNativeLink;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.content.ContentNativeLinkCollector;
import com.enonic.cms.core.content.contentdata.InvalidContentDataException;
import com.enonic.cms.core.content.contentdata.custom.DataEntryType;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;

public class HtmlAreaDataEntry
    extends AbstractStringBasedInputDataEntry
{
    private static String wrapElementName = "temp-wraped-element-if-you-can-see-me-something-went-wrong";

    private List<ContentNativeLink> contentNativeLinks;

    private String comparableValue;

    public HtmlAreaDataEntry( final DataEntryConfig config, final String value )
    {
        super( config, DataEntryType.HTML_AREA, validateXhtml( config, removeProlog( value ) ) );
    }

    protected void customValidate()
    {
        //Validation not implemented
    }

    public boolean breaksRequiredContract()
    {
        return StringUtils.isEmpty( value );
    }

    private static String validateXhtml( final DataEntryConfig config, final String value )
    {
        if ( StringUtils.isEmpty( value ) )
        {
            return value;
        }

        try
        {
            JDOMUtil.parseDocument( getStartWrap() + value + getEndWrap() );
        }
        catch ( final Exception e )
        {
            throw new InvalidContentDataException( "Input " + config.getName() + " has no vaild xhtml value", e );
        }

        return value;
    }

    private String getComparableValue()
    {
        if ( comparableValue == null )
        {
            try
            {
                final Document doc = JDOMUtil.parseDocument( getStartWrap() + value + getEndWrap() );
                comparableValue = JDOMUtil.prettyPrintDocument( doc, "", false );
            }
            catch ( final Exception e )
            {
                /* Should never occur as the same parsing are done in the constructor */
                return null;
            }
        }
        return comparableValue;
    }


    private static String getStartWrap()
    {
        return "<" + wrapElementName + ">";
    }

    private static String getEndWrap()
    {
        return "</" + wrapElementName + ">";
    }

    public Set<ContentKey> resolveRelatedContentKeys()
    {
        final Set<ContentKey> contentKeySet = new HashSet<ContentKey>();

        for ( final ContentNativeLink link : getContentSmartLinks() )
        {
            contentKeySet.add( link.getContentKey() );
        }
        return contentKeySet;
    }

    private List<ContentNativeLink> getContentSmartLinks()
    {
        if ( contentNativeLinks == null )
        {
            if ( getValue() == null )
            {
                contentNativeLinks = new ArrayList<ContentNativeLink>();
            }
            else
            {
                contentNativeLinks = new ContentNativeLinkCollector().collect( getValue() );
            }
        }
        return contentNativeLinks;
    }

    private static String removeProlog( final String value )
    {
        if ( StringUtils.isEmpty( value ) )
        {
            return value;
        }

        final String start = "<?xml";
        final String end = "?>";
        if ( !value.startsWith( start ) )
        {
            return value;
        }
        final int endPos = value.indexOf( end );
        if ( endPos < 0 )
        {
            return value;
        }
        return value.substring( endPos + end.length() ).trim();
    }


    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof HtmlAreaDataEntry ) )
        {
            return false;
        }
        final HtmlAreaDataEntry that = (HtmlAreaDataEntry) o;

        if ( getName() != null ? !getName().equals( that.getName() ) : that.getName() != null )
        {
            return false;
        }

        if ( getXPath() != null ? !getXPath().equals( that.getXPath() ) : that.getXPath() != null )
        {
            return false;
        }

        if ( getComparableValue() != null ? !getComparableValue().equals( that.getComparableValue() ) : that.getComparableValue() != null )
        {
            return false;
        }

        return true;
    }
}