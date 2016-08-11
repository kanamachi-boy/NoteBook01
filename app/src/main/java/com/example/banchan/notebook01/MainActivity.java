package com.example.banchan.notebook01;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    EditText ed1;
    ListView lv1;
    Button bt1;
    Integer edW;
    SimpleCursorAdapter SCA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ed1 = (EditText)findViewById(R.id.memoEditText);
        ed1.setHint("<ここに入力>");
        bt1 = (Button) findViewById(R.id.button);
        bt1.setOnClickListener(new ButtonClickListener());
        lv1 =(ListView) findViewById(R.id.listView);
        lv1.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        /////   他のアプリからデータ共有された場合
        Intent intent = getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_SEND.equals(action)) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                CharSequence ext = extras.getCharSequence(Intent.EXTRA_TEXT);
                if (ext != null) {
                    ed1.setText(ext.toString());    //  渡されたデータを表示
                }
            }
        }

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(0, null, this);

        // ListViewをコンテキストメニューの呼び出し元にする。
        registerForContextMenu(lv1);

        String[] from = {"name", "biko", "_id"};
        int[] to = {R.id.textView1, R.id.textView2, R.id.textView3};
        SCA = new SimpleCursorAdapter
                (this, R.layout.inbox_2row, null, from, to,
                        CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        lv1.setAdapter(SCA);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, NoteBook.CONTENT_URI, null, null, null, "_id DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        SCA.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        SCA.swapCursor(null);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        ////    画面の横幅とボタンの幅からエディットテキストの幅を決める
        //          onCreateでは画面が出来てないのでサイズを取得できない。

        WindowManager wm = getWindowManager();
        Display disp = wm.getDefaultDisplay();
        Point p1 = new Point();
        disp.getSize(p1);
        edW = p1.x - bt1.getWidth() - 25;
        ed1.setWidth(edW);
        //Toast.makeText(this, edW.toString() , Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //■■■■   オプションメニュー表示
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //■■■■   オプションメニューのイベント処理
        switch (item.getItemId()){

            case R.id.closing:
                finish();
                break;
            default:
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        //■■■■ registerForContextMenu()で登録したViewが長押しされると、
        // onCreateContextMenu()が呼ばれる。ここでメニューを作成する。
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menulv, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //■■■■ コンテキストメニューで項目が選択されるとonContextItemSelected()が呼ばれる。
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        //  選択されたリスト位置はCursor行を返す。これからcolumnを取り出す
        Cursor mCursor = (Cursor)lv1.getItemAtPosition(info.position);
        int mID = mCursor.getInt(mCursor.getColumnIndex("_id"));
        String mval = mCursor.getString(mCursor.getColumnIndex("name"));

        switch (item.getItemId()) {

            case R.id.menu0:
                //  クリップボートにcopy
                ClipData.Item citem = new ClipData.Item(mval);
                String[] mimeType = new String[1];
                mimeType[0] = ClipDescription.MIMETYPE_TEXT_PLAIN;
                ClipData cd = new ClipData(new ClipDescription("text_data", mimeType), citem);
                ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cm.setPrimaryClip(cd);

                String copyFolder = this.getFilesDir().getParent() + "/databases/";

                Toast.makeText(this,copyFolder , Toast.LENGTH_SHORT).show();
                return true;

            case R.id.menu1:

                /// 更新用Activityを開く
                Intent intent = new Intent(this, ActUpdate.class);
                //  渡すデータを用意
                intent.putExtra("KEY1",mID);
                intent.putExtra("DATA1",mval);
                intent.putExtra("EDW",edW);     //  EditText幅を引き継ぐ
                int requestCode =1000;  // リクエストコードを発行してリターンを受け取る用意
                startActivityForResult(intent, requestCode);
                //Toast.makeText(this,mval , Toast.LENGTH_SHORT).show();
                return true;

            case R.id.menu2:
                DeleteNote(info.position);  //  レコード削除
                return true;

            case R.id.menu3:
                //  ■■■■　データ共有　■■■■
                Intent intent1 = new Intent(Intent.ACTION_SEND);
                intent1.setType("text/plain");
                intent1.putExtra(Intent.EXTRA_TEXT, mval);
                startActivity(intent1);
                //Toast.makeText(this,"mail to" , Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent intent) {
        /////   ActUpdateアクティビティから帰還。
        super.onActivityResult(requestCode, resultCode, intent);

        if(resultCode == RESULT_OK && requestCode == 1000 && null != intent) {
            //  ActUpdateからのメッセージを表示
            String message = intent.getStringExtra("RESULT");
            Toast.makeText(this,message , Toast.LENGTH_LONG).show();
        }

    }

    private void DeleteNote(int position){
        //■■■■   ListViewの位置からレコードを特定して削除する
        Cursor mCursor = (Cursor)lv1.getItemAtPosition(position);
        int mID = mCursor.getInt(mCursor.getColumnIndex("_id"));
        String[] arg = {String.valueOf(mID)};
        getContentResolver().delete(NoteBook.CONTENT_URI, "_id = ? ", arg);
    }

    public class ButtonClickListener implements OnClickListener {
        //■■■■   メモの登録
        @Override
        public void onClick(View v) {
            //先頭の半・全角スペースを削除してから末尾の半・全角スペースも同様に削除。
            String mVal = ed1.getText().toString().replaceAll("^[\\s　]*", "").replaceAll("[\\s　]*$", "");
            if(mVal.isEmpty()){
                Toast.makeText(MainActivity.this,"空白メモは登録しません。", Toast.LENGTH_SHORT).show();
            }
            else {
                //  insert
                ContentValues values = new ContentValues();
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                values.put("name", mVal);
                values.put("biko", sdf.format(date));
                getContentResolver().insert(NoteBook.CONTENT_URI, values);

                ed1.setText("");
            }
        }
    }

}

