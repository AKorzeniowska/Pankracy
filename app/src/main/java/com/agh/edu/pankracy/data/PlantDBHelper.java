package com.agh.edu.pankracy.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.agh.edu.pankracy.data.PlantContract.PlantEntry;

public class PlantDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "plants.db";
    private static final Integer DATABASE_VERSION = 2;
    private static final String SQL_CREATE_TABLE_PLANTS_DATA = "CREATE TABLE " +
            PlantEntry.TABLE_NAME + "(" +
            PlantEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PlantEntry.COLUMN_NAME + " TEXT NOT NULL, " +
            PlantEntry.COLUMN_SPECIES + " TEXT, " +
            PlantEntry.COLUMN_WATERING + " INTEGER NOT NULL, " +
            PlantEntry.COLUMN_MIN_TEMP + " INTEGER, " +
            PlantEntry.COLUMN_LAST_WATERING + " TEXT NOT NULL);";

    private static final String SQL_DELETE_TABLE_PLANTS_DATA = "DROP TABLE IF EXISTS " +
            PlantEntry.TABLE_NAME + ";";

    public PlantDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_PLANTS_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE_PLANTS_DATA);
        db.execSQL(SQL_CREATE_TABLE_PLANTS_DATA);
    }
}
