package com.oportfolio.oportfolio.data.interfaces;

import android.net.Uri;

public interface IBase {
	public static final String REFLECTION_ID = "com.oportfolio.oportfolio.REFLECTION_ID";

	public static final String URL_BASE = "http://o-portfolio-api-2.herokuapp.com";
//	public static final String URL_LOGIN = URL_BASE + "/api-auth/login";
	public static final String URL_LOGIN = URL_BASE + "/authenticate";
	public static final String URL_REGISTER = URL_BASE + "/register";
	public static final String URL_USERS = URL_BASE + "/users";
	public static final String URL_ENTRIES = URL_BASE + "/entries";
	
	//public static final String DATE_ISO8601 = "yyyy-MM-dd HH:mm:ss.SSSZ";
	public static final String DATE_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	
	public static enum UserTags{
		username,
		password;
	}
	
	public static enum TokenTags{
		token;
	}
	
	public static enum EntrieTags{
		created_at,
		id,
		title,
		description,
		reflection,
		occurred_at,
		image_latitude,
		image_longitude,
		image_url;
	}
	
	
	
	public static final String ACCOUNT_TYPE = "com.oportfolio";
	public static final String AUTH_TOKEN = "com.oportfolio.account";
	public static final String DB_NAME = "portolio.db";
	public static final Integer DB_VERSION = 1;

	public static final String AUTHORITY = "com.oportfolio.data";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

	public static final int REFLECTION_TABLE_ID = 1000;
	public static final String REFLECTION_PREFIX = "reflection";
	public static final Uri REFLECTION_CONTENT_URL = Uri.withAppendedPath(CONTENT_URI, REFLECTION_PREFIX);
	
	public static final int REFLECTION_ITEM = REFLECTION_TABLE_ID;
	public static final int REFLECTION_ITEMS = REFLECTION_TABLE_ID + 1;
	public static final Uri REFLECTION_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + REFLECTION_PREFIX);
	
	public interface IColumns{
		public final static String ID = "_id";
		public final static String TAGS = "tags";
		public final static String CREATE_AT = "createdAt";
		public final static String UPDATED_AT = "updatedAt";
		public final static String DIRTY = "dirty";
		public final static String SYNCED_AT = "syncedAt";
		public final static String SYNC_ID = "sync_id";
		
	}
}
