package kr.co.skhu.schedule;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "scheduleDataBase";

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS ScheduleItem (id INTEGER PRIMARY KEY AUTOINCREMENT, checked INTEGER, title TEXT NOT NULL, content TEXT NOT NULL, dDay TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public ArrayList<ScheduleItem> getSchedule() {
        ArrayList<ScheduleItem> scheduleItems = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM ScheduleItem ORDER BY date DESC", null);

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int checked = cursor.getInt(cursor.getColumnIndex("checked"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String date = cursor.getString(cursor.getColumnIndex("date"));

                ScheduleItem scheduleItem = new ScheduleItem();
                scheduleItem.setId(id);
                scheduleItem.setTitle(title);
                scheduleItem.setContent(content);
                scheduleItem.setDate(date);
                scheduleItem.setChecked(checked);
            }
        }
        cursor.close();
        return scheduleItems;
    }

    //추가
    public void InsertSchedule(int _checked, String _title, String _content, String _date) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO TodoList (checked, title, content, date) VALUES('" + _checked + "','" + _title + "' , '" + _content + "' , '" + _date + "')");
    }

    //수정
    public void UpdateSchedule(int _checked, String _title, String _content, String _date, String _beforeDate){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE TodoList SET checked='" + _checked + "', title='" + _title + "', content='" + _content + "', dDay='" + _date + "' WHERE dDay='" + _beforeDate + "'");
    }

    //삭제
    public void DeleteSchedule(int _id){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM TodoList WHERE id = '" + _id + "'");
    }

}
