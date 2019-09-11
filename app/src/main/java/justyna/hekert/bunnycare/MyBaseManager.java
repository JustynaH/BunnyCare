package justyna.hekert.bunnycare;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyBaseManager extends SQLiteOpenHelper {

    public MyBaseManager(Context context) {
        super(context, "profile.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table profile(" +
                        "position integer primary key autoincrement," +
                        "name text," +
                        "date text," +
                        "weight real," +
                        "sex text," +
                        "type text," +
                        "speChar text," +
                        "picPath text);" +
                        "");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long addProfile(String name,String date, Double weight, String sex, String type, String speChar, String picPath){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues my_values = new ContentValues();
        my_values.put("name", name);
        my_values.put("date",date);
        my_values.put("weight", weight);
        my_values.put("sex",sex);
        my_values.put("type",type);
        my_values.put("speChar",speChar);
        my_values.put("picPath",picPath);

        return db.insertOrThrow("profile",null, my_values);
    }

    public Cursor giveAll(){
        String[] col={"position", "name", "date", "weight", "sex", "type", "speChar", "picPath"};
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("profile",col,null,null,null,null,null);
        return cursor;
    }

    public void modifyProfile(long position, String name,String date, Double weight, String sex, String type, String speChar, String picPath){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues myvalues = new ContentValues();
        myvalues.put("name", name);
        myvalues.put("date",date);
        myvalues.put("weight", weight);
        myvalues.put("sex",sex);
        myvalues.put("type",type);
        myvalues.put("speChar",speChar);
        myvalues.put("picPath",picPath);
        String args[]={position+""};
        db.update("profile", myvalues,"position=?",args);
    }

    public void deleteProfile(long id){
        SQLiteDatabase db = getWritableDatabase();
        String[] arg = {""+id};
        db.delete("profile", "position=?", arg);
    }
}
