package com.enonic.cms.launcher.util;

import java.util.*;
import java.text.*;

public final class Messages
{
	private final static ResourceBundle BUNDLE;

	static {
		BUNDLE = ResourceBundle.getBundle(Messages.class.getName().toLowerCase());
	}

	public static String get(String key)
	{
		try {
			return BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return "{" + key + "}";
		}
	}

	public static String get(String key, Object... args)
	{
		MessageFormat format = new MessageFormat(get(key));
		return format.format(args);
	}
}
