package com.enonic.cms.core.xslt;

import org.junit.Test;

import javax.xml.transform.TransformerException;

import static org.junit.Assert.*;

public class XsltProcessorExceptionTest
{
    @Test
    public void testConstructWithCause()
    {
        final Throwable cause = new Throwable("Message");
        final XsltProcessorException exception = new XsltProcessorException(cause);

        assertEquals("Message", exception.getMessage());
        assertSame(cause, exception.getCause());
        assertNotNull(exception.getErrors());
        assertEquals(false, exception.getErrors().hasErrors());
    }

    @Test
    public void testConstructWithCauseAndErrors()
    {
        final Throwable cause = new Throwable("Message");
        final XsltProcessorErrors errors = new XsltProcessorErrors();
        final XsltProcessorException exception = new XsltProcessorException(cause, errors);

        assertEquals("Message", exception.getMessage());
        assertSame(cause, exception.getCause());
        assertSame(errors, exception.getErrors());
    }

    @Test
    public void testConstructWithErrors()
    {
        final XsltProcessorErrors errors = new XsltProcessorErrors();
        final XsltProcessorException exception = new XsltProcessorException(errors);

        assertEquals("Errors occurred during transformation", exception.getMessage());
        assertNull(exception.getCause());
        assertSame(errors, exception.getErrors());
    }

    @Test
    public void testMessageWithErrors()
    {
        final XsltProcessorErrors errors = new XsltProcessorErrors();
        errors.fatalError(new TransformerException("Transformer fatal error #1"));
        errors.error(new TransformerException("Transformer error #2"));
        errors.warning(new TransformerException("Transformer warning #3"));

        final XsltProcessorException exception = new XsltProcessorException(errors);
        final String message = exception.getMessage();

        assertEquals(
                "Errors occurred during transformation:\n" +
                "1) Transformer error #2\n" +
                "2) Transformer fatal error #1\n", message);

    }
}
