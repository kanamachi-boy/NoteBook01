package com.example.banchan.notebook01;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class NoteContentProvider extends ContentProvider {

    private NoteOpenHelper mNoteOpenHelper;

    @Override
    public boolean onCreate() {
        mNoteOpenHelper = new NoteOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mNoteOpenHelper.getReadableDatabase();
        // URIからテーブル名を取得
        String tableName = uri.getPathSegments().get(0);
        Cursor cursor = db.query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
        // 設定したURIの変更を監視するように設定
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mNoteOpenHelper.getWritableDatabase();
        String tableName = uri.getPathSegments().get(0);
        long rowId = db.insert(tableName, null, values);
        // 追加された行のURIを生成。content://jp.mixi.sample.contentprovider.Book/book/1
        Uri insertedUri = ContentUris.withAppendedId(uri, rowId);
        // 設定したURIのデータに変更があったことを通知
        //Log.d("■", "insert " + tableName);
        getContext().getContentResolver().notifyChange(insertedUri, null);
        return insertedUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mNoteOpenHelper.getWritableDatabase();
        String tableName = uri.getPathSegments().get(0);
        Log.d("■", "" + tableName);
        int deletedCount = db.delete(tableName, selection, selectionArgs);
        // 設定したURIのデータに変更があったことを通知
        getContext().getContentResolver().notifyChange(uri, null);
        return deletedCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mNoteOpenHelper.getWritableDatabase();
        // URIからテーブル名を取得
        String tableName = uri.getPathSegments().get(0);
        int updatedCount = db.update(tableName, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return updatedCount;
    }
}
