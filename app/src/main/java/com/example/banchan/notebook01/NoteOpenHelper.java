package com.example.banchan.notebook01;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "banchan1.db";

    public NoteOpenHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE sample_table ("
                        + "_id integer primary key autoincrement,"
                        + "name text not null, "
                        + "biko text"
                        + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //  downgradeはエラーになる！
        if( oldVersion < newVersion ){
            db.execSQL("DROP TABLE IF EXISTS sample_table");
            onCreate(db);
        }
    }
}
