package thinkfactory.android.app.mindpage.db.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import thinkfactory.android.app.mindpage.db.helper.DbHelper;
import thinkfactory.android.app.mindpage.model.Textnote;
import thinkfactory.android.app.mindpage.model.TextnoteDetails;

/**
 * Created by Benjamin J on 18-08-2019.
 */
public class TextnoteManger extends DbHelper {
    private static final String TAG = TextnoteManger.class.getSimpleName();

    private static TextnoteManger instance;

    /*TABLE NAME*/
    public static final String TABLE_NOTE = "TEXTNOTE";

    /*COLUMNS*/
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "desc";
    public static final String COLUMN_IS_HIDDEN = "is_hidden";
    public static final String COLUMN_CREATED_TIME = "created_time";
    public static final String COLUMN_UPDATED_TIME = "updated_time";
    public static final String COLUMN_PRIORITY = "priority";

    /*CREATE TABLE QUERY*/
    public static final String CREATE_TABLE_NOTE = "CREATE TABLE " + TABLE_NOTE + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TITLE + " TEXT NOT NULL, "
            + COLUMN_DESCRIPTION + " TEXT, "
            + COLUMN_IS_HIDDEN + " INTEGER, "
            + COLUMN_CREATED_TIME + " BIGINT, "
            + COLUMN_UPDATED_TIME + " BIGINT, "
            + COLUMN_PRIORITY + " INTEGER"
            +")";

    public TextnoteManger(Context context) {
        super(context);
    }

