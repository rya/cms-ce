/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.security.userstore.config;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.enonic.cms.core.security.userstore.config.InvalidUserStoreConfigException;
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.core.security.userstore.config.UserStoreConfigParser;
import com.enonic.cms.core.security.userstore.config.UserStoreUserFieldConfig;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Test;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.domain.user.field.UserFieldType;

import static org.junit.Assert.*;

public class UserStoreConfigParserTest
{
    @Test
    public void testParseNull()
        throws IOException, JDOMException
    {
        final Element configEl = null;

        final UserStoreConfig config = UserStoreConfigParser.parse( configEl );

        assertNotNull( config );
        assertNotNull( config.getUserFieldConfigs() );
    }

    @Test
    public void testParseIllegalRoot()
        throws IOException, JDOMException
    {
        final StringBuffer configXml = new StringBuffer();
        configXml.append( "<fisk>" );
        configXml.append( "</fisk>" );
        final Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        try
        {
            UserStoreConfigParser.parse( configEl );
            fail( "An exception should have been thrown." );
        }
        catch ( final Exception ex )
        {
            assertEquals( InvalidUserStoreConfigException.class.getName(), ex.getClass().getName() );
        }
    }

    @Test
    public void testParseMissingUserElement()
        throws IOException, JDOMException
    {
        final StringBuffer configXml = new StringBuffer();
        configXml.append( "<config>" );
        configXml.append( "</config>" );
        final Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        try
        {
            UserStoreConfigParser.parse( configEl );
            fail( "An exception should have been thrown." );
        }
        catch ( Exception ex )
        {
            assertEquals( InvalidUserStoreConfigException.class.getName(), ex.getClass().getName() );
        }
    }

    @Test
    public void testParseMissingUserFieldsElement()
        throws IOException, JDOMException
    {
        final StringBuffer configXml = new StringBuffer();
        configXml.append( "<config>" );
        configXml.append( "  <user>" );
        configXml.append( "  </user>" );
        configXml.append( "</config>" );
        final Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        try
        {
            UserStoreConfigParser.parse( configEl );
            fail( "An exception should have been thrown." );
        }
        catch ( Exception ex )
        {
            assertEquals( InvalidUserStoreConfigException.class.getName(), ex.getClass().getName() );
        }
    }

    @Test
    public void testParseIllegalCustomFieldName()
        throws IOException, JDOMException
    {
        final StringBuffer configXml = new StringBuffer();
        configXml.append( "<config>" );
        configXml.append( "  <user-fields>" );
        configXml.append( "    <fisk/>" );
        configXml.append( "  </user-fields>" );
        configXml.append( "</config>" );
        final Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        try
        {
            UserStoreConfigParser.parse( configEl );
            fail( "An exception should have been thrown." );
        }
        catch ( Exception ex )
        {
            assertEquals( InvalidUserStoreConfigException.class.getName(), ex.getClass().getName() );
        }
    }

    @Test
    public void testParseIllegalUserFieldAttributeValue_Required()
        throws IOException, JDOMException
    {
        final StringBuffer configXml = new StringBuffer();
        configXml.append( "<config>" );
        configXml.append( "  <user-fields>" );
        configXml.append( "    <prefix required=\"fisk\"/>" );
        configXml.append( "  </user-fields>" );
        configXml.append( "</config>" );
        final Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        try
        {
            UserStoreConfigParser.parse( configEl );
            fail( "An exception should have been thrown." );
        }
        catch ( Exception ex )
        {
            assertEquals( InvalidUserStoreConfigException.class.getName(), ex.getClass().getName() );
        }
    }

    @Test
    public void testParseIllegalUserFieldAttributeValue_ReadOnly()
        throws IOException, JDOMException
    {
        final StringBuffer configXml = new StringBuffer();
        configXml.append( "<config>" );
        configXml.append( "  <user-fields>" );
        configXml.append( "    <prefix readonly=\"fisk\"/>" );
        configXml.append( "  </user-fields>" );
        configXml.append( "</config>" );
        final Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        try
        {
            UserStoreConfigParser.parse( configEl );
            fail( "An exception should have been thrown." );
        }
        catch ( Exception ex )
        {
            assertEquals( InvalidUserStoreConfigException.class.getName(), ex.getClass().getName() );
        }
    }

