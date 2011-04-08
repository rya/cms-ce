/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

import com.enonic.cms.portal.image.ImageServiceImpl;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Nov 26, 2009
 * Time: 9:55:23 AM
 */
public class ImageServiceImplTest
    extends TestCase
{


    private ImageServiceImpl imageService;

    private ImageRequestParser imageRequestParser;

    @Before
    public void setUp()
    {
        imageService = new ImageServiceImpl();
        imageRequestParser = new ImageRequestParser();

    }

    @Test
    public void testStuff()
    {

    }
}
