package tw.org.iii.iiiand08;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sp;//內容存放機制
    private SharedPreferences.Editor editor;
    private TextView content;
    private File sdroot, approot; //approot:sdroot-Android-data-<pkg-name>為入口

    private  MyDBHelper myDBHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //是否有拿到該拿到的權限 ; Android6之後這邊就有,不用加這段,因為已經拿到權限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,},
                    9487);
        }else{
            init();
        }
        //有拿到權限才給init()
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 9487){
            if(grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                init();
            }else {
                finish();
            }
        }
    }

    private void init(){
        content = findViewById(R.id.content);
        sp = getSharedPreferences("brad", MODE_PRIVATE);//檔名為brad
        editor = sp.edit();

        sdroot = Environment.getExternalStorageDirectory(); //槓掉的方法,可以未來會無法使用
        Log.v("brad", sdroot.getAbsolutePath());

        approot = new File(sdroot, "Android/data/" + getPackageName());//目前還沒pkg name
        if(!approot.exists()){
            approot.mkdirs();//有加s, 父目錄不在就新增; 沒s就直接掛點
        }

        myDBHelper = new MyDBHelper(this, "mydb", null, 1);
        db = myDBHelper.getReadableDatabase();//check能否增刪修改==>可


    }

    public void test1(View view) {//紀錄特定user 遊戲等級, 偏好設定 ;內存空間
        //focus android IO, stream
        //偏好設定
        //存
        //叫出來
        editor.putString("username","brad");//目前只是先存記憶體
        editor.putBoolean("sound",false);
        editor.putInt("stage",4);
        editor.commit();//把值真正存進去
        Toast.makeText(this,"save ok", Toast.LENGTH_SHORT).show();
        // data/data/packagename

    }

    public void test2(View view) { //預設值
        boolean isSound =  sp.getBoolean("sound", true);
        String username = sp.getString("username", "nobody");
        int stage = sp.getInt("stage", 1);
        Log.v("brad", username + ":" + stage + ":" + isSound);
    }

    //test3 才開始需要私有權限
    public  void test3(View view){ //寫, 資料存在app,要外存,否則app消失,資料消失 ;沒有說路徑,固定地方存取data-data-pkgname
        try {
            FileOutputStream fout = openFileOutput("brad.txt", MODE_APPEND);//副檔名是作業系統在看 MODE_PRIVATE
            fout.write("Hello, World\n".getBytes());
            fout.flush();//通常在close之前
            fout.close();
            Toast.makeText(this, "Save OK", Toast.LENGTH_SHORT).show();
            //}catch (FileNotFoundException e){
            //}catch (IOException){
            //}
        }catch (Exception e){
            Log.v("brad", e.toString());
        }
    }

    public  void test4(View view){ //讀 路徑同test3

        try(FileInputStream fin = openFileInput("brad.txt")){  //自動關閉機制java8 autoClosed
            //byte為單位
            StringBuffer sb = new StringBuffer();
            //int c;
            byte[] buf = new byte[1024];
            int len;
            while ((len=fin.read(buf)) != -1){
                sb.append(new String(buf, 0,len));//讀大檔實驗,buffer速度快很多倍
            }//已知為字串資料

            /* //一次一個byte讀
            while ((c = fin.read()) != -1){ //至檔案文件底, 查java api
                sb.append((char)c);
               //Log.v("brad", "=>" + (char)c);
               //alt + 65 輸出ascii code編碼"A"
            }
            */
            content.setText(sb.toString());
        }catch (Exception e){
            Log.v("brad", e.toString());
        }
    }


    //java原生
    public void test5(View view) { //普通java
        File file1 = new File(sdroot, "brad.ok");//file parent 在sdroot
        try {
            FileOutputStream fout =
                    new FileOutputStream(file1);
            fout.write("Hello, Brad000".getBytes());
            fout.flush();
            fout.close();
            Toast.makeText(this, "save ok", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Log.v("brad", e.toString());
        }
    }

    public void test6(View view) {
        File file1 = new File(approot, "brad.ok");//pkg與app共存亡,不占OS記憶體; game user刪除user仍要保留資料就不要放app
        try {
            FileOutputStream fout =
                    new FileOutputStream(file1);
            fout.write("Hello, Brad123".getBytes());
            fout.flush();
            fout.close();
            Toast.makeText(this, "save ok", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Log.v("brad", e.toString());
        }
    }

    //query
    public void test7(View view) {
        Cursor c = db.query("user", null, null,null,
                null,null,null);
        while(c.moveToNext()){
            //String data = c.getString(0);
            //Log.v("brad", data);

            String id = c.getString(0);
            String username = c.getString(1);
            String tel = c.getString(2);
            String birthday = c.getString(3);
            //Log.v("brad", id + ":" + username + ":" + tel + ":" + birthday);
            Log.v("brad", id + ":" + username + ":" + tel + ":" + birthday);
        }

    }

    public void test8(View view) {
        //String sql = "insert into user (username, tel, birthday) value ("aa","bb",'cc')"; //(此方法不好,sql injection) ;id自動遞增, 需要指底欄位給value
        //db.execute(sql);
        ContentValues values = new ContentValues();
        values.put("username", "brad");
        values.put("tel","1234567");
        values.put("birthday", 2000-01-02);
        db.insert("user", null, values);
        test7( null);
    }

    //delete
    public void test9(View view) {
        //delete from user where id=2 and username='brad'
        db.delete("user", "id = ? and username = ?", new String[]{"1","brad"});//1-1 也是為了避免隱碼攻擊; 沒有給where,會直接全砍
        test7(null);//query
    }

    //update
    public void test10(View view) {
        //update user set username='peter', tel='0912-123456' where id=4;
        ContentValues values = new ContentValues();
        values.put("username", "peter");
        values.put("tel","0912-123456");
        db.update("user", values, "id=?", new String[]{"10"}); //記得value要餵值, 否則會閃退
        test7(null);//query
    }
}
