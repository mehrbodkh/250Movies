package com.example.mehrbod.a250movies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mehrbod on 5/29/2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "movie.db";
    private static final String TABLE_NAME = "movie_table";
    private static final String COL_1 = "RANK";
    private static final String COL_2 = "TITLE";
    private static final String COL_3 = "STARS";
    private static final String COL_4 = "WRITER";
    private static final String COL_5 = "MUSIC";
    private static final String COL_6 = "PRODUCER";
    private static final String COL_7 = "DIRECTOR";
    private static final String COL_8 = "YEAR";
    private static final String CREATE_DB_TABLE_QUERY =
            "CREATE TABLE " + TABLE_NAME +
                    "(RANK INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "TITLE TEXT NOT NULL, " +
                    "STARS TEXT, " +
                    "WRITER TEXT, " +
                    "MUSIC TEXT, " +
                    "PRODUCER TEXT, " +
                    "DIRECTOR TEXT, " +
                    "YEAR TEXT)";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST " + TABLE_NAME);
    }

    public boolean insertData(String title, String stars, String writer, String music,
                              String producer, String director, String year)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, title);
        contentValues.put(COL_3, stars);
        contentValues.put(COL_4, writer);
        contentValues.put(COL_5, music);
        contentValues.put(COL_6, producer);
        contentValues.put(COL_7, director);
        contentValues.put(COL_8, year);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        }

        return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return cursor;
    }

    public boolean updateData(String rank, String title, String stars, String writer, String music,
                              String producer, String director, String year)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, rank);
        contentValues.put(COL_2, title);
        contentValues.put(COL_3, stars);
        contentValues.put(COL_4, writer);
        contentValues.put(COL_5, music);
        contentValues.put(COL_6, producer);
        contentValues.put(COL_7, director);
        contentValues.put(COL_8, year);

        int result = db.update(TABLE_NAME, contentValues, "RANK = ?", new String[]{rank});

        if (result == 0) {
            return false;
        }

        return true;
    }

    public Cursor getData(String rank) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE RANK = " + rank, null);
        return cursor;
    }

    public boolean deleteData(String rank) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(TABLE_NAME, "RANK = ?", new String[]{rank});

        if (result == 0) {
            return false;
        }
        return true;
    }

    public boolean isEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.getCount() == 0) {
            return true;
        }
        return false;
    }
}
