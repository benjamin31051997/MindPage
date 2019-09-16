package thinkfactory.android.app.mindpage.db.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import thinkfactory.android.app.mindpage.db.helper.DbHelper;
import thinkfactory.android.app.mindpage.model.Category;
import thinkfactory.android.app.mindpage.util.CommonUtil;

/**
 * Created by Benjamin J on 23-11-2018.
 */
public class CategoryManager extends DbHelper {
    private static final String TAG = CategoryManager.class.getSimpleName();

    private Context context;

    /*TABLE NAME*/
    public static final String TABLE_CATEGORY = "CATEGORY";

    /*COLUMNS*/
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SUBJECTS_ID_LIST = "sub_id_list";
    public static final String COLUMN_IS_HIDDEN = "is_hidden";
    public static final String COLUMN_CREATED_TIME = "created_time";
    public static final String COLUMN_UPDATED_TIME = "updated_time";
    public static final String COLUMN_PRIORITY = "priority";

    /*CREATE TABLE QUERY*/
    public static final String CREATE_TABLE_CATEGORY = "CREATE TABLE " + TABLE_CATEGORY + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_SUBJECTS_ID_LIST + " TEXT, "
            + COLUMN_IS_HIDDEN + " BOOLEAN, "
            + COLUMN_CREATED_TIME + " BIGINT, "
            + COLUMN_UPDATED_TIME + " BIGINT, "
            + COLUMN_PRIORITY + " INTEGER"
            +")";

    public CategoryManager(Context context) {
        super(context);
        this.context = context;
    }

    public CategoryManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static CategoryManager getInstance(Context context){
        return new CategoryManager(context);
    }

