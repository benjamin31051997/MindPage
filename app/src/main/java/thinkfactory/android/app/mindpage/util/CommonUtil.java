package thinkfactory.android.app.mindpage.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import thinkfactory.android.app.mindpage.R;

/**
 * Created by Benjamin J on 08-04-2019.
 */
public class CommonUtil {
    private static final String TAG = CommonUtil.class.getSimpleName();

    public static boolean checkIsEmpty(CharSequence val){
        return null == val || val.length() <= 0;
    }

    public static  <T>boolean checkIsEmpty(List<T> list){
        return null == list || list.isEmpty();
    }

    public static <T>List<T> getListFromString(String listString){
        try {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            ArrayList<T> list = gson.fromJson(listString, type);
            Log.i(TAG, "getListFromString: converted list: "+list);
            return list;
        }catch (Exception e) {e.printStackTrace();}
        return null;
    }

    public static GradientDrawable createBgDrawableWith(int backColor, float cornerRadius, int strokeWidth, int strokeColor){
        GradientDrawable gD = new GradientDrawable();
        gD.setColor(backColor);
        gD.setCornerRadius(cornerRadius);
        if (-1 != strokeWidth)
            gD.setStroke(strokeWidth, strokeColor);
//        gD.setShape(GradientDrawable.OVAL);
        return gD;
    }

    public static Dialog showDialog(Activity context, String title, String message, String posBtnTxt, String cancelBtnTxt, View.OnClickListener posClick, View.OnClickListener cancelClick){
        try {
            final Dialog dialog = new Dialog(context);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.white_tr99)));
            dialog.setContentView(R.layout.common_info_dialog);
            LinearLayout root = dialog.findViewById(R.id.comn_info_root);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) root.getLayoutParams();
            params.width = (int)(CommonUtil.getDisplayMetrics(context).widthPixels);
            if (null != title){
                dialog.findViewById(R.id.dialog_head_lyout).setVisibility(View.VISIBLE);
                ((TextView)dialog.findViewById(R.id.dialog_head)).setText(title);
            }
            ((TextView)dialog.findViewById(R.id.dialog_msg)).setText(message);
            if (null != posBtnTxt)
                ((Button)dialog.findViewById(R.id.dialog_ok)).setText(posBtnTxt);
            if (null != cancelBtnTxt)
                ((Button)dialog.findViewById(R.id.dialog_cancel)).setText(cancelBtnTxt);
            if (null != posClick){
                dialog.findViewById(R.id.dialog_ok).setOnClickListener(posClick);
            }else{
                dialog.findViewById(R.id.dialog_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
            if (null != cancelClick){
                dialog.findViewById(R.id.dialog_cancel).setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.dialog_cancel).setOnClickListener(cancelClick);
            }
            dialog.show();
            return dialog;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void showToast(final String message, final Context context){
        try {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }catch (Exception e){e.printStackTrace();}
    }

    public static DisplayMetrics getDisplayMetrics(Activity context){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics;
    }

    public static void showKeyboard(Context context){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    public static void hideKeyboard(Context context, View view){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    public static void hideKeyboard(Context context){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public static void disableTouch(Activity context){
        context.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public static void enableTouch(Activity context){
        context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public static SharedPreferences getSharedPreference(Activity context){
        return context.getApplicationContext().getSharedPreferences("default", context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor getSharedPreferenceEditor(Activity context){
        return getSharedPreference(context).edit();
    }

    public static int getTheme(Activity activity){
        return CommonUtil.getSharedPreference(activity).getInt(Constants.DISPLAY_THEME, Constants.THEME_DARK);
    }
}
