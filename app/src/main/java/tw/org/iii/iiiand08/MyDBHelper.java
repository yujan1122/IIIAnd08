package tw.org.iii.iiiand08;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import  android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDBHelper extends SQLiteOpenHelper{
    private static final String createTabel =
            "CREATE TABLE user (id INTEGER PRIMARY KEY AUTOINCREMENT"+
        ", username TEXT, tel TEXT, birthday DATE)"; //SQL關鍵字沒有大小寫;自訂庫名 欄位名有區分

    public MyDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version); //int有10位數,數字大當作新版
        //創表字串


    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTabel);//從這邊創表出來,輔助建立所需要的庫,表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
