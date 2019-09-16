package thinkfactory.android.app.mindpage.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import thinkfactory.android.app.mindpage.R;
import thinkfactory.android.app.mindpage.db.manager.TopicManager;
import thinkfactory.android.app.mindpage.model.TopicDetails;
import thinkfactory.android.app.mindpage.util.CommonUtil;
import thinkfactory.android.app.mindpage.util.Constants;

/**
 * Created by Benjamin J on 13-07-2019.
 */
public class TopicListActivity extends AppCompatActivity{
    private static final String TAG = TopicListActivity.class.getSimpleName();
    public static final String TOPIC_ID = "topic_id";
    public static final String SUBJECT_ID = "subject_id";
    public static final String SUBJECT_NAME = "subject_name";

    private RecyclerView topicsRV;

    private Bundle bundle;
    private TopicListActivity context;
    private HiddenTopicAdapter hiddenTopicsAdapter;
    private TopicListAdapter topicsAdapter;

    private String subName;
    private int subId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topiclist);

        if (null == getIntent().getExtras()){
            Log.e(TAG, "onCreate: bundle is null" );
            finish();
        }
        context = this;
        bundle = getIntent().getBundleExtra(Constants.BUNDLE);

        subId = bundle.getInt(SubjectListActivity.SUBJECT_ID, 0);
        subName = bundle.getString(SubjectListActivity.SUBJECT_NAME);

        Log.i(TAG, "onCreate: subId: "+subId+", "+subName) ;
        setToolbar();
        initViews();
    }

    private void setToolbar(){
        Toolbar toolbar = findViewById(R.id.topic_list_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(CommonUtil.checkIsEmpty(subName) ? "Topics" : subName);
        Drawable navDrawable = getResources().getDrawable(R.drawable.ic_action_back_blk);
        toolbar.setNavigationIcon(navDrawable);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: clicked");
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.def_toolbar_menu,menu);
        MenuItem kbSearch = menu.findItem(R.id.search_item);
        SearchView kbSearchview = (SearchView) kbSearch.getActionView();
        kbSearchview.setMaxWidth(Integer.MAX_VALUE);
        kbSearchview.setQueryHint("Search TopicDetails... ");

        kbSearchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getTopicList(newText);
                return false;
            }
        });

        kbSearchview.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getTopicList("");
                return false;
            }
        });

        menu.findItem(R.id.menu).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                showMenu();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void initViews(){
        topicsRV = findViewById(R.id.topics_rv);
        topicsRV.setLayoutManager(new LinearLayoutManager(context));
        topicsRV.setItemAnimator(new DefaultItemAnimator());

        findViewById(R.id.add_topic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddTopicDialog();
            }
        });

        getTopicList("");
    }

    private void showMenu() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.topics_menu);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        dialog.findViewById(R.id.tv_unhide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showUnhideDialog();
            }
        });

        dialog.show();
    }

    private void showUnhideDialog() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.list_items_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        LinearLayout rootLyout = dialog.findViewById(R.id.list_items_root);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rootLyout.getLayoutParams();
        params.width = (int)(CommonUtil.getDisplayMetrics(context).widthPixels*0.8f);
        params.height = (int)(CommonUtil.getDisplayMetrics(context).heightPixels*0.8f);

        RecyclerView hiddenTopicsRV = dialog.findViewById(R.id.items_rv);
        List<TopicDetails> hiddenTopicsList = TopicManager.getInstance(context).fetchTopicDetailsList(subId,true);
        if (CommonUtil.checkIsEmpty(hiddenTopicsList)){
            dialog.findViewById(R.id.items_emptyview).setVisibility(View.VISIBLE);
            ((TextView)dialog.findViewById(R.id.items_emptyview)).setText("No Hidden Topics...");
        }else {
            dialog.findViewById(R.id.items_emptyview).setVisibility(View.GONE);
            hiddenTopicsRV.setLayoutManager(new LinearLayoutManager(context));
            hiddenTopicsRV.setItemAnimator(new DefaultItemAnimator());
            hiddenTopicsAdapter = new HiddenTopicAdapter(hiddenTopicsList, dialog);
            hiddenTopicsRV.setAdapter(hiddenTopicsAdapter);
            hiddenTopicsAdapter.notifyDataSetChanged();
        }

        SearchView searchView = dialog.findViewById(R.id.items_sv);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (null != hiddenTopicsAdapter)
                    hiddenTopicsAdapter.filter(s);
                else
                    Log.e(TAG, "onQueryTextChange: hiddenCatgryAdapter is null");
                return false;
            }
        });


        dialog.findViewById(R.id.items_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                getTopicList("");
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Log.i(TAG, "onDismiss: ... ");
                getTopicList("");
            }
        });

        dialog.show();
    }

    private void getTopicList(String query){
        List<TopicDetails> topicDetailsList = TopicManager.getInstance(context).fetchFilteredTopicDetailsList(subId, false, query);
        Log.i(TAG, "getSubList: subId: "+subId);
        Log.i(TAG, "getSubList: topicDetailsList: "+ topicDetailsList);
        if (CommonUtil.checkIsEmpty(topicDetailsList)){
            findViewById(R.id.emptyview).setVisibility(View.VISIBLE);
        }else {
            findViewById(R.id.emptyview).setVisibility(View.GONE);
        }
        topicsAdapter = new TopicListAdapter(topicDetailsList);
        topicsRV.setAdapter(topicsAdapter);
        topicsAdapter.notifyDataSetChanged();
    }

    private void showManageTopicDialog(final TopicDetails topicDetails){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.topic_manage_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        dialog.findViewById(R.id.tv_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: edit... "+ topicDetails.getTitle()+", "+ topicDetails.getId());
                dialog.dismiss();
                showEditDialog(topicDetails);
            }
        });
        dialog.findViewById(R.id.tv_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: remove... "+ topicDetails.getTitle());
                dialog.dismiss();
                showDeleteDialog(topicDetails);
            }
        });
        dialog.findViewById(R.id.tv_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: hide... "+ topicDetails.getTitle());
                topicDetails.setHidden(true);
                TopicManager.getInstance(context).updateTopic(topicDetails);
                CommonUtil.showToast(topicDetails.getTitle()+" is hidden", context);
                dialog.dismiss();
                getTopicList("");
            }
        });

        dialog.show();
    }

    private void showEditDialog(final TopicDetails topicDetails) {
        final Dialog editDialog = new Dialog(context);
        editDialog.setContentView(R.layout.list_item_edit_dialog);
        editDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white_tr99)));
        final LinearLayout rootLyout = editDialog.findViewById(R.id.edit_catgry_root);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rootLyout.getLayoutParams();
        params.width = (int)(CommonUtil.getDisplayMetrics(context).widthPixels);
        final EditText editText = editDialog.findViewById(R.id.catgry_et);
        editText.setText(topicDetails.getTitle());
        editText.requestFocus();

        editDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                CommonUtil.showKeyboard(context);
            }
        });
        View decisionLyout = editDialog.findViewById(R.id.decision_lyout);
        TextView update = decisionLyout.findViewById(R.id.ok);
        final TextView cancel = decisionLyout.findViewById(R.id.cancel);
        cancel.setVisibility(View.VISIBLE);
        update.setText("Update");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonUtil.hideKeyboard(context, editText);
                editDialog.dismiss();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!CommonUtil.checkIsEmpty(editText.getText().toString())){
                    topicDetails.setTitle(editText.getText().toString());
                    TopicManager.getInstance(context).updateTopic(topicDetails);
                    Log.i(TAG, "onClick: updateed val: "+TopicManager.getInstance(context).getTopicDetails(topicDetails.getId()));
                    CommonUtil.hideKeyboard(context, editText);
                    editDialog.dismiss();
                    CommonUtil.showToast("TopicDetails updated successfully", context);
                }else
                    editText.setError("Enter TopicDetails");
            }
        });

        editDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Log.i(TAG, "onDismiss: .... .. .");
                CommonUtil.hideKeyboard(context);
                getTopicList("");
            }
        });

        editDialog.show();
    }

    private void showDeleteDialog(final TopicDetails topicDetails){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.list_item_delete_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white_tr99)));
        LinearLayout rootLyout = dialog.findViewById(R.id.delete_catgry_root);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rootLyout.getLayoutParams();
        params.width = (int)(CommonUtil.getDisplayMetrics(context).widthPixels);
        ((TextView)dialog.findViewById(R.id.catgry_del_tv)).setText("Delete "+ topicDetails.getTitle()+"?");
        View decisionLyout = dialog.findViewById(R.id.decision_lyout);
        TextView delete = decisionLyout.findViewById(R.id.ok);
        delete.setText("Delete");
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TopicManager.getInstance(context).deleteTopic(topicDetails.getId());
                CommonUtil.showToast(topicDetails.getTitle()+" Deleted", context);
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                getTopicList("");
            }
        });
        dialog.show();
    }

    private void showAddTopicDialog(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.list_item_add_lyout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white_tr99)));
        LinearLayout rootLyout = dialog.findViewById(R.id.add_item_root);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rootLyout.getLayoutParams();
        params.width = (int)(CommonUtil.getDisplayMetrics(context).widthPixels);
        final TextInputEditText editText = dialog.findViewById(R.id.edit);
        editText.requestFocus();
        CommonUtil.showKeyboard(context);
        dialog.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!CommonUtil.checkIsEmpty(editText.getText().toString()+"".trim())) {
                    TopicDetails topicDetails = new TopicDetails();
                    topicDetails.setSubId(subId);
                    topicDetails.setTitle(editText.getText().toString());
                    topicDetails.setPriority(-1);
                    topicDetails.setHidden(false);
                    long curTime = Calendar.getInstance().getTimeInMillis();
                    topicDetails.setCreatedTime(curTime);
                    topicDetails.setUpdatedTime(curTime);
                    TopicManager.getInstance(context).addTopic(topicDetails);
                    CommonUtil.hideKeyboard(context, editText);
                    dialog.dismiss();
//                    CommonUtil.showToast("Category Added", context);
                }
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                getTopicList("");
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: requestCode: "+requestCode+", resultCode: "+resultCode);
        CommonUtil.enableTouch(context);
        if (requestCode == Constants.REQUESTCODE_ACTIVITY_TOPIC_LIST){
            if (resultCode == Constants.IS_REFRESH_NEEDED)
                getTopicList("");
        }
    }

    private class TopicListAdapter extends RecyclerView.Adapter{
        private final List<TopicDetails> topicDetails;

        public TopicListAdapter(List<TopicDetails> topicDetails) {
            this.topicDetails = topicDetails;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
            ViewHolder vwHolder = (ViewHolder)viewHolder;
            final TopicDetails topicDetails = this.topicDetails.get(i);

            vwHolder.topicTV.setText(topicDetails.getTitle());
            vwHolder.rootLyout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showContent(topicDetails);
                }
            });
            vwHolder.topicTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showContent(topicDetails);
                }
            });
            vwHolder.nextIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showContent(topicDetails);
                }
            });
            ((ViewHolder) viewHolder).topicTV.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showManageTopicDialog(topicDetails);
                    return false;
                }
            });
            ((ViewHolder) viewHolder).rootLyout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showManageTopicDialog(topicDetails);
                    return false;
                }
            });
        }

        private void showContent(TopicDetails topicDetails){
            CommonUtil.disableTouch(context);
            Intent topicIntent = new Intent(context, ContentActivity.class);
            Log.i(TAG, "showContent: topicID:: "+ topicDetails.getId());
            topicIntent.putExtra(TOPIC_ID, Long.valueOf(topicDetails.getId()));
            topicIntent.putExtra(SUBJECT_ID, topicDetails.getSubId());
            topicIntent.putExtra(SUBJECT_NAME, subName);
            startActivityForResult(topicIntent, Constants.REQUESTCODE_ACTIVITY_TOPIC_LIST);
        }

        @Override
        public int getItemCount() {
            if (null != topicDetails)
                return topicDetails.size();
            return 0;
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            private RelativeLayout rootLyout;
            private TextView topicTV;
            private ImageView nextIV;

            public ViewHolder(@NonNull ViewGroup parent) {
                super(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_with_fwd, parent, false));
                rootLyout = itemView.findViewById(R.id.item_rootview);
                topicTV = itemView.findViewById(R.id.title_label);
                nextIV = itemView.findViewById(R.id.action_next);
            }
        }
    }

    private class HiddenTopicAdapter extends RecyclerView.Adapter{
        private List<TopicDetails> hiddenTopicDetails = new ArrayList<>();
        private List<TopicDetails> hiddenTopicsCopy = new ArrayList<>();
        private List<TopicDetails> hiddenTopicsFiltered = new ArrayList<>();
        private final Dialog dialog;

        public HiddenTopicAdapter(List<TopicDetails> hiddenTopicDetails, Dialog dialog) {
            this.hiddenTopicDetails.addAll(hiddenTopicDetails);
            hiddenTopicsCopy.addAll(hiddenTopicDetails);
            this.dialog = dialog;
            Log.i(TAG, "HiddenTopicAdapter: topicDetails: "+ hiddenTopicDetails);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            ViewHolder holder = (ViewHolder)viewHolder;
            final TopicDetails topicDetails = hiddenTopicDetails.get(i);

            if (null != topicDetails){
                holder.topicTV.setText(topicDetails.getTitle());
                holder.unhide.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        topicDetails.setHidden(false);
                        TopicManager.getInstance(context).updateTopic(topicDetails);
                        if (null != dialog)
                            dialog.dismiss();
                    }
                });
            }else{
                Log.e(TAG, "onBindViewHolder: topicDetails is null" );
            }
        }

        @Override
        public int getItemCount() {
            if (null != hiddenTopicDetails)
                return hiddenTopicDetails.size();
            return 0;
        }

        public void filter(String query){
            Log.i(TAG, "filter: query: "+query);
            if (CommonUtil.checkIsEmpty(query)) {
                if (CommonUtil.checkIsEmpty(hiddenTopicsCopy))
                    return;
                else{
                    hiddenTopicDetails.clear();
                    hiddenTopicDetails.addAll(hiddenTopicsCopy);
                }
            }else {
                hiddenTopicsFiltered.clear();
                for (TopicDetails s : hiddenTopicsCopy) {
                    if (s.getTitle().contains(query) || s.getDesc().contains(query))
                        hiddenTopicsFiltered.add(s);
                }
                hiddenTopicDetails.clear();
                hiddenTopicDetails.addAll(hiddenTopicsFiltered);
            }
            if (null != hiddenTopicsAdapter)
                hiddenTopicsAdapter.notifyDataSetChanged();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            private TextView topicTV;
            private ImageView unhide;

            public ViewHolder(@NonNull ViewGroup parent) {
                super(LayoutInflater.from(parent.getContext()).inflate(R.layout.hidden_item, parent, false));
                topicTV = itemView.findViewById(R.id.item_name);
                unhide = itemView.findViewById(R.id.unhide);
            }
        }
    }
}