    @Test
    public void testParseIllegalUserFieldAttributeValue_Remote()
        throws IOException, JDOMException
    {
        final StringBuffer configXml = new StringBuffer();
        configXml.append( "<config>" );
        configXml.append( "  <user-fields>" );
        configXml.append( "    <prefix remote=\"fisk\"/>" );
        configXml.append( "  </user-fields>" );
        configXml.append( "</config>" );
        final Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        try
        {
            UserStoreConfigParser.parse(configEl);
            fail( "An exception should have been thrown." );
        }
        catch ( Exception ex )
        {
            assertEquals( InvalidUserStoreConfigException.class.getName(), ex.getClass().getName() );
        }
    }

    @Test
    public void testParseIllegalUserFieldAttributeValue_Iso()
        throws IOException, JDOMException
    {
        final StringBuffer configXml = new StringBuffer();
        configXml.append( "<config>" );
        configXml.append( "  <user-fields>" );
        configXml.append( "    <address iso=\"fisk\"/>" );
        configXml.append( "  </user-fields>" );
        configXml.append( "</config>" );
        final Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        try
        {
            UserStoreConfigParser.parse( configEl );
            fail( "An exception should have been thrown." );
        }
        catch ( Exception ex )
        {
            assertEquals( InvalidUserStoreConfigException.class.getName(), ex.getClass().getName() );
        }
    }

    @Test
    public void testParseIllegalUserFieldAttribute_IsoForOtherThanAddress()
        throws IOException, JDOMException
    {
        final StringBuffer configXml = new StringBuffer();
        configXml.append( "<config>" );
        configXml.append( "  <user-fields>" );
        configXml.append( "    <prefix iso=\"true\"/>" );
        configXml.append( "  </user-fields>" );
        configXml.append( "</config>" );
        final Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        try
        {
            UserStoreConfigParser.parse( configEl );
            fail( "An exception should have been thrown." );
        }
        catch ( Exception ex )
        {
            assertEquals( InvalidUserStoreConfigException.class.getName(), ex.getClass().getName() );
        }
    }

    @Test
    public void testParseIllegalUserFieldAttributeCombination_RequiredAndReadOnly()
        throws IOException, JDOMException
    {
        final StringBuffer configXml = new StringBuffer();
        configXml.append( "<config>" );
        configXml.append( "  <user-fields>" );
        configXml.append( "    <prefix required=\"true\" readonly=\"true\"/>" );
        configXml.append( "  </user-fields>" );
        configXml.append( "</config>" );
        final Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        try
        {
            UserStoreConfigParser.parse( configEl );
            fail( "An exception should have been thrown." );
        }
        catch ( Exception ex )
        {
            assertEquals( InvalidUserStoreConfigException.class.getName(), ex.getClass().getName() );
        }
    }

    @Test
    public void testParseIllegalUserFieldAttributePresent_Name()
        throws IOException, JDOMException
    {
        final StringBuffer configXml = new StringBuffer();
        configXml.append( "<config>" );
        configXml.append( "  <user-fields>" );
        configXml.append( "    <prefix name=\"fisk\"/>" );
        configXml.append( "  </user-fields>" );
        configXml.append( "</config>" );
        final Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        try
        {
            UserStoreConfigParser.parse( configEl );
            fail( "An exception should have been thrown." );
        }
        catch ( Exception ex )
        {
            assertTrue( ex instanceof InvalidUserStoreConfigException );
        }
    }

    @Test
    public void testParseIllegalUserFieldAttributePresent_RequiredForTypeBoolean()
        throws IOException, JDOMException
    {
        final StringBuffer configXml = new StringBuffer();
        configXml.append( "<config>" );
        configXml.append( "  <user-fields>" );
        configXml.append( "    <html-email required=\"true\"/>" );
        configXml.append( "  </user-fields>" );
        configXml.append( "</config>" );
        final Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        try
        {
            UserStoreConfigParser.parse( configEl );
            fail( "An exception should have been thrown." );
        }
        catch ( Exception ex )
        {
            assertEquals( InvalidUserStoreConfigException.class.getName(), ex.getClass().getName() );
        }
    }

