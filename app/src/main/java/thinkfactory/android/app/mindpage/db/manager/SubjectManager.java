package thinkfactory.android.app.mindpage.db.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import thinkfactory.android.app.mindpage.db.helper.DbHelper;
import thinkfactory.android.app.mindpage.model.Subject;
import thinkfactory.android.app.mindpage.util.CommonUtil;

/**
 * Created by Benjamin J on 25-11-2018.
 */
public class SubjectManager extends DbHelper {
    private static final String TAG = SubjectManager.class.getSimpleName();

    private Context context;

    /*TABLE NAME*/
    public static final String TABLE_SUBJECT = "SUBJECT";

    /*COLUMNS*/
    public static final String COLUMN_CATGRY_ID = "catgry_id";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TOPICS_ID_LIST = "topics_id_list";
    public static final String COLUMN_IS_HIDDEN = "is_hidden";
    public static final String COLUMN_CREATED_TIME = "created_time";
    public static final String COLUMN_UPDATED_TIME = "updated_time";
    public static final String COLUMN_PRIORITY = "priority";

    /*CREATE TABLE QUERY*/
    public static final String CREATE_TABLE_SUBJECT = "CREATE TABLE " + TABLE_SUBJECT + "("
            + COLUMN_CATGRY_ID + " INTEGER NOT NULL, "
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_TOPICS_ID_LIST + " TEXT, "
            + COLUMN_IS_HIDDEN + " INTEGER, "
            + COLUMN_CREATED_TIME + " BIGINT, "
            + COLUMN_UPDATED_TIME + " BIGINT, "
            + COLUMN_PRIORITY + " INTEGER"
            +")";

    public SubjectManager(Context context) {
        super(context);
        this.context = context;
    }

    public SubjectManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static SubjectManager getInstance(Context context){
        return new SubjectManager(context);
    }

