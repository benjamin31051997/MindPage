package thinkfactory.android.app.mindpage.db.helper;

/**
 * Created by Benjamin J on 21-11-2018.
 */
public class DbInfoHelper {

    /** DB */
    public static final String DB_NAME = "MINDPAGE.DB";
    public static final int DB_VERSION = 1;


    /** TABLE NAME*/
    public static final String TABLE_SUBJECT = "SUBJECT";


    /** COLUMNS */


    /*SUBJECT'S COLUMNS*/
    public static final String COLUMN_SUB_CATGRY_ID = "sub_catgry_id";
    public static final String COLUMN_SUB_ID = "sub_id";
    public static final String COLUMN_SUB_NAME = "sub_name";
    public static final String COLUMN_SUB_IS_HIDDEN = "sub_is_hidden";
    public static final String COLUMN_SUB_CREATED_TIME = "sub_created_time";
    public static final String COLUMN_SUB_TOPIC_IDS = "sub_topic_ids";


    /** TABLE CREATE QUERIES*/
    public static final String CREATE_TABLE_SUBJECT = "CREATE TABLE " + TABLE_SUBJECT + "("
            + COLUMN_SUB_CATGRY_ID + " INTEGER NOT NULL, "
            + COLUMN_SUB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_SUB_NAME + " TEXT NOT NULL, "
            + COLUMN_SUB_IS_HIDDEN + " INTEGER, "
            + COLUMN_SUB_CREATED_TIME + " DATETIME NOT NULL, "
            + COLUMN_SUB_TOPIC_IDS + " TEXT "
            + ")";







}
