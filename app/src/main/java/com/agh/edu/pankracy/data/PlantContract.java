package com.agh.edu.pankracy.data;

import android.net.Uri;
import android.provider.BaseColumns;

    public final class PlantContract implements BaseColumns {
        static final String CONTENT_AUTHORITY = "com.agh.edu.pankracy.plants";
        static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
        static final String PATH_PLANTS = "plant_data";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PLANTS);

        public static Uri CONTENT_URI_ID (int id){
            return Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PLANTS + "/" + String.valueOf(id));
        }

        final static String TABLE_NAME = PATH_PLANTS;
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN_SPECIES = "species";
        public final static String COLUMN_WATERING = "watering";
        public final static String COLUMN_MIN_TEMP = "min_temperature";
        public final static String COLUMN_LAST_WATERING = "last_watering";

    }
