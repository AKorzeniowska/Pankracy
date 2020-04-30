package com.agh.edu.pankracy.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class PlantContract {
    private  PlantContract () {}

    public static final class PlantEntry implements BaseColumns {
        public static final String CONTENT_AUTHORITY = "com.agh.edu.pankracy.plants";
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
        public static final String PATH_PLANTS = "plant_data";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PLANTS);

        public static final Uri CONTENT_URI_ID (int id){
            Uri uri_with_id = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PLANTS + "/" + String.valueOf(id));
            return uri_with_id;
        }

        public final static String TABLE_NAME = "plant_data";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN_SPECIES = "species";
        public final static String COLUMN_WATERING = "watering";
        public final static String COLUMN_FERTILIZING = "fertilizing";
        public final static String COLUMN_MIN_TEMP = "min_temperature";
        public final static String COLUMN_LAST_WATERING = "last_watering";

        public static final String DATE_FORMAT_PATTERN = "dd-MM-yyyy";

    }
}
