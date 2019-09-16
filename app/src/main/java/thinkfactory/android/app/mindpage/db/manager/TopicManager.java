package thinkfactory.android.app.mindpage.db.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import thinkfactory.android.app.mindpage.db.helper.DbHelper;
import thinkfactory.android.app.mindpage.model.Topic;
import thinkfactory.android.app.mindpage.model.TopicDetails;

/**
 * Created by Benjamin J on 25-11-2018.
 */
public class TopicManager extends DbHelper {
    private static final String TAG = TopicManager.class.getSimpleName();

    private static TopicManager instance;

    /*TABLE NAME*/
    public static final String TABLE_TOPIC = "TOPIC";

    /*COLUMNS*/
    public static final String COLUMN_SUBJECT_ID = "sub_id";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "desc";
    public static final String COLUMN_IS_HIDDEN = "is_hidden";
    public static final String COLUMN_CREATED_TIME = "created_time";
    public static final String COLUMN_UPDATED_TIME = "updated_time";
    public static final String COLUMN_PRIORITY = "priority";

    /*CREATE TABLE QUERY*/
    public static final String CREATE_TABLE_TOPIC = "CREATE TABLE " + TABLE_TOPIC + "("
            + COLUMN_SUBJECT_ID + " INTEGER NOT NULL, "
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TITLE + " TEXT NOT NULL, "
            + COLUMN_DESCRIPTION + " TEXT, "
            + COLUMN_IS_HIDDEN + " INTEGER, "
            + COLUMN_CREATED_TIME + " BIGINT, "
            + COLUMN_UPDATED_TIME + " BIGINT, "
            + COLUMN_PRIORITY + " INTEGER"
            +")";

    public TopicManager(Context context) {
        super(context);
    }

    public TopicManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static TopicManager getInstance(Context context){
        if (null == instance)
            instance = new TopicManager(context);
        return instance;
    }