    public TextnoteManger(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static TextnoteManger getInstance(Context context){
        if (null == instance)
            instance = new TextnoteManger(context);
        return instance;
    }

    public long addNote(TextnoteDetails textnoteDetails){
        Log.i(TAG, "addNote: adding textnoteDetails...  textnoteDetails data: " + (null == textnoteDetails ? "null" : textnoteDetails.getData()));
        try {
            if (null != textnoteDetails) {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_TITLE, textnoteDetails.getTitle());
                values.put(COLUMN_DESCRIPTION, textnoteDetails.getDesc());
                values.put(COLUMN_IS_HIDDEN, textnoteDetails.isHidden());
                values.put(COLUMN_CREATED_TIME, textnoteDetails.getCreatedTime());
                values.put(COLUMN_UPDATED_TIME, textnoteDetails.getUpdatedTime());
                values.put(COLUMN_PRIORITY, textnoteDetails.getPriority());

                return db.insert(TABLE_NOTE, null, values);
            }else
                Log.e(TAG, "addNote: textnoteDetails is null");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Textnote getNote(long noteId){
        Log.i(TAG, "getNote: geting note...  noteId: "+ noteId);
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT "+ COLUMN_ID+ COMMA+ COLUMN_TITLE+ COMMA+ COLUMN_IS_HIDDEN+ COMMA+ COLUMN_CREATED_TIME+ COMMA+
                    COLUMN_UPDATED_TIME+ COMMA+ COLUMN_PRIORITY+ " FROM " + TABLE_NOTE + " WHERE " + COLUMN_ID + " = " + noteId;
            Log.i(TAG, "getNote: getQuery: "+query);
            Cursor c = db.rawQuery(query, null);
            if (null != c) {
                c.moveToFirst();
                Textnote textnote = new Textnote();
                textnote.setId(c.getInt(c.getColumnIndex(COLUMN_ID)));
                textnote.setTitle(c.getString(c.getColumnIndex(COLUMN_TITLE)));
                boolean isHidden = 1 == c.getInt(c.getColumnIndex(COLUMN_IS_HIDDEN));
                textnote.setHidden(isHidden);
                textnote.setCreatedTime(c.getLong(c.getColumnIndex(COLUMN_CREATED_TIME)));
                textnote.setUpdatedTime(c.getLong(c.getColumnIndex(COLUMN_UPDATED_TIME)));
                textnote.setPriority(c.getInt(c.getColumnIndex(COLUMN_PRIORITY)));
                return textnote;
            }else
                Log.e(TAG, "getNote: cursor is null" );
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    public TextnoteDetails getNoteDetails(long noteId){
        Log.i(TAG, "getNoteDetails: geting note...  noteId: "+ noteId);
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_NOTE + " WHERE " + COLUMN_ID + " = " + noteId;
            Log.i(TAG, "getNoteDetails: getQuery: "+query);
            Cursor c = db.rawQuery(query, null);
            if (null != c) {
                c.moveToFirst();
                TextnoteDetails textnoteDetails = new TextnoteDetails();
                textnoteDetails.setId(c.getInt(c.getColumnIndex(COLUMN_ID)));
                textnoteDetails.setTitle(c.getString(c.getColumnIndex(COLUMN_TITLE)));
                textnoteDetails.setDesc(c.getString(c.getColumnIndex(COLUMN_DESCRIPTION)));
                boolean isHidden = 1 == c.getInt(c.getColumnIndex(COLUMN_IS_HIDDEN));
                textnoteDetails.setHidden(isHidden);
                textnoteDetails.setCreatedTime(c.getLong(c.getColumnIndex(COLUMN_CREATED_TIME)));
                textnoteDetails.setUpdatedTime(c.getLong(c.getColumnIndex(COLUMN_UPDATED_TIME)));
                textnoteDetails.setPriority(c.getInt(c.getColumnIndex(COLUMN_PRIORITY)));
                return textnoteDetails;
            }else
                Log.e(TAG, "getNoteDetails: cursor is null" );
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    public List<Textnote> getNoteList(){
        Log.i(TAG, "getNoteList: fecthing all notes... ");
        try {
            List<Textnote> noteList = new ArrayList<>();
            String selectQuery = "SELECT "+ COLUMN_ID+ " FROM " + TABLE_NOTE;
            Log.i(TAG, "getNoteList: selectQuery: "+selectQuery);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    Textnote note = getNote(c.getInt(c.getColumnIndex(COLUMN_ID)));
                    if (null != note){
                        noteList.add(note);
                        Log.i(TAG, "getNoteList: note "+ note.getTitle()+" added to filter list");
                    }else
                        Log.e(TAG, "getNoteList: note is null");
                } while (c.moveToNext());
                return noteList;
            }
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    public List<TextnoteDetails> getNoteDetailsList(){
        Log.i(TAG, "getNoteDetailsList: fecthing all notes... ");
        try {
            List<TextnoteDetails> noteList = new ArrayList<>();
            String selectQuery = "SELECT "+ COLUMN_ID+ " FROM " + TABLE_NOTE;
            Log.i(TAG, "getNoteDetailsList: selectQuery: "+selectQuery);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    TextnoteDetails note = getNoteDetails(c.getInt(c.getColumnIndex(COLUMN_ID)));
                    if (null != note){
                        noteList.add(note);
                        Log.i(TAG, "getNoteDetailsList: note "+ note.getTitle()+" added to filter list");
                    }else
                        Log.e(TAG, "getNoteDetailsList: note is null");
                } while (c.moveToNext());
                return noteList;
            }
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    public List<Textnote> getNoteList(boolean isHidden, String query){
        Log.i(TAG, "getNoteDetailsList: fecthing notes based on query...  query: "+query);
        try {
            List<Textnote> noteList = new ArrayList<>();
            String selectQuery = "SELECT "+ COLUMN_ID+ COMMA+ COLUMN_TITLE+ COMMA+ COLUMN_IS_HIDDEN+ " FROM " + TABLE_NOTE;
            Log.i(TAG, "getNoteDetailsList: selectQuery: "+selectQuery);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            if (null == query)
                query = "";
            if (c.moveToFirst()) {
                do {
                    String noteTitle = null != c.getString(c.getColumnIndex(COLUMN_TITLE)) ? (c.getString(c.getColumnIndex(COLUMN_TITLE))) : "";
                    if ((isHidden?TRUE:FALSE) == c.getInt(c.getColumnIndex(COLUMN_IS_HIDDEN)) && noteTitle.contains(query)) {
                        Textnote note = getNote(c.getInt(c.getColumnIndex(COLUMN_ID)));
                        if (null != note){
                            noteList.add(note);
                            Log.i(TAG, "getNoteDetailsList: note "+ note.getTitle()+" added to filter list");
                        }else
                            Log.e(TAG, "getNoteDetailsList: note is null, title: "+ noteTitle);
                    }
                } while (c.moveToNext());
                return noteList;
            }
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    public List<TextnoteDetails> getNoteDetailsList(boolean isHidden, String query){
        Log.i(TAG, "getNoteDetailsList: fecthing notes based on query...  query: "+query);
        try {
            List<TextnoteDetails> noteList = new ArrayList<>();
            String selectQuery = "SELECT "+ COLUMN_ID+ COMMA+ COLUMN_TITLE+ COMMA+ COLUMN_IS_HIDDEN+ " FROM " + TABLE_NOTE;
            Log.i(TAG, "getNoteDetailsList: selectQuery: "+selectQuery);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            if (null == query)
                query = "";
            if (c.moveToFirst()) {
                do {
                    String noteTitle = null != c.getString(c.getColumnIndex(COLUMN_TITLE)) ? (c.getString(c.getColumnIndex(COLUMN_TITLE))) : "";
                    if ((isHidden?TRUE:FALSE) == c.getInt(c.getColumnIndex(COLUMN_IS_HIDDEN)) && noteTitle.contains(query)) {
                        TextnoteDetails note = getNoteDetails(c.getInt(c.getColumnIndex(COLUMN_ID)));
                        if (null != note){
                            noteList.add(note);
                            Log.i(TAG, "getNoteDetailsList: note "+ note.getTitle()+" added to filter list");
                        }else
                            Log.e(TAG, "getNoteDetailsList: note is null, title: "+ noteTitle);
                    }
                } while (c.moveToNext());
                return noteList;
            }
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    public int updateNote(TextnoteDetails note) {
        Log.i(TAG, "updateNote: updating note... ");
        if (null != note) {
            Log.i(TAG, "updateNote: note update data: "+note.getData());
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, note.getId());
            values.put(COLUMN_TITLE, note.getTitle());
            values.put(COLUMN_DESCRIPTION, note.getDesc());
            values.put(COLUMN_IS_HIDDEN, note.isHidden());
            values.put(COLUMN_CREATED_TIME, note.getCreatedTime());
            values.put(COLUMN_UPDATED_TIME, note.getUpdatedTime());
            values.put(COLUMN_PRIORITY, note.getPriority());

            return db.update(TABLE_NOTE, values, COLUMN_ID + " = ?",
                    new String[]{String.valueOf(note.getId())});
        }
        return 0;
    }

    public int deleteNote(long noteId) {
        Log.i(TAG, "deleteNote: deleting note...  noteId: "+ noteId);
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete(TABLE_NOTE, COLUMN_ID + " = ?",
                    new String[] { String.valueOf(noteId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
