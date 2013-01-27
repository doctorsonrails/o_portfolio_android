package com.oportfolio.oportfolio.data.interfaces;

public interface IReflection {
	public final static String TAG = "reflection";
	public interface IColumns extends IBase.IColumns{
		public final static String ACCOUNT = "accountName";
		public final static String TITLE = "title";
		public final static String DATE = "eventDate";
		public final static String DURATION_LENGTH = "eventLength";
		public final static String DURATION_TYPE = "eventDurationType";
		public final static String DESCRIPTION = "description";
		public final static String REFLECTION = "reflection";
		public final static String IMAGE_LONG = "imageLong";
		public final static String IMAGE_LAT = "imageLat";
		public final static String IMAGE_URL = "imageUrl";
		
	}
}
