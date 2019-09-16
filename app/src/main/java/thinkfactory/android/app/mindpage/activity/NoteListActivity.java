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
import thinkfactory.android.app.mindpage.db.manager.CategoryManager;
import thinkfactory.android.app.mindpage.db.manager.TextnoteManger;
import thinkfactory.android.app.mindpage.model.Textnote;
import thinkfactory.android.app.mindpage.model.TextnoteDetails;
import thinkfactory.android.app.mindpage.util.CommonUtil;
import thinkfactory.android.app.mindpage.util.Constants;


/**
 * Created by Benjamin J on 18-08-2019.
 */
public class NoteListActivity extends AppCompatActivity {
    private static final String TAG = NoteListActivity.class.getSimpleName();
    public static final String NOTE_ID = "noteId";

    private RecyclerView notesRV;

    private List<Textnote> notes = new ArrayList<>();

    private NoteListActivity context;
    private NotesAdapter notesAdapter;
    private HiddenNotesAdapter hiddenNotesAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notelist);
        context = this;

        setToolbar();
    }

    private void setToolbar(){
        Toolbar toolbar = findViewById(R.id.note_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notes");
        Drawable navDrawable = getResources().getDrawable(R.drawable.ic_action_back_blk);
        toolbar.setNavigationIcon(navDrawable);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonUtil.getSharedPreference(context).getInt(Constants.DEFAULT_ACTIVITY, Constants.DEFAULT_ACTIVITY_HOME) == Constants.DEFAULT_ACTIVITY_WORKBOOK)
                    exit();
                else
                    onBackPressed();
            }
        });

        initViews();
    }

    private void initViews(){
        notesRV = findViewById(R.id.notes_rv);
        notesRV.setLayoutManager(new LinearLayoutManager(context));
        notesRV.setItemAnimator(new DefaultItemAnimator());

        findViewById(R.id.add_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddNoteDialog();
            }
        });

        getNotes("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.def_toolbar_menu,menu);
        MenuItem kbSearch = menu.findItem(R.id.search_item);
        SearchView kbSearchview = (SearchView) kbSearch.getActionView();
        kbSearchview.setMaxWidth(Integer.MAX_VALUE);
        kbSearchview.setQueryHint("Search Category... ");

        kbSearchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getNotes(newText);
                return false;
            }
        });

        kbSearchview.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getNotes("");
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

    private void showMenu() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.notes_menu);
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

        RecyclerView hiddenCatgryRV = dialog.findViewById(R.id.items_rv);
        List<Textnote> hiddenNoteList = TextnoteManger.getInstance(context).getNoteList(true, "");
        if (CommonUtil.checkIsEmpty(hiddenNoteList)){
            dialog.findViewById(R.id.items_emptyview).setVisibility(View.VISIBLE);
            ((TextView)dialog.findViewById(R.id.items_emptyview)).setText("No Hidden Categories...");
        }else {
            dialog.findViewById(R.id.items_emptyview).setVisibility(View.GONE);
            hiddenCatgryRV.setLayoutManager(new LinearLayoutManager(context));
            hiddenCatgryRV.setItemAnimator(new DefaultItemAnimator());
            hiddenNotesAdapter = new HiddenNotesAdapter(hiddenNoteList, dialog);
            hiddenCatgryRV.setAdapter(hiddenNotesAdapter);
            hiddenNotesAdapter.notifyDataSetChanged();
        }
        SearchView searchView = dialog.findViewById(R.id.items_sv);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (null != hiddenNotesAdapter)
                    hiddenNotesAdapter.filter(s);
                else
                    Log.e(TAG, "onQueryTextChange: hiddenNotesAdapter is null");
                return false;
            }
        });

        dialog.findViewById(R.id.items_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                getNotes("");
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Log.i(TAG, "onDismiss: ... ");
                getNotes("");
            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if (CommonUtil.getSharedPreference(context).getInt(Constants.DEFAULT_ACTIVITY, Constants.DEFAULT_ACTIVITY_HOME) == Constants.DEFAULT_ACTIVITY_TEXTNOTE)
            exit();
        else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        }
    }

    private void getNotes(String query){
        notes = TextnoteManger.getInstance(context).getNoteList(false, query);
        if (CommonUtil.checkIsEmpty(notes)){
            findViewById(R.id.emptyview).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.emptyview).setVisibility(View.GONE);
        }
        notesAdapter = new NotesAdapter(notes);
        notesRV.setAdapter(notesAdapter);
        notesAdapter.notifyDataSetChanged();
    }

    private void exit() {
        CommonUtil.showDialog(context,
                null,
                "Do you want to exit",
                "OK",
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finishAffinity();
                    }
                },
                null);
    }

    private void showManageCatgryDialog(final Textnote note){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.catrgy_manage_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        dialog.findViewById(R.id.tv_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: edit... "+ note.getTitle()+", "+ note.getId());
                dialog.dismiss();
                showEditDialog(note);
            }
        });
        dialog.findViewById(R.id.tv_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: remove... "+ note.getTitle());
                dialog.dismiss();
                showDeleteDialog(note);
            }
        });
        dialog.findViewById(R.id.tv_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: hide... "+ note.getTitle());
                if (null == note){
                    Log.e(TAG, "onClick: note is null" );
                    CommonUtil.showToast("Unable to hide Note...", context);
                    return;
                }
                TextnoteDetails details = TextnoteManger.getInstance(context).getNoteDetails(note.getId());
                if (null == details){
                    Log.e(TAG, "onClick: fetched note details is null" );
                    CommonUtil.showToast("Unable to hide Note...", context);
                    return;
                }
                details.setHidden(true);
                TextnoteManger.getInstance(context).updateNote(details);
                CommonUtil.showToast(note.getTitle()+" is hidden", context);
                dialog.dismiss();
                getNotes("");
            }
        });

        dialog.show();
    }

    private void showEditDialog(final Textnote note) {
        final Dialog editDialog = new Dialog(context);
        editDialog.setContentView(R.layout.list_item_edit_dialog);
        editDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white_tr99)));
        LinearLayout rootLyout = editDialog.findViewById(R.id.edit_catgry_root);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rootLyout.getLayoutParams();
        params.width = (int)(CommonUtil.getDisplayMetrics(context).widthPixels);
        final EditText editText = editDialog.findViewById(R.id.catgry_et);
        editText.setText(note.getTitle());
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
                    if (null == note){
                        Log.e(TAG, "onClick: note is null" );
                        CommonUtil.showToast("Unable to update Note...", context);
                        return;
                    }
                    TextnoteDetails details = TextnoteManger.getInstance(context).getNoteDetails(note.getId());
                    if (null == details){
                        Log.e(TAG, "onClick: fetched note details is null" );
                        CommonUtil.showToast("Unable to update Note...", context);
                        return;
                    }
                    details.setTitle(editText.getText().toString());
                    TextnoteManger.getInstance(context).updateNote(details);
                    Log.i(TAG, "onClick: updateed val: "+CategoryManager.getInstance(context).getCategory(note.getId()));
                    CommonUtil.hideKeyboard(context, editText);
                    editDialog.dismiss();
                    CommonUtil.showToast("Category updated successfully", context);
                }else
                    editText.setError("Enter Category");
            }
        });

        editDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                CommonUtil.hideKeyboard(context);
                getNotes("");
            }
        });

        editDialog.show();
    }

    private void showDeleteDialog(final Textnote note){
        if (null == note){
            Log.e(TAG, "onClick: note is null" );
            CommonUtil.showToast("Unable to delete Note...", context);
            return;
        }
        TextnoteDetails details = TextnoteManger.getInstance(context).getNoteDetails(note.getId());
        if (null == details){
            Log.e(TAG, "onClick: fetched note details is null" );
            CommonUtil.showToast("Unable to delete Note...", context);
            return;
        }
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.list_item_delete_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white_tr99)));
        LinearLayout rootLyout = dialog.findViewById(R.id.delete_catgry_root);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rootLyout.getLayoutParams();
        params.width = (int)(CommonUtil.getDisplayMetrics(context).widthPixels);
        ((TextView)dialog.findViewById(R.id.catgry_del_tv)).setText("Delete "+ note.getTitle()+"?");
        View decisionLyout = dialog.findViewById(R.id.decision_lyout);
        TextView delete = decisionLyout.findViewById(R.id.ok);
        delete.setText("Delete");
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextnoteManger.getInstance(context).deleteNote(note.getId());
                CommonUtil.showToast("Note Deleted...", context);
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                getNotes("");
            }
        });
        dialog.show();
    }

    private void showAddNoteDialog(){
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
                    TextnoteDetails note = new TextnoteDetails();
                    note.setTitle(editText.getText().toString());
                    note.setPriority(-1);
                    note.setHidden(false);
                    long curTime = Calendar.getInstance().getTimeInMillis();
                    note.setCreatedTime(curTime);
                    note.setUpdatedTime(curTime);
                    TextnoteManger.getInstance(context).addNote(note);
                    CommonUtil.hideKeyboard(context, editText);
                    dialog.dismiss();
//                    CommonUtil.showToast("Category Added", context);
                }
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                getNotes("");
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: requestCode: "+requestCode+", resultCode: "+resultCode);
        CommonUtil.enableTouch(context);
    }

    private class NotesAdapter extends RecyclerView.Adapter{
        private final List<Textnote> notes;

        public NotesAdapter(List<Textnote> notes) {
            this.notes = notes;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
            ViewHolder vwHolder = (ViewHolder)viewHolder;
            final Textnote note = notes.get(i);

            vwHolder.noteTV.setText(note.getTitle());
            vwHolder.rootLyout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showContent(note);
                }
            });
            vwHolder.noteTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showContent(note);
                }
            });
            vwHolder.nextIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showContent(note);
                }
            });
            ((ViewHolder) viewHolder).noteTV.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showManageCatgryDialog(note);
                    return false;
                }
            });
            ((ViewHolder) viewHolder).rootLyout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showManageCatgryDialog(note);
                    return false;
                }
            });
        }

        private void showContent(Textnote note){
            CommonUtil.disableTouch(context);
            Bundle bundle = new Bundle();
            Log.i(TAG, "onClick: noteId: "+ note.getId());
            bundle.putInt(NOTE_ID, note.getId());
            Intent intent = new Intent(context, NoteContentActivity.class);
            intent.putExtra(Constants.BUNDLE, bundle);
            startActivityForResult(intent, Constants.REQUESTCODE_ACTIVITY_TEXTNOTE);
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        }

        @Override
        public int getItemCount() {
            if (null != notes)
                return notes.size();
            return 0;
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            private RelativeLayout rootLyout;
            private TextView noteTV;
            private ImageView nextIV;

            public ViewHolder(@NonNull ViewGroup parent) {
                super(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_with_fwd, parent, false));
                rootLyout = itemView.findViewById(R.id.item_rootview);
                noteTV = itemView.findViewById(R.id.title_label);
                nextIV = itemView.findViewById(R.id.action_next);
            }
        }
    }

    private class HiddenNotesAdapter extends RecyclerView.Adapter {
        private List<Textnote> hiddenNotes = new ArrayList<>();
        private List<Textnote> hiddenNotesCopy = new ArrayList<>();
        private List<Textnote> hiddenNotesFiltered = new ArrayList<>();
        private final Dialog dialog;

        public HiddenNotesAdapter(List<Textnote> hiddenNotes, Dialog dialog) {
            this.hiddenNotes.addAll(hiddenNotes);
            hiddenNotesCopy.addAll(hiddenNotes);
            this.dialog = dialog;
            Log.i(TAG, "HiddenNotesAdapter: notes: "+ hiddenNotes);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            ViewHolder holder = (ViewHolder)viewHolder;
            final Textnote note = hiddenNotes.get(i);

            if (null != note){
                holder.noteTV.setText(note.getTitle());
                holder.unhide.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextnoteDetails details = TextnoteManger.getInstance(context).getNoteDetails(note.getId());
                        if (null == details){
                            Log.e(TAG, "onClick: fetched note details is null" );
                            CommonUtil.showToast("Unable to unhide Note...", context);
                            return;
                        }
                        details.setHidden(false);
                        TextnoteManger.getInstance(context).updateNote(details);
                        if (null != dialog)
                            dialog.dismiss();
                    }
                });
            }else{
                Log.e(TAG, "onBindViewHolder: note is null" );
                CommonUtil.showToast("Unable to unhide Note...", context);
            }
        }

        @Override
        public int getItemCount() {
            if (null != hiddenNotes)
                return hiddenNotes.size();
            return 0;
        }

        public void filter(String query){
            Log.i(TAG, "filter: query: "+query);
            if (CommonUtil.checkIsEmpty(query)) {
                if (CommonUtil.checkIsEmpty(hiddenNotesCopy))
                    return;
                else{
                    hiddenNotes.clear();
                    hiddenNotes.addAll(hiddenNotesCopy);
                }
            }else {
                hiddenNotesFiltered.clear();
                for (Textnote note : hiddenNotesCopy) {
                    if (note.getTitle().contains(query))
                        hiddenNotesFiltered.add(note);
                }
                hiddenNotes.clear();
                hiddenNotes.addAll(hiddenNotesFiltered);
            }
            if (null != hiddenNotesAdapter)
                hiddenNotesAdapter.notifyDataSetChanged();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            private TextView noteTV;
            private ImageView unhide;

            public ViewHolder(@NonNull ViewGroup parent) {
                super(LayoutInflater.from(parent.getContext()).inflate(R.layout.hidden_item, parent, false));
                noteTV = itemView.findViewById(R.id.item_name);
                unhide = itemView.findViewById(R.id.unhide);
            }
        }
    }
}
