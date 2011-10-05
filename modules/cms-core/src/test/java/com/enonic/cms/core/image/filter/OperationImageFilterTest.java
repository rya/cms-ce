package com.enonic.cms.core.image.filter;

import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public class OperationImageFilterTest
{
    @Test
    public void testFilter()
    {
        final BufferedImage source = Mockito.mock(BufferedImage.class);
        final BufferedImage target = Mockito.mock(BufferedImage.class);

        final BufferedImageOp op = Mockito.mock(BufferedImageOp.class);
        Mockito.when(op.filter(source, null)).thenReturn(target);

        final OperationImageFilter filter = new OperationImageFilter(op);

        final BufferedImage result = filter.filter(source);
        assertNotNull(result);
        assertSame(target, result);
    }
}
