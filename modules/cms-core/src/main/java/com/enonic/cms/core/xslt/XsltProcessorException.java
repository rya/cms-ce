/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.xslt;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Formatter;

import javax.xml.transform.TransformerException;

/**
 * This class implements the processor exception.
 */
public final class XsltProcessorException
    extends RuntimeException
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Error list.
     */
    private final XsltProcessorErrors errors;

    /**
     * Construct the errors.
     */
    public XsltProcessorException( String message )
    {
        super( message );
        this.errors = null;
    }

    /**
     * Construct the errors.
     */
    public XsltProcessorException( String message, XsltProcessorErrors errors )
    {
        super( message );
        this.errors = errors;
    }

    /**
     * Construct the errors.
     */
    public XsltProcessorException( Throwable cause )
    {
        this( cause.getMessage(), cause );
    }

    /**
     * Construct the errors.
     */
    public XsltProcessorException( Throwable cause, XsltProcessorErrors errors )
    {
        this( cause.getMessage(), cause, errors );
    }

    /**
     * Construct the errors.
     */
    public XsltProcessorException( String message, Throwable cause )
    {
        super( message, cause );
        this.errors = null;
    }

    /**
     * Construct the errors.
     */
    public XsltProcessorException( String message, Throwable cause, XsltProcessorErrors errors )
    {
        super( message, cause );
        this.errors = errors;
    }

    /**
     * Return the errors.
     */
    public XsltProcessorErrors getErrors()
    {
        return this.errors;
    }

    @Override
    public String getMessage()
    {
        return formatMessage(super.getMessage(), this.errors.getAllErrors());
    }

    private static String formatMessage(final String heading, final Collection<TransformerException> errors)
    {
        if (errors.isEmpty()) {
            return heading;
        }

        final Formatter fmt = new Formatter().format(heading).format(":%n");

        int index = 1;
        for (final TransformerException error : errors) {
            fmt.format("%s) %s%n", index++, error.getMessageAndLocation());
        }

        return fmt.toString();
    }
}
