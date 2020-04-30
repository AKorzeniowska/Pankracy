package com.agh.edu.pankracy.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.agh.edu.pankracy.data.PlantContract.PlantEntry;

public class PlantProvider extends ContentProvider {
    private PlantDBHelper mDbHelper;
    public static final String LOG_TAG = PlantProvider.class.getSimpleName();
    private static final int PLANTS = 100;
    private static final int PLANT_ID = 101;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(PlantEntry.CONTENT_AUTHORITY, PlantEntry.PATH_PLANTS, PLANTS);
        uriMatcher.addURI(PlantEntry.CONTENT_AUTHORITY, PlantEntry.PATH_PLANTS + "/#", PLANT_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new PlantDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor = null;
        int match = uriMatcher.match(uri);

        switch (match){
            case PLANTS:
                cursor = database.query(PlantEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PLANT_ID:
                selection = PlantEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(PlantEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("unknown URI: " + uri);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = uriMatcher.match(uri);
        switch (match){
            case PLANTS:
                return insertPlant(uri, values);
            default:
                throw new IllegalArgumentException("unknown URI: " + uri);
        }
    }

    private Uri insertPlant(Uri uri, ContentValues values){
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(PlantEntry.TABLE_NAME, null, values);
        if (id==-1){
            Log.e(LOG_TAG, "failed to insert to database for: " + uri);
            return null;
        }
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        switch (match){
            case PLANTS:
                return deletePlants(uri, selection, selectionArgs);
            case PLANT_ID:
                selection = PlantEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return deletePlants(uri, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("unknown URI: " + uri);
        }
    }

    private int deletePlants(Uri uri, String selection, String[] selectionArgs){
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        return database.delete(PlantEntry.TABLE_NAME, selection, selectionArgs);
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        switch (match){
            case PLANTS:
                return updatePlants(uri, values, selection, selectionArgs);
            case PLANT_ID:
                selection = PlantEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePlants(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("unknown URI: " + uri);
        }
    }

    private int updatePlants(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        if (values.size() == 0)
            return 0;
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        return database.update(PlantEntry.TABLE_NAME, values, selection, selectionArgs);
    }
}
