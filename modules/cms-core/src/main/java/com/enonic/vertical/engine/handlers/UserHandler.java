/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.VerticalEngineLogger;
import com.enonic.vertical.event.VerticalEventListener;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.security.UserNameXmlCreator;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.domain.security.group.GroupKey;
import com.enonic.cms.domain.security.user.User;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserSpecification;
import com.enonic.cms.domain.security.userstore.UserStoreKey;

public final class UserHandler
    extends BaseHandler
    implements VerticalEventListener
{

    @Autowired
    private UserDao userDao;

    public static String latinToAZ( String input )
    {
        HashMap<String, String> charTable = new HashMap<String, String>( 58 );

        charTable.put( "�", "A" ); // Capital A, grave accent
        charTable.put( "�", "A" ); // Capital A, acute accent
        charTable.put( "�", "A" ); // Capital A, circumflex accent
        charTable.put( "�", "A" ); // Capital A, tilde
        charTable.put( "�", "A" ); // Capital A, dieresis or umlaut mark
        charTable.put( "�", "A" ); // Capital A, ring
        charTable.put( "�", "A" ); // Capital AE dipthong (ligature)
        charTable.put( "�", "C" ); // Capital C, cedilla
        charTable.put( "�", "E" ); // Capital E, grave accent
        charTable.put( "�", "E" ); // Capital E, acute accent
        charTable.put( "�", "E" ); // Capital E, circumflex accent
        charTable.put( "�", "E" ); // Capital E, dieresis or umlaut mark
        charTable.put( "�", "I" ); // Capital I, grave accent
        charTable.put( "�", "I" ); // Capital I, acute accent
        charTable.put( "�", "I" ); // Capital I, circumflex accent
        charTable.put( "�", "I" ); // Capital I, dieresis or umlaut mark
        //charTable.put("�", "");		// Capital Eth, Icelandic
        charTable.put( "�", "N" ); // Capital N, tilde
        charTable.put( "�", "O" ); // Capital O, grave accent
        charTable.put( "�", "O" ); // Capital O, acute accent
        charTable.put( "�", "O" ); // Capital O, circumflex accent
        charTable.put( "�", "O" ); // Capital O, tilde
        charTable.put( "�", "O" ); // Capital O, dieresis or umlaut mark
        charTable.put( "�", "O" ); // Capital O, slash
        charTable.put( "�", "U" ); // Capital U, grave accent
        charTable.put( "�", "U" ); // Capital U, acute accent
        charTable.put( "�", "U" ); // Capital U, circumflex accent
        charTable.put( "�", "U" ); // Capital U, dieresis or umlaut mark
        charTable.put( "�", "Y" ); // Capital Y, acute accent
        //charTable.put("�", "");		// Capital THORN, Icelandic
        charTable.put( "�", "S" ); // Small sharp s, German (sz ligature)
        charTable.put( "�", "a" ); // Small a, grave accent
        charTable.put( "�", "a" ); // Small a, acute accent
        charTable.put( "�", "a" ); // Small a, circumflex accent
        charTable.put( "�", "a" ); // Small a, tilde
        charTable.put( "�", "a" ); // Small a, dieresis or umlaut mark
        charTable.put( "�", "a" ); // Small a, ring
        charTable.put( "�", "a" ); // Small ae dipthong (ligature)
        charTable.put( "�", "c" ); // Small c, cedilla
        charTable.put( "�", "e" ); // Small e, grave accent
        charTable.put( "�", "e" ); // Small e, acute accent
        charTable.put( "�", "e" ); // Small e, circumflex accent
        charTable.put( "�", "e" ); // Small e, dieresis or umlaut mark
        charTable.put( "�", "i" ); // Small i, grave accent
        charTable.put( "�", "i" ); // Small i, acute accent
        charTable.put( "�", "i" ); // Small i, circumflex accent
        charTable.put( "�", "i" ); // Small i, dieresis or umlaut mark
        //charTable.put("�", "");		// Small eth, Icelandic
        charTable.put( "�", "n" ); // Small n, tilde
        charTable.put( "�", "o" ); // Small o, grave accent
        charTable.put( "�", "o" ); // Small o, acute accent
        charTable.put( "�", "o" ); // Small o, circumflex accent
        charTable.put( "�", "o" ); // Small o, tilde
        charTable.put( "�", "o" ); // Small o, dieresis or umlaut mark
        charTable.put( "�", "o" ); // Small o, slash
        charTable.put( "�", "u" ); // Small u, grave accent
        charTable.put( "�", "u" ); // Small u, acute accent
        charTable.put( "�", "u" ); // Small u, circumflex accent
        charTable.put( "�", "u" ); // Small u, dieresis or umlaut mark
        charTable.put( "�", "y" ); // Small y, acute accent
        //charTable.put("�", "");		// Small thorn, Icelandic
        charTable.put( "�", "y" ); // Small y, dieresis or umlaut mark

        String output = "";
        String inputChar;
        String outputChar;
        for ( int i = 0; i < input.length(); i++ )
        {
            inputChar = input.substring( i, i + 1 );

            try
            {
                outputChar = new String( inputChar.getBytes( "US-ASCII" ) );
            }
            catch ( UnsupportedEncodingException uee )
            {
                continue;
            }

            if ( ( outputChar.charAt( 0 ) >= 'a' ) && ( outputChar.charAt( 0 ) <= 'z' ) ||
                ( outputChar.charAt( 0 ) >= 'A' ) && ( outputChar.charAt( 0 ) <= 'Z' ) )
            {
                output += outputChar;
            }
            else if ( charTable.containsKey( inputChar ) )
            {
                output += charTable.get( inputChar );
            }
        }
        return output;
    }

    public String generateUID( String fName, String sName, UserStoreKey userStoreKey )
    {
        final int uidLength = 8;

        if ( fName == null || sName == null )
        {
            return null;
        }

        if ( fName.length() == 0 || sName.length() == 0 )
        {
            return null;
        }

        fName = latinToAZ( fName ).toLowerCase();
        sName = latinToAZ( sName ).toLowerCase();

        String suffix = "";
        int counter = 0;
        boolean done = false;
        String newUID = null;

        while ( !done )
        {

            int iterations = sName.length() + fName.length() - 1;
            if ( ( iterations + 1 ) > ( uidLength - suffix.length() ) )
            {
                iterations -= iterations + 1 - ( uidLength - suffix.length() );
            }

            for ( int i = 1; i <= iterations; i++ )
            {
                int letters_from_sname = Math.min( Math.min( i, sName.length() ), uidLength - 1 - suffix.length() );
                int letters_from_fname =
                    Math.min( fName.length(), uidLength - letters_from_sname - suffix.length() ) - Math.max( 0, i - letters_from_sname );

                newUID = fName.substring( 0, letters_from_fname ) + sName.substring( 0, letters_from_sname ) + suffix;

                if ( !existsUser( newUID, userStoreKey ) )
                {
                    done = true;
                    break;
                }
                else
                {
                    newUID = null;
                }
            }
            counter++;
            suffix = Integer.toString( counter );

            // Not very likely to happen, exit to prevent infinite loop
            if ( counter == 100 )
            {
                newUID = null;
                break;
            }
        }

        if ( newUID == null )
        {
            VerticalEngineLogger.warn( this.getClass(), 10, "Unable to generate UID for user (%0, %1).", new Object[]{fName, sName}, null );
        }

        return newUID;
    }

    public boolean existsUser( String uid, UserStoreKey userStoreKey )
    {
        UserSpecification userSpec = new UserSpecification();
        userSpec.setName( uid );
        userSpec.setUserStoreKey( userStoreKey );
        userSpec.setDeletedStateNotDeleted();
        final List<UserEntity> users = userDao.findBySpecification( userSpec );
        return users != null && users.size() > 0;
    }


    public Document getUsersByGroupKeys( String[] groupKeys )
    {
        if ( groupKeys.length == 0 )
        {
            return XMLTool.createDocument( "usernames" );
        }
        List<User> userKeys = new ArrayList<User>();
        for ( String groupKey : groupKeys )
        {
            UserSpecification userSpec = new UserSpecification();
            userSpec.setUserGroupKey( new GroupKey( groupKey ) );
            userSpec.setDeletedStateNotDeleted();
            UserEntity user = userDao.findSingleBySpecification( userSpec );
            if ( user != null )
            {
                userKeys.add( user );
            }
        }

        UserNameXmlCreator userNameXmlCreator = new UserNameXmlCreator();
        return XMLDocumentFactory.create( userNameXmlCreator.createUserNamesDocument( userKeys ) ).getAsDOMDocument();
    }

    public User getAnonymousUser()
    {
        return userDao.findBuiltInAnonymousUser();
    }


}
