/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import com.enonic.cms.core.security.group.GroupType;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Nov 14, 2010
 * Time: 11:54:15 PM
 */
public class DatabaseBaseValuesInitializer001
    extends DatabaseBaseValuesInitializer
{
    public final static String ANONYMOUS_UID = "anonymous";

    public final static String ANONYMOUS_FULLNAME = "Anonymous User";

    public final static String ADMIN_UID = "admin";

    public final static String ADMIN_FULLNAME = "Enterprise Administrator";

    /**
     * Do not pull these or the utility-methods up into the base-handler, since the implementation of these could vary based on the model-version
     */
    private static final String INSERT_USER_SQL =
        "INSERT INTO tUser ( usr_hkey, usr_sUID, usr_sFullName, usr_dteTimestamp, usr_bIsDeleted, usr_ut_lkey, usr_sSyncValue ) VALUES (?, ?, ?, @currentTimestamp@, ?, ?, ? )";

    private static final String INSERT_GROUP_SQL =
        "INSERT INTO tGroup (grp_hKey, grp_sName, grp_bIsDeleted, grp_bRestricted, grp_sSyncValue, grp_lType, grp_usr_hKey) VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String INSERT_LANGUAGES_SQL =
        "INSERT INTO tLanguage (lan_lkey, lan_scode, lan_sdescription, lan_dtetimestamp) values (?, ?, ?, @currentTimestamp@)";

    private static final String INSERT_CONTENTHANDLER_SQL =
        "Insert into tContentHandler " + "(han_lkey, han_sname, han_sclass, han_sdescription, han_xmlconfig, han_dtetimestamp) " +
            "values (?, ?, ?, ?, ?, @currentTimestamp@)";


    protected DatabaseBaseValuesInitializer001()
    {
        super();
    }

    public void initializeDatabaseValues( Connection conn )
        throws Exception
    {
        String anonymousUserKey = insertUser( conn, ANONYMOUS_UID, ANONYMOUS_FULLNAME, 1 );
        insertUser( conn, ADMIN_UID, ADMIN_FULLNAME, 2 );

        createGroup( conn, GroupType.ENTERPRISE_ADMINS, null );
        createGroup( conn, GroupType.ADMINS, null );
        String anonymousGroupKey = createGroup( conn, GroupType.ANONYMOUS, anonymousUserKey );
        createGroup( conn, GroupType.CONTRIBUTORS, null );
        createGroup( conn, GroupType.DEVELOPERS, null );
        createGroup( conn, GroupType.EXPERT_CONTRIBUTORS, null );

        insertLanguages( conn );

        insertContentHandlers( conn );

        updateKeyTable( conn, "tcontenttype", 1000 );
        updateKeyTable( conn, "tlanguage", 10 );

        Statement stmt = conn.createStatement();
        stmt.executeUpdate( "UPDATE tUser SET usr_grp_hkey = '" + anonymousGroupKey + "' WHERE usr_hkey = '" + anonymousUserKey + "'" );
        stmt.close();
    }

    public String createGroup( Connection conn, GroupType groupType, String userKey )
        throws Exception
    {
        return insertGroup( conn, groupType.getName(), groupType.toInteger(), userKey );
    }

    private void insertLanguages( Connection conn )
        throws Exception
    {
        insertLanguage( conn, 0, "en", "English" );
        insertLanguage( conn, 1, "no", "Norwegian" );
        insertLanguage( conn, 2, "sv", "Swedish" );
        insertLanguage( conn, 3, "da", "Danish" );
        insertLanguage( conn, 4, "fi", "Finnish" );
        insertLanguage( conn, 5, "hu", "Hungarian" );
    }


    private void insertContentHandlers( Connection conn )
        throws Exception
    {
        int key = 0;
        String name = "Custom content";
        String clazz = "com.enonic.vertical.adminweb.handlers.SimpleContentHandlerServlet";
        String description = "";
        String xmlConfig = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xmlconfig><config/><indexparameters/><ctydefault/></xmlconfig>";
        insertContentHandler( conn, key, name, clazz, description, xmlConfig );

        key = 2;
        name = "Files";
        clazz = "com.enonic.vertical.adminweb.handlers.ContentFileHandlerServlet";
        description = "";
        xmlConfig =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xmlconfig><config/><indexparameters><index type=\"text\" xpath=\"contentdata/name\"/><index type=\"text\" xpath=\"contentdata/description\"/><index type=\"text\" xpath=\"contentdata/keywords/keyword\"/></indexparameters><ctydefault><browse><column maincolumn=\"true\" orderby=\"contentdata/name\"><filename xpath=\"contentdata/name\"/></column><column title=\"Download\" columnalign=\"center\" titlealign=\"center\" width=\"100\"><binarylink newwindow=\"true\" text=\"Download\" xpath=\"@key\"/></column><column columnalign=\"right\" title=\"Filesize\" titlealign=\"center\" width=\"100\"><filesize xpath=\"contentdata/filesize\"/></column><column><timestamp/></column></browse></ctydefault></xmlconfig>";
        insertContentHandler( conn, key, name, clazz, description, xmlConfig );

        key = 3;
        name = "Images";
        clazz = "com.enonic.vertical.adminweb.handlers.ContentEnhancedImageHandlerServlet";
        description = "";
        xmlConfig =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xmlconfig><config/><indexparameters><index type=\"text\" xpath=\"contentdata/name\"/><index type=\"text\" xpath=\"contentdata/description\"/><index type=\"text\" xpath=\"contentdata/photographer/@name\"/><index type=\"text\" xpath=\"contentdata/photographer/@email\"/><index type=\"text\" xpath=\"contentdata/copyright\"/><index type=\"text\" xpath=\"contentdata/keywords\"/></indexparameters><ctydefault><browse><column maincolumn=\"true\" orderby=\"title\" title=\"Name\"><title/></column><column columnalign=\"center\" title=\"Original size\" titlealign=\"center\" width=\"100\"><xpath>contentdata/images/image[@type = 'original']/width</xpath><text>x</text><xpath>contentdata/images/image[@type = 'original']/height</xpath></column><column><timestamp/></column></browse></ctydefault></xmlconfig>";
        insertContentHandler( conn, key, name, clazz, description, xmlConfig );

        key = 4;
        name = "Newsletters";
        clazz = "com.enonic.vertical.adminweb.handlers.ContentNewsletterHandlerServlet";
        description = "";
        xmlConfig =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xmlconfig><config/><indexparameters><index type=\"text\" xpath=\"contentdata/subject\"/><index type=\"text\" xpath=\"contentdata/summary\"/></indexparameters><ctydefault/></xmlconfig>";
        insertContentHandler( conn, key, name, clazz, description, xmlConfig );

        key = 9;
        name = "Shop Orders";
        clazz = "com.enonic.vertical.adminweb.handlers.ContentOrderHandlerServlet";
        description = "";
        xmlConfig =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xmlconfig><config/><indexparameters><index type=\"text\" xpath=\"contentdata/status\"/><index type=\"text\" xpath=\"contentdata/customer/firstname\"/><index type=\"text\" xpath=\"contentdata/customer/lastname\"/><index type=\"text\" xpath=\"contentdata/customer/company\"/><index type=\"text\" xpath=\"contentdata/customer/email\"/><index type=\"text\" xpath=\"contentdata/customer/telephone\"/><index type=\"text\" xpath=\"contentdata/customer/mobile\"/><index type=\"text\" xpath=\"contentdata/customer/fax\"/><index type=\"text\" xpath=\"contentdata/billingaddress/postaladdress\"/><index type=\"text\" xpath=\"contentdata/billingaddress/postalcode\"/><index type=\"text\" xpath=\"contentdata/billingaddress/location\"/><index type=\"text\" xpath=\"contentdata/billingaddress/country\"/><index type=\"text\" xpath=\"contentdata/shippingaddress/postaladdress\"/><index type=\"text\" xpath=\"contentdata/shippingaddress/postalcode\"/><index type=\"text\" xpath=\"contentdata/shippingaddress/location\"/><index type=\"text\" xpath=\"contentdata/shippingaddress/country\"/><index type=\"text\" xpath=\"contentdata/items/item\"/></indexparameters><ctydefault><browse><column><timestamp/></column><column title=\"Orderno.\"><key/></column><column orderby=\"title\" title=\"Customer\"><mailto emailxpath=\"contentdata/customer/email\" namexpath=\"title\"/></column><column title=\"Total price\" width=\"120\"><number>contentdata/items/@total</number></column><column><contenttype/></column></browse></ctydefault></xmlconfig>";
        insertContentHandler( conn, key, name, clazz, description, xmlConfig );

        key = 10;
        name = "Polls";
        description = "";
        clazz = "com.enonic.vertical.adminweb.handlers.ContentPollHandlerServlet";
        xmlConfig = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xmlconfig><config/><indexparameters/><ctydefault/></xmlconfig>";
        insertContentHandler( conn, key, name, clazz, description, xmlConfig );

        key = 18;
        name = "Forms";
        clazz = "com.enonic.vertical.adminweb.handlers.ContentFormHandlerServlet";
        description = "";
        xmlConfig =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xmlconfig><config/><indexparameters><index type=\"text\" xpath=\"contentdata/form/item/data\"/></indexparameters><ctydefault><browse><column><title/></column><column><contenttype/></column><column><timestamp/></column></browse></ctydefault></xmlconfig>";
        insertContentHandler( conn, key, name, clazz, description, xmlConfig );
    }

    protected String insertUser( Connection conn, String uid, String fullName, int userType )
        throws Exception
    {
        String syncValue = generateUserSyncValue( uid.getBytes() );
        String key = generateUserKey( null, syncValue );

        PreparedStatement preparedStatement = conn.prepareStatement( INSERT_USER_SQL );

        preparedStatement.setString( 1, key );
        preparedStatement.setString( 2, uid );
        preparedStatement.setString( 3, fullName );
        preparedStatement.setInt( 4, 0 );
        preparedStatement.setInt( 5, userType );
        preparedStatement.setString( 6, syncValue );

        preparedStatement.execute();

        return key;
    }

    protected String insertGroup( Connection conn, String groupName, int groupType, String userKey )
        throws Exception
    {
        String groupSyncValue = generateGroupSyncValue();
        String groupKey = generateGroupKey( null, groupSyncValue );

        PreparedStatement preparedStatement = conn.prepareStatement( INSERT_GROUP_SQL );
        preparedStatement.setString( 1, groupKey );
        preparedStatement.setString( 2, groupName );
        preparedStatement.setInt( 3, 0 );
        preparedStatement.setInt( 4, 1 );
        preparedStatement.setString( 5, groupSyncValue );
        preparedStatement.setInt( 6, groupType );
        preparedStatement.setString( 7, userKey );

        preparedStatement.execute();

        return groupKey;
    }

    protected void insertLanguage( Connection conn, int key, String code, String descr )
        throws Exception
    {
        PreparedStatement preparedStatement = conn.prepareStatement( INSERT_LANGUAGES_SQL );
        preparedStatement.setInt( 1, key );
        preparedStatement.setString( 2, code );
        preparedStatement.setString( 3, descr );

        preparedStatement.execute();
    }


    protected void insertContentHandler( Connection conn, int key, String name, String clazz, String description, String xmlConfig )
        throws Exception
    {
        PreparedStatement preparedStatement = conn.prepareStatement( INSERT_CONTENTHANDLER_SQL );

        preparedStatement.setInt( 1, key );
        preparedStatement.setString( 2, name );
        preparedStatement.setString( 3, clazz );
        preparedStatement.setString( 4, description );
        preparedStatement.setBytes( 5, xmlConfig.getBytes() );

        preparedStatement.execute();
    }

}
