package thinkfactory.android.app.mindpage.activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import thinkfactory.android.app.mindpage.R;
import thinkfactory.android.app.mindpage.db.manager.TopicManager;
import thinkfactory.android.app.mindpage.model.TopicDetails;
import thinkfactory.android.app.mindpage.util.CommonUtil;
import thinkfactory.android.app.mindpage.util.Constants;

/**
 * Created by Benjamin J on 16-08-2019.
 */
public class ContentActivity extends AppCompatActivity {
    private static final String TAG = ContentActivity.class.getSimpleName();
    private static final String THEME_LIGHT = "LightMode";
    private static final String THEME_DARK = "DarkMode";

    private EditText titleET;
    private EditText contentET;
    private TextView themeTV;
    private SearchView kbSearchview;
    private NestedScrollView scrollView;
    private LinearLayout searchLyout;
    private ImageView searchPrev;
    private ImageView searchNext;

    private MenuItem updateContent;
    private MenuItem contentMenu;
    private ContentActivity context;
    private TopicDetails topicDetails;

    private String topicSub;
    private String cntTitle;
    private String cntDesc;
    private String query;
    private boolean topicRefreshNeeded;
    private long topicId;
    private int curOccuranceIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        context = this;

        if (null == getIntent().getExtras()){
            Log.e(TAG, "onCreate: intent data is null" );
            finish();
        }

        topicSub = getIntent().getStringExtra(TopicListActivity.SUBJECT_NAME);

        topicId = getIntent().getLongExtra(TopicListActivity.TOPIC_ID, -1L);

        if (topicId == -1){
            Log.e(TAG, "onCreate: invalid topicDetails is" );
            finish();
        }