    @Test
    public void testParseUnknownUserFieldAttribute()
        throws IOException, JDOMException
    {
        final StringBuffer configXml = new StringBuffer();
        configXml.append( "<config>" );
        configXml.append( "  <user-fields>" );
        configXml.append( "    <prefix fisk=\"ost\"/>" );
        configXml.append( "  </user-fields>" );
        configXml.append( "</config>" );
        final Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        try
        {
            UserStoreConfigParser.parse( configEl );
            fail( "An exception should have been thrown." );
        }
        catch ( Exception ex )
        {
            assertEquals( InvalidUserStoreConfigException.class.getName(), ex.getClass().getName() );
        }
    }

    @Test
    public void testParseUnknownUserFieldElement()
        throws IOException, JDOMException
    {
        final StringBuffer configXml = new StringBuffer();
        configXml.append( "<config>" );
        configXml.append( "  <user-fields>" );
        configXml.append( "    <prefix>" );
        configXml.append( "    <fisk/>" );
        configXml.append( "    </prefix>" );
        configXml.append( "  </user-fields>" );
        configXml.append( "</config>" );
        final Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        try
        {
            UserStoreConfigParser.parse( configEl );
            fail( "An exception should have been thrown." );
        }
        catch ( Exception ex )
        {
            assertEquals( InvalidUserStoreConfigException.class.getName(), ex.getClass().getName() );
        }
    }

    @Test
    public void testParseIllegalDuplicatedUserFieldElement()
        throws IOException, JDOMException
    {
        final StringBuffer configXml = new StringBuffer();
        configXml.append( "<config>" );
        configXml.append( "  <user-fields>" );
        configXml.append( "    <prefix/>" );
        configXml.append( "    <prefix/>" );
        configXml.append( "  </user-fields>" );
        configXml.append( "</config>" );
        final Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        try
        {
            UserStoreConfigParser.parse( configEl );
            fail( "An exception should have been thrown." );
        }
        catch ( Exception ex )
        {
            assertEquals( InvalidUserStoreConfigException.class.getName(), ex.getClass().getName() );
        }
    }

    @Test
    public void testAllParseUserFieldTypes()
        throws IOException, JDOMException
    {
        final StringBuffer configXml = new StringBuffer();
        configXml.append( "<config>" );
        configXml.append( "  <user-fields>" );
        for ( final UserFieldType type : UserFieldType.values() )
        {
            configXml.append( "    <" + type.getName() + "/>" );
        }
        configXml.append( "  </user-fields>" );
        configXml.append( "</config>" );
        final Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        final UserStoreConfig config = UserStoreConfigParser.parse( configEl );

        assertNotNull( config );
        assertNotNull( config.getUserFieldConfigs() );
        assertEquals( UserFieldType.values().length, config.getUserFieldConfigs().size() );

        /* Verify unique types */
        final Set<UserFieldType> userFields = new HashSet<UserFieldType>();
        for ( final UserStoreUserFieldConfig userFieldConfig : config.getUserFieldConfigs() )
        {
            userFields.add( userFieldConfig.getType() );
        }
        assertEquals( UserFieldType.values().length, userFields.size() );
    }


