/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.user.field;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.jdom.Document;
import org.jdom.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.domain.user.Address;
import com.enonic.cms.domain.user.Gender;
import com.enonic.cms.domain.user.UserInfo;

import static org.junit.Assert.*;

public class UserInfoXmlCreatorTest
{
    private static final Logger LOG = LoggerFactory.getLogger( UserInfoXmlCreatorTest.class.getName() );

    private UserInfoXmlCreator creator;

    private User userWithBasicFieldValue;

    @Before
    public void setup()
    {
        setupUserWithBasicFieldValue();
        this.creator = new UserInfoXmlCreator();
    }

    private void setupUserWithBasicFieldValue()
    {
        final UserInfo userInfo = new UserInfo();
        userInfo.setPrefix( "user_prefix_value" );

        final UserEntity user = new UserEntity();
        user.updateUserInfo( userInfo );

        this.userWithBasicFieldValue = user;
    }

    @Test
    public void testCreateFromUserWithBasicFieldValue_WithoutGivenRoot()
    {
        final Element returnedRootEl = creator.createUserInfoElement( userWithBasicFieldValue );

        assertEquals( "block", returnedRootEl.getName() );
        assertEquals( 1, returnedRootEl.getChildren().size() );
        assertEquals( "prefix", ( (Element) returnedRootEl.getChildren().get( 0 ) ).getName() );
        assertEquals( "user_prefix_value", ( (Element) returnedRootEl.getChildren().get( 0 ) ).getValue() );
    }

    @Test
    public void testCreateFromUserWithBasicFieldValue_WithGivenRoot_WithoutReplaceExisting()
    {
        final Element givenRootEl = new Element( "given_root" );
        final Element existingPrefixEl = new Element( "prefix" );
        existingPrefixEl.setText( "existing_prefix_value" );
        givenRootEl.addContent( existingPrefixEl );

        final Element returnedRootEl = creator.addUserInfoToElement( givenRootEl, userWithBasicFieldValue.getUserInfo(), false );

        assertEquals( "given_root", returnedRootEl.getName() );
        assertEquals( 1, returnedRootEl.getChildren().size() );
        assertEquals( "prefix", ( (Element) returnedRootEl.getChildren().get( 0 ) ).getName() );
        assertEquals( "existing_prefix_value", ( (Element) returnedRootEl.getChildren().get( 0 ) ).getValue() );
    }

    @Test
    public void testCreateFromUserWithBasicFieldValue_WithGivenRoot_WithReplaceExisting()
    {
        final Element givenRootEl = new Element( "given_root" );
        final Element existingPrefixEl = new Element( "prefix" );
        existingPrefixEl.setText( "existing_prefix_value" );
        givenRootEl.addContent( existingPrefixEl );

        final Element returnedRootEl = creator.addUserInfoToElement( givenRootEl, userWithBasicFieldValue.getUserInfo(), true );

        assertEquals( "given_root", returnedRootEl.getName() );
        assertEquals( 1, returnedRootEl.getChildren().size() );
        assertEquals( "prefix", ( (Element) returnedRootEl.getChildren().get( 0 ) ).getName() );
        assertEquals( "user_prefix_value", ( (Element) returnedRootEl.getChildren().get( 0 ) ).getValue() );
    }

    @Test
    public void testCreateFromUserWithAllValues_NoAddresses()
    {
        final UserInfo userInfo = new UserInfo();
        final Date birthday = new Date();
        userInfo.setBirthday( birthday );
        userInfo.setCountry( "country_value" );
        userInfo.setDescription( "description_value" );
        userInfo.setFax( "fax_value" );
        userInfo.setFirstName( "firstname_value" );
        userInfo.setGender( Gender.MALE );
        userInfo.setGlobalPosition( "globalposition_value" );
        userInfo.setHomePage( "homepage_value" );
        userInfo.setHtmlEmail( true );
        userInfo.setInitials( "initials_value" );
        userInfo.setLastName( "lastname_value" );
        userInfo.setLocale( Locale.ENGLISH );
        userInfo.setMemberId( "memberid_value" );
        userInfo.setMiddleName( "middlename_value" );
        userInfo.setMobile( "mobile_value" );
        userInfo.setNickName( "nickname_value" );
        userInfo.setOrganization( "organization_value" );
        userInfo.setPersonalId( "personalid_value" );
        userInfo.setPhone( "phone_value" );
        userInfo.setPhoto( new byte[]{0x23, 0x24, 0x25} );
        userInfo.setPrefix( "prefix_value" );
        userInfo.setSuffix( "suffix_value" );
        userInfo.setTimezone( TimeZone.getTimeZone( "GMT" ) );
        userInfo.setTitle( "title_value" );

        final UserEntity user = new UserEntity();
        user.updateUserInfo( userInfo );

        final Document doc = creator.createUserInfoDocument( user );

        LOG.info( JDOMUtil.prettyPrintDocument( doc ) );

        assertEquals( "block", doc.getRootElement().getName() );
        assertSingleXPathValueEquals( "block/birthday", doc, new SimpleDateFormat( "yyyy-MM-dd" ).format( birthday ) );
        assertSingleXPathValueEquals( "block/country", doc, "country_value" );
        assertSingleXPathValueEquals( "block/description", doc, "description_value" );
        assertSingleXPathValueEquals( "block/fax", doc, "fax_value" );
        assertSingleXPathValueEquals( "block/first-name", doc, "firstname_value" );
        assertSingleXPathValueEquals( "block/gender", doc, "male" );
        assertSingleXPathValueEquals( "block/global-position", doc, "globalposition_value" );
        assertSingleXPathValueEquals( "block/home-page", doc, "homepage_value" );
        assertSingleXPathValueEquals( "block/html-email", doc, "true" );
        assertSingleXPathValueEquals( "block/initials", doc, "initials_value" );
        assertSingleXPathValueEquals( "block/locale", doc, "en" );
        assertSingleXPathValueEquals( "block/member-id", doc, "memberid_value" );
        assertSingleXPathValueEquals( "block/middle-name", doc, "middlename_value" );
        assertSingleXPathValueEquals( "block/mobile", doc, "mobile_value" );
        assertSingleXPathValueEquals( "block/nick-name", doc, "nickname_value" );
        assertSingleXPathValueEquals( "block/organization", doc, "organization_value" );
        assertSingleXPathValueEquals( "block/personal-id", doc, "personalid_value" );
        assertSingleXPathValueEquals( "block/photo/@exists", doc, "true" );
        assertSingleXPathValueEquals( "block/prefix", doc, "prefix_value" );
        assertSingleXPathValueEquals( "block/suffix", doc, "suffix_value" );
        assertSingleXPathValueEquals( "block/time-zone", doc, "GMT" );
    }

