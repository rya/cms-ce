/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * The Localizer provides convenient access to localized strings.
 */
public final class Localizer
{
    /**
     * Map of localizers.
     */
    private final static Map LOCALIZERS = new HashMap();

    /**
     * Resource bundle.
     */
    private final ResourceBundle bundle;

    /**
     * Format cache.
     */
    private final Map formats;

    /**
     * Private constructor.
     */
    private Localizer()
    {
        this( null );
    }

    /**
     * Private constructor.
     */
    private Localizer( ResourceBundle bundle )
    {
        this.bundle = bundle;
        this.formats = new HashMap();
    }

    /**
     * Return a Localizer instance that will access the properties file in the package of the given class using the system default locale.
     */
    public static Localizer forPackage( Class clz )
    {
        return forPackage( clz, null );
    }

    /**
     * Return a Localizer instance that will access the properties file in the package of the given class using the system default locale.
     */
    public static Localizer forPackage( Class clz, Locale locale )
    {
        if ( locale == null )
        {
            locale = Locale.getDefault();
        }

        String name = "localizer";
        Package pck = clz.getPackage();
        if ( pck != null )
        {
            name = pck.getName() + "." + name;
        }

        String key = name + locale.toString();
        synchronized ( LOCALIZERS )
        {
            Localizer loc = (Localizer) LOCALIZERS.get( key );

            if ( loc == null )
            {
                loc = newLocalizer( name, locale, clz.getClassLoader() );
                LOCALIZERS.put( key, loc );
            }

            return loc;
        }
    }

    /**
     * Return a new localizer.
     */
    private static Localizer newLocalizer( String name, Locale locale, ClassLoader loader )
    {
        try
        {
            return new Localizer( ResourceBundle.getBundle( name, locale, loader ) );
        }
        catch ( Exception e )
        {
            return new Localizer();
        }
    }

    /**
     * Return the localized message.
     */
    public String get( String key )
    {
        if ( this.bundle != null )
        {
            try
            {
                return this.bundle.getString( key );
            }
            catch ( MissingResourceException e )
            {
                // Do nothing
            }
        }

        return key;
    }

    /**
     * Return the localized message.
     */
    public String get( String key, Object arg1 )
    {
        return get( key, new Object[]{arg1} );
    }

    /**
     * Return the localized message.
     */
    public String get( String key, Object arg1, Object arg2 )
    {
        return get( key, new Object[]{arg1, arg2} );
    }

    /**
     * Return the localized message.
     */
    public String get( String key, Object arg1, Object arg2, Object arg3 )
    {
        return get( key, new Object[]{arg1, arg2, arg3} );
    }

    /**
     * Return the localized message.
     */
    public String get( String key, Object[] args )
    {
        try
        {
            MessageFormat mf = (MessageFormat) this.formats.get( key );

            if ( mf == null )
            {
                String msg = null;

                if ( this.bundle != null )
                {
                    try
                    {
                        msg = this.bundle.getString( key );
                    }
                    catch ( MissingResourceException e )
                    {
                        // Do nothing
                    }
                }

                if ( msg != null )
                {
                    mf = new MessageFormat( get( key ) );
                }
                else
                {
                    return key;
                }

                this.formats.put( key, mf );
            }

            return mf.format( args );
        }
        catch ( Exception x )
        {
            return "Error processing message " + key;
        }
    }

    /**
     * Return the localized message.
     */
    public static String get( Class clz, String key )
    {
        return forPackage( clz ).get( key );
    }

    /**
     * Return the localized message.
     */
    public static String get( Class clz, String key, Object arg1 )
    {
        return get( clz, key, new Object[]{arg1} );
    }

    /**
     * Return the localized message.
     */
    public static String get( Class clz, String key, Object arg1, Object arg2 )
    {
        return get( clz, key, new Object[]{arg1, arg2} );
    }

    /**
     * Return the localized message.
     */
    public static String get( Class clz, String key, Object arg1, Object arg2, Object arg3 )
    {
        return get( clz, key, new Object[]{arg1, arg2, arg3} );
    }

    /**
     * Return the localized message.
     */
    public static String get( Class clz, String key, Object[] args )
    {
        return forPackage( clz ).get( key, args );
    }
}