        setToolbar();
    }

    private void setToolbar(){
        Toolbar toolbar = findViewById(R.id.content_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(CommonUtil.checkIsEmpty(topicSub)?"TopicDetails":topicSub);
        Drawable navDrawable = getResources().getDrawable(R.drawable.ic_action_back_blk);
        toolbar.setNavigationIcon(navDrawable);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu: ");
        getMenuInflater().inflate(R.menu.content_toolbar_menu,menu);
        MenuItem kbSearch = menu.findItem(R.id.search_item);

        kbSearchview = (SearchView) kbSearch.getActionView();
        kbSearchview.setMaxWidth(Integer.MAX_VALUE);
        kbSearchview.setQueryHint("Search TopicDetails... ");
        
        contentMenu = menu.findItem(R.id.menu);
        updateContent = menu.findItem(R.id.save);
        initViews();
        return super.onCreateOptionsMenu(menu);
    }

    private void initViews(){
        Log.i(TAG, "initViews: ");
        titleET = findViewById(R.id.topic_title_et);
        contentET = findViewById(R.id.topic_content_et);
        scrollView = findViewById(R.id.content_sv);
        searchLyout = findViewById(R.id.search_lyout);
        searchPrev = findViewById(R.id.prev);
        searchNext = findViewById(R.id.next);

        setTheme();
        setContent();

//        searchPrev.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!CommonUtil.checkIsEmpty(occurances) && curOccuranceIndex >= 0){
//                    showoccurance(getPrevOccuranceIndex());
//                }
//            }
//        });
//
//        searchNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!CommonUtil.checkIsEmpty(occurances) && curOccuranceIndex >= 0){
//                    showoccurance(getNextOccuranceIndex());
//                }
//            }
//        });
    }

    private void searchText(String query){
        if (CommonUtil.checkIsEmpty(query)) {
            contentET.setText(Html.fromHtml(cntDesc));
            return;
        }
        String fullText = contentET.getText().toString();

        int occurance = fullText.indexOf(query,0);
        Spannable WordtoSpan = new SpannableString( fullText );

        for(int ofs = 0; ofs< fullText.length() && occurance !=-1; ofs= occurance +1) {
            occurance = fullText.indexOf(query,ofs);
            if(occurance == -1)
                break;
            else {
                WordtoSpan.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.orange_tr70)), occurance, occurance + query.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                WordtoSpan.setSpan(new StyleSpan(Typeface.BOLD), occurance, occurance + query.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        contentET.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
    }

//    private void searchText(String qry){
//        Log.i(TAG, "searchText: searching text...");
//        searchLyout.setVisibility(View.GONE);
//        contentET.setText(Html.fromHtml(cntDesc));
//        query = qry;
//        occurances.clear();
//        curOccuranceIndex = -1;
//        if (CommonUtil.checkIsEmpty(qry)){
//            Log.e(TAG, "searchText: qry is empty.." );
//            scrollView.scrollTo(0, 0);
//            return;
//        }
//        String fullText = contentET.getText().toString();
//        int occuranceIndex = fullText.indexOf(qry,0);
//        for(int ofs = 0; ofs< fullText.length() && occuranceIndex !=-1; ofs= occuranceIndex +1) {
//            occuranceIndex = fullText.indexOf(qry,ofs);
//            if(occuranceIndex == -1)
//                break;
//            occurances.add(occuranceIndex);
//        }
//        if (CommonUtil.checkIsEmpty(occurances))
//            return;
//        searchLyout.setVisibility(View.VISIBLE);
//        int index = getNextOccuranceIndex();
//        if (-1 != index)
//            showoccurance(index);
//    }
//
//    private int getNextOccuranceIndex(){
//        if (occurances.size() > curOccuranceIndex ) {
//            int index = occurances.get(curOccuranceIndex);
//            Log.i(TAG, "getNextOccuranceIndex: returning index: "+index);
//            return index;
//        }
//        return -1;
//    }
//
//    private int getPrevOccuranceIndex(){
//        if (occurances.size() > curOccuranceIndex && curOccuranceIndex > 0) {
//            curOccuranceIndex--;
//            int index = occurances.get(curOccuranceIndex);
//            Log.i(TAG, "getPrevOccuranceIndex: returning index: "+index);
//            return index;
//        }
//        return -1;
//    }
//
//    private void showoccurance(final int occuranceIndex){
//        if(occuranceIndex == -1)
//            return;
//        else {
//            contentET.setText(Html.fromHtml(cntDesc));
//            Log.i(TAG, "searchText: pos of word occuranceIndex: "+ occuranceIndex);
//            Spannable wordtoSpan = new SpannableString(Html.fromHtml(cntDesc));
//            wordtoSpan.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.orange_tr70)), occuranceIndex, occuranceIndex + query.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            wordtoSpan.setSpan(new StyleSpan(Typeface.BOLD), occuranceIndex, occuranceIndex + query.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            contentET.setText(wordtoSpan, TextView.BufferType.SPANNABLE);
//            contentET.post(new Runnable() {
//                @Override
//                public void run() {
//                    int line = contentET.getLayout().getLineForOffset(occuranceIndex);
//                    int y = contentET.getLayout().getLineTop(line);
//                    scrollView.scrollTo(0, y);
//                }
//            });
//        }
//    }

    private void setContent() {
        Log.i(TAG, "setContent: ");
       topicDetails = TopicManager.getInstance(context).getTopicDetails(topicId);
       if (null == topicDetails){
           Log.e(TAG, "setContent: fetched topicDetails object is null" );
           return;
       }
        Log.i(TAG, "setContent: topicdata: "+ topicDetails.getTitle()+"\n"+ topicDetails.getDesc());
       cntTitle = topicDetails.getTitle().replaceAll("\\n", "<br/>").replaceAll(" ", "&nbsp;");
       titleET.setText(Html.fromHtml(cntTitle));
       if (!CommonUtil.checkIsEmpty(topicDetails.getDesc())) {
           cntDesc = topicDetails.getDesc().replaceAll("\\n", "<br/>").replaceAll(" ", "&nbsp;");
           contentET.setText(Html.fromHtml(cntDesc));
       }else{
           editContent();
           Log.e(TAG, "setContent: topic desc is empty");
       }

        kbSearchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i(TAG, "onQueryTextChange: newText");
                searchText(newText);
                return false;
            }
        });
        kbSearchview.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchText("");
                return false;
            }
        });

        contentMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                showMenu();
                return false;
            }
        });

        updateContent.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                String updatedTitle = titleET.getText()+"";
                String updatedContent = contentET.getText()+"";
                if (CommonUtil.checkIsEmpty(updatedTitle) || CommonUtil.checkIsEmpty(updatedContent)){
                    CommonUtil.showToast("Title or content can't be empty!!!", context);
                }else {
                    if ((topicDetails.getTitle()+"").equals(updatedTitle) && (topicDetails.getDesc()+"").equals(updatedContent)) {
                        updateContent.setVisible(false);
                        contentET.setEnabled(false);
                        titleET.setEnabled(false);
                        return false;
                    }
                    TopicDetails updatedTopicDetails = new TopicDetails();
                    updatedTopicDetails.setUpdatedTime(Calendar.getInstance().getTimeInMillis());
                    updatedTopicDetails.setCreatedTime(topicDetails.getCreatedTime());
                    updatedTopicDetails.setSubId(topicDetails.getSubId());
                    updatedTopicDetails.setPriority(topicDetails.getPriority());
                    updatedTopicDetails.setHidden(topicDetails.isHidden());
                    updatedTopicDetails.setId(topicDetails.getId());
                    updatedTopicDetails.setTitle(titleET.getText().toString());
                    updatedTopicDetails.setDesc(contentET.getText().toString());
                    int status = TopicManager.getInstance(context).updateTopic(updatedTopicDetails);
                    if (status > 0){
                        CommonUtil.showToast("TopicDetails Updated successfully", context);
                        updateContent.setVisible(false);
                        contentET.setEnabled(false);
                        titleET.setEnabled(false);
                        topicRefreshNeeded = true;
                        setContent();
                    }else {
                        CommonUtil.showToast("TopicDetails Updation failed", context);
                        Log.e(TAG, "onMenuItemClick: topicDetails updation failes");
                    }
                }
                return false;
            }
        });
    }

    private void showMenu() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.topic_content_menu);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        dialog.findViewById(R.id.tv_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editContent();
                dialog.dismiss();
            }
        });

        themeTV = dialog.findViewById(R.id.tv_theme);
        themeTV.setText(CommonUtil.getTheme(context) == Constants.THEME_DARK ? THEME_LIGHT : THEME_DARK);
        themeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int theme = CommonUtil.getTheme(context);
                theme = theme == Constants.THEME_DARK ? Constants.THEME_LIGHT : Constants.THEME_DARK;
                CommonUtil.getSharedPreferenceEditor(context).putInt(Constants.DISPLAY_THEME, theme).commit();
                setTheme();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void editContent(){
        updateContent.setVisible(true);
        titleET.setEnabled(true);
        contentET.setEnabled(true);
    }

    public void setTheme(){
        int theme = CommonUtil.getTheme(context);
        if (null != themeTV)
            themeTV.setText(theme == Constants.THEME_DARK ? THEME_LIGHT : THEME_DARK);
        switch (theme){
            case Constants.THEME_LIGHT:
                titleET.setBackgroundColor(getResources().getColor(R.color.white));
                titleET.setTextColor(getResources().getColor(R.color.grey_m3));
                contentET.setBackgroundColor(getResources().getColor(R.color.white));
                contentET.setTextColor(getResources().getColor(R.color.black));
                break;
            default:
                titleET.setBackgroundColor(getResources().getColor(R.color.black_trans));
                titleET.setTextColor(getResources().getColor(R.color.grey_p5));
                contentET.setBackgroundColor(getResources().getColor(R.color.black_trans));
                contentET.setTextColor(getResources().getColor(R.color.white));
        }
    }

    @Override
    public void onBackPressed() {
        if (topicRefreshNeeded)
            setResult(Constants.IS_REFRESH_NEEDED);
        finish();
    }
}
