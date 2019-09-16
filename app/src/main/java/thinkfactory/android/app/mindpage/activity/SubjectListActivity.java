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
import thinkfactory.android.app.mindpage.db.manager.SubjectManager;
import thinkfactory.android.app.mindpage.model.Subject;
import thinkfactory.android.app.mindpage.util.CommonUtil;
import thinkfactory.android.app.mindpage.util.Constants;

/**
 * Created by Benjamin J on 30-06-2019.
 */
public class SubjectListActivity extends AppCompatActivity {
    private static final String TAG = SubjectListActivity.class.getSimpleName();
    public static final String SUBJECT_ID = "SUBJECT_ID";
    public static final String SUBJECT_NAME = "SUBJECT_NAME";

    private RecyclerView subRV;

    private SubjectListAdapter subjectsAdapter;
    private SubjectListActivity context;
    private Bundle bundle;

    private String catgryName;
    private int catgryId;
    private HiddenSubjectAdapter hiddenSubjectadapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjectlist);

        if (null == getIntent().getExtras()){
            Log.e(TAG, "onCreate: bundle is null" );
            finish();
        }
        context = this;
        bundle = getIntent().getBundleExtra(Constants.BUNDLE);

        catgryId = bundle.getInt(CategoryListActivity.CATGRY_ID, 0);
        catgryName = bundle.getString(CategoryListActivity.CATGRY_NAME);

        Log.i(TAG, "onCreate: catgryId: "+catgryId+", "+catgryName);
        setToolbar();
        initViews();
    }

    private void setToolbar(){
        Toolbar toolbar = findViewById(R.id.sub_list_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(CommonUtil.checkIsEmpty(catgryName) ? "Subjects" : catgryName);
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
        kbSearchview.setQueryHint("Search Subject... ");

        kbSearchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getSubList(newText);
                return false;
            }
        });

        kbSearchview.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getSubList("");
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
        subRV = findViewById(R.id.sub_rv);
        subRV.setLayoutManager(new LinearLayoutManager(context));
        subRV.setItemAnimator(new DefaultItemAnimator());

        findViewById(R.id.add_sub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddSubjectDialog();
            }
        });

        getSubList("");
    }

    private void showMenu() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.subs_menu);
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

        RecyclerView hiddenSubsRV = dialog.findViewById(R.id.items_rv);
        List<Subject> hiddensubList = SubjectManager.getInstance(context).fetchSubjectsForCategory(catgryId,true);
        if (CommonUtil.checkIsEmpty(hiddensubList)){
            dialog.findViewById(R.id.items_emptyview).setVisibility(View.VISIBLE);
            ((TextView)dialog.findViewById(R.id.items_emptyview)).setText("No Hidden Subjects...");
        }else {
            dialog.findViewById(R.id.items_emptyview).setVisibility(View.GONE);
            hiddenSubsRV.setLayoutManager(new LinearLayoutManager(context));
            hiddenSubsRV.setItemAnimator(new DefaultItemAnimator());
            hiddenSubjectadapter = new HiddenSubjectAdapter(hiddensubList, dialog);
            hiddenSubsRV.setAdapter(hiddenSubjectadapter);
            hiddenSubjectadapter.notifyDataSetChanged();
        }

        SearchView searchView = dialog.findViewById(R.id.items_sv);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (null != hiddenSubjectadapter)
                    hiddenSubjectadapter.filter(s);
                else
                    Log.e(TAG, "onQueryTextChange: hiddenCatgryAdapter is null");
                return false;
            }
        });


        dialog.findViewById(R.id.items_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                getSubList("");
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Log.i(TAG, "onDismiss: ... ");
                getSubList("");
            }
        });

        dialog.show();
    }

    private void getSubList(String query){
        List<Subject> subjectList = SubjectManager.getInstance(context).fetchFilteredSubjects(catgryId, false, query);
        Log.i(TAG, "getSubList: catgryId: "+catgryId);
        Log.i(TAG, "getSubList: sublist: "+subjectList);
        if (CommonUtil.checkIsEmpty(subjectList)){
            findViewById(R.id.emptyview).setVisibility(View.VISIBLE);
        }else {
            findViewById(R.id.emptyview).setVisibility(View.GONE);
        }
        subjectsAdapter = new SubjectListAdapter(subjectList);
        subRV.setAdapter(subjectsAdapter);
        subjectsAdapter.notifyDataSetChanged();
    }

    private void showManageSubDialog(final Subject subject){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.sub_manage_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        dialog.findViewById(R.id.tv_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: edit... "+ subject.getName()+", "+ subject.getId());
                dialog.dismiss();
                showEditDialog(subject);
            }
        });
        dialog.findViewById(R.id.tv_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: remove... "+ subject.getName());
                dialog.dismiss();
                showDeleteDialog(subject);
            }
        });
        dialog.findViewById(R.id.tv_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: hide... "+ subject.getName());
                subject.setHidden(true);
                SubjectManager.getInstance(context).updateSubject(subject);
                CommonUtil.showToast(subject.getName()+" is hidden", context);
                dialog.dismiss();
                getSubList("");
            }
        });

        dialog.show();
    }

    private void showEditDialog(final Subject subject) {
        final Dialog editDialog = new Dialog(context);
        editDialog.setContentView(R.layout.list_item_edit_dialog);
        editDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white_tr99)));
        final LinearLayout rootLyout = editDialog.findViewById(R.id.edit_catgry_root);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rootLyout.getLayoutParams();
        params.width = (int)(CommonUtil.getDisplayMetrics(context).widthPixels);
        final EditText editText = editDialog.findViewById(R.id.catgry_et);
        editText.setText(subject.getName());
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
                    subject.setName(editText.getText().toString());
                    SubjectManager.getInstance(context).updateSubject(subject);
                    Log.i(TAG, "onClick: updateed val: "+SubjectManager.getInstance(context).getSubject(subject.getId()));
                    CommonUtil.hideKeyboard(context, editText);
                    editDialog.dismiss();
                    CommonUtil.showToast("Subject updated successfully", context);
                }else
                    editText.setError("Enter Subject");
            }
        });

        editDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Log.i(TAG, "onDismiss: .... .. .");
                CommonUtil.hideKeyboard(context);
                getSubList("");
            }
        });

        editDialog.show();
    }

    private void showDeleteDialog(final Subject subject){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.list_item_delete_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white_tr99)));
        LinearLayout rootLyout = dialog.findViewById(R.id.delete_catgry_root);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rootLyout.getLayoutParams();
        params.width = (int)(CommonUtil.getDisplayMetrics(context).widthPixels);
        ((TextView)dialog.findViewById(R.id.catgry_del_tv)).setText("Delete "+ subject.getName()+"?");
        View decisionLyout = dialog.findViewById(R.id.decision_lyout);
        TextView delete = decisionLyout.findViewById(R.id.ok);
        delete.setText("Delete");
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubjectManager.getInstance(context).deleteSubject(subject.getId());
                CommonUtil.showToast(subject.getName()+" Deleted", context);
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                getSubList("");
            }
        });
        dialog.show();
    }

    private void showAddSubjectDialog(){
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
                if (!CommonUtil.checkIsEmpty(editText.getText().toString())) {
                    Subject subject = new Subject();
                    subject.setCatgryId(catgryId);
                    subject.setName(editText.getText().toString());
                    subject.setPriority(-1);
                    subject.setHidden(false);
                    long curTime = Calendar.getInstance().getTimeInMillis();
                    subject.setCreatedTime(curTime);
                    subject.setUpdatedTime(curTime);
                    SubjectManager.getInstance(context).addSubject(subject);
                    CommonUtil.hideKeyboard(context, editText);
                    dialog.dismiss();
//                    CommonUtil.showToast("Category Added", context);
                }
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                getSubList("");
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: requestCode: "+requestCode+", resultCod: "+resultCode);
        CommonUtil.enableTouch(context);
    }

    private class SubjectListAdapter extends RecyclerView.Adapter{
        private final List<Subject> subjects;

        public SubjectListAdapter(List<Subject> subjects) {
            this.subjects = subjects;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
            ViewHolder vwHolder = (ViewHolder)viewHolder;
            final Subject subject = subjects.get(i);

            vwHolder.subTV.setText(subject.getName());
            vwHolder.rootLyout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showTopics(subject);
                }
            });
            vwHolder.subTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showTopics(subject);
                }
            });
            vwHolder.nextIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showTopics(subject);
                }
            });
            ((ViewHolder) viewHolder).subTV.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showManageSubDialog(subject);
                    return false;
                }
            });
            ((ViewHolder) viewHolder).rootLyout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showManageSubDialog(subject);
                    return false;
                }
            });
        }

        private void showTopics(Subject subject){
            CommonUtil.disableTouch(context);
            Bundle bundle = new Bundle();
            Log.i(TAG, "onClick: subId: "+subject.getId());
            bundle.putInt(SUBJECT_ID, subject.getId());
            bundle.putString(SUBJECT_NAME, subject.getName());
            Intent intent = new Intent(context, TopicListActivity.class);
            intent.putExtra(Constants.BUNDLE, bundle);
            startActivityForResult(intent, Constants.REQUESTCODE_ACTIVITY_SUBJECT_LIST);
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        }

        @Override
        public int getItemCount() {
            if (null != subjects)
                return subjects.size();
            return 0;
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            private RelativeLayout rootLyout;
            private TextView subTV;
            private ImageView nextIV;

            public ViewHolder(@NonNull ViewGroup parent) {
                super(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_with_fwd, parent, false));
                rootLyout = itemView.findViewById(R.id.item_rootview);
                subTV = itemView.findViewById(R.id.title_label);
                nextIV = itemView.findViewById(R.id.action_next);
            }
        }
    }

    private class HiddenSubjectAdapter extends RecyclerView.Adapter{
        private List<Subject> hiddenSubs = new ArrayList<>();
        private List<Subject> hiddenSubsCopy = new ArrayList<>();
        private List<Subject> hiddenSubsFiltered = new ArrayList<>();
        private final Dialog dialog;

        public HiddenSubjectAdapter(List<Subject> hiddenSubs, Dialog dialog) {
            this.hiddenSubs.addAll(hiddenSubs);
            hiddenSubsCopy.addAll(hiddenSubs);
            this.dialog = dialog;
            Log.i(TAG, "HiddenTopicAdapter: subjects: "+ hiddenSubs);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            ViewHolder holder = (ViewHolder)viewHolder;
            final Subject subject = hiddenSubs.get(i);

            if (null != subject){
                holder.subTV.setText(subject.getName());
                holder.unhide.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        subject.setHidden(false);
                        SubjectManager.getInstance(context).updateSubject(subject);
                        if (null != dialog)
                            dialog.dismiss();
                    }
                });
            }else{
                Log.e(TAG, "onBindViewHolder: subject is null" );
            }
        }

        @Override
        public int getItemCount() {
            if (null != hiddenSubs)
                return hiddenSubs.size();
            return 0;
        }

        public void filter(String query){
            Log.i(TAG, "filter: query: "+query);
            if (CommonUtil.checkIsEmpty(query)) {
                if (CommonUtil.checkIsEmpty(hiddenSubsCopy))
                    return;
                else{
                    hiddenSubs.clear();
                    hiddenSubs.addAll(hiddenSubsCopy);
                }
            }else {
                hiddenSubsFiltered.clear();
                for (Subject s : hiddenSubsCopy) {
                    if (s.getName().contains(query))
                        hiddenSubsFiltered.add(s);
                }
                hiddenSubs.clear();
                hiddenSubs.addAll(hiddenSubsFiltered);
            }
            if (null != hiddenSubjectadapter)
                hiddenSubjectadapter.notifyDataSetChanged();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            private TextView subTV;
            private ImageView unhide;

            public ViewHolder(@NonNull ViewGroup parent) {
                super(LayoutInflater.from(parent.getContext()).inflate(R.layout.hidden_item, parent, false));
                subTV = itemView.findViewById(R.id.item_name);
                unhide = itemView.findViewById(R.id.unhide);
            }
        }
    }
}
