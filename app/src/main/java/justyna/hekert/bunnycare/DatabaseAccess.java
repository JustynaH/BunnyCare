package justyna.hekert.bunnycare;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseAccess {
     private SQLiteOpenHelper openHelper;
     private SQLiteDatabase database;
     private static DatabaseAccess instance;

     /**
       * Private constructor to aboid object creation from outside classes.
       *
       * @param context
       */
     private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
     }

     /**
       * Return a singleton instance of DatabaseAccess.
       *
       * @param context the Context
       * @return the instance of DabaseAccess
       */
     public static DatabaseAccess getInstance(Context context) {
     if (instance == null) {
        instance = new DatabaseAccess(context);
     }
     return instance;
     }

     /**
       * Open the database connection.
       */
     public void open() {
        this.database = openHelper.getWritableDatabase();
     }

     /**
       * Close the database connection.
       */
     public void close() {
        if (database != null) {
            this.database.close();
        }
     }


    public ArrayList<HashMap<String, String>> getInfoFrom(String table_name){
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM "+table_name+" ORDER BY name", null);
        //cursor.moveToFirst();
        //while (!cursor.isAfterLast())
        while (cursor.moveToNext()){
            HashMap<String,String> info = new HashMap<>();
            info.put("name",cursor.getString(cursor.getColumnIndex("name")));
            info.put("parts", cursor.getString(cursor.getColumnIndex("parts")));
            info.put("comments",cursor.getString(cursor.getColumnIndex("comments")));
            list.add(info);
        }
        cursor.close();
        return  list;
    }
}