package com.example.banchan.notebook01;

import android.net.Uri;
import android.provider.BaseColumns;

public class NoteBook implements BaseColumns {

    private static final String AUTHORITY = "com.example.banchan.notebook01.NoteBook";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/sample_table");

}
