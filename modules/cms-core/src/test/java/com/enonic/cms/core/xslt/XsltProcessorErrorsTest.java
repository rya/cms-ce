package com.enonic.cms.core.xslt;

import org.junit.Test;

import javax.xml.transform.TransformerException;

import static org.junit.Assert.*;

public class XsltProcessorErrorsTest
{
    @Test
    public void testEmpty()
    {
        final XsltProcessorErrors errors = new XsltProcessorErrors();

        assertEquals(false, errors.hasErrors());
        assertErrors(errors, 0, 0, 0);
    }

    @Test
    public void testAddFatalError()
    {
        final XsltProcessorErrors errors = new XsltProcessorErrors();

        assertEquals(false, errors.hasErrors());

        errors.fatalError(new TransformerException("Some Error"));
        assertEquals(true, errors.hasErrors());
        assertErrors(errors, 1, 0, 0);
    }

    @Test
    public void testAddError()
    {
        final XsltProcessorErrors errors = new XsltProcessorErrors();

        assertEquals(false, errors.hasErrors());

        errors.error(new TransformerException("Some Error"));
        assertEquals(true, errors.hasErrors());
        assertErrors(errors, 0, 1, 0);
    }

    @Test
    public void testAddWarning()
    {
        final XsltProcessorErrors errors = new XsltProcessorErrors();

        assertEquals(false, errors.hasErrors());

        errors.warning(new TransformerException("Some Error"));
        assertEquals(false, errors.hasErrors());
        assertErrors(errors, 0, 0, 1);
    }

    private void assertErrors(final XsltProcessorErrors errors, final int numFatal,
                              final int numError, final int numWarning)
    {
        assertNotNull(errors.getAllErrors());
        assertEquals(numFatal + numError, errors.getAllErrors().size());

        assertNotNull(errors.getFatalErrors());
        assertEquals(numFatal, errors.getFatalErrors().size());

        assertNotNull(errors.getErrors());
        assertEquals(numError, errors.getErrors().size());

        assertNotNull(errors.getWarnings());
        assertEquals(numWarning, errors.getWarnings().size());
    }
}
