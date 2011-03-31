/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.xslt.trace;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.trace.InstructionInfo;
import net.sf.saxon.trace.TraceListener;

/**
 * Trace listener that does nothing.
 */
public final class NopTraceListener
    implements TraceListener
{
    public void open()
    {
        // Do nothing
    }

    public void close()
    {
        // Do nothing
    }

    public void enter( InstructionInfo instructionInfo, XPathContext xPathContext )
    {
        // Do nothing
    }

    public void leave( InstructionInfo instructionInfo )
    {
        // Do nothing
    }

    public void startCurrentItem( Item item )
    {
        // Do nothing
    }

    public void endCurrentItem( Item item )
    {
        // Do nothing
    }
}