    public long addCategory(Category category){
        Log.i(TAG, "createCategory: adding category...  category data: " + (null == category ? "null" : category.getFields()));
        try {
            if (null != category) {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_NAME, category.getName());
                values.put(COLUMN_SUBJECTS_ID_LIST, (CommonUtil.checkIsEmpty(category.getSubjectIdList()) ? "" : category.getSubjectIdList().toString()));
                values.put(COLUMN_IS_HIDDEN, category.isHidden());
                values.put(COLUMN_CREATED_TIME, category.getCreatedTime());
                values.put(COLUMN_UPDATED_TIME, category.getUpdatedTime());
                values.put(COLUMN_PRIORITY, category.getPriority());

                return db.insert(TABLE_CATEGORY, null, values);
            }else
                Log.e(TAG, "createCategory: category is null");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Category getCategory(long catgryId){
        Log.i(TAG, "getCategory: fetching category...  categoryId: "+catgryId);
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_CATEGORY + " WHERE " + COLUMN_ID + " = " + catgryId;
            Log.i(TAG, "getCategory: getQuery: "+query);
            Cursor c = db.rawQuery(query, null);
            if (null != c) {
                c.moveToFirst();
                Category category = new Category();
                category.setId(c.getInt(c.getColumnIndex(COLUMN_ID)));
                category.setName(c.getString(c.getColumnIndex(COLUMN_NAME)));
                List<Long> list = CommonUtil.getListFromString(c.getString(c.getColumnIndex(COLUMN_SUBJECTS_ID_LIST)));
                if (null == list)
                    list = new ArrayList<>();
                category.setSubjectIdList(list);
                boolean isHidden = 1 == c.getInt(c.getColumnIndex(COLUMN_IS_HIDDEN));
                category.setHidden(isHidden);
                category.setCreatedTime(c.getLong(c.getColumnIndex(COLUMN_CREATED_TIME)));
                category.setUpdatedTime(c.getLong(c.getColumnIndex(COLUMN_UPDATED_TIME)));
                category.setPriority(c.getInt(c.getColumnIndex(COLUMN_PRIORITY)));
                return category;
            }else
                Log.e(TAG, "getCategory: cursor is null" );
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    public List<Category> fetchAllCategories(){
        Log.i(TAG, "fetchAllCategories: fecthing all categories... ");
        try {
            List<Category> categoryList = new ArrayList<Category>();
            String selectQuery = "SELECT "+ COLUMN_ID+ " FROM " + TABLE_CATEGORY;
            Log.i(TAG, "fetchAllCategories: selectQuery: "+selectQuery);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {
                do {
                    Category category = getCategory(c.getInt(c.getColumnIndex(COLUMN_ID)));
                    if (null != category) {
                        categoryList.add(category);
                        Log.i(TAG, "fetchAllCategories: category " + category.getName() + "added to list" );
                    }else
                        Log.e(TAG, "fetchAllCategories: category is  null, catgryId: " + c.getInt(c.getColumnIndex(COLUMN_ID)) );
                } while (c.moveToNext());
            }
            return categoryList;
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    public List<Category> fetchFilteredCategories(String query, boolean isHidden){
        Log.i(TAG, "fetchFilteredCategories: fecthing all categories... ");
        try {
            List<Category> filteredCategoryList = new ArrayList<Category>();
            String selectQuery = "SELECT "+ COLUMN_ID+ COMMA+ COLUMN_NAME+ COMMA+ COLUMN_IS_HIDDEN+ " FROM " + TABLE_CATEGORY;
            Log.i(TAG, "fetchFilteredCategories: selectQuery: "+selectQuery);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            if (null == query)
                query = "";
            if (c.moveToFirst()) {
                do {
                    String catgryName = null != c.getString(c.getColumnIndex(COLUMN_NAME)) ? (c.getString(c.getColumnIndex(COLUMN_NAME))) : "";
                    if (catgryName.contains(query) && (isHidden?TRUE:FALSE) == c.getInt(c.getColumnIndex(COLUMN_IS_HIDDEN))) {
                        Category category = getCategory(c.getInt(c.getColumnIndex(COLUMN_ID)));
                        if (null != category){
                            filteredCategoryList.add(category);
                            Log.i(TAG, "fetchFilteredCategories: category "+category.getName()+" added to filter list");
                        }else
                            Log.e(TAG, "fetchFilteredCategories: category null,  catgryname: "+catgryName);
                    }
                } while (c.moveToNext());
                return filteredCategoryList;
            }
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    public List<Category> fetchCategoriesByVisibility(boolean isHidden){
        Log.i(TAG, "fetchCategoriesByVisibility: fecthing visible categories... isHidden: "+isHidden);
        try {
            List<Category> filteredCategoryList = new ArrayList<Category>();
            String selectQuery = "SELECT "+ COLUMN_ID+ COMMA+ COLUMN_NAME+ COMMA+ COLUMN_IS_HIDDEN+ " FROM " + TABLE_CATEGORY;
            Log.i(TAG, "fetchCategoriesByVisibility: selectQuery: "+selectQuery);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    String catgryName = null != c.getString(c.getColumnIndex(COLUMN_NAME)) ? (c.getString(c.getColumnIndex(COLUMN_NAME))) : "";
                    if ((isHidden?TRUE:FALSE) == c.getInt(c.getColumnIndex(COLUMN_IS_HIDDEN))) {
                        Category category = getCategory(c.getInt(c.getColumnIndex(COLUMN_ID)));
                        if (null != category){
                            filteredCategoryList.add(category);
                            Log.i(TAG, "fetchCategoriesByVisibility: category "+category.getName()+" added to filter list");
                        }else
                            Log.e(TAG, "fetchCategoriesByVisibility: category null,  catgryname: "+catgryName);
                    }
                } while (c.moveToNext());
                return filteredCategoryList;
            }
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    /**returns number of columns affected*/
    public int updateCategory(Category category) {
        Log.i(TAG, "updateCategory: updating category... ");
        if (null != category) {
            Log.i(TAG, "updateCategory: category update data: "+category.getFields());
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
//            values.put(COLUMN_ID, category.getId());
            values.put(COLUMN_NAME, category.getName());
            values.put(COLUMN_SUBJECTS_ID_LIST, CommonUtil.checkIsEmpty(category.getSubjectIdList())  ? "" : category.getSubjectIdList().toString());
            values.put(COLUMN_IS_HIDDEN, category.isHidden());
            values.put(COLUMN_CREATED_TIME, category.getCreatedTime());
            values.put(COLUMN_UPDATED_TIME, category.getUpdatedTime());
            values.put(COLUMN_PRIORITY, category.getPriority());

            return db.update(TABLE_CATEGORY, values, COLUMN_ID + " = ?",
                    new String[]{String.valueOf(category.getId())});
        }
        return 0;
    }

    /**returns number of columns affected */
    public int deleteCategory(long categoryId) {
        Log.i(TAG, "deleteToDo: deleting category...  catgryId: "+categoryId);
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            SubjectManager.getInstance(context).deleteCategorySubjects(categoryId);
            return db.delete(TABLE_CATEGORY, COLUMN_ID + " = ?", new String[] {String.valueOf(categoryId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


}
