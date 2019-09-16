package thinkfactory.android.app.mindpage.activity;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.TextView;

import java.util.Calendar;

import thinkfactory.android.app.mindpage.R;
import thinkfactory.android.app.mindpage.db.manager.TextnoteManger;
import thinkfactory.android.app.mindpage.db.manager.TopicManager;
import thinkfactory.android.app.mindpage.model.TextnoteDetails;
import thinkfactory.android.app.mindpage.model.TopicDetails;
import thinkfactory.android.app.mindpage.util.CommonUtil;
import thinkfactory.android.app.mindpage.util.Constants;

/**
 * Created by Benjamin J on 08-09-2019.
 */
public class NoteContentActivity extends AppCompatActivity {
    private static final String TAG = NoteContentActivity.class.getSimpleName();

    private EditText contentET;
    private SearchView searchview;
    private MenuItem contentMenu;
    private MenuItem updateContent;

    private NoteContentActivity context;
    private Bundle bundle;
    private TextnoteDetails note;

    private int noteId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notecontent);

        context = this;

        bundle = getIntent().getBundleExtra(Constants.BUNDLE);
        if (null == bundle){
            Log.e(TAG, "onCreate: bundle is null " );
            finish();
        }

        noteId = bundle.getInt(NoteListActivity.NOTE_ID);

        note = TextnoteManger.getInstance(context).getNoteDetails(noteId);

        if (null == note){
            Log.e(TAG, "onCreate: note is null" );
            finish();
        }

        setToolbar();
    }

    private void setToolbar(){
        Toolbar toolbar = findViewById(R.id.content_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(CommonUtil.checkIsEmpty(note.getTitle()) ? "Content" : note.getTitle());
        Drawable navDrawable = getResources().getDrawable(R.drawable.ic_action_back_blk);
        toolbar.setNavigationIcon(navDrawable);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu: ");
        getMenuInflater().inflate(R.menu.content_toolbar_menu,menu);
        MenuItem kbSearch = menu.findItem(R.id.search_item);

        searchview = (SearchView) kbSearch.getActionView();
        searchview.setMaxWidth(Integer.MAX_VALUE);
        searchview.setQueryHint("Search TopicDetails... ");

        contentMenu = menu.findItem(R.id.menu);
        updateContent = menu.findItem(R.id.save);
        initViews();
        return super.onCreateOptionsMenu(menu);
    }

    private void initViews(){
        contentET = findViewById(R.id.note_content_et);

        if (CommonUtil.checkIsEmpty(note.getDesc())){
            Log.e(TAG, "initViews: note title is null" );
            editContent();
        }else{
            Log.i(TAG, "initViews: desc not empty");
            updateContent.setVisible(false);
            cntDesc = topicDetails.getDesc().replaceAll("\\n", "<br/>").replaceAll(" ", "&nbsp;");

        }

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

    private void populateViews(){
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        searchview.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchText("");
                return false;
            }
        });

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

    private void editContent(){
        updateContent.setVisible(true);
        contentET.setEnabled(true);
    }

}
