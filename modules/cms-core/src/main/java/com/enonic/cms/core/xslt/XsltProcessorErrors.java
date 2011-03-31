/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.xslt;

import java.util.Collection;
import java.util.LinkedList;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

/**
 * This class holds the processor errors.
 */
public final class XsltProcessorErrors
    implements ErrorListener
{
    /**
     * A list of errors.
     */
    private final LinkedList<TransformerException> errors;

    /**
     * A list of fatal errors.
     */
    private final LinkedList<TransformerException> fatalErrors;

    /**
     * A list of warnings.
     */
    private final LinkedList<TransformerException> warnings;

    /**
     * Construct the errors.
     */
    public XsltProcessorErrors()
    {
        this.errors = new LinkedList<TransformerException>();
        this.fatalErrors = new LinkedList<TransformerException>();
        this.warnings = new LinkedList<TransformerException>();
    }

    /**
     * Return true if it has errors.
     */
    public boolean hasErrors()
    {
        return !( this.errors.isEmpty() && this.fatalErrors.isEmpty() );
    }

    /**
     * Return the warnings.
     */
    public Collection<TransformerException> getWarnings()
    {
        return this.warnings;
    }

    /**
     * Return the errors.
     */
    public Collection<TransformerException> getErrors()
    {
        return this.errors;
    }

    /**
     * Return all fatal errors.
     */
    public Collection<TransformerException> getFatalErrors()
    {
        return this.fatalErrors;
    }

    /**
     * Return the all errors.
     */
    public Collection<TransformerException> getAllErrors()
    {
        LinkedList<TransformerException> allErrors = new LinkedList<TransformerException>();
        allErrors.addAll( this.errors );
        allErrors.addAll( this.fatalErrors );
        return allErrors;
    }

    /**
     * Report error.
     */
    public void error( TransformerException exception )
    {
        this.errors.add( exception );
    }

    /**
     * Report fatal error.
     */
    public void fatalError( TransformerException exception )
    {
        this.fatalErrors.add( exception );
    }

    /**
     * Report warning.
     */
    public void warning( TransformerException exception )
    {
        this.warnings.add( exception );
    }
}
