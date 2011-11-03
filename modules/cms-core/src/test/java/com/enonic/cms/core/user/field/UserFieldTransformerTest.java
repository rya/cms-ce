/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.user.field;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import junit.framework.Assert;

import com.enonic.esl.containers.ExtendedMap;

import com.enonic.cms.api.client.model.user.Gender;

public class UserFieldTransformerTest
{
    @Test
    public void testToUserFields()
    {
        ExtendedMap form = new ExtendedMap();
        form.put( "first_name", "Ola" );
        form.put( "last_name", "Normann" );
        form.put( "gender", "male" );
        form.put( "birthday", "2009-11-10" );

        UserFieldTransformer transformer = new UserFieldTransformer();
        UserFieldMap fields = transformer.toUserFields( form );
        Assert.assertEquals( 4, fields.getSize() );

        Assert.assertNotNull( fields.getField( UserFieldType.FIRST_NAME ) );
        Assert.assertEquals( "Ola", fields.getField( UserFieldType.FIRST_NAME ).getValue() );

        Assert.assertNotNull( fields.getField( UserFieldType.LAST_NAME ) );
        Assert.assertEquals( "Normann", fields.getField( UserFieldType.LAST_NAME ).getValue() );

        Assert.assertNotNull( fields.getField( UserFieldType.GENDER ) );
        Assert.assertEquals( Gender.MALE, fields.getField( UserFieldType.GENDER ).getValue() );

        Assert.assertNotNull( fields.getField( UserFieldType.BIRTHDAY ) );

        Object birthday = fields.getField( UserFieldType.BIRTHDAY ).getValue();
        Assert.assertEquals( birthday.getClass(), Date.class );

        Date date = Date.class.cast(birthday);
        DateFormat dateFormat = DateFormat.getDateInstance( DateFormat.MEDIUM , Locale.ROOT);

        // the year param is year minus 1900. the month is between 0-11.
        Assert.assertEquals( dateFormat.format( new Date(109, 10, 10) ), dateFormat.format( date ) );
    }
}
