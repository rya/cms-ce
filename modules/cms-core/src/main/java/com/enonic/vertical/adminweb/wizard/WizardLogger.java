/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.wizard;

import com.enonic.vertical.VerticalException;
import com.enonic.vertical.VerticalRuntimeException;
import com.enonic.vertical.adminweb.VerticalAdminLogger;

public final class WizardLogger
    extends VerticalAdminLogger
{
    public static void errorWizard( Class clazz, int msgKey, String message, Object[] msgData, Throwable throwable )
        throws WizardException
    {

        try
        {
            error( clazz, msgKey, message, msgData, throwable, WizardException.class );
        }
        catch ( VerticalException ve )
        {
            throw (WizardException) ve;
        }
    }

    public static void errorWizard( Class clazz, int msgKey, String message, Object msgData, Throwable throwable )
        throws WizardException
    {

        try
        {
            error( clazz, msgKey, message, msgData, throwable, WizardException.class );
        }
        catch ( VerticalException ve )
        {
            throw (WizardException) ve;
        }
    }

    public static void errorWizard( Class clazz, int msgKey, String message, Throwable throwable )
        throws WizardException
    {

        try
        {
            error( clazz, msgKey, message, throwable, WizardException.class );
        }
        catch ( VerticalException ve )
        {
            throw (WizardException) ve;
        }
    }

    public static void fatalWizard( Class clazz, int msgKey, String message, Object[] msgData, Throwable throwable )
        throws WizardRuntimeException
    {

        try
        {
            fatal( clazz, msgKey, message, msgData, throwable, WizardRuntimeException.class );
        }
        catch ( VerticalRuntimeException vre )
        {
            throw (WizardRuntimeException) vre;
        }
    }

    public static void fatalWizard( Class clazz, int msgKey, String message, Object msgData, Throwable throwable )
        throws WizardRuntimeException
    {

        try
        {
            fatal( clazz, msgKey, message, msgData, throwable, WizardRuntimeException.class );
        }
        catch ( VerticalRuntimeException vre )
        {
            throw (WizardRuntimeException) vre;
        }
    }

    public static void fatalWizard( Class clazz, int msgKey, String message, Throwable throwable )
        throws WizardRuntimeException
    {

        try
        {
            fatal( clazz, msgKey, message, throwable, WizardRuntimeException.class );
        }
        catch ( VerticalRuntimeException vre )
        {
            throw (WizardRuntimeException) vre;
        }
    }
}