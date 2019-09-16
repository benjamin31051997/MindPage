package thinkfactory.android.app.mindpage.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.List;

import thinkfactory.android.app.mindpage.R;
import thinkfactory.android.app.mindpage.db.manager.TopicManager;
import thinkfactory.android.app.mindpage.model.Topic;
import thinkfactory.android.app.mindpage.util.CommonUtil;
import thinkfactory.android.app.mindpage.util.Constants;

/**
 * Created by Benjamin J on 08-04-2019.
 */
public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();

    private HomeActivity context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context = this;

        setToolbar();
        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (null != context)
            CommonUtil.enableTouch(context);
    }

    private void setToolbar(){
        Toolbar toolbar = findViewById(R.id.home_toolbar);
        toolbar.setTitle("Home");
        Drawable navDrawable = getResources().getDrawable(R.drawable.ic_action_back_blk);
        toolbar.setNavigationIcon(navDrawable);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit();
            }
        });
    }

    @Override
    public void onBackPressed() {
        exit();
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


    private void initViews() {
        findViewById(R.id.workbook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonUtil.disableTouch(context);
                Intent intent = new Intent(context, CategoryListActivity.class);
                startActivityForResult(intent, Constants.REQUESTCODE_ACTIVITY_HOME);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        findViewById(R.id.textnote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonUtil.disableTouch(context);
                Intent intent = new Intent(context, NoteListActivity.class);
                startActivityForResult(intent, Constants.REQUESTCODE_ACTIVITY_HOME);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: requestCode: "+requestCode+", resultCode:  "+resultCode);
        CommonUtil.enableTouch(context);
    }
}
