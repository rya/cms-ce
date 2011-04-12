/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal;

/**
 * Enumeration for request types when clicking a menu item.  It can either be a real page, or a directive.  In Enonic CMS, a directive
 * corresponds to the menu item types URL and Shortcut.
 */
public enum MenuItemRequestType
{
    DIRECTIVE,
    PAGE
}