    @Test
    public void testCreateFromUserWithAddressValues()
    {
        final UserInfo userInfo = new UserInfo();

        Address adr1 = new Address();
        adr1.setCountry( "NO" );
        adr1.setStreet( "Kirkegata 1-3" );
        adr1.setLabel( "Work" );
        adr1.setPostalCode( "0153" );
        adr1.setPostalAddress( "Oslo" );
        adr1.setRegion( "Oslo Fylke" );
        adr1.setIsoCountry( "ISO-NO" );
        adr1.setIsoRegion( "ISO-Oslo" );

        Address adr2 = new Address();
        adr2.setCountry( "NO" );
        adr2.setStreet( "Ole Vigsgt 9" );
        adr2.setLabel( "Priv" );
        adr2.setPostalCode( "0357" );
        adr2.setPostalAddress( "Oslo" );
        adr2.setRegion( "Oslo Fylke" );
        adr2.setIsoCountry( "ISO-NO" );
        adr2.setIsoRegion( "ISO-Oslo" );

        userInfo.setAddresses( adr1, adr2 );

        final UserEntity user = new UserEntity();
        user.updateUserInfo( userInfo );

        Document doc = creator.createUserInfoDocument( user );

        assertEquals( "block", doc.getRootElement().getName() );
        assertSingleXPathValueEquals( "block/addresses/address[1]/country", doc, "NO" );
        assertSingleXPathValueEquals( "block/addresses/address[1]/street", doc, "Kirkegata 1-3" );
        assertSingleXPathValueEquals( "block/addresses/address[1]/label", doc, "Work" );
        assertSingleXPathValueEquals( "block/addresses/address[1]/postal-code", doc, "0153" );
        assertSingleXPathValueEquals( "block/addresses/address[1]/postal-address", doc, "Oslo" );
        assertSingleXPathValueEquals( "block/addresses/address[1]/region", doc, "Oslo Fylke" );
        assertSingleXPathValueEquals( "block/addresses/address[1]/iso-country", doc, "ISO-NO" );
        assertSingleXPathValueEquals( "block/addresses/address[1]/iso-region", doc, "ISO-Oslo" );

        assertSingleXPathValueEquals( "block/addresses/address[2]/country", doc, "NO" );
        assertSingleXPathValueEquals( "block/addresses/address[2]/street", doc, "Ole Vigsgt 9" );
        assertSingleXPathValueEquals( "block/addresses/address[2]/label", doc, "Priv" );
        assertSingleXPathValueEquals( "block/addresses/address[2]/postal-code", doc, "0357" );
        assertSingleXPathValueEquals( "block/addresses/address[2]/postal-address", doc, "Oslo" );
        assertSingleXPathValueEquals( "block/addresses/address[2]/region", doc, "Oslo Fylke" );
        assertSingleXPathValueEquals( "block/addresses/address[2]/iso-country", doc, "ISO-NO" );
        assertSingleXPathValueEquals( "block/addresses/address[2]/iso-region", doc, "ISO-Oslo" );
    }

    private void assertSingleXPathValueEquals( final String xpathString, final Document doc, final String expectedValue )
    {
        final String actualValue = JDOMUtil.evaluateSingleXPathValueAsString( xpathString, doc );
        Assert.assertEquals( xpathString, expectedValue, actualValue );
    }
}
