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
import thinkfactory.android.app.mindpage.db.manager.SubjectManager;
import thinkfactory.android.app.mindpage.model.Category;
import thinkfactory.android.app.mindpage.util.CommonUtil;
import thinkfactory.android.app.mindpage.util.Constants;

/**
 * Created by Benjamin J on 08-04-2019.
 */
public class CategoryListActivity extends AppCompatActivity {
    private static final String TAG = CategoryListActivity.class.getSimpleName();
    public static final String CATGRY_ID = "CATGRY_ID";
    public static final String CATGRY_NAME = "CATGRY_NAME";

    private RecyclerView catryRV;
    private CategoriesAdapter categoriesAdapter;

    private CategoryListActivity context;
    private HiddenCatgryAdapter hiddenCatgryAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catgrylist);
        context = this;
        setToolbar();
        initViews();
    }

    private void setToolbar(){
        Toolbar toolbar = findViewById(R.id.catgry_list_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Categories");
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
                getCatgryList(newText);
                return false;
            }
        });

        kbSearchview.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getCatgryList("");
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

    @Override
    public void onBackPressed() {
        if (CommonUtil.getSharedPreference(context).getInt(Constants.DEFAULT_ACTIVITY, Constants.DEFAULT_ACTIVITY_HOME) == Constants.DEFAULT_ACTIVITY_WORKBOOK)
            exit();
        else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        }
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


    private void initViews(){
        catryRV = findViewById(R.id.catgry_rv);
        catryRV.setLayoutManager(new LinearLayoutManager(context));
        catryRV.setItemAnimator(new DefaultItemAnimator());

        findViewById(R.id.add_catgry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddCatgryDialog();
            }
        });

        getCatgryList("");
    }

    private void showMenu() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.catgrys_menu);
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
        List<Category> hiddenCatgryList = CategoryManager.getInstance(context).fetchCategoriesByVisibility(true);
        if (CommonUtil.checkIsEmpty(hiddenCatgryList)){
            dialog.findViewById(R.id.items_emptyview).setVisibility(View.VISIBLE);
            ((TextView)dialog.findViewById(R.id.items_emptyview)).setText("No Hidden Categories...");
        }else {
            dialog.findViewById(R.id.items_emptyview).setVisibility(View.GONE);
            hiddenCatgryRV.setLayoutManager(new LinearLayoutManager(context));
            hiddenCatgryRV.setItemAnimator(new DefaultItemAnimator());
            hiddenCatgryAdapter = new HiddenCatgryAdapter(hiddenCatgryList, dialog);
            hiddenCatgryRV.setAdapter(hiddenCatgryAdapter);
            hiddenCatgryAdapter.notifyDataSetChanged();
        }
        SearchView searchView = dialog.findViewById(R.id.items_sv);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (null != hiddenCatgryAdapter)
                    hiddenCatgryAdapter.filter(s);
                else
                    Log.e(TAG, "onQueryTextChange: hiddenCatgryAdapter is null");
                return false;
            }
        });

        dialog.findViewById(R.id.items_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                getCatgryList("");
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Log.i(TAG, "onDismiss: ... ");
                getCatgryList("");
            }
        });

        dialog.show();
    }

    private void getCatgryList(String query){
        Log.i(TAG, "getCatgryList: query: "+query);
        List<Category> catgryList = CategoryManager.getInstance(context).fetchFilteredCategories(query, false);
        if (CommonUtil.checkIsEmpty(catgryList)){
            findViewById(R.id.emptyview).setVisibility(View.VISIBLE);
        }else {
            findViewById(R.id.emptyview).setVisibility(View.GONE);
        }
        categoriesAdapter = new CategoriesAdapter(catgryList);
        catryRV.setAdapter(categoriesAdapter);
        categoriesAdapter.notifyDataSetChanged();
    }

    private void showManageCatgryDialog(final Category category){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.catrgy_manage_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        dialog.findViewById(R.id.tv_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: edit... "+category.getName()+", "+category.getId());
                dialog.dismiss();
                showEditDialog(category);
            }
        });
        dialog.findViewById(R.id.tv_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: remove... "+category.getName());
                dialog.dismiss();
                showDeleteDialog(category);
            }
        });
        dialog.findViewById(R.id.tv_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: hide... "+category.getName());
                category.setHidden(true);
                CategoryManager.getInstance(context).updateCategory(category);
                CommonUtil.showToast(category.getName()+" is hidden", context);
                dialog.dismiss();
                getCatgryList("");
            }
        });

        dialog.show();
    }

    private void showEditDialog(final Category category) {
        final Dialog editDialog = new Dialog(context);
        editDialog.setContentView(R.layout.list_item_edit_dialog);
        editDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white_tr99)));
        LinearLayout rootLyout = editDialog.findViewById(R.id.edit_catgry_root);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rootLyout.getLayoutParams();
        params.width = (int)(CommonUtil.getDisplayMetrics(context).widthPixels);
        final EditText editText = editDialog.findViewById(R.id.catgry_et);
        editText.setText(category.getName());
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
                    category.setName(editText.getText().toString());
                    CategoryManager.getInstance(context).updateCategory(category);
                    Log.i(TAG, "onClick: updateed val: "+CategoryManager.getInstance(context).getCategory(category.getId()));
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
                getCatgryList("");
            }
        });

        editDialog.show();
    }

    private void showDeleteDialog(final Category category){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.list_item_delete_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white_tr99)));
        LinearLayout rootLyout = dialog.findViewById(R.id.delete_catgry_root);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rootLyout.getLayoutParams();
        params.width = (int)(CommonUtil.getDisplayMetrics(context).widthPixels);
        ((TextView)dialog.findViewById(R.id.catgry_del_tv)).setText("Delete "+category.getName()+"?");
        View decisionLyout = dialog.findViewById(R.id.decision_lyout);
        TextView delete = decisionLyout.findViewById(R.id.ok);
        delete.setText("Delete");
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubjectManager.getInstance(context).deleteCategorySubjects(category.getId());
                CategoryManager.getInstance(context).deleteCategory(category.getId());
                CommonUtil.showToast(category.getName()+" Deleted", context);
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                getCatgryList("");
            }
        });
        dialog.show();
    }

    private void showAddCatgryDialog(){
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
                    Category category = new Category();
                    category.setName(editText.getText().toString());
                    category.setPriority(-1);
                    category.setHidden(false);
                    long curTime = Calendar.getInstance().getTimeInMillis();
                    category.setCreatedTime(curTime);
                    category.setUpdatedTime(curTime);
                    CategoryManager.getInstance(context).addCategory(category);
                    CommonUtil.hideKeyboard(context, editText);
                    dialog.dismiss();
//                    CommonUtil.showToast("Category Added", context);
                }
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                getCatgryList("");
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

    private class CategoriesAdapter extends RecyclerView.Adapter{
        private final List<Category> categories;

        public CategoriesAdapter(List<Category> categories) {
            this.categories = categories;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
            ViewHolder vwHolder = (ViewHolder)viewHolder;
            final Category category = categories.get(i);

            vwHolder.catgryTV.setText(category.getName());
            vwHolder.rootLyout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showSubjects(category);
                }
            });
            vwHolder.catgryTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showSubjects(category);
                }
            });
            vwHolder.nextIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showSubjects(category);
                }
            });
            ((ViewHolder) viewHolder).catgryTV.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showManageCatgryDialog(category);
                    return false;
                }
            });
            ((ViewHolder) viewHolder).rootLyout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showManageCatgryDialog(category);
                    return false;
                }
            });
        }

        private void showSubjects(Category category){
            CommonUtil.disableTouch(context);
            Bundle bundle = new Bundle();
            Log.i(TAG, "onClick: catId: "+category.getId());
            bundle.putInt(CATGRY_ID, category.getId());
            bundle.putString(CATGRY_NAME, category.getName());
            Intent intent = new Intent(context, SubjectListActivity.class);
            intent.putExtra(Constants.BUNDLE, bundle);
            startActivityForResult(intent, Constants.REQUESTCODE_ACTIVITY_CATEGORY_LIST);
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        }

        @Override
        public int getItemCount() {
            if (null != categories)
                return categories.size();
            return 0;
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            private RelativeLayout rootLyout;
            private TextView catgryTV;
            private ImageView nextIV;

            public ViewHolder(@NonNull ViewGroup parent) {
                super(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_with_fwd, parent, false));
                rootLyout = itemView.findViewById(R.id.item_rootview);
                catgryTV = itemView.findViewById(R.id.title_label);
                nextIV = itemView.findViewById(R.id.action_next);
            }
        }
    }

    private class HiddenCatgryAdapter extends RecyclerView.Adapter {
        private List<Category> hiddenCategories = new ArrayList<>();
        private List<Category> hiddenCategoriesCopy = new ArrayList<>();
        private List<Category> hiddenCategoriesFiltered = new ArrayList<>();
        private final Dialog dialog;

        public HiddenCatgryAdapter(List<Category> hiddenCategories, Dialog dialog) {
            this.hiddenCategories.addAll(hiddenCategories);
            hiddenCategoriesCopy.addAll(hiddenCategories);
            this.dialog = dialog;
            Log.i(TAG, "HiddenTopicAdapter: categories: "+hiddenCategories);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            ViewHolder holder = (ViewHolder)viewHolder;
            final Category category = hiddenCategories.get(i);

            if (null != category){
                holder.catgryTV.setText(category.getName());
                holder.unhide.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        category.setHidden(false);
                        CategoryManager.getInstance(context).updateCategory(category);
                        if (null != dialog)
                            dialog.dismiss();
                    }
                });
            }else{
                Log.e(TAG, "onBindViewHolder: category is null" );
            }
        }

        @Override
        public int getItemCount() {
            if (null != hiddenCategories)
                return hiddenCategories.size();
            return 0;
        }

        public void filter(String query){
            Log.i(TAG, "filter: query: "+query);
            if (CommonUtil.checkIsEmpty(query)) {
                if (CommonUtil.checkIsEmpty(hiddenCategoriesCopy))
                    return;
                else{
                    hiddenCategories.clear();
                    hiddenCategories.addAll(hiddenCategoriesCopy);
                }
            }else {
                hiddenCategoriesFiltered.clear();
                for (Category c : hiddenCategoriesCopy) {
                    if (c.getName().contains(query))
                        hiddenCategoriesFiltered.add(c);
                }
                hiddenCategories.clear();
                hiddenCategories.addAll(hiddenCategoriesFiltered);
            }
            if (null != hiddenCatgryAdapter)
                hiddenCatgryAdapter.notifyDataSetChanged();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            private TextView catgryTV;
            private ImageView unhide;

            public ViewHolder(@NonNull ViewGroup parent) {
                super(LayoutInflater.from(parent.getContext()).inflate(R.layout.hidden_item, parent, false));
                catgryTV = itemView.findViewById(R.id.item_name);
                unhide = itemView.findViewById(R.id.unhide);
            }
        }
    }
}