    public long addSubject(Subject subject){
        Log.i(TAG, "addSubject: adding subject...  subject data: " + (null == subject ? "null" : subject.getFields()));
        try {
            if (null != subject) {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_CATGRY_ID, subject.getCatgryId());
                values.put(COLUMN_NAME, subject.getName());
                values.put(COLUMN_TOPICS_ID_LIST, (CommonUtil.checkIsEmpty(subject.getTopicIdList()) ? "" :subject.getTopicIdList().toString()));
                values.put(COLUMN_IS_HIDDEN, subject.isHidden());
                values.put(COLUMN_CREATED_TIME, subject.getCreatedTime());
                values.put(COLUMN_UPDATED_TIME, subject.getUpdatedTime());
                values.put(COLUMN_PRIORITY, subject.getPriority());

                return db.insert(TABLE_SUBJECT, null, values);
            }else
                Log.e(TAG, "addSubject: subject is null");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Subject getSubject(long subId){
        Log.i(TAG, "getSubject: fetching subject...  subjectId: "+ subId);
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_SUBJECT + " WHERE " + COLUMN_ID + " = " + subId;
            Log.i(TAG, "getSubject: getQuery: "+query);
            Cursor c = db.rawQuery(query, null);
            if (null != c) {
                c.moveToFirst();
                Subject category = new Subject();
                category.setCatgryId(c.getInt(c.getColumnIndex(COLUMN_CATGRY_ID)));
                category.setId(c.getInt(c.getColumnIndex(COLUMN_ID)));
                category.setName(c.getString(c.getColumnIndex(COLUMN_NAME)));
                List<Long> list = CommonUtil.getListFromString(c.getString(c.getColumnIndex(COLUMN_TOPICS_ID_LIST)));
                category.setTopicIdList(list);
                boolean isHidden = 1 == c.getInt(c.getColumnIndex(COLUMN_IS_HIDDEN));
                category.setHidden(isHidden);
                category.setCreatedTime(c.getLong(c.getColumnIndex(COLUMN_CREATED_TIME)));
                category.setUpdatedTime(c.getLong(c.getColumnIndex(COLUMN_UPDATED_TIME)));
                category.setPriority(c.getInt(c.getColumnIndex(COLUMN_PRIORITY)));
                return category;
            }else
                Log.e(TAG, "getSubject: cursor is null" );
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    public List<Subject> fetchAllSubjects(){
        Log.i(TAG, "fetchAllSubjects: fetching all categories... ");
        try {
            List<Subject> subjectList = new ArrayList<>();
            String selectQuery = "SELECT "+ COLUMN_ID+ " FROM " + TABLE_SUBJECT;
            Log.i(TAG, "fetchAllSubjects: selectQuery: "+selectQuery);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {
                do {
                    Subject subject = getSubject(c.getInt(c.getColumnIndex(COLUMN_ID)));
                    if (null != subject) {
                        subjectList.add(subject);
                        Log.i(TAG, "fetchAllSubjects: subject " + subject.getName() + "added to list" );
                    }else
                        Log.e(TAG, "fetchAllSubjects: subject is null, catgryId: " + c.getInt(c.getColumnIndex(COLUMN_ID)) );
                } while (c.moveToNext());
            }
            return subjectList;
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    public List<Subject> fetchFilteredSubjects(int catgryId, boolean isHidden, String query){
        Log.i(TAG, "fetchFilteredSubjects: fetching subjects based on query...  query: "+query);
        try {
            List<Subject> filteredSubjectList = new ArrayList<>();
            String selectQuery = "SELECT "+ COLUMN_ID+ COMMA+ COLUMN_CATGRY_ID+ COMMA+ COLUMN_NAME+ COMMA+ COLUMN_IS_HIDDEN+ " FROM " + TABLE_SUBJECT;
            Log.i(TAG, "fetchFilteredSubjects: selectQuery: "+selectQuery);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            if (null == query)
                query = "";
            if (c.moveToFirst()) {
                do {
                    int subCatgryId = c.getInt(c.getColumnIndex(COLUMN_CATGRY_ID));
                    String subjectName = null != c.getString(c.getColumnIndex(COLUMN_NAME)) ? (c.getString(c.getColumnIndex(COLUMN_NAME))) : "";
                    if (subCatgryId == catgryId && (isHidden?TRUE:FALSE) == c.getInt(c.getColumnIndex(COLUMN_IS_HIDDEN)) && subjectName.contains(query)) {
                        Subject category = getSubject(c.getInt(c.getColumnIndex(COLUMN_ID)));
                        if (null != category){
                            filteredSubjectList.add(category);
                            Log.i(TAG, "fetchFilteredSubjects: subject "+category.getName()+" added to filter list");
                        }else
                            Log.e(TAG, "fetchFilteredSubjects: subject null, subjectName: "+ subjectName);
                    }
                } while (c.moveToNext());
                return filteredSubjectList;
            }
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    public List<Subject> fetchSubjectsForCategory(int catgryId, boolean isHidden){
        Log.i(TAG, "fetchSubjectsForCategory: fetching subjects based on catgryId...  catgryId: "+catgryId);
        try {
            List<Subject> filteredSubjectList = new ArrayList<>();
            String selectQuery = "SELECT "+ COLUMN_ID+ COMMA+ COLUMN_CATGRY_ID+ COMMA+ COLUMN_IS_HIDDEN+ " FROM " + TABLE_SUBJECT;
            Log.i(TAG, "fetchSubjectsForCategory: selectQuery: "+selectQuery);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    int subCatgryId = c.getInt(c.getColumnIndex(COLUMN_CATGRY_ID));
                    if (subCatgryId == catgryId) {
                        Subject sub = getSubject(c.getInt(c.getColumnIndex(COLUMN_ID)));
                        if (null != sub){
                            if ((isHidden?TRUE:FALSE) == c.getInt(c.getColumnIndex(COLUMN_IS_HIDDEN))) {
                                filteredSubjectList.add(sub);
                                Log.i(TAG, "fetchSubjectsForCategory: subject " + sub.getName() + " added to filter list");
                            }else
                                Log.e(TAG, "fetchSubjectsForCategory: visibililty not true");
                        }else
                            Log.e(TAG, "fetchSubjectsForCategory: subject null, subCatgryId: "+ subCatgryId);
                    }
                } while (c.moveToNext());
                return filteredSubjectList;
            }
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    public List<Subject> fetchCategorySubjects(int catgryId){
        Log.i(TAG, "fetchSubjectsForCategory: fetching subjects based on catgryId...  catgryId: "+catgryId);
        try {
            List<Subject> filteredSubjectList = new ArrayList<>();
            String selectQuery = "SELECT "+ COLUMN_ID+ COMMA+ COLUMN_CATGRY_ID+ " FROM " + TABLE_SUBJECT;
            Log.i(TAG, "fetchSubjectsForCategory: selectQuery: "+selectQuery);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    int subCatgryId = c.getInt(c.getColumnIndex(COLUMN_CATGRY_ID));
                    if (subCatgryId == catgryId) {
                        Subject sub = getSubject(c.getInt(c.getColumnIndex(COLUMN_ID)));
                        if (null != sub){
                            filteredSubjectList.add(sub);
                            Log.i(TAG, "fetchSubjectsForCategory: subject " + sub.getName() + " added to sub list");
                        }else
                            Log.e(TAG, "fetchSubjectsForCategory: subject null, subCatgryId: "+ subCatgryId);
                    }
                } while (c.moveToNext());
                return filteredSubjectList;
            }
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    /**returns number of columns affected*/
    public int updateSubject(Subject subject) {
        Log.i(TAG, "updateSubject: updating subject... ");
        if (null != subject) {
            Log.i(TAG, "updateSubject: subject update data: "+subject.getFields());
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_CATGRY_ID, subject.getCatgryId());
            values.put(COLUMN_ID, subject.getId());
            values.put(COLUMN_NAME, subject.getName());
            values.put(COLUMN_TOPICS_ID_LIST, CommonUtil.checkIsEmpty(subject.getTopicIdList())  ? "" : subject.getTopicIdList().toString());
            values.put(COLUMN_IS_HIDDEN, subject.isHidden());
            values.put(COLUMN_CREATED_TIME, subject.getCreatedTime());
            values.put(COLUMN_UPDATED_TIME, subject.getUpdatedTime());
            values.put(COLUMN_PRIORITY, subject.getPriority());

            return db.update(TABLE_SUBJECT, values, COLUMN_ID + " = ?",
                    new String[]{String.valueOf(subject.getId())});
        }
        return 0;
    }

    /**returns number of columns affected */
    public int deleteSubject(long subjectId) {
        Log.i(TAG, "deleteSubject: deleting subject...  subjectId: "+ subjectId);
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            TopicManager.getInstance(context).deleteSubjectTopics(subjectId);
            return db.delete(TABLE_SUBJECT, COLUMN_ID + " = ?",
                    new String[] { subjectId+"" });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void deleteCategorySubjects(long categoryId){
        Log.i(TAG, "deleteCategorySubjects: delseting subjects of category... categoryId: "+categoryId);
        List<Subject> subList = fetchAllSubjects();
        if (CommonUtil.checkIsEmpty(subList)) {
            Log.i(TAG, "onClick: delsubs"+fetchAllSubjects().size());
            for (Subject subject : subList) {
                if (categoryId == subject.getCatgryId())
                    deleteSubject(subject.getId());
            }
            Log.i(TAG, "onClick: delsubs after"+fetchAllSubjects());
        }else
            Log.e(TAG, "onClick: no subjects under category: " );
    }

}
