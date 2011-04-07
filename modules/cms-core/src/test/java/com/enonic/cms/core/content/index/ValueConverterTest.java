/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

import java.util.GregorianCalendar;

import org.junit.Test;

import com.enonic.cms.domain.content.index.util.ValueConverter;

import static org.junit.Assert.*;


public class ValueConverterTest
{

    @Test
    public void testToTypedStringWithDate()
    {

        String orderBefore = ValueConverter.toTypedString( new GregorianCalendar( 2008, 6, 1, 12, 0, 0 ).getTime() );
        String orderAfter = ValueConverter.toTypedString( new GregorianCalendar( 2008, 6, 1, 12, 0, 1 ).getTime() );
        assertTrue( orderBefore.compareTo( orderAfter ) < 0 );

        String orderSame1 = ValueConverter.toTypedString( new GregorianCalendar( 2008, 7, 1, 12, 0, 0 ).getTime() );
        String orderSame2 = ValueConverter.toTypedString( new GregorianCalendar( 2008, 7, 1, 12, 0, 0 ).getTime() );
        assertTrue( orderSame1.compareTo( orderSame2 ) == 0 );
    }


}
