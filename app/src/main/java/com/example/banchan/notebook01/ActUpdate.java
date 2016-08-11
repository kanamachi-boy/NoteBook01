package com.example.banchan.notebook01;

import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.support.v4.app.NotificationCompat.Builder;

public class ActUpdate extends Activity {

    Integer key1;
    EditText ed2;
    MenuItem menu5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_update);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle("メモの修正");//
        actionBar.setSubtitle("");

        Button button0 = (Button) findViewById(R.id.button9);
        button0.setOnClickListener(new UpdateButtonClickListener());
        //  登録画面から渡されたデータ引継ぎ
        ed2 = (EditText) findViewById(R.id.memoEditText);
        ed2.setWidth(getIntent().getIntExtra("EDW", 0));
        ed2.setText(getIntent().getStringExtra("DATA1"));
        key1 = (getIntent().getIntExtra("KEY1", 0));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //■■■■   オプションメニュー表示
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_update, menu);
        //  onPrepareOptionsMenuで外側のflgを判定して操作した方が良いか？
        menu5 = (MenuItem) menu.findItem(R.id.OP5);
        menu5.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //■■■■   オプションメニューのイベント処理
        String a1 = "";
        switch (item.getItemId()) {

            case R.id.OP2:
                //  カーソル行を削除
                //  クラスにする程で無いが新カーソル位置も返したいので。オブジェクトを作る必要無いのでstaticメンバーだけ持つ。
                a1 = LineDelete.deleThisLine(ed2.getText().toString(), ed2.getSelectionStart());
                ed2.setText(a1);
                ed2.setSelection(LineDelete.newCurPositon);
                menu5.setVisible(true);
                break;
            case R.id.OP3:
                //  カーソルの前を削除
                a1 = LineDelete.deletePreCur(ed2.getText().toString(), ed2.getSelectionStart());
                ed2.setText(a1);
                ed2.setSelection(LineDelete.newCurPositon);
                menu5.setVisible(true);
                break;

            case R.id.OP4:
                //  カーソルの後ろを削除
                a1 = LineDelete.deleteAfterCur(ed2.getText().toString(), ed2.getSelectionStart());
                ed2.setText(a1);
                ed2.setSelection(LineDelete.newCurPositon);
                menu5.setVisible(true);
                break;
            case R.id.OP5:
                //  元に戻す
                a1 = LineDelete.preStr;
                ed2.setText(a1);
                menu5.setVisible(false);
                //ed2.setSelection(LineDelete.PreCurPositon);
                break;
            case R.id.OP6:
                finish();
                break;
            default:

        }
        return true;
    }

    public class UpdateButtonClickListener implements View.OnClickListener {
        public void onClick(View v) {
            //先頭の半・全角スペースを削除してから末尾の半・全角スペースも同様に削除。
            String mVal = ed2.getText().toString().replaceAll("^[\\s　]*", "").replaceAll("[\\s　]*$", "");
            if (mVal.isEmpty()) {
                Toast.makeText(ActUpdate.this, "空白メモは変更できません。", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    mUpdate(key1, mVal);
                    ed2.setText("");
                } catch (Exception e) {
                    notifyUpdate(e.getMessage());
                }

            }
            Intent intent = new Intent();
            intent.putExtra("RESULT", "変更完了しました。");
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void mUpdate(int id, String memo){
        /////   更新
        ContentValues values = new ContentValues();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        values.put("name", memo);
        values.put("biko", sdf.format(date));
        String[] arg = {String.valueOf(id)};
        getContentResolver().update(NoteBook.CONTENT_URI, values, "_id = ?", arg);

    }

    private void notifyUpdate(String str) {
        //  Notificationを作成
        Builder builder = new Builder(getApplicationContext());

        builder.setSmallIcon(R.drawable.abc_ic_go);
        builder.setContentTitle("更新"); // 1行目
        builder.setContentText(str); // 2行目
        builder.setSubText(""); // 3行目
        builder.setContentInfo("Information"); // 右端
        builder.setWhen(1400000000000l); // タイムスタンプ（現在時刻、メール受信時刻、カウントダウンなどに使用）
        builder.setTicker("updated...");

        //  Notificationがタップされた時に起動するアプリをpendingとし、Notificationにセットする
        //      完全修飾が必要か？
        Intent intent = new Intent(this, com.example.banchan.notebook01.MainActivity.class);
        /*
        Uri uri = Uri.parse("http://www.google.com");
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        */
        PendingIntent pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pending);

        //  NotificationManagerを使ってNotificationを表示
        //NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        //manager.notify(19570218, builder.build());

    }

}