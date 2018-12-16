package duti.com.pushmyorder.library;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static duti.com.pushmyorder.config.Constants.DATABASE_VERSION;
import static duti.com.pushmyorder.config.Constants.DB_NAME;

/**
 * Created by imrose on 6/16/2018.
 */

public class Db extends SQLiteOpenHelper {

    Context context;

    public Db(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void executeQuery(String sql) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