    public long addTopic(TopicDetails topicDetails){
        Log.i(TAG, "addTopic: adding topicDetails...  topicDetails data: " + (null == topicDetails ? "null" : topicDetails.getData()));
        try {
            if (null != topicDetails) {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_SUBJECT_ID, topicDetails.getSubId());
                values.put(COLUMN_TITLE, topicDetails.getTitle());
                values.put(COLUMN_DESCRIPTION, topicDetails.getDesc());
                values.put(COLUMN_IS_HIDDEN, topicDetails.isHidden());
                values.put(COLUMN_CREATED_TIME, topicDetails.getCreatedTime());
                values.put(COLUMN_UPDATED_TIME, topicDetails.getUpdatedTime());
                values.put(COLUMN_PRIORITY, topicDetails.getPriority());

                return db.insert(TABLE_TOPIC, null, values);
            }else
                Log.e(TAG, "addTopic: topicDetails is null");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Topic getTopic(long topicId){
        Log.i(TAG, "getTopic: fetching topic...  topicId: "+ topicId);
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT "+COLUMN_SUBJECT_ID+ COMMA+ COLUMN_ID+ COMMA+ COLUMN_TITLE+ COMMA+ COLUMN_IS_HIDDEN+ COMMA+ COLUMN_CREATED_TIME+ COMMA+
                    COLUMN_UPDATED_TIME+ COMMA+ COLUMN_PRIORITY+ " FROM " + TABLE_TOPIC + " WHERE " + COLUMN_ID + " = " + topicId;
            Log.i(TAG, "getTopic: getQuery: "+query);
            Cursor c = db.rawQuery(query, null);
            if (null != c) {
                c.moveToFirst();
                Topic topic = new Topic();
                topic.setSubId(c.getInt(c.getColumnIndex(COLUMN_SUBJECT_ID)));
                topic.setId(c.getInt(c.getColumnIndex(COLUMN_ID)));
                topic.setTitle(c.getString(c.getColumnIndex(COLUMN_TITLE)));
                boolean isHidden = 1 == c.getInt(c.getColumnIndex(COLUMN_IS_HIDDEN));
                topic.setHidden(isHidden);
                topic.setCreatedTime(c.getLong(c.getColumnIndex(COLUMN_CREATED_TIME)));
                topic.setUpdatedTime(c.getLong(c.getColumnIndex(COLUMN_UPDATED_TIME)));
                topic.setPriority(c.getInt(c.getColumnIndex(COLUMN_PRIORITY)));
                return topic;
            }else
                Log.e(TAG, "getTopicDetails: cursor is null" );
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    public TopicDetails getTopicDetails(long topicId){
        Log.i(TAG, "getTopicDetails: fetching topic...  topicId: "+ topicId);
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_TOPIC + " WHERE " + COLUMN_ID + " = " + topicId;
            Log.i(TAG, "getTopicDetails: getQuery: "+query);
            Cursor c = db.rawQuery(query, null);
            if (null != c) {
                c.moveToFirst();
                TopicDetails topicDetails = new TopicDetails();
                topicDetails.setSubId(c.getInt(c.getColumnIndex(COLUMN_SUBJECT_ID)));
                topicDetails.setId(c.getInt(c.getColumnIndex(COLUMN_ID)));
                topicDetails.setTitle(c.getString(c.getColumnIndex(COLUMN_TITLE)));
                topicDetails.setDesc(c.getString(c.getColumnIndex(COLUMN_DESCRIPTION)));
                boolean isHidden = 1 == c.getInt(c.getColumnIndex(COLUMN_IS_HIDDEN));
                topicDetails.setHidden(isHidden);
                topicDetails.setCreatedTime(c.getLong(c.getColumnIndex(COLUMN_CREATED_TIME)));
                topicDetails.setUpdatedTime(c.getLong(c.getColumnIndex(COLUMN_UPDATED_TIME)));
                topicDetails.setPriority(c.getInt(c.getColumnIndex(COLUMN_PRIORITY)));
                return topicDetails;
            }else
                Log.e(TAG, "getTopicDetails: cursor is null" );
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    public List<Topic> fetchTopicList(){
        Log.i(TAG, "fetchTopicList: fetching all topics...");
        try {
            List<Topic> topicList = new ArrayList<>();
            String selectQuery = "SELECT "+COLUMN_ID+" FROM " + TABLE_TOPIC;
            Log.i(TAG, "fetchTopicList: selectQuery: "+selectQuery);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {
                do {
                    Topic topic = getTopic(c.getInt(c.getColumnIndex(COLUMN_ID)));
                    if (null != topic) {
                        topicList.add(topic);
                        Log.i(TAG, "fetchTopicDetailsList: topic " + topic.getTitle() + "added to list" );
                    }else
                        Log.e(TAG, "fetchTopicDetailsList: topic is null, topicId: " + c.getInt(c.getColumnIndex(COLUMN_ID)) );
                } while (c.moveToNext());
            }
            return topicList;
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    public List<TopicDetails> fetchTopicDetailsList(){
        Log.i(TAG, "fetchTopicDetailsList: fetching all categories... ");
        try {
            List<TopicDetails> topicDetailsList = new ArrayList<>();
            String selectQuery = "SELECT "+COLUMN_ID+" FROM " + TABLE_TOPIC;
            Log.i(TAG, "fetchTopicDetailsList: selectQuery: "+selectQuery);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {
                do {
                    TopicDetails topicDetails = getTopicDetails(c.getInt(c.getColumnIndex(COLUMN_ID)));
                    if (null != topicDetails) {
                        topicDetailsList.add(topicDetails);
                        Log.i(TAG, "fetchTopicDetailsList: topicDetails " + topicDetails.getTitle() + "added to list" );
                    }else
                        Log.e(TAG, "fetchTopicDetailsList: topicDetails is null, topicId: " + c.getInt(c.getColumnIndex(COLUMN_ID)) );
                } while (c.moveToNext());
            }
            return topicDetailsList;
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    public List<TopicDetails> fetchSubjectTopicDetailsList(int subId){
        Log.i(TAG, "fetchTopicDetailsList: fetching all categories... ");
        try {
            List<TopicDetails> topicDetailsList = new ArrayList<>();
            String selectQuery = "SELECT "+COLUMN_ID+" FROM " + TABLE_TOPIC + " WHERE " + COLUMN_SUBJECT_ID + " = "+subId;
            Log.i(TAG, "fetchTopicDetailsList: selectQuery: "+selectQuery);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {
                do {
                    TopicDetails topicDetails = getTopicDetails(c.getInt(c.getColumnIndex(COLUMN_ID)));
                    if (null != topicDetails) {
                        topicDetailsList.add(topicDetails);
                        Log.i(TAG, "fetchTopicDetailsList: topicDetails " + topicDetails.getTitle() + "added to list" );
                    }else
                        Log.e(TAG, "fetchTopicDetailsList: topicDetails is null, topicId: " + c.getInt(c.getColumnIndex(COLUMN_ID)) );
                } while (c.moveToNext());
            }
            return topicDetailsList;
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    public List<TopicDetails> fetchTopicDetailsList(int subId, boolean isHidden){
        Log.i(TAG, "fetchTopicDetailsList: fetching all categories... ");
        try {
            List<TopicDetails> topicDetailsList = new ArrayList<>();
            String selectQuery = "SELECT "+ COLUMN_ID+ COMMA+ COLUMN_IS_HIDDEN+ " FROM " + TABLE_TOPIC + " WHERE " + COLUMN_SUBJECT_ID + " = "+subId;
            Log.i(TAG, "fetchTopicDetailsList: selectQuery: "+selectQuery);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {
                do {
                    TopicDetails topicDetails = getTopicDetails(c.getInt(c.getColumnIndex(COLUMN_ID)));
                    if (null != topicDetails) {
                        if ((isHidden?TRUE:FALSE) == c.getInt(c.getColumnIndex(COLUMN_IS_HIDDEN))) {
                            topicDetailsList.add(topicDetails);
                            Log.i(TAG, "fetchTopicDetailsList: topicDetails " + topicDetails.getTitle() + "added to list");
                        }else
                            Log.e(TAG, "fetchTopicDetailsList: visibility not true" );
                    }else
                        Log.e(TAG, "fetchTopicDetailsList: topicDetails is null, topicId: " + c.getInt(c.getColumnIndex(COLUMN_ID)) );
                } while (c.moveToNext());
            }
            return topicDetailsList;
        }catch (Exception e){e.printStackTrace();}
        return null;
    }



    public List<TopicDetails> fetchFilteredTopicDetailsList(int subId, boolean isHidden, String query){
        Log.i(TAG, "fetchFilteredTopicDetailsList: fecthing topics based on query...  query: "+query);
        try {
            List<TopicDetails> filteredSubjectList = new ArrayList<>();
            String selectQuery = "SELECT "+ COLUMN_ID+ COMMA+ COLUMN_TITLE+ COMMA+ COLUMN_IS_HIDDEN+ " FROM " + TABLE_TOPIC + " WHERE " + COLUMN_SUBJECT_ID + " = "+subId;
            Log.i(TAG, "fetchFilteredTopicDetailsList: selectQuery: "+selectQuery);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            if (null == query)
                query = "";
            if (c.moveToFirst()) {
                do {
                    String subjectName = null != c.getString(c.getColumnIndex(COLUMN_TITLE)) ? (c.getString(c.getColumnIndex(COLUMN_TITLE))) : "";
                    if ((isHidden?TRUE:FALSE) == c.getInt(c.getColumnIndex(COLUMN_IS_HIDDEN)) && subjectName.contains(query)) {
                        TopicDetails category = getTopicDetails(c.getInt(c.getColumnIndex(COLUMN_ID)));
                        if (null != category){
                            filteredSubjectList.add(category);
                            Log.i(TAG, "fetchFilteredTopicDetailsList: topic "+category.getTitle()+" added to filter list");
                        }else
                            Log.e(TAG, "fetchFilteredTopicDetailsList: topic is null, topicName: "+ subjectName);
                    }
                } while (c.moveToNext());
                return filteredSubjectList;
            }
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    /**returns number of columns affected*/
    public int updateTopic(TopicDetails topicDetails) {
        Log.i(TAG, "updateSubject: updating topicDetails... ");
        if (null != topicDetails) {
            Log.i(TAG, "updateSubject: topicDetails update data: "+ topicDetails.getData());
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_SUBJECT_ID, topicDetails.getSubId());
            values.put(COLUMN_ID, topicDetails.getId());
            values.put(COLUMN_TITLE, topicDetails.getTitle());
            values.put(COLUMN_DESCRIPTION, topicDetails.getDesc());
            values.put(COLUMN_IS_HIDDEN, topicDetails.isHidden());
            values.put(COLUMN_CREATED_TIME, topicDetails.getCreatedTime());
            values.put(COLUMN_UPDATED_TIME, topicDetails.getUpdatedTime());
            values.put(COLUMN_PRIORITY, topicDetails.getPriority());

            return db.update(TABLE_TOPIC, values, COLUMN_ID + " = ?",
                    new String[]{String.valueOf(topicDetails.getId())});
        }
        return 0;
    }

    /**returns number of columns affected */
    public int deleteTopic(long topicId) {
        Log.i(TAG, "deleteTopic: deleting topic...  topicId: "+ topicId);
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete(TABLE_TOPIC, COLUMN_ID + " = ?",
                    new String[] { String.valueOf(topicId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void deleteSubjectTopics(long subjectId){
        Log.i(TAG, "deleteCategorySubjects: deleting topics of subject... subjectId: "+subjectId);
        List<TopicDetails> topicDetailsList = fetchTopicDetailsList();
        if (null != topicDetailsList && !topicDetailsList.isEmpty()) {
            Log.i(TAG, "onClick: deltopics: "+ topicDetailsList.size());
            for (TopicDetails topicDetails : topicDetailsList) {
                if (subjectId == topicDetails.getSubId())
                    deleteTopic(topicDetails.getId());
            }
            Log.i(TAG, "onClick: delTopics after"+ fetchTopicDetailsList());
        }else
            Log.e(TAG, "onClick: no subjects under category: " );
    }

}
