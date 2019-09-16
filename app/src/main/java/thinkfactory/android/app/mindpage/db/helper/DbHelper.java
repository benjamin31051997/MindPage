package thinkfactory.android.app.mindpage.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import thinkfactory.android.app.mindpage.db.manager.CategoryManager;
import thinkfactory.android.app.mindpage.db.manager.SubjectManager;
import thinkfactory.android.app.mindpage.db.manager.TextnoteManger;
import thinkfactory.android.app.mindpage.db.manager.TopicManager;


/**
 * Created by Benjamin J on 30-09-2018.
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = DbHelper.class.getSimpleName();
    public static final String COMMA = ", ";

    public static final int FALSE = 0;
    public static final int TRUE = 1;


    public DbHelper(Context context) {
        super(context, DbInfoHelper.DB_NAME, null, DbInfoHelper.DB_VERSION);
    }

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CategoryManager.CREATE_TABLE_CATEGORY);
        db.execSQL(SubjectManager.CREATE_TABLE_SUBJECT);
        db.execSQL(TopicManager.CREATE_TABLE_TOPIC);
        db.execSQL(TextnoteManger.CREATE_TABLE_NOTE);
        Log.i(TAG, "onCreate: tables created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CategoryManager.TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + DbInfoHelper.TABLE_SUBJECT);
        Log.i(TAG, "onUpgrade: tables deleted.");
        onCreate(db);
    }
}