    @Test
    public void testParseRemoteFlag()
        throws IOException, JDOMException
    {
        final StringBuffer configXml = new StringBuffer();
        configXml.append( "<config>" );
        configXml.append( "  <user-fields>" );
        configXml.append( "    <first-name remote='true' />" );
        configXml.append( "    <initials remote='false' />" );
        configXml.append( "    <birthday/>" );
        configXml.append( "  </user-fields>" );
        configXml.append( "</config>" );
        final Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        final UserStoreConfig config = UserStoreConfigParser.parse( configEl );

        final Collection<UserStoreUserFieldConfig> allUserFieldConfigs = config.getUserFieldConfigs();
        final Collection<UserStoreUserFieldConfig> retmoteUserFieldConfigs = config.getRemoteOnlyUserFieldConfigs();

        assertEquals( 1, retmoteUserFieldConfigs.size() );

        for ( final UserStoreUserFieldConfig userFieldConfig : allUserFieldConfigs )
        {
            if ( userFieldConfig.getType().getName().equals( "first-name" ) )
            {
                assertEquals( true, userFieldConfig.isRemote() );
            }
            else if ( userFieldConfig.getType().getName().equals( "initials" ) )
            {
                assertEquals( false, userFieldConfig.isRemote() );
            }
            else if ( userFieldConfig.getType().getName().equals( "birthday" ) )
            {
                assertEquals( false, userFieldConfig.isRemote() );
            }
        }
    }

    @Test
    public void testParseReadOnlyFlag()
        throws IOException, JDOMException
    {
        final StringBuffer configXml = new StringBuffer();
        configXml.append( "<config>" );
        configXml.append( "  <user-fields>" );
        configXml.append( "    <first-name readonly='true' />" );
        configXml.append( "    <initials readonly='false' />" );
        configXml.append( "    <birthday/>" );
        configXml.append( "  </user-fields>" );
        configXml.append( "</config>" );
        final Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        final UserStoreConfig config = UserStoreConfigParser.parse( configEl );

        final Collection<UserStoreUserFieldConfig> allUserFieldConfigs = config.getUserFieldConfigs();

        for ( final UserStoreUserFieldConfig userFieldConfig : allUserFieldConfigs )
        {
            if ( userFieldConfig.getType().getName().equals( "first-name" ) )
            {
                assertEquals( true, userFieldConfig.isReadOnly() );
            }
            else if ( userFieldConfig.getType().getName().equals( "initials" ) )
            {
                assertEquals( false, userFieldConfig.isReadOnly() );
            }
            else if ( userFieldConfig.getType().getName().equals( "birthday" ) )
            {
                assertEquals( false, userFieldConfig.isReadOnly() );
            }
        }
    }

    @Test
    public void testParseRequiredFlag()
        throws IOException, JDOMException
    {
        final StringBuffer configXml = new StringBuffer();
        configXml.append( "<config>" );
        configXml.append( "  <user-fields>" );
        configXml.append( "    <first-name required='true' />" );
        configXml.append( "    <initials required='false' />" );
        configXml.append( "    <birthday/>" );
        configXml.append( "  </user-fields>" );
        configXml.append( "</config>" );
        final Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        final UserStoreConfig config = UserStoreConfigParser.parse( configEl );

        final Collection<UserStoreUserFieldConfig> allUserFieldConfigs = config.getUserFieldConfigs();

        for ( final UserStoreUserFieldConfig userFieldConfig : allUserFieldConfigs )
        {
            if ( userFieldConfig.getType().getName().equals( "first-name" ) )
            {
                assertEquals( true, userFieldConfig.isRequired() );
            }
            else if ( userFieldConfig.getType().getName().equals( "initials" ) )
            {
                assertEquals( false, userFieldConfig.isRequired() );
            }
            else if ( userFieldConfig.getType().getName().equals( "birthday" ) )
            {
                assertEquals( false, userFieldConfig.isRequired() );
            }
        }
    }

    @Test
    public void testParseIsoFlag()
        throws IOException, JDOMException
    {
        final StringBuffer configXml = new StringBuffer();
        configXml.append( "<config>" );
        configXml.append( "  <user-fields>" );
        configXml.append( "    <address iso='true' />" );
        configXml.append( "  </user-fields>" );
        configXml.append( "</config>" );
        final Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        final UserStoreConfig config = UserStoreConfigParser.parse( configEl );

        final Collection<UserStoreUserFieldConfig> allUserFieldConfigs = config.getUserFieldConfigs();

        for ( final UserStoreUserFieldConfig userFieldConfig : allUserFieldConfigs )
        {
            if ( userFieldConfig.getType().getName().equals( "address" ) )
            {
                assertEquals( true, userFieldConfig.useIso() );
            }
        }
    }
}
