/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.xmlbased;

import com.enonic.cms.core.content.contentdata.custom.DataEntryType;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;

public class XmlDataEntry
    extends AbstractXmlBasedInputDataEntry
{
    public XmlDataEntry( DataEntryConfig config, String value )
    {
        super( config, DataEntryType.XML, value );
    }

    protected void customValidate()
    {
        //No validation implemented
    }

    public boolean breaksRequiredContract()
    {
        return value == null;
    }
}